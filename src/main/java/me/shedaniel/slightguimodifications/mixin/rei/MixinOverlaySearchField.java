package me.shedaniel.slightguimodifications.mixin.rei;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.gui.OverlaySearchField;
import me.shedaniel.rei.gui.widget.TextFieldWidget;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OverlaySearchField.class)
public class MixinOverlaySearchField extends TextFieldWidget {
    public MixinOverlaySearchField(Rectangle rectangle) {super(rectangle);}
    
    @ModifyArg(method = "renderBorder", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/gui/OverlaySearchField;fill(IIIII)V", ordinal = 2), index = 4,
               remap = false)
    private int modifyBackgroundColor(int color) {return SlightGuiModifications.getConfig().textFieldModifications.enabled ? SlightGuiModifications.getConfig().textFieldModifications.backgroundColor | 255 << 24 : color;}
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = false)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (getBounds().contains(mouseX, mouseY) && this.isVisible() && SlightGuiModifications.getConfig().textFieldModifications.rightClickActions && button == 1) {
            if (editable) if (cursorMin - cursorMax != 0)
                ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createSelectingMenu()));
            else
                ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createNonSelectingMenu()));
            else if (cursorMin - cursorMax != 0)
                ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createSelectingNotEditableMenu()));
            else
                ((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).applyMenu(new MenuWidget(new Point(mouseX + 2, getBounds().y - 2), createNonSelectingNotEditableMenu()));
            cir.setReturnValue(true);
            boolean boolean_1 = mouseX >= (double) this.getBounds().x && mouseX < (double) (this.getBounds().x + this.getBounds().width) && mouseY >= (double) this.getBounds().y && mouseY < (double) (this.getBounds().y + this.getBounds().height);
            this.setFocused(boolean_1);
        }
    }
    
    @Unique
    private void removeSelfMenu() {((MenuWidgetListener) MinecraftClient.getInstance().currentScreen).removeMenu();}
    
    @Unique
    private List<MenuEntry> createNonSelectingNotEditableMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.method_1884(0);
            removeSelfMenu();
        }));
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.translate("text.slightguimodifications.paste"), () -> {
            if (this.editable) this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.method_1884(0);
            removeSelfMenu();
        }), new TextMenuEntry(I18n.translate("text.slightguimodifications.clearAll"), () -> {
            this.setText("");
            removeSelfMenu();
        }));
    }
    
    @Unique
    private List<MenuEntry> createSelectingNotEditableMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.translate("text.slightguimodifications.copy"), () -> {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.method_1884(0);
            removeSelfMenu();
        }));
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(new TextMenuEntry(I18n.translate("text.slightguimodifications.copy"), () -> {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.translate("text.slightguimodifications.cut"), () -> {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            if (this.editable) this.addText("");
            removeSelfMenu();
        }), new TextMenuEntry(I18n.translate("text.slightguimodifications.paste"), () -> {
            if (this.editable) this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
            removeSelfMenu();
        }), new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
            this.moveCursorToEnd();
            this.method_1884(0);
            removeSelfMenu();
        }), new TextMenuEntry(I18n.translate("text.slightguimodifications.clearAll"), () -> {
            this.setText("");
            removeSelfMenu();
        }));
    }
}
