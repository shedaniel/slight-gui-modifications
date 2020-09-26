package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToastComponent.ToastInstance.class)
public class MixinToastComponent$ToastInstance {
    @Shadow
    private long animationTime;
    
    @Shadow
    private Toast.Visibility visibility;
    
    @Inject(method = "getVisibility", cancellable = true, at = @At("HEAD"))
    private void getDissapearProgress(long time, CallbackInfoReturnable<Float> cir) {
        if (!SlightGuiModifications.getGuiConfig().fluidAdvancements)
            return;
        float f = SlightGuiModifications.ease(Mth.clamp((float) (time - this.animationTime) / 600.0F, 0.0F, 1.0F));
        cir.setReturnValue(this.visibility == Toast.Visibility.HIDE ? 1.0F - f : f);
    }
}
