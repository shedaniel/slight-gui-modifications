package me.shedaniel.slightguimodifications.gui.cts.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.slightguimodifications.gui.cts.elements.Text;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int j = isMouseOver(mouseX, mouseY) ? hoveredColor : color;
        int x = this.getX();
        if (alignment == 1) {
            x -= textRenderer.width(getMessage()) / 2;
        } else if (alignment == 2) {
            x -= textRenderer.width(getMessage());
        }
        if (!hasShadow) graphics.drawString(textRenderer, this.getMessage(), x, this.getY(), j | Mth.ceil(this.alpha * 255.0F) << 24, false);
        else graphics.drawString(textRenderer, this.getMessage(), x, this.getY(), j | Mth.ceil(this.alpha * 255.0F) << 24);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Font textRenderer = minecraftClient.font;
        if (mouseY < getY() || mouseY > getY() + textRenderer.lineHeight) return false;
        int width = textRenderer.width(getMessage());
        int x = this.getX();
        if (alignment == 1) {
            x -= width / 2;
        } else if (alignment == 2) {
            x -= width;
        }
        return !(mouseX < x) && !(mouseX > x + width);
    }
    
    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        return null;
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
    public void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, getMessage());
    }
}
