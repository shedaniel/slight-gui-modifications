package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.util.function.Consumer;

@Mixin(Screenshot.class)
public abstract class MixinScreenshot {
    @Shadow
    public static NativeImage takeScreenshot(int width, int height, RenderTarget framebuffer) {
        return null;
    }
    
    @ModifyVariable(method = "_grab", at = @At(value = "HEAD"), ordinal = 0)
    private static Consumer<Component> saveScreenshotInner(Consumer<Component> textConsumer) {
        return text -> {
            if (SlightGuiModifications.getGuiConfig().satisfyingScreenshots)
                return;
            textConsumer.accept(text);
        };
    }
    
    @Inject(method = "_grab", at = @At(value = "RETURN"))
    private static void preScreenshotConsumer(File gameDirectory, String fileName, int framebufferWidth, int framebufferHeight, RenderTarget framebuffer, Consumer<Component> messageReceiver, CallbackInfo ci) {
        if (SlightGuiModifications.prettyScreenshots) {
            NativeImage image = takeScreenshot(framebufferWidth, framebufferHeight, framebuffer);
            SlightGuiModifications.startPrettyScreenshot(image);
        } else {
            SlightGuiModifications.startPrettyScreenshot(null);
        }
        SlightGuiModifications.prettyScreenshots = false;
    }
}
