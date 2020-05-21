package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuEntry;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
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
    protected MixinBookEditScreen(Text title) {
        super(title);
    }
    
    @Shadow private boolean signing;
    
    @Shadow
    protected abstract BookEditScreen.class_5234 method_27582(BookEditScreen.class_5234 arg);
    
    @Shadow
    protected abstract BookEditScreen.class_5233 method_27576();
    
    @Shadow @Final private SelectionManager field_24269;
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (((MenuWidgetListener) this).getMenu() != null) {
            if (signing) ((MenuWidgetListener) this).removeMenu();
            return;
        }
        if (signing) return;
        if (SlightGuiModifications.getGuiConfig().rightClickActions && button == 1) {
            BookEditScreen.class_5233 lv = this.method_27576();
            BookEditScreen.class_5234 mousePos = new BookEditScreen.class_5234((int) mouseX, (int) mouseY);
            mousePos = this.method_27582(mousePos);
            int index = lv.method_27602(textRenderer, mousePos);
            if (field_24269.method_27568() && index >= Math.min(field_24269.getSelectionStart(), field_24269.getSelectionEnd()) && index <= Math.max(field_24269.getSelectionStart(), field_24269.getSelectionEnd())) {
                ((MenuWidgetListener) this).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2), createSelectingMenu()));
            } else {
                field_24269.method_27548(index, index);
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
                    this.field_24269.paste();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.field_24269.selectAll();
                    removeSelfMenu();
                })
        );
    }
    
    @Unique
    private List<MenuEntry> createSelectingMenu() {
        return ImmutableList.of(
                new TextMenuEntry(I18n.translate("text.slightguimodifications.copy"), () -> {
                    this.field_24269.copy();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.cut"), () -> {
                    this.field_24269.cut();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.paste"), () -> {
                    this.field_24269.paste();
                    removeSelfMenu();
                }),
                new TextMenuEntry(I18n.translate("text.slightguimodifications.selectAll"), () -> {
                    this.field_24269.selectAll();
                    removeSelfMenu();
                })
        );
    }
}
