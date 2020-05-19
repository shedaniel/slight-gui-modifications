package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.api.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.SplitterMenuEntry;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinMultiplayerServerListWidget$ServerEntry {
    @Shadow @Final private MinecraftClient client;
    
    @Shadow @Final private MultiplayerScreen screen;
    
    @Shadow
    public abstract ServerInfo getServer();
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (SlightGuiModifications.getGuiConfig().rightClickActions && button == 1) {
            this.screen.select((MultiplayerServerListWidget.ServerEntry) (Object) this);
            ((MenuWidgetListener) screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2),
                    ImmutableList.of(
                            new TextMenuEntry(I18n.translate("selectServer.edit"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                ServerInfo serverInfo = getServer();
                                screen.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
                                screen.selectedEntry.copyFrom(serverInfo);
                                this.client.openScreen(new AddServerScreen(screen, screen::editEntry, screen.selectedEntry));
                            }),
                            new TextMenuEntry(I18n.translate("selectWorld.delete"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                String string = getServer().name;
                                if (string != null) {
                                    Text text = new TranslatableText("selectServer.deleteQuestion");
                                    Text text2 = new TranslatableText("selectServer.deleteWarning", string);
                                    String string2 = I18n.translate("selectServer.deleteButton");
                                    String string3 = I18n.translate("gui.cancel");
                                    this.client.openScreen(new ConfirmScreen(screen::removeEntry, text, text2, string2, string3));
                                }
                            }),
                            new SplitterMenuEntry(),
                            new TextMenuEntry(I18n.translate("selectServer.select"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                this.client.openScreen(new ConnectScreen(screen, this.client, getServer()));
                            })
                    )
            ));
            cir.cancel();
        }
    }
}
