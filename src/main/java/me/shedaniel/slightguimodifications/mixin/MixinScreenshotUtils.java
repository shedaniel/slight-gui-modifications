package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

@Mixin(ScreenshotUtils.class)
public abstract class MixinScreenshotUtils {
    @Shadow
    public static NativeImage takeScreenshot(int width, int height, Framebuffer framebuffer) {
        return null;
    }
    
    @ModifyVariable(method = "saveScreenshotInner", at = @At(value = "HEAD"), ordinal = 0)
    private static Consumer<Text> saveScreenshotInner(Consumer<Text> textConsumer) {
        return text -> {
            if (SlightGuiModifications.getGuiConfig().satisfyingScreenshots)
                return;
            textConsumer.accept(text);
        };
    }
    
    @Inject(method = "saveScreenshotInner", at = @At(value = "RETURN"))
    private static void preScreenshotConsumer(File gameDirectory, String fileName, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver, CallbackInfo ci) {
        if (SlightGuiModifications.prettyScreenshots) {
            NativeImage image = takeScreenshot(framebufferWidth, framebufferHeight, framebuffer);
            SlightGuiModifications.startPrettyScreenshot(image);
        } else {
            SlightGuiModifications.startPrettyScreenshot(null);
        }
        SlightGuiModifications.prettyScreenshots = false;
    }
}
