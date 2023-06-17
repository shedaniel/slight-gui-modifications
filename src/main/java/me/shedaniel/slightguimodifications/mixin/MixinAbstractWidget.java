package me.shedaniel.slightguimodifications.mixin;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractButton.class)
public class MixinAbstractWidget {
    @Unique
    private static final ResourceLocation US_WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets_original.png");
    
    @ModifyArg(method = "renderWidget",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIII)V"))
    private ResourceLocation modifyId(ResourceLocation id) {
        if ((Object) this instanceof MerchantScreen.TradeOfferButton && id == AbstractWidget.WIDGETS_LOCATION) {
            return US_WIDGETS_LOCATION;
        }
        return id;
    }
}
