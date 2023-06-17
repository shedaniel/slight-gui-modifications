package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import dev.architectury.hooks.client.screen.ScreenHooks;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.Cts;
import me.shedaniel.slightguimodifications.gui.cts.elements.WidgetElement;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
    @Shadow @Final private boolean fading;
    
    @Shadow private long fadeInStart;
    
    @Shadow
    public abstract boolean shouldCloseOnEsc();
    
    @Unique
    private GuiGraphics lastMatrices;
    
    protected MixinTitleScreen(Component title) {
        super(title);
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.lastMatrices = graphics;
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V"))
    private void thing(PanoramaRenderer rotatingCubeMapRenderer, float delta, float alpha) {
        Cts cts = SlightGuiModifications.getCtsConfig();
        if (!cts.enabled) {
            rotatingCubeMapRenderer.render(delta, alpha);
        } else {
            lastMatrices.fill(0, 0, this.width, this.height, 0xFF000000);
            int tmp = ((AnimationListener) this).slightguimodifications_getAnimationState();
            ((AnimationListener) this).slightguimodifications_setAnimationState(0);
            List<Cts.BackgroundInfo> list = Lists.newArrayList(cts.backgroundInfos);
            list.sort(Comparator.comparingDouble(Cts.BackgroundInfo::getAlpha));
            Collections.reverse(list);
            for (Cts.BackgroundInfo info : list) {
                if (info.getAlpha() > 0)
                    info.render(lastMatrices, (TitleScreen) (Object) this, delta, alpha);
            }
            ((AnimationListener) this).slightguimodifications_setAnimationState(tmp);
        }
        lastMatrices = null;
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE",
                                          target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIFFIIII)V"))
    private void thing(GuiGraphics graphics, ResourceLocation los, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texWidth, int texHeight) {
        if (!SlightGuiModifications.getCtsConfig().enabled || SlightGuiModifications.getCtsConfig().renderGradientShade) {
            int tmp = ((AnimationListener) this).slightguimodifications_getAnimationState();
            ((AnimationListener) this).slightguimodifications_setAnimationState(0);
            graphics.blit(los, x, y, width, height, u, v, uWidth, vHeight, texWidth, texHeight);
            ((AnimationListener) this).slightguimodifications_setAnimationState(tmp);
        }
    }
    
    @Inject(method = "init",
            at = @At(value = "TAIL"))
    private void postInit(CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled && SlightGuiModifications.getCtsConfig().clearAllButtons) {
            List<GuiEventListener> buttons = Lists.newArrayList(this.children());
            buttons.removeIf(listener -> !(listener instanceof AbstractWidget));
            this.children().removeAll(buttons);
            ScreenHooks.getRenderables(this).removeAll(buttons);
            ScreenHooks.getNarratables(this).removeAll(buttons);
        }
        if (SlightGuiModifications.getCtsConfig().enabled) {
            for (WidgetElement element : SlightGuiModifications.getCtsConfig().widgetElements) {
                AbstractWidget widget = element.build(this);
                ScreenHooks.addRenderableWidget(this, widget);
            }
        }
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    private void generateRefmap(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    }
    
    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"))
    private int stringRender(GuiGraphics graphics, Font textRenderer, String text, int x, int y, int color) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().clearAllLabels)
                return 0;
        return graphics.drawString(textRenderer, text, x, y, color);
    }
    
    @Inject(method = "realmsNotificationsEnabled", at = @At("HEAD"), cancellable = true)
    private void areRealmsNotificationsEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().clearAllButtons)
                cir.setReturnValue(false);
    }
}
