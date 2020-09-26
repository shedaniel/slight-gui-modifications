package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiComponent.class)
public class MixinGuiComponent {
    @Inject(method = "innerBlit(Lcom/mojang/math/Matrix4f;IIIIIFFFF)V", at = @At("HEAD"))
    private static void innerBlit(Matrix4f matrix, int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getAlpha();
            if (alpha >= 0) {
                RenderSystem.enableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
                SlightGuiModifications.setAlpha(alpha);
            }
        }
    }
    
    @Inject(method = "innerBlit(Lcom/mojang/math/Matrix4f;IIIIIFFFF)V", at = @At("RETURN"))
    private static void postInnerBlit(Matrix4f matrix, int xStart, int xEnd, int yStart, int yEnd, int z, float uStart, float uEnd, float vStart, float vEnd, CallbackInfo ci) {
        RenderSystem.popMatrix();
        SlightGuiModifications.restoreAlpha();
    }
}
