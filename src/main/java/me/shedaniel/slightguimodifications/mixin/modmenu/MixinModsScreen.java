package me.shedaniel.slightguimodifications.mixin.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModsScreen.class)
public abstract class MixinModsScreen extends Screen {
    protected MixinModsScreen(Component title) {
        super(title);
    }
    
    @Inject(method = "overlayBackground",
            at = @At(value = "HEAD"), remap = false)
    private static void preRenderHoleBackground(int x1, int y1, int x2, int y2, int red, int green, int blue, int startAlpha, int endAlpha, CallbackInfo ci) {
//        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
//        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);
    }
    
    @Inject(method = "overlayBackground",
            at = @At(value = "RETURN"), remap = false)
    private static void postRenderHoleBackground(int x1, int y1, int x2, int y2, int red, int green, int blue, int startAlpha, int endAlpha, CallbackInfo ci) {
//        RenderSystem.popMatrix();
    }
}
