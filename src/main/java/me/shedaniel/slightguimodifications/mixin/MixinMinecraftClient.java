package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    public Screen currentScreen;
    @Shadow @Final private Window window;
    
    @Shadow
    public abstract boolean forcesUnicodeFont();
    
    @Unique
    private Screen lastScreen;
    
    @Inject(method = "openScreen", at = @At("HEAD"))
    private void preOpenScreen(Screen screen, CallbackInfo ci) {
        this.lastScreen = this.currentScreen;
    }
    
    @Inject(method = "openScreen", at = @At("RETURN"))
    private void openScreen(Screen screen, CallbackInfo ci) {
        if (this.currentScreen != null)
            ((AnimationListener) this.currentScreen).slightguimodifications_openScreen(this.lastScreen);
        this.lastScreen = null;
    }
    
    @Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
    private void getFramerateLimit(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() == 60 && SlightGuiModifications.getConfig().unlimitTitleScreenFps) {
            cir.setReturnValue(window.getFramerateLimit());
        }
    }
    
    @ModifyArg(method = "onResolutionChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setScaleFactor(D)V", ordinal = 0),
               index = 0)
    private double getGuiScale(double scale) {
        if (SlightGuiModifications.getConfig().customScaling.scale <= 1) return scale;
        double configGuiScale = SlightGuiModifications.getConfig().customScaling.scale;
        return Math.min(configGuiScale, scale + 2);
    }
}
