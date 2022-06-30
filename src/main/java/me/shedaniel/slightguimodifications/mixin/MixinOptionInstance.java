package me.shedaniel.slightguimodifications.mixin;

import com.mojang.serialization.Codec;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.ClampingLazyMaxIntRangeListener;
import me.shedaniel.slightguimodifications.listener.CoolOptionInstanceSliderButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(OptionInstance.class)
public abstract class MixinOptionInstance<T> {
    @Shadow @Final private OptionInstance.ValueSet<T> values;
    
    @Mutable @Shadow @Final Function<T, Component> toString;
    
    
    @Shadow @Final private OptionInstance.TooltipSupplierFactory<T> tooltip;
    
    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplierFactory;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/util/function/Consumer;)V",
            at = @At("RETURN"))
    private void init(String string, OptionInstance.TooltipSupplierFactory tooltipSupplierFactory, OptionInstance.CaptionBasedToString captionBasedToString, OptionInstance.ValueSet valueSet, Codec codec, Object object, Consumer consumer, CallbackInfo ci) {
        if (string.equals("options.guiScale")) {
            Function<T, Component> toString1 = this.toString;
            this.toString = (t) -> {
                return CommonComponents.optionNameValue(Component.translatable(string), toString1.apply(t));
            };
        }
    }
    
    @Inject(method = "createButton", at = @At("HEAD"), cancellable = true)
    private void createButton(Options options, int x, int y, int width, CallbackInfoReturnable<AbstractWidget> cir) {
        OptionInstance<T> optionInstance = (OptionInstance<T>) (Object) this;
        if (SlightGuiModifications.getGuiConfig().customScaling.vanillaScaleSlider && optionInstance == options.guiScale()) {
            OptionInstance.TooltipSupplier<Integer> tooltipSupplier = (OptionInstance.TooltipSupplier<Integer>) this.tooltip.apply(Minecraft.getInstance());
            OptionInstance.ClampingLazyMaxIntRange range = (OptionInstance.ClampingLazyMaxIntRange) this.values;
            range = new OptionInstance.ClampingLazyMaxIntRange(range.minInclusive(), range.maxSupplier());
            cir.setReturnValue(
                    new CoolOptionInstanceSliderButton(options, x, y, width, 20, optionInstance, range, tooltipSupplier));
        }
    }
}
