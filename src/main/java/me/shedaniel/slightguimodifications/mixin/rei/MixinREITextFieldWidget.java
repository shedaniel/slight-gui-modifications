package me.shedaniel.slightguimodifications.mixin.rei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.gui.OverlaySearchField;
import me.shedaniel.rei.gui.widget.TextFieldWidget;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Tickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TextFieldWidget.class)
public abstract class MixinREITextFieldWidget extends WidgetWithBounds implements Tickable {
    @Shadow(remap = false)
    public abstract boolean hasBorder();
    
    @Shadow(remap = false) private Rectangle bounds;
    
    @Shadow(remap = false)
    public abstract boolean isVisible();
    
    @Shadow(remap = false) protected boolean editable;
    
    @Shadow(remap = false) protected int cursorMin;
    
    @Shadow(remap = false) protected int cursorMax;
    
    @Shadow(remap = false)
    public abstract boolean isFocused();
    
    @Shadow(remap = false)
    public abstract void moveCursorToEnd();
    
    @Shadow(remap = false)
    public abstract void method_1884(int int_1);
    
    @Shadow(remap = false)
    public abstract void addText(String string_1);
    
    @Shadow(remap = false)
    public abstract String getSelectedText();
    
    @Shadow(remap = false)
    public abstract void setFocused(boolean boolean_1);
    
    @Shadow(remap = false) private boolean field_2096;
    
    @Shadow(remap = false)
    public abstract void setText(String string_1);
    
    @Redirect(method = "renderBorder",
              at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/gui/widget/TextFieldWidget;hasBorder()Z", ordinal = 0, remap = false), remap = false)
    private boolean hasBorder(TextFieldWidget textFieldWidget) {
        boolean border = hasBorder();
        if (border && SlightGuiModifications.getConfig().textFieldModifications.enabled && SlightGuiModifications.getConfig().textFieldModifications.backgroundMode == SlightGuiModificationsConfig.TextFieldModifications.BackgroundMode.TEXTURE) {
            renderTextureBorder();
            return false;
        }
        return border;
    }
    
    @Unique
    private void renderTextureBorder() {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SlightGuiModifications.TEXT_FIELD_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);
        int x = bounds.x, y = bounds.y, width = bounds.width, height = bounds.height;
        // 9 Patch Texture
        
        // Four Corners
        blit(x - 1, y - 1, getBlitOffset(), 0, 0, 8, 8, 256, 256);
        blit(x + width - 7, y - 1, getBlitOffset(), 248, 0, 8, 8, 256, 256);
        blit(x - 1, y + height - 7, getBlitOffset(), 0, 248, 8, 8, 256, 256);
        blit(x + width - 7, y + height - 7, getBlitOffset(), 248, 248, 8, 8, 256, 256);
        
        // Sides
        DrawableHelper.innerBlit(x + 7, x + width - 7, y - 1, y + 7, getBlitOffset(), (8) / 256f, (248) / 256f, (0) / 256f, (8) / 256f);
        DrawableHelper.innerBlit(x + 7, x + width - 7, y + height - 7, y + height + 1, getBlitOffset(), (8) / 256f, (248) / 256f, (248) / 256f, (256) / 256f);
        DrawableHelper.innerBlit(x - 1, x + 7, y + 7, y + height - 7, getBlitOffset(), (0) / 256f, (8) / 256f, (8) / 256f, (248) / 256f);
        DrawableHelper.innerBlit(x + width - 7, x + width + 1, y + 7, y + height - 7, getBlitOffset(), (248) / 256f, (256) / 256f, (8) / 256f, (248) / 256f);
        
        // Center
        DrawableHelper.innerBlit(x + 7, x + width - 7, y + 7, y + height - 7, getBlitOffset(), (8) / 256f, (248) / 256f, (8) / 256f, (248) / 256f);
    }
    
    @ModifyArg(method = "renderBorder", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/gui/widget/TextFieldWidget;fill(IIIII)V", ordinal = 0),
               index = 4, remap = false)
    private int modifyBorderHighlightedColor(int color) {
        return SlightGuiModifications.getConfig().textFieldModifications.enabled ? SlightGuiModifications.getConfig().textFieldModifications.borderColor | 255 << 24 : color;
    }
    
    @ModifyArg(method = "renderBorder", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/gui/widget/TextFieldWidget;fill(IIIII)V", ordinal = 1),
               index = 4, remap = false)
    private int modifyBorderColor(int color) {
        return SlightGuiModifications.getConfig().textFieldModifications.enabled ? SlightGuiModifications.getConfig().textFieldModifications.borderColor | 255 << 24 : color;
    }
    
    @ModifyArg(method = "renderBorder", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/gui/widget/TextFieldWidget;fill(IIIII)V", ordinal = 2),
               index = 4, remap = false)
    private int modifyBackgroundColor(int color) {
        return SlightGuiModifications.getConfig().textFieldModifications.enabled ? SlightGuiModifications.getConfig().textFieldModifications.backgroundColor | 255 << 24 : color;
    }
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = false)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (getBounds().contains(mouseX, mouseY) && this.isVisible() && SlightGuiModifications.getConfig().textFieldModifications.rightClickActions && button == 1 && !((Object) this instanceof OverlaySearchField)) {
            if (editable) {
                if (cursorMin - cursorMax != 0) {
                    ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createSelectingMenu()));
                } else {
                    ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createNonSelectingMenu()));
                }
            } else {
                if (cursorMin - cursorMax != 0) {
                    ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createSelectingNotEditableMenu()));
                } else {
                    ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createNonSelectingNotEditableMenu()));
                }
            }
            cir.setReturnValue(true);
            boolean boolean_1 = mouseX >= (double) this.bounds.x && mouseX < (double) (this.bounds.x + this.bounds.width) && mouseY >= (double) this.bounds.y && mouseY < (double) (this.bounds.y + this.bounds.height);
            if (this.field_2096) {
                this.setFocused(boolean_1);
            }
        }
    }
    
    @Unique
    private void removeSelfMenu() {
        ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).removeMenu();
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingNotEditableMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.method_1884(0);
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.translate("text.slightguimodifications.paste"), () -> {
                    if (this.editable) {
                        this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.method_1884(0);
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.clearAll"), () -> {
                    this.setText("");
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingNotEditableMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.translate("text.slightguimodifications.copy"), () -> {
                    MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.method_1884(0);
                    removeSelfMenu();
                })
        
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.translate("text.slightguimodifications.copy"), () -> {
                    MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.cut"), () -> {
                    MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                    if (this.editable) {
                        this.addText("");
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.paste"), () -> {
                    if (this.editable) {
                        this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
                    }
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
                    this.method_1884(0);
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.clearAll"), () -> {
                    this.setText("");
                    removeSelfMenu();
                })
        );
    }
}
