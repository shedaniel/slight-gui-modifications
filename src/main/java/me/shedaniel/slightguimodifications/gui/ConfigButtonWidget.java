package me.shedaniel.slightguimodifications.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class ConfigButtonWidget extends Button {
    public ConfigButtonWidget(int i, int j, int k, int l, Component component, OnPress onPress) {
        super(i, j, k, l, component, onPress, Supplier::get);
    }
    
    @Override
    public void renderWidget(PoseStack matrices, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blitNineSliced(matrices, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int j = this.active ? 16777215 : 10526880;
        matrices.pushPose();
        float scale = 1 / 1.3f;
        matrices.scale(scale, scale, 0);
        textRenderer.drawShadow(matrices, this.getMessage(), (this.getX() + this.width / 2) * (1 / scale) - textRenderer.width(getMessage()) / 2, (this.getY() + (this.height - 6) / 2) * (1 / scale), j | Mth.ceil(this.alpha * 255.0F) << 24);
        matrices.popPose();
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
