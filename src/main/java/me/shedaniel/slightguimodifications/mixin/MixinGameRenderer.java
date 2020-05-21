package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
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
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
                     ordinal = 0))
    private void preRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Screen screen = client.currentScreen;
        if (screen instanceof AnimationListener)
            ((AnimationListener) screen).slightguimodifications_startRendering();
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", ordinal = 0,
                     shift = At.Shift.AFTER))
    private void postRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Screen screen = client.currentScreen;
        if (screen instanceof AnimationListener)
            ((AnimationListener) screen).slightguimodifications_stopRendering();
    }
    
    @Inject(method = "render", at = @At("RETURN"))
    private void postRenderEverything(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MatrixStack matrices = new MatrixStack();
        Identifier lastPrettyScreenshotTextureId = SlightGuiModifications.lastPrettyScreenshotTextureId;
        if (lastPrettyScreenshotTextureId != null) {
            if (!client.options.hudHidden && !SlightGuiModifications.prettyScreenshots) {
                matrices.push();
                matrices.translate(10, 10, 500);
                NativeImageBackedTexture lastPrettyScreenshotTexture = SlightGuiModifications.lastPrettyScreenshotTexture;
                client.getTextureManager().bindTexture(lastPrettyScreenshotTextureId);
                int width = (int) (client.getWindow().getScaledWidth() * .2);
                int height = (int) (client.getWindow().getScaledWidth() * .2 / lastPrettyScreenshotTexture.getImage().getWidth() * lastPrettyScreenshotTexture.getImage().getHeight());
                DrawableHelper.drawTexturedQuad(matrices.peek().getModel(), 0, width, 0, height, 0, 0, 1, 0, 1);
                matrices.pop();
            }
        }
        Identifier prettyScreenshotTextureId = SlightGuiModifications.prettyScreenshotTextureId;
        if (prettyScreenshotTextureId != null) {
            NativeImageBackedTexture prettyScreenshotTexture = SlightGuiModifications.prettyScreenshotTexture;
            long prettyScreenshotTime = SlightGuiModifications.prettyScreenshotTime;
            if (prettyScreenshotTime == -1) {
                prettyScreenshotTime = SlightGuiModifications.prettyScreenshotTime = Util.getMeasuringTimeMs();
            }
            client.getTextureManager().bindTexture(prettyScreenshotTextureId);
            long currentMs = Util.getMeasuringTimeMs();
            double scaleTime = 600.0;
            double translateTime = 800.0;
            double fadeTime = 300.0;
            if (currentMs - prettyScreenshotTime > 1000 && lastPrettyScreenshotTextureId != null) {
                SlightGuiModifications.lastPrettyScreenshotTextureId = null;
                SlightGuiModifications.lastPrettyScreenshotTexture.close();
                SlightGuiModifications.lastPrettyScreenshotTexture = null;
            }
            int width = (int) MathHelper.lerp(EasingMethod.EasingMethodImpl.QUAD.apply(MathHelper.clamp((currentMs - prettyScreenshotTime) / scaleTime, 0.0, 1.0)), client.getWindow().getScaledWidth(), client.getWindow().getScaledWidth() * .2);
            int height = (int) MathHelper.lerp(EasingMethod.EasingMethodImpl.QUAD.apply(MathHelper.clamp((currentMs - prettyScreenshotTime) / scaleTime, 0.0, 1.0)), client.getWindow().getScaledHeight(), client.getWindow().getScaledWidth() * .2 / prettyScreenshotTexture.getImage().getWidth() * prettyScreenshotTexture.getImage().getHeight());
            double x = MathHelper.lerp(EasingMethod.EasingMethodImpl.QUAD.apply(MathHelper.clamp((currentMs - prettyScreenshotTime) / translateTime, 0.0, 1.0)), client.getWindow().getScaledWidth() / 2 - width / 2, 10);
            double y = MathHelper.lerp(SlightGuiModifications.bezierEase(EasingMethod.EasingMethodImpl.QUAD.apply(MathHelper.clamp((currentMs - prettyScreenshotTime) / translateTime, 0.0, 1.0)), new double[]{0, .49, .78, 1}), client.getWindow().getScaledHeight() / 2 - height / 2, 10);
//            int height = (int) ((float) width / prettyScreenshotTexture.getImage().getWidth() * prettyScreenshotTexture.getImage().getHeight());
//            int x = (int) MathHelper.lerp(SlightGuiModifications.bezierEase(MathHelper.clamp((currentMs - prettyScreenshotTime) / translateTime, 0.0, 1.0),
//                    new double[]{0, 0.290, 0.6, 1}), client.getWindow().getScaledWidth() / 2 - width / 2, 10);
//            int y = (int) MathHelper.lerp(SlightGuiModifications.bezierEase(MathHelper.clamp((currentMs - prettyScreenshotTime) / translateTime, 0.0, 1.0),
//                    new double[]{0, 0.8, 0.95, 1}), client.getWindow().getScaledHeight() / 2 - height / 2, 10);
            x -= (x + width + x) * EasingMethod.EasingMethodImpl.SINE.apply(MathHelper.clamp((currentMs - prettyScreenshotTime - 3000) / 500.0, 0, 1));
            if (x + width < 0) {
                SlightGuiModifications.prettyScreenshotTexture.close();
                SlightGuiModifications.prettyScreenshotTexture = null;
                SlightGuiModifications.prettyScreenshotTextureId = null;
                SlightGuiModifications.prettyScreenshotTime = -1;
            } else if (!client.options.hudHidden && !SlightGuiModifications.prettyScreenshots) {
                RenderSystem.pushMatrix();
                matrices.push();
                matrices.translate(x, y, 500);
                RenderSystem.enableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
                RenderSystem.color4f(1, 1, 1, 1);
                DrawableHelper.drawTexturedQuad(matrices.peek().getModel(), 0, width, 0, height, 0, 0, 1, 0, 1);
                float a = (1 - (float) MathHelper.clamp((currentMs - prettyScreenshotTime) / fadeTime, 0.0, 1.0));
                DrawableHelper.fill(matrices, 0, 0, width, height, 0xFFFFFF | (int) (a * 255.0F) << 24);
                matrices.pop();
                RenderSystem.popMatrix();
            }
        }
        if (!client.options.hudHidden) {
            if (!(client.overlay instanceof SplashScreen)) {
                long ms = Util.getMeasuringTimeMs();
                if (SlightGuiModifications.getGuiConfig().debugInformation.showFps) {
                    endFps = -1;
                    if (startFps == -1) startFps = ms;
                    matrices.push();
                    matrices.translate(0, -(client.textRenderer.fontHeight + 2) * (1 - EasingMethod.EasingMethodImpl.EXPO.apply(Math.min(1, (ms - startFps) / 500.0))), 0);
                    String s = I18n.translate("text.slightguimodifications.debugFps", MinecraftClient.currentFps);
                    DrawableHelper.fill(matrices, 0, 0, client.textRenderer.getStringWidth(s) + 2, client.textRenderer.fontHeight + 2, -16777216);
                    client.textRenderer.draw(matrices, s, 1, 2, -1);
                    matrices.pop();
                } else {
                    startFps = -1;
                    if (endFps == -1) endFps = ms;
                    if (ms - endFps <= 600) {
                        matrices.push();
                        matrices.translate(0, -(client.textRenderer.fontHeight + 2) * EasingMethod.EasingMethodImpl.QUART.apply(Math.min(1, (ms - endFps) / 500.0)), 0);
                        String s = I18n.translate("text.slightguimodifications.debugFps", MinecraftClient.currentFps);
                        DrawableHelper.fill(matrices, 0, 0, client.textRenderer.getStringWidth(s) + 2, client.textRenderer.fontHeight + 2, -16777216);
                        client.textRenderer.draw(matrices, s, 1, 2, -1);
                        matrices.pop();
                    }
                }
            } else {
                startFps = -1;
                endFps = 0;
            }
        }
        if (client.currentScreen != null) {
            if (((MenuWidgetListener) client.currentScreen).getMenu() != null) {
                matrices.push();
                matrices.translate(0, 0, 700f);
                Point point = PointHelper.ofMouse();
                ((MenuWidgetListener) client.currentScreen).getMenu().render(matrices, point.x, point.y, client.getTickDelta());
                matrices.pop();
            }
        }
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    private void preRenderAll(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        SlightGuiModifications.backgroundTint = Math.max(SlightGuiModifications.backgroundTint - client.getLastFrameDuration() * 4, 0);
    }
    
    @ModifyArg(method = "render",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
                        ordinal = 0),
               index = 1)
    private int transformScreenRenderMouseY(int mouseY) {
        return SlightGuiModifications.applyMouseYAnimation(mouseY);
    }
}
