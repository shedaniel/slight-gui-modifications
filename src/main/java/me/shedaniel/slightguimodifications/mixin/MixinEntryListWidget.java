package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntryListWidget.class)
public class MixinEntryListWidget {
    @Inject(method = "renderHoleBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(ILnet/minecraft/client/render/VertexFormat;)V"))
    private void renderHoleBackgroundChangeAlpha(int top, int bottom, int alphaTop, int alphaBottom, CallbackInfo ci) {
        if (this instanceof AnimationListener) {
            float animatedAlpha = ((AnimationListener) this).slightguimodifications_getAlpha();
            if (animatedAlpha >= 0) {
                RenderSystem.enableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
//                SlightGuiModifications.setAlpha(animatedAlpha);
            }
        }
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(ILnet/minecraft/client/render/VertexFormat;)V", ordinal = 0))
    private void renderBackgroundChangeAlpha(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this instanceof AnimationListener) {
            float animatedAlpha = ((AnimationListener) this).slightguimodifications_getAlpha();
            if (animatedAlpha >= 0) {
                RenderSystem.enableBlend();
                RenderSystem.disableAlphaTest();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
//                SlightGuiModifications.setAlpha(animatedAlpha);
            }
        }
    }
}
