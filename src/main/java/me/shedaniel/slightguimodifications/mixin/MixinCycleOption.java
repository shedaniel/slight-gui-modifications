package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.scale.GuiScaleSliderButton;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

@Mixin(CycleOption.class)
public abstract class MixinCycleOption<T> extends Option {
    @Shadow @Final private Supplier<CycleButton.Builder<T>> buttonSetup;
    
    public MixinCycleOption(String string) {
        super(string);
    }
    
    @Inject(method = "createButton", at = @At("HEAD"), cancellable = true)
    private void createButton(Options options, int x, int y, int width, CallbackInfoReturnable<AbstractWidget> cir) {
        Component caption = getCaption();
        CycleButton.Builder<T> builder = buttonSetup.get();
        CycleButtonBuilderAccessor accessor = (CycleButtonBuilderAccessor) builder;
        CycleOption<T> option = (CycleOption<T>) (Object) this;
        if (SlightGuiModifications.getGuiConfig().customScaling.vanillaScaleSlider && option == Option.GUI_SCALE) {
            IntSupplier maxScale = () -> Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode());
            cir.setReturnValue(new GuiScaleSliderButton((CycleOption<Integer>) option, options, x, y, width, 20,
                    caption, accessor.getValueStringifier(), maxScale, options.guiScale / (double) maxScale.getAsInt()));
        }
    }
}
