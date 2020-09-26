package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BookEditScreen.class)
public abstract class MixinBookEditScreen extends Screen {
    protected MixinBookEditScreen(Component title) {
        super(title);
    }
    
    @Shadow private boolean isSigning;
    
    @Shadow
    protected abstract BookEditScreen.Pos2i convertScreenToLocal(BookEditScreen.Pos2i arg);
    
    @Shadow
    protected abstract BookEditScreen.DisplayCache getDisplayCache();
    
    @Shadow @Final private TextFieldHelper pageEdit;
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (((MenuWidgetListener) this).getMenu() != null) {
            if (isSigning) ((MenuWidgetListener) this).removeMenu();
            return;
        }
        if (isSigning) return;
        if (SlightGuiModifications.getGuiConfig().rightClickActions && button == 1) {
            BookEditScreen.DisplayCache lv = this.getDisplayCache();
            BookEditScreen.Pos2i mousePos = new BookEditScreen.Pos2i((int) mouseX, (int) mouseY);
            mousePos = this.convertScreenToLocal(mousePos);
            int index = lv.getIndexAtPosition(font, mousePos);
            if (pageEdit.isSelecting() && index >= Math.min(pageEdit.getCursorPos(), pageEdit.getSelectionPos()) && index <= Math.max(pageEdit.getCursorPos(), pageEdit.getSelectionPos())) {
                ((MenuWidgetListener) this).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createSelectingMenu()));
            } else {
                pageEdit.setSelectionRange(index, index);
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
                new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
                    this.pageEdit.paste();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.pageEdit.selectAll();
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.get("text.slightguimodifications.copy"), () -> {
                    this.pageEdit.copy();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.cut"), () -> {
                    this.pageEdit.cut();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.paste"), () -> {
                    this.pageEdit.paste();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.get("text.slightguimodifications.selectAll"), () -> {
                    this.pageEdit.selectAll();
                    removeSelfMenu();
                })
        );
    }
}
