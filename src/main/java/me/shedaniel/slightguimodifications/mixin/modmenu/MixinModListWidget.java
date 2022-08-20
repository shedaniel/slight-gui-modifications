package me.shedaniel.slightguimodifications.mixin.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.terraformersmc.modmenu.gui.widget.ModListWidget;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("rawtypes")
@Mixin(ModListWidget.class)
public abstract class MixinModListWidget extends AbstractSelectionList {
    public MixinModListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
    }
    
    @Inject(method = "renderList",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"))
    private void preSelectionBufferDraw(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
//        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        float alpha = ((AnimationListener) minecraft.screen).slightguimodifications_getAlpha();
        if (alpha >= 0) {
            SlightGuiModifications.setAlpha(alpha);
        }
    }
    
    @Inject(method = "renderList",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/Tesselator;end()V", shift = At.Shift.AFTER))
    private void postSelectionBufferDraw(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
//        RenderSystem.popMatrix();
        SlightGuiModifications.restoreAlpha();
    }
}
