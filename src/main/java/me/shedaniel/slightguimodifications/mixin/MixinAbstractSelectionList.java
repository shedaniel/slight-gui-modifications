package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public class MixinAbstractSelectionList {
    // TODO Readd this
//    @Inject(method = "renderHoleBackground",
//            at = @At(value = "HEAD"))
//    private void preRenderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom, CallbackInfo ci) {
//        RenderSystem.pushMatrix();
//        RenderSystem.enableBlend();
//        RenderSystem.disableAlphaTest();
//        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);
//    }
//    
//    @Inject(method = "renderHoleBackground",
//            at = @At(value = "RETURN"))
//    private void postRenderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom, CallbackInfo ci) {
//        RenderSystem.popMatrix();
//    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(ILcom/mojang/blaze3d/vertex/VertexFormat;)V"))
    private void preBufferDraw(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/Tesselator;end()V", shift = At.Shift.AFTER))
    private void postBufferDraw(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.popMatrix();
    }
}
