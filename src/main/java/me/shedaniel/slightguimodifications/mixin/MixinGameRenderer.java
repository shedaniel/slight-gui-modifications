package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.math.Point;
import me.shedaniel.math.impl.PointHelper;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
    @Shadow @Final private Minecraft minecraft;
    @Unique private long startFps = -1;
    @Unique private long endFps = 0;
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V",
                     ordinal = 0))
    private void preRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Screen screen = minecraft.screen;
        if (screen instanceof AnimationListener)
            ((AnimationListener) screen).slightguimodifications_startRendering();
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V", ordinal = 0,
                     shift = At.Shift.AFTER))
    private void postRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Screen screen = minecraft.screen;
        if (screen instanceof AnimationListener)
            ((AnimationListener) screen).slightguimodifications_stopRendering();
    }
    
    @Inject(method = "render", at = @At("RETURN"))
    private void postRenderEverything(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        PoseStack matrices = new PoseStack();
        ResourceLocation lastPrettyScreenshotTextureId = SlightGuiModifications.lastPrettyScreenshotTextureId;
        if (lastPrettyScreenshotTextureId != null) {
            if (!minecraft.options.hideGui && !SlightGuiModifications.prettyScreenshots) {
                matrices.pushPose();
                matrices.translate(10, 10, 500);
                DynamicTexture lastPrettyScreenshotTexture = SlightGuiModifications.lastPrettyScreenshotTexture;
                RenderSystem.setShaderTexture(0, lastPrettyScreenshotTextureId);
                int width = (int) (minecraft.getWindow().getGuiScaledWidth() * .2);
                int height = (int) (minecraft.getWindow().getGuiScaledWidth() * .2 / lastPrettyScreenshotTexture.getPixels().getWidth() * lastPrettyScreenshotTexture.getPixels().getHeight());
                GuiComponent.innerBlit(matrices.last().pose(), 0, width, 0, height, 0, 0, 1, 0, 1);
                matrices.popPose();
            }
        }
        ResourceLocation prettyScreenshotTextureId = SlightGuiModifications.prettyScreenshotTextureId;
        if (prettyScreenshotTextureId != null) {
            DynamicTexture prettyScreenshotTexture = SlightGuiModifications.prettyScreenshotTexture;
            long prettyScreenshotTime = SlightGuiModifications.prettyScreenshotTime;
            if (prettyScreenshotTime == -1) {
                prettyScreenshotTime = SlightGuiModifications.prettyScreenshotTime = Util.getMillis();
            }
            RenderSystem.setShaderTexture(0, prettyScreenshotTextureId);
            long currentMs = Util.getMillis();
            double scaleTime = 600.0;
            double translateTime = 800.0;
            double fadeTime = 300.0;
            if (currentMs - prettyScreenshotTime > 1000 && lastPrettyScreenshotTextureId != null) {
                SlightGuiModifications.lastPrettyScreenshotTextureId = null;
                SlightGuiModifications.lastPrettyScreenshotTexture.close();
                SlightGuiModifications.lastPrettyScreenshotTexture = null;
            }
            int width = (int) Mth.lerp(EasingMethod.EasingMethodImpl.QUAD.apply(Mth.clamp((currentMs - prettyScreenshotTime) / scaleTime, 0.0, 1.0)), minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledWidth() * .2);
            int height = (int) Mth.lerp(EasingMethod.EasingMethodImpl.QUAD.apply(Mth.clamp((currentMs - prettyScreenshotTime) / scaleTime, 0.0, 1.0)), minecraft.getWindow().getGuiScaledHeight(), minecraft.getWindow().getGuiScaledWidth() * .2 / prettyScreenshotTexture.getPixels().getWidth() * prettyScreenshotTexture.getPixels().getHeight());
            double x = Mth.lerp(EasingMethod.EasingMethodImpl.QUAD.apply(Mth.clamp((currentMs - prettyScreenshotTime) / translateTime, 0.0, 1.0)), minecraft.getWindow().getGuiScaledWidth() / 2 - width / 2, 10);
            double y = Mth.lerp(SlightGuiModifications.bezierEase(EasingMethod.EasingMethodImpl.QUAD.apply(Mth.clamp((currentMs - prettyScreenshotTime) / translateTime, 0.0, 1.0)), new double[]{0, .49, .78, 1}), minecraft.getWindow().getGuiScaledHeight() / 2 - height / 2, 10);
            x -= (x + width + x) * EasingMethod.EasingMethodImpl.SINE.apply(Mth.clamp((currentMs - prettyScreenshotTime - 3000) / 500.0, 0, 1));
            if (x + width < 0) {
                SlightGuiModifications.prettyScreenshotTexture.close();
                SlightGuiModifications.prettyScreenshotTexture = null;
                SlightGuiModifications.prettyScreenshotTextureId = null;
                SlightGuiModifications.prettyScreenshotTime = -1;
            } else if (!minecraft.options.hideGui && !SlightGuiModifications.prettyScreenshots) {
                matrices.pushPose();
                matrices.translate(x, y, 500);
                RenderSystem.enableBlend();
//                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
//                RenderSystem.shadeModel(GL11.GL_SMOOTH);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                GuiComponent.innerBlit(matrices.last().pose(), 0, width, 0, height, 0, 0, 1, 0, 1);
                float a = (1 - (float) Mth.clamp((currentMs - prettyScreenshotTime) / fadeTime, 0.0, 1.0));
                GuiComponent.fill(matrices, 0, 0, width, height, 0xFFFFFF | (int) (a * 255.0F) << 24);
                matrices.popPose();
            }
        }
        if (!minecraft.options.hideGui) {
            if (!(minecraft.getOverlay() instanceof LoadingOverlay)) {
                long ms = Util.getMillis();
                if (SlightGuiModifications.getGuiConfig().debugInformation.showFps) {
                    endFps = -1;
                    if (startFps == -1) startFps = ms;
                    matrices.pushPose();
                    matrices.translate(0, -(minecraft.font.lineHeight + 2) * (1 - EasingMethod.EasingMethodImpl.EXPO.apply(Math.min(1, (ms - startFps) / 500.0))), 0);
                    String s = I18n.get("text.slightguimodifications.debugFps", Minecraft.fps);
                    GuiComponent.fill(matrices, 0, 0, minecraft.font.width(s) + 2, minecraft.font.lineHeight + 2, -16777216);
                    minecraft.font.draw(matrices, s, 1, 2, -1);
                    matrices.popPose();
                } else {
                    startFps = -1;
                    if (endFps == -1) endFps = ms;
                    if (ms - endFps <= 600) {
                        matrices.pushPose();
                        matrices.translate(0, -(minecraft.font.lineHeight + 2) * EasingMethod.EasingMethodImpl.QUART.apply(Math.min(1, (ms - endFps) / 500.0)), 0);
                        String s = I18n.get("text.slightguimodifications.debugFps", Minecraft.fps);
                        GuiComponent.fill(matrices, 0, 0, minecraft.font.width(s) + 2, minecraft.font.lineHeight + 2, -16777216);
                        minecraft.font.draw(matrices, s, 1, 2, -1);
                        matrices.popPose();
                    }
                }
            } else {
                startFps = -1;
                endFps = 0;
            }
        }
        if (minecraft.screen != null) {
            if (((MenuWidgetListener) minecraft.screen).getMenu() != null) {
                matrices.pushPose();
                matrices.translate(0, 0, 700f);
                Point point = PointHelper.ofMouse();
                ((MenuWidgetListener) minecraft.screen).getMenu().render(matrices, point.x, point.y, minecraft.getFrameTime());
                matrices.popPose();
            }
        }
    }
    
    @Inject(method = "render", at = @At("HEAD"))
    private void preRenderAll(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        SlightGuiModifications.backgroundTint = Math.max(SlightGuiModifications.backgroundTint - minecraft.getDeltaFrameTime() * 4, 0);
    }
    
    @ModifyArg(method = "render",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V",
                        ordinal = 0),
               index = 1)
    private int transformScreenRenderMouseY(int mouseY) {
        return SlightGuiModifications.applyMouseYAnimation(mouseY);
    }
}
