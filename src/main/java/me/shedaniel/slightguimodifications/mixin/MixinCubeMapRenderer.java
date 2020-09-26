package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.renderer.CubeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CubeMap.class)
public class MixinCubeMapRenderer {
    @Redirect(method = "render",
              at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;vertex(DDD)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer blit(BufferBuilder bufferBuilder, double x, double y, double z) {
        return bufferBuilder.vertex(x, SlightGuiModifications.reverseYAnimation(y), z);
    }
}
