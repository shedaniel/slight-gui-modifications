package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ContainerScreen.class)
public class MixinContainerScreen {
    @ModifyArg(method = "render", index = 4,
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;fillGradient(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
    private int renderSlotHighlight1(int color) {
        return SlightGuiModifications.getGuiConfig().slotHighlight.enabled ? SlightGuiModifications.getGuiConfig().slotHighlight.color : color;
    }
    
    @ModifyArg(method = "render", index = 5,
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;fillGradient(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
    private int renderSlotHighlight2(int color) {
        return SlightGuiModifications.getGuiConfig().slotHighlight.enabled ? SlightGuiModifications.getGuiConfig().slotHighlight.color : color;
    }
}
