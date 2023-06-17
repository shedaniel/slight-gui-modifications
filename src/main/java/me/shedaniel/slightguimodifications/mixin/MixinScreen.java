package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.util.Mth;
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
public abstract class MixinScreen extends AbstractContainerEventHandler implements Renderable, AnimationListener, MenuWidgetListener {
    @Shadow
    public int height;
    
    @Shadow @Final protected List<GuiEventListener> children;
    @Shadow protected Minecraft minecraft;
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
        if ((Object) this instanceof TitleScreen && ((TitleScreenHooks) this).isDoBackgroundFade() && (((TitleScreenHooks) this).getBackgroundFadeStart() == 0 || Util.getMillis() - ((TitleScreenHooks) this).getBackgroundFadeStart() <= 1000))
            return;
        SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
        if (lastScreen != null && (Object) this.getClass() == lastScreen.getClass())
            return;
        boolean affected = (Object) this instanceof AbstractContainerScreen ? config.openingAnimation.affectsInventories : config.openingAnimation.affectsGameMenus;
        this.slide = affected && config.openingAnimation.fluidOpenSlideFromBottom && (lastScreen == null || lastScreen instanceof TitleScreen || !config.openingAnimation.ignoreSlideWhenRedirected);
        this.fade = affected && config.openingAnimation.fluidOpenFade && (lastScreen == null || lastScreen instanceof TitleScreen || !config.openingAnimation.ignoreFadeWhenRedirected);
        if (this.slide || this.fade) {
            this.fadeStart = Util.getMillis();
            this.currentFade = Util.getMillis() - fadeStart;
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
            this.currentFade = Util.getMillis() - fadeStart;
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
    
    @Redirect(method = "renderBackground",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"))
    private void fillGradientRedirect(PoseStack matrices, int top, int left, int right, int bottom, int color1, int color2) {
        if (this.renderingState == 2) {
            float alpha = slightguimodifications_getEasedYOffset();
            this.renderingState = 0;
            if (alpha >= 0) {
                SlightGuiModifications.backgroundTint = Math.min(SlightGuiModifications.backgroundTint + minecraft.getDeltaFrameTime() * 8, SlightGuiModifications.getSpeed() / 20f);
                float f = Math.min(SlightGuiModifications.backgroundTint / SlightGuiModifications.getSpeed() * 20f, 1f);
                fillGradient(matrices, top, SlightGuiModifications.reverseYAnimation(left), right, SlightGuiModifications.reverseYAnimation(bottom),
                        color1 & 16777215 | Mth.ceil(f * (float) (color1 >> 24 & 255)) << 24,
                        color2 & 16777215 | Mth.ceil(f * (float) (color2 >> 24 & 255)) << 24);
            } else fillGradient(matrices, top, left, right, bottom, color1, color2);
            this.renderingState = 2;
        } else fillGradient(matrices, top, left, right, bottom, color1, color2);
    }
    
    
    @Inject(method = "renderDirtBackground",
            at = @At(value = "HEAD"))
    private void preRenderDirtBackground(PoseStack poseStack, CallbackInfo ci) {
        if (this.renderingState == 2) {
            this.renderingState = 0;
        }
    }
    
    @Inject(method = "renderDirtBackground",
            at = @At("RETURN"))
    private void postRenderDirtBackground(PoseStack poseStack, CallbackInfo ci) {
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
    
    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"))
    private void init(Minecraft client, int width, int height, CallbackInfo ci) {
        this.menuWidget = null;
    }
    
    @Override
    public MenuWidget getMenu() {
        return menuWidget;
    }
}
