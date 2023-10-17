package me.shedaniel.slightguimodifications.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class ConfigButtonWidget extends Button {
    public ConfigButtonWidget(int i, int j, int k, int l, Component component, OnPress onPress) {
        super(i, j, k, l, component, onPress, Supplier::get);
    }
    
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        var widgetLocation = new ResourceLocation("widget/button" + (this.isHoveredOrFocused() ? "_highlighted" : ""));
        graphics.blitSprite(widgetLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        int j = this.active ? 16777215 : 10526880;
        graphics.pose().pushPose();
        float scale = 1 / 1.3f;
        graphics.pose().scale(scale, scale, 0);
        graphics.drawString(textRenderer, this.getMessage(), (int) ((this.getX() + this.width / 2) * (1 / scale) - textRenderer.width(getMessage()) / 2), (int) ((this.getY() + (this.height - 6) / 2) * (1 / scale)), j | Mth.ceil(this.alpha * 255.0F) << 24);
        graphics.pose().popPose();
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
