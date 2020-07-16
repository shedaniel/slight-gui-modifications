package me.shedaniel.slightguimodifications.gui.cts.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.MathHelper;

public class CustomizedButtonWidget extends ButtonWidget {
    private final Lazy<Identifier> texture;
    private final Lazy<Identifier> hoveredTexture;
    
    public CustomizedButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, Lazy<Identifier> texture, Lazy<Identifier> hoveredTexture) {
        super(x, y, width, height, message, onPress);
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
    }
    
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Identifier textureId = texture.get();
        if (textureId != null) {
            Identifier hoveredTextureId = hoveredTexture.get();
            if (isHovered() && hoveredTextureId != null)
                textureId = hoveredTextureId;
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            TextRenderer textRenderer = minecraftClient.textRenderer;
            minecraftClient.getTextureManager().bindTexture(textureId);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            drawTexturedQuad(matrices.peek().getModel(), x, x + width, y, y + height, getZOffset(), 0F, 1F, 0F, 1F);
            this.renderBg(matrices, minecraftClient, mouseX, mouseY);
            int j = this.active ? 16777215 : 10526880;
            this.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
            if (this.isHovered()) {
                this.renderToolTip(matrices, mouseX, mouseY);
            }
            return;
        }
        super.renderButton(matrices, mouseX, mouseY, delta);
    }
}
