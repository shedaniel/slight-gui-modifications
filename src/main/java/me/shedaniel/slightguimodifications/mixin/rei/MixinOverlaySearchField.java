package me.shedaniel.slightguimodifications.mixin.rei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget;
import me.shedaniel.rei.impl.client.gui.widget.search.OverlaySearchField;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OverlaySearchField.class)
public class MixinOverlaySearchField extends TextFieldWidget {
    public MixinOverlaySearchField(Rectangle rectangle) {super(rectangle);}

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (getBounds().contains(mouseX, mouseY) && this.isVisible() && SlightGuiModifications.getGuiConfig().textFieldModifications.rightClickActions && button == 1) {
            if (editable) if (cursorPos - highlightPos != 0)
                ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createSelectingMenu()));
            else
                ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createNonSelectingMenu()));
            else if (cursorPos - highlightPos != 0)
                ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createSelectingNotEditableMenu()));
            else
                ((MenuWidgetListener) Minecraft.getInstance().screen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createNonSelectingNotEditableMenu()));
            cir.setReturnValue(true);
            boolean boolean_1 = mouseX >= (double) this.getBounds().x && mouseX < (double) (this.getBounds().x + this.getBounds().width) && mouseY >= (double) this.getBounds().y && mouseY < (double) (this.getBounds().y + this.getBounds().height);
            this.setFocused(boolean_1);
        }
    }
    
    @Unique
    private void removeSelfMenu() {((MenuWidgetListener) Minecraft.getInstance().screen).removeMenu();}
    
    @Unique
    private List<MenuEntry> createNonSelectingNotEditableMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            removeSelfMenu();
        }));
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
            if (this.editable) this.addText(Minecraft.getInstance().keyboardHandler.getClipboard());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            removeSelfMenu();
        }), new TextMenuEntry(I18n.get("text.slightguimodifications.clearAll"), () -> {
            this.setText("");
            removeSelfMenu();
        }));
    }
    
    @Unique
    private List<MenuEntry> createSelectingNotEditableMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            removeSelfMenu();
        }));
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.get("text.slightguimodifications.cut"), () -> {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
            if (this.editable) this.addText("");
            removeSelfMenu();
        }), new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
            if (this.editable) this.addText(Minecraft.getInstance().keyboardHandler.getClipboard());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            removeSelfMenu();
        }), new TextMenuEntry(I18n.get("text.slightguimodifications.clearAll"), () -> {
            this.setText("");
            removeSelfMenu();
        }));
    }
}
