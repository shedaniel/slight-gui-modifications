package me.shedaniel.slightguimodifications.mixin.cloth;

import me.shedaniel.clothconfig2.impl.ScissorsHandlerImpl;
import me.shedaniel.math.Rectangle;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScissorsHandlerImpl.class)
public class MixinScissorsHandlerImpl {
    @Inject(method = "_applyScissor", at = @At(value = "HEAD"), remap = false)
    private void applyScissorsOffset(Rectangle rectangle, CallbackInfo ci) {
        if (rectangle != null)
            rectangle.translate(0, SlightGuiModifications.applyYAnimation(0));
    }
}
