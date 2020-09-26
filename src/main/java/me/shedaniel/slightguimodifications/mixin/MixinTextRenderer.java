package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Font.class)
public abstract class MixinTextRenderer {
    @ModifyVariable(method = "renderText(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)F",
                    ordinal = 0, at = @At("HEAD"))
    private int drawLayerStringChangeColor(int color) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getAlpha();
            if (alpha >= 0) {
                return color & 16777215 | Mth.ceil(alpha * (float) (color >> 24 & 255)) << 24;
            }
        }
        return color;
    }
    
    @ModifyVariable(
            method = "renderText(Lnet/minecraft/util/FormattedCharSequence;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)F",
            ordinal = 0, at = @At("HEAD"))
    private int drawLayerStringRenderableChangeColor(int color) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getAlpha();
            if (alpha >= 0) {
                return color & 16777215 | Mth.ceil(alpha * (float) (color >> 24 & 255)) << 24;
            }
        }
        return color;
    }
}
