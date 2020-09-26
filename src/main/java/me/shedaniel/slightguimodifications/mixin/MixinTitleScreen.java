package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.cts.elements.WidgetElement;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
public class MixinTitleScreen extends Screen {
    @Shadow @Final public boolean fading;
    
    @Shadow private long fadeInStart;
    
    @Unique
    private PoseStack lastMatrices;
    
    protected MixinTitleScreen(Component title) {
        super(title);
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.lastMatrices = matrices;
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V"))
    private void thing(PanoramaRenderer rotatingCubeMapRenderer, float delta, float alpha) {
        SlightGuiModificationsConfig.Cts cts = SlightGuiModifications.getCtsConfig();
        if (!cts.enabled) {
            rotatingCubeMapRenderer.render(delta, alpha);
        } else {
            fill(lastMatrices, 0, 0, this.width, this.height, 0xFF000000);
            int tmp = ((AnimationListener) this).slightguimodifications_getAnimationState();
            ((AnimationListener) this).slightguimodifications_setAnimationState(0);
            List<SlightGuiModificationsConfig.Cts.BackgroundInfo> list = Lists.newArrayList(cts.backgroundInfos);
            list.sort(Comparator.comparingDouble(SlightGuiModificationsConfig.Cts.BackgroundInfo::getAlpha));
            Collections.reverse(list);
            for (SlightGuiModificationsConfig.Cts.BackgroundInfo info : list) {
                if (info.getAlpha() > 0)
                    info.render(lastMatrices, (TitleScreen) (Object) this, delta, alpha);
            }
            ((AnimationListener) this).slightguimodifications_setAnimationState(tmp);
        }
        lastMatrices = null;
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE",
                                          target = "Lnet/minecraft/client/gui/screens/TitleScreen;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIFFIIII)V"))
    private void thing(PoseStack matrices, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texWidth, int texHeight) {
        if (!SlightGuiModifications.getCtsConfig().enabled || SlightGuiModifications.getCtsConfig().renderGradientShade) {
            int tmp = ((AnimationListener) this).slightguimodifications_getAnimationState();
            ((AnimationListener) this).slightguimodifications_setAnimationState(0);
            GuiComponent.blit(matrices, x, y, width, height, u, v, uWidth, vHeight, texWidth, texHeight);
            ((AnimationListener) this).slightguimodifications_setAnimationState(tmp);
        }
    }
    
    @Inject(method = "init",
            at = @At(value = "TAIL"))
    private void postInit(CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled && SlightGuiModifications.getCtsConfig().clearAllButtons) {
            List<AbstractWidget> buttons = Lists.newArrayList(this.buttons);
            this.buttons.clear();
            this.children.removeAll(buttons);
        }
        if (SlightGuiModifications.getCtsConfig().enabled) {
            for (WidgetElement element : SlightGuiModifications.getCtsConfig().widgetElements) {
                AbstractWidget widget = element.build(this);
                this.buttons.add(widget);
                this.children.add(widget);
            }
        }
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;color4f(FFFF)V", ordinal = 1, shift = At.Shift.AFTER))
    private void preLogoRender(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled && SlightGuiModifications.getCtsConfig().removeMinecraftEditionTexture)
            RenderSystem.color4f(1, 1, 1, 0);
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bind(Lnet/minecraft/resources/ResourceLocation;)V", ordinal = 2))
    private void preEditionRender(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().removeMinecraftLogoTexture)
                RenderSystem.color4f(1, 1, 1, 0);
            else
                RenderSystem.color4f(1, 1, 1, this.fading ? (float) Mth.ceil(Mth.clamp((float) (Util.getMillis() - this.fadeInStart) / 1000.0F, 0.0F, 1.0F)) : 1.0F);
    }
    
    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/gui/screens/TitleScreen;drawString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
    private void stringRender(PoseStack matrices, Font textRenderer, String text, int x, int y, int color) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().clearAllLabels)
                return;
        GuiComponent.drawString(matrices, textRenderer, text, x, y, color);
    }
    
    @Inject(method = "realmsNotificationsEnabled", at = @At("HEAD"), cancellable = true)
    private void areRealmsNotificationsEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().clearAllButtons)
                cir.setReturnValue(false);
    }
}
