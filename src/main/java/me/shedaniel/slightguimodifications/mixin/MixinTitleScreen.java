package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.cts.elements.WidgetElement;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    @Shadow @Final public boolean doBackgroundFade;
    
    @Shadow private long backgroundFadeStart;
    
    protected MixinTitleScreen(Text title) {
        super(title);
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/RotatingCubeMapRenderer;render(FF)V"))
    private void thing(RotatingCubeMapRenderer rotatingCubeMapRenderer, float delta, float alpha) {
        SlightGuiModificationsConfig.Cts cts = SlightGuiModifications.getCtsConfig();
        if (!cts.enabled) {
            rotatingCubeMapRenderer.render(delta, alpha);
        } else {
            fill(0, 0, this.width, this.height, 0xFF000000);
            int tmp = ((AnimationListener) this).slightguimodifications_getAnimationState();
            ((AnimationListener) this).slightguimodifications_setAnimationState(0);
            List<SlightGuiModificationsConfig.Cts.BackgroundInfo> list = Lists.newArrayList(cts.backgroundInfos);
            list.sort(Comparator.comparingDouble(SlightGuiModificationsConfig.Cts.BackgroundInfo::getAlpha));
            Collections.reverse(list);
            for (SlightGuiModificationsConfig.Cts.BackgroundInfo info : list) {
                if (info.getAlpha() > 0)
                    info.render((TitleScreen) (Object) this, delta, alpha);
            }
            ((AnimationListener) this).slightguimodifications_setAnimationState(tmp);
        }
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;blit(IIIIFFIIII)V"))
    private void thing(int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texWidth, int texHeight) {
        if (!SlightGuiModifications.getCtsConfig().enabled || SlightGuiModifications.getCtsConfig().renderGradientShade) {
            int tmp = ((AnimationListener) this).slightguimodifications_getAnimationState();
            ((AnimationListener) this).slightguimodifications_setAnimationState(0);
            DrawableHelper.blit(x, y, width, height, u, v, uWidth, vHeight, texWidth, texHeight);
            ((AnimationListener) this).slightguimodifications_setAnimationState(tmp);
        }
    }
    
    @Inject(method = "init",
            at = @At(value = "TAIL"))
    private void postInit(CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled && SlightGuiModifications.getCtsConfig().clearAllButtons) {
            List<AbstractButtonWidget> buttons = Lists.newArrayList(this.buttons);
            this.buttons.clear();
            this.children.removeAll(buttons);
        }
        if (SlightGuiModifications.getCtsConfig().enabled) {
            for (WidgetElement element : SlightGuiModifications.getCtsConfig().widgetElements) {
                AbstractButtonWidget widget = element.build(this);
                this.buttons.add(widget);
                this.children.add(widget);
            }
        }
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;color4f(FFFF)V", ordinal = 1, shift = At.Shift.AFTER))
    private void preLogoRender(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled && SlightGuiModifications.getCtsConfig().removeMinecraftEditionTexture)
            RenderSystem.color4f(1, 1, 1, 0);
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V", ordinal = 2))
    private void preEditionRender(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().removeMinecraftLogoTexture)
                RenderSystem.color4f(1, 1, 1, 0);
            else
                RenderSystem.color4f(1, 1, 1, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp((float) (Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F, 0.0F, 1.0F)) : 1.0F);
    }
    
    @Inject(method = "areRealmsNotificationsEnabled", at = @At("HEAD"), cancellable = true)
    private void areRealmsNotificationsEnabled(CallbackInfoReturnable<Boolean> cir) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().clearAllButtons)
                cir.setReturnValue(false);
    }
}
