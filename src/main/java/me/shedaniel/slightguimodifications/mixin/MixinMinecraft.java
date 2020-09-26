package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.Window;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow @Nullable public Screen screen;
    @Shadow @Final private Window window;
    
    @Unique
    private Screen lastScreen;
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void preOpenScreen(Screen screen, CallbackInfo ci) {
        this.lastScreen = this.screen;
    }
    
    @Inject(method = "setScreen", at = @At("RETURN"))
    private void openScreen(Screen screen, CallbackInfo ci) {
        if (this.screen != null)
            ((AnimationListener) this.screen).slightguimodifications_openScreen(this.lastScreen);
        this.lastScreen = null;
    }
    
    @Inject(method = "getFramerateLimit", at = @At("RETURN"), cancellable = true)
    private void getFramerateLimit(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() == 60 && SlightGuiModifications.getGuiConfig().unlimitTitleScreenFps) {
            cir.setReturnValue(window.getFramerateLimit());
        }
    }
    
    @ModifyArg(method = "resizeDisplay", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;setGuiScale(D)V", ordinal = 0),
               index = 0)
    private double getGuiScale(double scale) {
        if (SlightGuiModifications.getGuiConfig().customScaling.scale <= 1) return scale;
        double configGuiScale = SlightGuiModifications.getGuiConfig().customScaling.scale;
        return Math.min(configGuiScale, scale + 2);
    }
}
