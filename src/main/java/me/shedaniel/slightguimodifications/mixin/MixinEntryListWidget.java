package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public class MixinEntryListWidget {
    @Shadow @Final protected MinecraftClient minecraft;
    
    @Inject(method = "renderHoleBackground",
            at = @At(value = "HEAD"))
    private void preRenderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }
    
    @Inject(method = "renderHoleBackground",
            at = @At(value = "RETURN"))
    private void postRenderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom, CallbackInfo ci) {
        RenderSystem.popMatrix();
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(ILnet/minecraft/client/render/VertexFormat;)V"))
    private void preBufferDraw(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;draw()V", shift = At.Shift.AFTER))
    private void postBufferDraw(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.popMatrix();
    }
}
