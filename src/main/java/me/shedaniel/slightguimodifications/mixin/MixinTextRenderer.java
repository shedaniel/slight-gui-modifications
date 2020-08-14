package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextRenderer.class)
public abstract class MixinTextRenderer {
    @ModifyVariable(method = "drawLayer(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F",
                    ordinal = 0, at = @At("HEAD"))
    private int drawLayerStringChangeColor(int color) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getAlpha();
            if (alpha >= 0) {
                return color & 16777215 | MathHelper.ceil(alpha * (float) (color >> 24 & 255)) << 24;
            }
        }
        return color;
    }
    
    @ModifyVariable(
            method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F",
            ordinal = 0, at = @At("HEAD"))
    private int drawLayerStringRenderableChangeColor(int color) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getAlpha();
            if (alpha >= 0) {
                return color & 16777215 | MathHelper.ceil(alpha * (float) (color >> 24 & 255)) << 24;
            }
        }
        return color;
    }
}
