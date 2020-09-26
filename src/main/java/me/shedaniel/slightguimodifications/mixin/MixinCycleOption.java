package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.scale.GuiScaleSliderButton;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntSupplier;

@Mixin(CycleOption.class)
public class MixinCycleOption {
    @Inject(method = "createButton", at = @At("HEAD"), cancellable = true)
    private void createButton(Options options, int x, int y, int width, CallbackInfoReturnable<AbstractWidget> cir) {
        CycleOption option = (CycleOption) (Object) this;
        if (SlightGuiModifications.getGuiConfig().customScaling.vanillaScaleSlider && option == Option.GUI_SCALE) {
            IntSupplier maxScale = () -> Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode());
            cir.setReturnValue(new GuiScaleSliderButton(option, options, x, y, width, 20, option.getMessage(options), maxScale, options.guiScale / (double) maxScale.getAsInt()));
        }
    }
}
