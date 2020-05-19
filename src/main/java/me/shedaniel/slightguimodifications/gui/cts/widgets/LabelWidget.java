package me.shedaniel.slightguimodifications.gui.cts.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class LabelWidget extends AbstractButtonWidget {
    private int alignment, color, hoveredColor;
    private Runnable onClicked;
    
    public LabelWidget(int x, int y, int alignment, @NotNull String text, int color, int hoveredColor, @NotNull Runnable onClicked) {
        super(x, y, 0, 0, text);
        this.alignment = alignment;
        this.color = color;
        this.hoveredColor = hoveredColor;
        this.onClicked = onClicked;
    }
    
    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        int j = isMouseOver(mouseX, mouseY) ? hoveredColor : color;
        int x = this.x;
        if (alignment == 1) {
            x -= textRenderer.getStringWidth(getMessage()) / 2;
        } else if (alignment == 2) {
            x -= textRenderer.getStringWidth(getMessage());
        }
//        System.out.println(y +" "+minecraftClient.getWindow().getScaledHeight());
        textRenderer.draw(this.getMessage(), x, this.y, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        if (mouseY < y || mouseY > y + textRenderer.fontHeight) return false;
        int width = textRenderer.getStringWidth(getMessage());
        int x = this.x;
        if (alignment == 1) {
            x -= width / 2;
        } else if (alignment == 2) {
            x -= width;
        }
        return !(mouseX < x) && !(mouseX > x + width);
    }
    
    @Override
    public boolean changeFocus(boolean bl) {
        return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                if (this.isMouseOver(mouseX, mouseY)) {
                    this.onClicked.run();
                    return true;
                }
            }
            
            return false;
        }
        return false;
    }
}
