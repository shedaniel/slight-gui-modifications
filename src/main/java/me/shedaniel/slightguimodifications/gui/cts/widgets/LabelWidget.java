package me.shedaniel.slightguimodifications.gui.cts.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.slightguimodifications.gui.cts.elements.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class LabelWidget extends AbstractWidget {
    private int alignment, color, hoveredColor;
    private Runnable onClicked;
    private boolean hasShadow;
    
    public LabelWidget(int x, int y, int alignment, @NotNull Text text, int color, int hoveredColor, boolean hasShadow, @NotNull Runnable onClicked) {
        super(x, y, 0, 0, text.unwrap());
        this.alignment = alignment;
        this.color = color;
        this.hoveredColor = hoveredColor;
        this.onClicked = onClicked;
        this.hasShadow = hasShadow;
    }
    
    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int j = isMouseOver(mouseX, mouseY) ? hoveredColor : color;
        int x = this.x;
        if (alignment == 1) {
            x -= textRenderer.width(getMessage()) / 2;
        } else if (alignment == 2) {
            x -= textRenderer.width(getMessage());
        }
        if (!hasShadow) textRenderer.draw(matrices, this.getMessage(), x, this.y, j | Mth.ceil(this.alpha * 255.0F) << 24);
        else textRenderer.drawShadow(matrices, this.getMessage(), x, this.y, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        if (mouseY < y || mouseY > y + textRenderer.lineHeight) return false;
        int width = textRenderer.width(getMessage());
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
    
    @Override
    public void updateNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, getMessage());
    }
}
