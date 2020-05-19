package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class MixinSplashTextResourceSupplier {
    @Mutable
    @Shadow
    @Final
    private List<String> splashTexts;
    
    @Unique
    private List<String> vanillaSplashTexts = Lists.newArrayList();
    
    @Unique
    private void prepareForModded(boolean enabled, SlightGuiModificationsConfig.Cts.SplashText.CustomSplashesApplyMode applyMode, List<String> customSplashes) {
        if (!enabled) {
            this.splashTexts = Lists.newArrayList(this.vanillaSplashTexts);
        } else {
            if (applyMode == SlightGuiModificationsConfig.Cts.SplashText.CustomSplashesApplyMode.APPEND) {
                this.splashTexts = Lists.newArrayList(this.vanillaSplashTexts);
                this.splashTexts.addAll(customSplashes);
            } else {
                this.splashTexts = Lists.newArrayList(customSplashes);
            }
        }
    }
    
    @Inject(method = "apply", at = @At("RETURN"))
    private void apply(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        this.vanillaSplashTexts = Lists.newArrayList(this.splashTexts);
    }
    
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void preGet(CallbackInfoReturnable<String> cir) {
        SlightGuiModificationsConfig.Cts config = SlightGuiModifications.getCtsConfig();
        if (config.enabled && config.splashText.enabled) {
            if (config.splashText.removeSplashes) {
                cir.setReturnValue("");
                return;
            }
        }
        prepareForModded(config.enabled && config.splashText.enabled && config.splashText.customSplashesEnabled, config.splashText.customSplashesApplyMode, config.splashText.customSplashes);
    }
}
