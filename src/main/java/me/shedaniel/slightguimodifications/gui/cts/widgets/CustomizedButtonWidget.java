package me.shedaniel.slightguimodifications.gui.cts.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class CustomizedButtonWidget extends Button {
    private final Supplier<ResourceLocation> texture;
    private final Supplier<ResourceLocation> hoveredTexture;
    
    public CustomizedButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, Supplier<ResourceLocation> texture, Supplier<ResourceLocation> hoveredTexture) {
        super(x, y, width, height, message, onPress, Supplier::get);
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
    }
    
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
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
            graphics.blitNineSliced(textureId, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int j = this.active ? 16777215 : 10526880;
            graphics.drawCenteredString(textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
            return;
        }
        super.renderWidget(graphics, mouseX, mouseY, delta);
    }
    
    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        
        return 46 + i * 20;
    }
}
