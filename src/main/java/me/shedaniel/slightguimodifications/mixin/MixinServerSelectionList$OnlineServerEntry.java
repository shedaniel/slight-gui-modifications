package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.ImmutableList;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.SplitterMenuEntry;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class MixinServerSelectionList$OnlineServerEntry {
    @Shadow @Final private Minecraft minecraft;
    
    @Shadow @Final private JoinMultiplayerScreen screen;
    
    @Shadow
    public abstract ServerData getServerData();
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void preMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (SlightGuiModifications.getGuiConfig().rightClickActions && button == 1) {
            this.screen.setSelected((ServerSelectionList.OnlineServerEntry) (Object) this);
            ((MenuWidgetListener) screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2),
                    ImmutableList.of(
                            new TextMenuEntry(I18n.get("selectServer.edit"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                ServerData serverInfo = getServerData();
                                screen.editingServer = new ServerData(serverInfo.name, serverInfo.ip, false);
                                screen.editingServer.copyFrom(serverInfo);
                                this.minecraft.setScreen(new EditServerScreen(screen, screen::editServerCallback, screen.editingServer));
                            }),
                            new TextMenuEntry(I18n.get("selectWorld.delete"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                String string = getServerData().name;
                                if (string != null) {
                                    Component text = new TranslatableComponent("selectServer.deleteQuestion");
                                    Component text2 = new TranslatableComponent("selectServer.deleteWarning", string);
                                    Component string2 = new TranslatableComponent("selectServer.deleteButton");
                                    Component string3 = new TranslatableComponent("gui.cancel");
                                    this.minecraft.setScreen(new ConfirmScreen(screen::deleteCallback, text, text2, string2, string3));
                                }
                            }),
                            new SplitterMenuEntry(),
                            new TextMenuEntry(I18n.get("selectServer.select"), () -> {
                                ((MenuWidgetListener) screen).removeMenu();
                                ServerData serverData = getServerData();
                                ConnectScreen.startConnecting(this.screen, this.minecraft, ServerAddress.parseString(serverData.ip), serverData);
                            })
                    )
            ));
            cir.cancel();
        }
    }
}
