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
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
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
    public abstract void moveCursorToEnd();
    
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
    private PoseStack lastMatrices;
    
    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void preRenderButton(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.lastMatrices = matrices;
    }
    
    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;isBordered()Z", ordinal = 0))
    private boolean isBordered(EditBox textFieldWidget) {
        boolean border = isBordered();
        if (border && SlightGuiModifications.getGuiConfig().textFieldModifications.enabled && SlightGuiModifications.getGuiConfig().textFieldModifications.backgroundMode == SlightGuiModificationsConfig.Gui.TextFieldModifications.BackgroundMode.TEXTURE) {
            renderTextureBorder();
            return false;
        }
        return border;
    }
    
    @Unique
    private void renderTextureBorder() {
        RenderSystem.setShaderTexture(0, SlightGuiModifications.TEXT_FIELD_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.blendFunc(770, 771);
        // 9 Patch Texture
        
        // Four Corners
        int x = getX(), y = getY();
        blit(lastMatrices, x - 1, y - 1, 0, 0, 0, 8, 8, 256, 256);
        blit(lastMatrices, x + width - 7, y - 1, 0, 248, 0, 8, 8, 256, 256);
        blit(lastMatrices, x - 1, y + height - 7, 0, 0, 248, 8, 8, 256, 256);
        blit(lastMatrices, x + width - 7, y + height - 7, 0, 248, 248, 8, 8, 256, 256);
        
        Matrix4f matrix = lastMatrices.last().pose();
        // Sides
        GuiComponent.innerBlit(matrix, x + 7, x + width - 7, y - 1, y + 7, 0, (8) / 256f, (248) / 256f, (0) / 256f, (8) / 256f);
        GuiComponent.innerBlit(matrix, x + 7, x + width - 7, y + height - 7, y + height + 1, 0, (8) / 256f, (248) / 256f, (248) / 256f, (256) / 256f);
        GuiComponent.innerBlit(matrix, x - 1, x + 7, y + 7, y + height - 7, 0, (0) / 256f, (8) / 256f, (8) / 256f, (248) / 256f);
        GuiComponent.innerBlit(matrix, x + width - 7, x + width + 1, y + 7, y + height - 7, 0, (248) / 256f, (256) / 256f, (8) / 256f, (248) / 256f);
        
        // Center
        GuiComponent.innerBlit(matrix, x + 7, x + width - 7, y + 7, y + height - 7, 0, (8) / 256f, (248) / 256f, (8) / 256f, (248) / 256f);
        this.lastMatrices = null;
    }
    
    @ModifyArg(method = "renderWidget",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V",
                        ordinal = 0),
               index = 5)
    private int modifyBorderColor(int color) {
        return SlightGuiModifications.getGuiConfig().textFieldModifications.enabled ? SlightGuiModifications.getGuiConfig().textFieldModifications.borderColor | 255 << 24 : color;
    }
    
    @ModifyArg(method = "renderWidget",
               at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V",
                        ordinal = 1),
               index = 5)
    private int modifyBackgroundColor(int color) {
        return SlightGuiModifications.getGuiConfig().textFieldModifications.enabled ? SlightGuiModifications.getGuiConfig().textFieldModifications.backgroundColor | 255 << 24 : color;
    }
    
    @Inject(method = "renderHighlight", at = @At("HEAD"), cancellable = true)
    private void drawSelectionHighlight(PoseStack poseStack, int x1, int y1, int x2, int y2, CallbackInfo ci) {
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
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
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
            cir.setReturnValue(true);
            boolean boolean_1 = mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
            if (this.canLoseFocus) {
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
                    if (this.isEditable) {
                        this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    }
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
    private List<MenuEntry> createSelectingNotEditableMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.moveCursorToEnd();
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
                    this.moveCursorToEnd();
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
