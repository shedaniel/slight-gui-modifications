package me.shedaniel.slightguimodifications.mixin.cloth;

import me.shedaniel.clothconfig2.impl.ScissorsHandlerImpl;
import me.shedaniel.math.Rectangle;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ScissorsHandlerImpl.class)
public class MixinScissorsHandlerImpl {
    @Inject(method = "applyScissors",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getInstance()Lnet/minecraft/client/MinecraftClient;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void applyScissorsOffset(CallbackInfo ci, Rectangle rectangle) {rectangle.translate(0, SlightGuiModifications.applyYAnimation(0));}
}
