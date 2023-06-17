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
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.blit(matrices, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(matrices, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        matrices.pushPose();
        float scale = 1 / 1.3f;
        matrices.scale(scale, scale, 0);
        textRenderer.drawShadow(matrices, this.getMessage(), (this.getX() + this.width / 2) * (1 / scale) - textRenderer.width(getMessage()) / 2, (this.getY() + (this.height - 6) / 2) * (1 / scale), j | Mth.ceil(this.alpha * 255.0F) << 24);
        matrices.popPose();
    }
}
