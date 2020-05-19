package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.toast.Toast;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.toast.ToastManager$Entry")
public class MixinToastManager {
    @Shadow
    private long field_2243;
    
    @Shadow
    private Toast.Visibility visibility;
    
    @SuppressWarnings({"UnresolvedMixinReference", "InvalidMemberReference"})
    @Inject(method = {"getDissapearProgress", "method_2003"}, cancellable = true, at = @At("HEAD"), remap = false)
    private void getDissapearProgress(long time, CallbackInfoReturnable<Float> cir) {
        if (!SlightGuiModifications.getGuiConfig().fluidAdvancements)
            return;
        float f = SlightGuiModifications.ease(MathHelper.clamp((float) (time - this.field_2243) / 600.0F, 0.0F, 1.0F));
        cir.setReturnValue(this.visibility == Toast.Visibility.HIDE ? 1.0F - f : f);
    }
}
