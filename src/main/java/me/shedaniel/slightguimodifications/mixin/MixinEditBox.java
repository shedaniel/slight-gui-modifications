package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EditBox.class)
public abstract class MixinEditBox extends AbstractWidget implements Renderable, GuiEventListener {
    @Shadow private boolean isEditable;
    
    @Shadow private int textColor;
    
    @Shadow private int textColorUneditable;
    
    public MixinEditBox(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
    
    @Shadow
    protected abstract boolean isBordered();
    
    @Shadow
    public abstract boolean isVisible();
    
    @Shadow private int cursorPos;
    
    @Shadow private int highlightPos;
    
    @Shadow
    public abstract void insertText(String text);
    
    @Shadow
    public abstract void moveCursorToEnd(boolean bl);
    
    @Shadow
    public abstract void setHighlightPos(int i);
    
    @Shadow
    protected abstract boolean isEditable();
    
    @Shadow
    public abstract String getHighlighted();
    
    @Shadow
    public abstract void setValue(String text);
    
    @Shadow private boolean canLoseFocus;
    
    @Shadow
    public abstract boolean isMouseOver(double mouseX, double mouseY);
    
    @Unique
    private GuiGraphics lastMatrices;
    
    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void preRenderButton(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.lastMatrices = graphics;
    }

    @Inject(method = "renderHighlight", at = @At("HEAD"), cancellable = true)
    private void drawSelectionHighlight(GuiGraphics graphics, int x1, int y1, int x2, int y2, CallbackInfo ci) {
        if (!SlightGuiModifications.getGuiConfig().textFieldModifications.enabled || SlightGuiModifications.getGuiConfig().textFieldModifications.selectionMode != SlightGuiModificationsConfig.Gui.TextFieldModifications.SelectionMode.HIGHLIGHT)
            return;
        ci.cancel();
        int tmp;
        if (x1 < x2) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        
        if (y1 < y2) {
            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        
        if (x2 > this.getX() + this.width) {
            x2 = this.getX() + this.width;
        }
        
        if (x1 > this.getX() + this.width) {
            x1 = this.getX() + this.width;
        }
        
        int color = this.isEditable ? this.textColor : this.textColorUneditable;
        int r = (color >> 16 & 255);
        int g = (color >> 8 & 255);
        int b = (color & 255);
        RenderSystem.enableBlend();
//        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
//        RenderSystem.shadeModel(7425);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(x1, y2, 0 + 50d).color(r, g, b, 120).endVertex();
        buffer.vertex(x2, y2, 0 + 50d).color(r, g, b, 120).endVertex();
        buffer.vertex(x2, y1, 0 + 50d).color(r, g, b, 120).endVertex();
        buffer.vertex(x1, y1, 0 + 50d).color(r, g, b, 120).endVertex();
        tessellator.end();
//        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
//        RenderSystem.enableAlphaTest();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (preMouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private boolean preMouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && this.isVisible() && SlightGuiModifications.getGuiConfig().textFieldModifications.rightClickActions && button == 1) {
            if (isEditable()) {
                if (cursorPos - highlightPos != 0) {
                    ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createSelectingMenu()));
                } else {
                    ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createNonSelectingMenu()));
                }
            } else {
                if (cursorPos - highlightPos != 0) {
                    ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createSelectingNotEditableMenu()));
                } else {
                    ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createNonSelectingNotEditableMenu()));
                }
            }
            boolean boolean_1 = mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
            if (this.canLoseFocus) {
                this.setFocused(boolean_1);
            }
            return true;
        }
        
        return false;
    }
    
    @Unique
    private void removeSelfMenu() {
        ((MenuWidgetListener) Minecraft.getInstance().screen).removeMenu();
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingNotEditableMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd(false);
                    this.setHighlightPos(0);
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
                    if (this.isEditable) {
                        this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd(false);
                    this.setHighlightPos(0);
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingNotEditableMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd(false);
                    this.setHighlightPos(0);
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.clearAll"), () -> {
                    this.setValue("");
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.cut"), () -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    if (this.isEditable) {
                        this.insertText("");
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
                    if (this.isEditable) {
                        this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd(false);
                    this.setHighlightPos(0);
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.clearAll"), () -> {
                    this.setValue("");
                    removeSelfMenu();
                })
        );
    }
}
