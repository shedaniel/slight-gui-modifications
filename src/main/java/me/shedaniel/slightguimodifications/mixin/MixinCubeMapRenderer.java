package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CubeMapRenderer.class)
public class MixinCubeMapRenderer {
    @Redirect(method = "draw",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(DDD)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer blit(BufferBuilder bufferBuilder, double x, double y, double z) {
        return bufferBuilder.vertex(x, SlightGuiModifications.reverseYAnimation(y), z);
    }
}
