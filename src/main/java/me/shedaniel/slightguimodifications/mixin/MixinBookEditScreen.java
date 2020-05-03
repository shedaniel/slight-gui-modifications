package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.api.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BookEditScreen.class)
public abstract class MixinBookEditScreen extends Screen {
    protected MixinBookEditScreen(Text title) {
        super(title);
    }
    
    @Shadow
    protected abstract String getCurrentPageContent();
    
    @Shadow
    protected abstract void localizePosition(BookEditScreen.Position position);
    
    @Shadow
    protected abstract void translateGlPositionToRelativePosition(BookEditScreen.Position position);
    
    @Shadow
    protected abstract int getCharacterCountInFrontOfCursor(String content, BookEditScreen.Position cursorPosition);
    
    @Shadow private int cursorIndex;
    
    @Shadow private int highlightTo;
    
    @Shadow
    protected abstract void writeString(String string);
    
    @Shadow
    protected abstract String stripFromatting(String string);
    
    @Shadow
    protected abstract String getHighlightedText();
    
    @Shadow
    protected abstract void removeHighlightedText();
    
    @Shadow private boolean signing;
    
    @Shadow
    protected abstract void setPageContent(String newContent);
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (((MenuWidgetListener) this).getMenu() != null) {
            if (signing) ((MenuWidgetListener) this).removeMenu();
            return;
        }
        if (signing) return;
        if (SlightGuiModifications.getConfig().rightClickActions && button == 1) {
            String pageContent = this.getCurrentPageContent();
            BookEditScreen.Position mousePos = ((BookEditScreen) (Object) this).new Position((int) mouseX, (int) mouseY);
            this.translateGlPositionToRelativePosition(mousePos);
            this.localizePosition(mousePos);
            int index = this.getCharacterCountInFrontOfCursor(pageContent, mousePos);
            if (cursorIndex - highlightTo != 0 && index >= Math.min(cursorIndex, highlightTo) && index <= Math.max(cursorIndex, highlightTo)) {
                ((MenuWidgetListener) this).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createSelectingMenu()));
            } else {
                this.highlightTo = this.cursorIndex = index;
                ((MenuWidgetListener) this).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createNonSelectingMenu()));
            }
        }
    }
    
    @Unique
    private void removeSelfMenu() {
        ((MenuWidgetListener) this).removeMenu();
    }
    
    @Unique
    private List<MenuEntry> createNonSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.translate("text.slightguimodifications.paste"), () -> {
                    this.writeString(this.stripFromatting(Formatting.strip(this.minecraft.keyboard.getClipboard().replaceAll("\\r", ""))));
                    this.highlightTo = this.cursorIndex;
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.highlightTo = 0;
                    this.cursorIndex = getCurrentPageContent().length();
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.translate("text.slightguimodifications.copy"), () -> {
                    this.minecraft.keyboard.setClipboard(this.getHighlightedText());
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.cut"), () -> {
                    this.minecraft.keyboard.setClipboard(this.getHighlightedText());
                    this.removeHighlightedText();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.paste"), () -> {
                    this.writeString(this.stripFromatting(Formatting.strip(this.minecraft.keyboard.getClipboard().replaceAll("\\r", ""))));
                    this.highlightTo = this.cursorIndex;
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.highlightTo = 0;
                    this.cursorIndex = getCurrentPageContent().length();
                    removeSelfMenu();
                })
        
        );
    }
}
