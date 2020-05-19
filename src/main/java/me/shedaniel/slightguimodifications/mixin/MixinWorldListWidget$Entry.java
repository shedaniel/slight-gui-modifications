package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.api.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.SplitterMenuEntry;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldListWidget.Entry.class)
public abstract class MixinWorldListWidget$Entry {
    @Shadow @Final private SelectWorldScreen screen;
    
    @Shadow
    public abstract void delete();
    
    @Shadow
    public abstract void edit();
    
    @Shadow
    public abstract void recreate();
    
    @Shadow
    public abstract void play();
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (SlightGuiModifications.getGuiConfig().rightClickActions && button == 1) {
            screen.levelList.setSelected((WorldListWidget.Entry) (Object) this);
            this.screen.worldSelected(screen.levelList.method_20159().isPresent());
            ((MenuWidgetListener) screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2),
                    ImmutableList.of(
                            new TextMenuEntry(I18n.translate("selectWorld.delete"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                delete();
                            }),
                            new TextMenuEntry(I18n.translate("selectWorld.edit"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                edit();
                            }),
                            new TextMenuEntry(I18n.translate("selectWorld.recreate"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                recreate();
                            }),
                            new SplitterMenuEntry(),
                            new TextMenuEntry(I18n.translate("selectWorld.select"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                play();
                            })
                    )
            ));
            cir.cancel();
        }
    }
}
