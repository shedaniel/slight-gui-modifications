package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement implements Drawable, AnimationListener, MenuWidgetListener {
    @Shadow
    public int height;
    @Shadow
    @Final
    protected List<AbstractButtonWidget> buttons;
    
    @Shadow
    public abstract void renderDirtBackground(int alpha);
    
    @Shadow protected MinecraftClient minecraft;
    @Shadow protected TextRenderer font;
    @Shadow @Final protected List<Element> children;
    @Unique
    private long fadeStart = -1;
    @Unique
    private long currentFade = -1;
    @Unique
    private int renderingState = 0;
    @Unique
    private Screen lastScreen;
    @Unique
    private boolean slide = false;
    @Unique
    private boolean fade = false;
    @Unique
    private MenuWidget menuWidget = null;
    @Unique
    private Runnable runnable = null;
    
    @Override
    public void slightguimodifications_openScreen(Screen lastScreen) {
        this.lastScreen = null;
        this.slide = false;
        this.fade = false;
        if ((Object) this instanceof ChatScreen)
            return;
        if ((Object) this instanceof TitleScreen && ((TitleScreenHooks) this).isDoBackgroundFade() && (((TitleScreenHooks) this).getBackgroundFadeStart() == 0 || Util.getMeasuringTimeMs() - ((TitleScreenHooks) this).getBackgroundFadeStart() <= 1000))
            return;
        SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
        if (lastScreen != null && (Object) this.getClass() == lastScreen.getClass())
            return;
        boolean affected = (Object) this instanceof ContainerScreen ? config.openingAnimation.affectsInventories : config.openingAnimation.affectsGameMenus;
        this.slide = affected && config.openingAnimation.fluidOpenSlideFromBottom && (lastScreen == null || lastScreen instanceof TitleScreen || !config.openingAnimation.ignoreSlideWhenRedirected);
        this.fade = affected && config.openingAnimation.fluidOpenFade && (lastScreen == null || lastScreen instanceof TitleScreen || !config.openingAnimation.ignoreFadeWhenRedirected);
        if (this.slide || this.fade) {
            this.fadeStart = Util.getMeasuringTimeMs();
            this.currentFade = Util.getMeasuringTimeMs() - fadeStart;
            this.lastScreen = lastScreen;
            if (this.lastScreen instanceof AnimationListener && this.lastScreen != (AnimationListener) this)
                ((AnimationListener) this.lastScreen).slightguimodifications_reset();
        }
    }
    
    @Override
    public void slightguimodifications_reset() {
        this.lastScreen = null;
        this.slide = false;
        this.fade = false;
        this.fadeStart = -1;
    }
    
    @Override
    public float slightguimodifications_getAlpha() {
        if (renderingState == 1 || renderingState == 3) return 1;
        return renderingState == 2 && fadeStart >= 0 && fade ? Math.min(currentFade / SlightGuiModifications.getSpeed(), 1f) : -1;
    }
    
    @Override
    public float slightguimodifications_getEasedYOffset() {
        if (renderingState == 1) return 1;
        return renderingState == 2 || renderingState == 3 ? slightguimodifications_getEasedMouseY() : -1;
    }
    
    @Override
    public float slightguimodifications_getEasedMouseY() {
        return fadeStart >= 0 && slide ? SlightGuiModifications.ease(Math.min(currentFade / SlightGuiModifications.getSpeed(), 1f)) : -1;
    }
    
    @Override
    public void slightguimodifications_startRendering() {
//        if (fadeStart >= 0 && Util.getMeasuringTimeMs() - fadeStart < GuiAnimations.SPEED * 1.09f) {
//            this.renderingState = 1;
//            if (lastScreen != null) {
//                RenderSystem.pushMatrix();
//                RenderSystem.translatef(0, 0, -500f);
//                lastScreen.render(-1, -1, 0);
//                RenderSystem.popMatrix();
//                RenderSystem.pushMatrix();
//                RenderSystem.translatef(0, 0, -10f);
//                float v = Math.min((Util.getMeasuringTimeMs() - fadeStart) / 200f, 1f);
//                this.fillGradient(0, 0, lastScreen.width, lastScreen.height, 1052688 | MathHelper.ceil(v * 192) << 24, 1052688 | MathHelper.ceil(v * 208) << 24);
//                RenderSystem.popMatrix();
//            }
//        }
        this.renderingState = 2;
        if (fadeStart != -1) {
            this.currentFade = Util.getMeasuringTimeMs() - fadeStart;
        }
    }
    
    @Override
    public void slightguimodifications_stopRendering() {
        this.renderingState = 0;
        if (runnable != null) {
            runnable.run();
            runnable = null;
        }
    }
    
    @Override
    public int slightguimodifications_getAnimationState() {
        return renderingState;
    }
    
    @Override
    public void slightguimodifications_setAnimationState(int stage) {
        this.renderingState = stage;
    }
    
    @Override
    public void slightguimodifications_setCurrentFade(long currentFade) {
        this.currentFade = currentFade;
    }
    
    @Redirect(method = "renderBackground(I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(IIIIII)V"))
    private void fillGradient(Screen screen, int top, int left, int right, int bottom, int color1, int color2) {
        if (this.renderingState == 2) {
            float alpha = slightguimodifications_getEasedYOffset();
            this.renderingState = 0;
            if (alpha >= 0) {
                SlightGuiModifications.backgroundTint = Math.min(SlightGuiModifications.backgroundTint + minecraft.getLastFrameDuration() * 8, SlightGuiModifications.getSpeed() / 20f);
                float f = Math.min(SlightGuiModifications.backgroundTint / SlightGuiModifications.getSpeed() * 20f, 1f);
                fillGradient(top, SlightGuiModifications.reverseYAnimation(left), right, SlightGuiModifications.reverseYAnimation(bottom),
                        color1 & 16777215 | MathHelper.ceil(f * (float) (color1 >> 24 & 255)) << 24,
                        color2 & 16777215 | MathHelper.ceil(f * (float) (color2 >> 24 & 255)) << 24);
            } else fillGradient(top, left, right, bottom, color1, color2);
            this.renderingState = 2;
        } else fillGradient(top, left, right, bottom, color1, color2);
    }
    
    
    @Inject(method = "renderDirtBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(ILnet/minecraft/client/render/VertexFormat;)V", ordinal = 0))
    private void preRenderDirtBackground(int alpha, CallbackInfo ci) {
        if (this.renderingState == 2) {
            this.renderingState = 0;
        }
    }
    
    @Inject(method = "renderDirtBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;draw()V", ordinal = 0))
    private void postRenderDirtBackground(int alpha, CallbackInfo ci) {
        this.renderingState = 2;
    }
    
    @Override
    public void removeMenu() {
        if (this.menuWidget != null) {
            MenuWidget tmpWidget = menuWidget;
            runnable = () -> this.children.remove(tmpWidget);
            this.menuWidget = null;
        }
    }
    
    @Override
    public void applyMenu(MenuWidget menuWidget) {
        runnable = () -> this.children.add(this.menuWidget = menuWidget);
    }
    
    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
    private void init(MinecraftClient client, int width, int height, CallbackInfo ci) {
        this.menuWidget = null;
    }
    
    @Override
    public MenuWidget getMenu() {
        return menuWidget;
    }
}
