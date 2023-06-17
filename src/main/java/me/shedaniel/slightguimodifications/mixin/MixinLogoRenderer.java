package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public abstract class MixinLogoRenderer {
    @Shadow
    @Final
    private boolean keepLogoThroughFade;
    
    @Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V", at = @At("HEAD"))
    private void generateRefmap(GuiGraphics graphics, int x, float f, int y, CallbackInfo ci) {
    }
    
    @Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setColor(FFFF)V", ordinal = 0,
                    shift = At.Shift.AFTER))
    private void preLogoRender(GuiGraphics graphics, int x, float f, int y, CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled && SlightGuiModifications.getCtsConfig().removeMinecraftEditionTexture)
            RenderSystem.setShaderColor(1, 1, 1, 0);
    }
    
    @Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V",
                            ordinal = 1),
            })
    private void preEditionRender(GuiGraphics graphics, int x, float f, int y, CallbackInfo ci) {
        if (SlightGuiModifications.getCtsConfig().enabled)
            if (SlightGuiModifications.getCtsConfig().removeMinecraftLogoTexture)
                RenderSystem.setShaderColor(1, 1, 1, 0);
            else
                RenderSystem.setShaderColor(1, 1, 1, this.keepLogoThroughFade ? 1.0F : f);
    }
}
