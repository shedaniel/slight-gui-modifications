package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen {
    @ModifyArg(method = "render", index = 5,
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;fillGradient(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V",
                        ordinal = 0))
    private int renderSlotHighlight1(int color) {
        return SlightGuiModifications.getGuiConfig().slotHighlight.enabled ? SlightGuiModifications.getGuiConfig().slotHighlight.color : color;
    }
    
    @ModifyArg(method = "render", index = 6,
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;fillGradient(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V",
                        ordinal = 0))
    private int renderSlotHighlight2(int color) {
        return SlightGuiModifications.getGuiConfig().slotHighlight.enabled ? SlightGuiModifications.getGuiConfig().slotHighlight.color : color;
    }
}
