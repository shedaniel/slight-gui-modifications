package me.shedaniel.slightguimodifications.gui.cts.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class CustomizedButtonWidget extends Button {
    private final Supplier<ResourceLocation> texture;
    private final Supplier<ResourceLocation> hoveredTexture;
    
    public CustomizedButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, Supplier<ResourceLocation> texture, Supplier<ResourceLocation> hoveredTexture) {
        super(x, y, width, height, message, onPress);
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
    }
    
    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        ResourceLocation textureId = texture.get();
        if (textureId != null) {
            ResourceLocation hoveredTextureId = hoveredTexture.get();
            if (isHoveredOrFocused() && hoveredTextureId != null)
                textureId = hoveredTextureId;
            Minecraft minecraftClient = Minecraft.getInstance();
            Font textRenderer = minecraftClient.font;
            RenderSystem.setShaderTexture(0, textureId);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            innerBlit(matrices.last().pose(), x, x + width, y, y + height, getBlitOffset(), 0F, 1F, 0F, 1F);
            this.renderBg(matrices, minecraftClient, mouseX, mouseY);
            int j = this.active ? 16777215 : 10526880;
            drawCenteredString(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
            if (this.isHoveredOrFocused()) {
                this.renderToolTip(matrices, mouseX, mouseY);
            }
            return;
        }
        super.renderButton(matrices, mouseX, mouseY, delta);
    }
}
