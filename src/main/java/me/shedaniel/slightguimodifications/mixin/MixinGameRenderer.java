package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.math.Point;
import me.shedaniel.math.impl.PointHelper;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow @Final private MinecraftClient client;
    @Unique private long startFps = -1;
    @Unique private long endFps = 0;
    
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V", ordinal = 0))
    private void preRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Screen screen = client.currentScreen;
        if (screen instanceof AnimationListener)
            ((AnimationListener) screen).slightguimodifications_startRendering();
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V", ordinal = 0, shift = At.Shift.BY, by = 2))
    private void postRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Screen screen = client.currentScreen;
        if (screen instanceof AnimationListener)
            ((AnimationListener) screen).slightguimodifications_stopRendering();
    }
    
    @Inject(method = "render", at = @At("RETURN"))
    private void postRenderEverything(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (!(client.overlay instanceof SplashScreen)) {
            long ms = Util.getMeasuringTimeMs();
            if (SlightGuiModifications.getConfig().debugInformation.showFps) {
                endFps = -1;
                if (startFps == -1) startFps = ms;
                RenderSystem.pushMatrix();
                RenderSystem.translated(0, -(client.textRenderer.fontHeight + 2) * (1 - EasingMethod.EasingMethodImpl.EXPO.apply(Math.min(1, (ms - startFps) / 500.0))), 0);
                String s = I18n.translate("text.slightguimodifications.debugFps", MinecraftClient.currentFps);
                DrawableHelper.fill(0, 0, client.textRenderer.getStringWidth(s) + 2, client.textRenderer.fontHeight + 2, -16777216);
                client.textRenderer.draw(s, 1, 2, -1);
                RenderSystem.popMatrix();
            } else {
                startFps = -1;
                if (endFps == -1) endFps = ms;
                if (ms - endFps <= 600) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translated(0, -(client.textRenderer.fontHeight + 2) * EasingMethod.EasingMethodImpl.QUART.apply(Math.min(1, (ms - endFps) / 500.0)), 0);
                    String s = I18n.translate("text.slightguimodifications.debugFps", MinecraftClient.currentFps);
                    DrawableHelper.fill(0, 0, client.textRenderer.getStringWidth(s) + 2, client.textRenderer.fontHeight + 2, -16777216);
                    client.textRenderer.draw(s, 1, 2, -1);
                    RenderSystem.popMatrix();
                }
            }
        } else {
            startFps = -1;
            endFps = 0;
        }
        if (client.currentScreen != null) {
            if (((MenuWidgetListener) client.currentScreen).getMenu() != null) {
                RenderSystem.pushMatrix();
                RenderSystem.translatef(0, 0, 700f);
                Point point = PointHelper.ofMouse();
                ((MenuWidgetListener) client.currentScreen).getMenu().render(point.x, point.y, client.getTickDelta());
                RenderSystem.popMatrix();
            }
        }
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    private void preRenderAll(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        SlightGuiModifications.backgroundTint = Math.max(SlightGuiModifications.backgroundTint - client.getLastFrameDuration() * 4, 0);
    }
    
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V", ordinal = 0),
               index = 1)
    private int transformScreenRenderMouseY(int mouseY) {
        return SlightGuiModifications.applyMouseYAnimation(mouseY);
    }
}
