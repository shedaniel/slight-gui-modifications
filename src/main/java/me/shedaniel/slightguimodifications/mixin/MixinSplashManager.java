package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.Cts;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SplashManager.class)
public class MixinSplashManager {
    @Mutable
    @Shadow
    @Final
    private List<String> splashes;
    
    @Unique
    private List<String> vanillaSplashTexts = Lists.newArrayList();
    
    @Unique
    private void prepareForModded(boolean enabled, Cts.SplashText.CustomSplashesApplyMode applyMode, List<String> customSplashes) {
        if (!enabled) {
            this.splashes = Lists.newArrayList(this.vanillaSplashTexts);
        } else {
            if (applyMode == Cts.SplashText.CustomSplashesApplyMode.APPEND) {
                this.splashes = Lists.newArrayList(this.vanillaSplashTexts);
                this.splashes.addAll(customSplashes);
            } else {
                this.splashes = Lists.newArrayList(customSplashes);
            }
        }
    }
    
    @Inject(method = "apply", at = @At("RETURN"))
    private void apply(List<String> list, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        this.vanillaSplashTexts = Lists.newArrayList(this.splashes);
    }
    
    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void preGet(CallbackInfoReturnable<String> cir) {
        Cts config = SlightGuiModifications.getCtsConfig();
        if (config.enabled && config.splashText.enabled) {
            if (config.splashText.removeSplashes) {
                cir.setReturnValue("");
                return;
            }
        }
        prepareForModded(config.enabled && config.splashText.enabled && config.splashText.customSplashesEnabled, config.splashText.customSplashesApplyMode, config.splashText.customSplashes);
    }
}
