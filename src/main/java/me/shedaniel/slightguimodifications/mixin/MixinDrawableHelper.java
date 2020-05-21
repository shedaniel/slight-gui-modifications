package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawableHelper.class)
public class MixinDrawableHelper {
    @Inject(method = "drawTexturedQuad", at = @At("HEAD"))
    private static void innerBlit(Matrix4f matrix, int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getAlpha();
            if (alpha >= 0) {
                RenderSystem.enableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
                SlightGuiModifications.setAlpha(alpha);
            }
        }
    }
    
    @Inject(method = "drawTexturedQuad", at = @At("RETURN"))
    private static void postInnerBlit(Matrix4f matrix, int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd, CallbackInfo ci) {
        RenderSystem.popMatrix();
        SlightGuiModifications.restoreAlpha();
    }
}
