package me.shedaniel.slightguimodifications.mixin.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.prospector.modmenu.gui.ModListWidget;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("rawtypes")
@Mixin(ModListWidget.class)
public class MixinModListWidget extends EntryListWidget {
    public MixinModListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
    }
    
    @Inject(method = "renderList",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(ILnet/minecraft/client/render/VertexFormat;)V"))
    private void preSelectionBufferDraw(int x, int y, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        float alpha = ((AnimationListener) minecraft.currentScreen).slightguimodifications_getAlpha();
        if (alpha >= 0) {
            SlightGuiModifications.setAlpha(alpha);
        }
    }
    
    @Inject(method = "renderList",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;draw()V", shift = At.Shift.AFTER))
    private void postSelectionBufferDraw(int x, int y, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderSystem.popMatrix();
        SlightGuiModifications.restoreAlpha();
    }
}
