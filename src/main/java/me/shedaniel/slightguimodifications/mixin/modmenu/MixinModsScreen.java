package me.shedaniel.slightguimodifications.mixin.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.prospector.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModsScreen.class)
public abstract class MixinModsScreen extends Screen {
    protected MixinModsScreen(Text title) {
        super(title);
    }
    
    @Inject(method = "overlayBackground",
            at = @At(value = "HEAD"), remap = false)
    private static void preRenderHoleBackground(int x1, int y1, int x2, int y2, int red, int green, int blue, int startAlpha, int endAlpha, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }
    
    @Inject(method = "overlayBackground",
            at = @At(value = "RETURN"), remap = false)
    private static void postRenderHoleBackground(int x1, int y1, int x2, int y2, int red, int green, int blue, int startAlpha, int endAlpha, CallbackInfo ci) {
        RenderSystem.popMatrix();
    }
}
