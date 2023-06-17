package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.SplitterMenuEntry;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.resources.language.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class MixinWorldSelectionList$WorldListEntry {
    @Shadow @Final private SelectWorldScreen screen;
    
    @Shadow
    public abstract void deleteWorld();
    
    @Shadow
    public abstract void editWorld();
    
    @Shadow
    public abstract void recreateWorld();
    
    @Shadow
    public abstract void joinWorld();
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (SlightGuiModifications.getGuiConfig().rightClickActions && button == 1) {
            screen.list.setSelected((WorldSelectionList.WorldListEntry) (Object) this);
            ((MenuWidgetListener) screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2),
                    ImmutableList.of(
                            new TextMenuEntry(I18n.get("selectWorld.delete"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                deleteWorld();
                            }),
                            new TextMenuEntry(I18n.get("selectWorld.edit"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                editWorld();
                            }),
                            new TextMenuEntry(I18n.get("selectWorld.recreate"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                recreateWorld();
                            }),
                            new SplitterMenuEntry(),
                            new TextMenuEntry(I18n.get("selectWorld.select"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                joinWorld();
                            })
                    )
            ));
            cir.cancel();
        }
    }
}
