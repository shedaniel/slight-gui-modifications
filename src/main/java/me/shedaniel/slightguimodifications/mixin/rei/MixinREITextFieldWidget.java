package me.shedaniel.slightguimodifications.mixin.rei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.TickableWidget;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget;
import me.shedaniel.rei.impl.client.gui.widget.search.OverlaySearchField;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TextFieldWidget.class)
public abstract class MixinREITextFieldWidget extends WidgetWithBounds implements TickableWidget {
    @Shadow(remap = false)
    public abstract boolean hasBorder();
    
    @Shadow(remap = false) private Rectangle bounds;
    
    @Shadow(remap = false)
    public abstract boolean isVisible();
    
    @Shadow(remap = false) protected boolean editable;
    
    @Shadow(remap = false) protected int cursorPos;
    
    @Shadow(remap = false) protected int highlightPos;
    
    @Shadow(remap = false)
    public abstract boolean isFocused();
    
    @Shadow(remap = false)
    public abstract void moveCursorToEnd();
    
    @Shadow(remap = false)
    public abstract void setHighlightPos(int int_1);
    
    @Shadow(remap = false)
    public abstract void addText(String string_1);
    
    @Shadow(remap = false)
    public abstract String getSelectedText();
    
    @Shadow(remap = false)
    public abstract void setFocused(boolean boolean_1);
    
    @Shadow(remap = false) private boolean focusUnlocked;
    
    @Shadow(remap = false)
    public abstract void setText(String string_1);
    
    @Inject(method = "renderBorder",
            at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void renderBorder(PoseStack matrices, CallbackInfo ci) {
        boolean border = hasBorder();
        if (border && SlightGuiModifications.getGuiConfig().textFieldModifications.enabled && SlightGuiModifications.getGuiConfig().textFieldModifications.backgroundMode == SlightGuiModificationsConfig.Gui.TextFieldModifications.BackgroundMode.TEXTURE) {
            renderTextureBorder(matrices);
            ci.cancel();
        }
    }
    
    @Unique
    private void renderTextureBorder(PoseStack matrices) {
        RenderSystem.setShaderTexture(0, SlightGuiModifications.TEXT_FIELD_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);
        int x = bounds.x, y = bounds.y, width = bounds.width, height = bounds.height;
        // 9 Patch Texture
        
        // Four Corners
        blit(matrices, x - 1, y - 1, getBlitOffset(), 0, 0, 8, 8, 256, 256);
        blit(matrices, x + width - 7, y - 1, getBlitOffset(), 248, 0, 8, 8, 256, 256);
        blit(matrices, x - 1, y + height - 7, getBlitOffset(), 0, 248, 8, 8, 256, 256);
        blit(matrices, x + width - 7, y + height - 7, getBlitOffset(), 248, 248, 8, 8, 256, 256);
        
        Matrix4f matrix = matrices.last().pose();
        // Sides
        GuiComponent.innerBlit(matrix, x + 7, x + width - 7, y - 1, y + 7, getBlitOffset(), (8) / 256f, (248) / 256f, (0) / 256f, (8) / 256f);
        GuiComponent.innerBlit(matrix, x + 7, x + width - 7, y + height - 7, y + height + 1, getBlitOffset(), (8) / 256f, (248) / 256f, (248) / 256f, (256) / 256f);
        GuiComponent.innerBlit(matrix, x - 1, x + 7, y + 7, y + height - 7, getBlitOffset(), (0) / 256f, (8) / 256f, (8) / 256f, (248) / 256f);
        GuiComponent.innerBlit(matrix, x + width - 7, x + width + 1, y + 7, y + height - 7, getBlitOffset(), (248) / 256f, (256) / 256f, (8) / 256f, (248) / 256f);
        
        // Center
        GuiComponent.innerBlit(matrix, x + 7, x + width - 7, y + 7, y + height - 7, getBlitOffset(), (8) / 256f, (248) / 256f, (8) / 256f, (248) / 256f);
    }
    
    @ModifyArg(method = "renderBorder",
               at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V",
                        ordinal = 0),
               index = 5)
    private int modifyBorderHighlightedColor(int color) {
        return SlightGuiModifications.getGuiConfig().textFieldModifications.enabled ? SlightGuiModifications.getGuiConfig().textFieldModifications.borderColor | 255 << 24 : color;
    }
    
    @ModifyArg(method = "renderBorder",
               at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V",
                        ordinal = 1),
               index = 5)
    private int modifyBorderColor(int color) {
        return SlightGuiModifications.getGuiConfig().textFieldModifications.enabled ? SlightGuiModifications.getGuiConfig().textFieldModifications.borderColor | 255 << 24 : color;
    }
    
    @ModifyArg(method = "renderBorder",
               at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V",
                        ordinal = 2),
               index = 5)
    private int modifyBackgroundColor(int color) {
        return SlightGuiModifications.getGuiConfig().textFieldModifications.enabled ? SlightGuiModifications.getGuiConfig().textFieldModifications.backgroundColor | 255 << 24 : color;
    }
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (getBounds().contains(mouseX, mouseY) && this.isVisible() && SlightGuiModifications.getGuiConfig().textFieldModifications.rightClickActions && button == 1 && !((Object) this instanceof OverlaySearchField)) {
            if (editable) {
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
            cir.setReturnValue(true);
            boolean boolean_1 = mouseX >= (double) this.bounds.x && mouseX < (double) (this.bounds.x + this.bounds.width) && mouseY >= (double) this.bounds.y && mouseY < (double) (this.bounds.y + this.bounds.height);
            if (this.focusUnlocked) {
                this.setFocused(boolean_1);
            }
        }
    }
    
    @Unique
    private void removeSelfMenu() {
        ((MenuWidgetListener) Minecraft.getInstance().screen).removeMenu();
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingNotEditableMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.setHighlightPos(0);
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
                    if (this.editable) {
                        this.addText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.setHighlightPos(0);
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.clearAll"), () -> {
                    this.setText("");
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingNotEditableMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.setHighlightPos(0);
                    removeSelfMenu();
                })
        
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.cut"), () -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                    if (this.editable) {
                        this.addText("");
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
                    if (this.editable) {
                        this.addText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.setHighlightPos(0);
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.clearAll"), () -> {
                    this.setText("");
                    removeSelfMenu();
                })
        );
    }
}
