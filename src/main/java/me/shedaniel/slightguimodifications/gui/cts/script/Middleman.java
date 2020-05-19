package me.shedaniel.slightguimodifications.gui.cts.script;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.cts.Position;
import me.shedaniel.slightguimodifications.gui.cts.elements.Text;
import me.shedaniel.slightguimodifications.gui.cts.elements.WidgetElement;
import me.shedaniel.slightguimodifications.gui.cts.widgets.LabelWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.options.AccessibilityScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Window;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Middleman {
    public static Text modMenuText() {
        return Text.translatable("modmenu.title").append(" ").append(Text.translatable("modmenu.loaded", SlightGuiModifications.getModMenuModsCount()));
    }
    
    public static void url(String string) {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen screen = client.currentScreen;
        client.openScreen(new ConfirmChatLinkScreen(open -> {
            if (open) {
                Util.getOperatingSystem().open(string);
            }
            client.openScreen(screen);
        }, string, false));
    }
    
    public static void language() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.openScreen(new LanguageOptionsScreen(client.currentScreen, client.options, client.getLanguageManager()));
    }
    
    public static void options() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.openScreen(new SettingsScreen(client.currentScreen, client.options));
    }
    
    public static void exit() {
        MinecraftClient.getInstance().scheduleStop();
    }
    
    public static void accessibility() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.openScreen(new AccessibilityScreen(client.currentScreen, client.options));
    }
    
    public static void singleplayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.openScreen(new SelectWorldScreen(client.currentScreen));
    }
    
    public static void multiplayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.skipMultiplayerWarning) {
            client.openScreen(new MultiplayerScreen(client.currentScreen));
        } else {
            client.openScreen(new MultiplayerWarningScreen(client.currentScreen));
        }
    }
    
    public static void realms() {
        MinecraftClient client = MinecraftClient.getInstance();
        RealmsBridge realmsBridge = new RealmsBridge();
        realmsBridge.switchToRealms(client.currentScreen);
    }
    
    public static void reloadCts() {
        SlightGuiModifications.reloadCts();
        MinecraftClient.getInstance().openScreen(new TitleScreen());
    }
    
    public static WidgetElement buildFromLabel(LabelBuilder builder) {
        return screen -> {
            Window window = MinecraftClient.getInstance().getWindow();
            Position position = builder.getPositionBuilt();
            int alignInt = -1;
            if (builder.getAlign().equals("left")) alignInt = 0;
            if (builder.getAlign().equals("center")) alignInt = 1;
            if (builder.getAlign().equals("right")) alignInt = 2;
            if (alignInt == -1) throw new IllegalArgumentException("Illegal alignment: $align");
            return new LabelWidget(position.getX(window.getScaledWidth()), position.getY(window.getScaledHeight()), alignInt, builder.getText().asFormattedString(), builder.getColor(), builder.getHoveredColor(), builder.getOnClicked());
        };
    }
    
    public interface LabelBuilder {
        String getAlign();
        
        Position getPositionBuilt();
        
        Text getText();
        
        int getColor();
        
        int getHoveredColor();
        
        Runnable getOnClicked();
    }
    
    public static WidgetElement buildFromButton(ButtonBuilder builder) {
        return screen -> {
            Window window = MinecraftClient.getInstance().getWindow();
            Position position = builder.getPositionBuilt();
            double alignInt = -1.0;
            if (builder.getAlign().equals("left")) alignInt = 0;
            if (builder.getAlign().equals("center")) alignInt = -0.5;
            if (builder.getAlign().equals("right")) alignInt = -1;
            if (alignInt == -1.0) throw new IllegalArgumentException("Illegal alignment: $align");
            return new ButtonWidget((int) (position.getX(window.getScaledWidth()) + builder.getWidth() * alignInt), position.getY(window.getScaledHeight()), builder.getWidth(), builder.getHeight(), builder.getText().asFormattedString(), button -> builder.getOnClicked().run());
        };
    }
    
    public interface ButtonBuilder {
        String getAlign();
        
        Position getPositionBuilt();
        
        Text getText();
        
        int getWidth();
        
        int getHeight();
        
        Runnable getOnClicked();
    }
    
    public static SlightGuiModificationsConfig.Cts.TextureProvider file(String file) {
        return new FileTextureProvider(new File(FabricLoader.getInstance().getGameDirectory(), file));
    }
    
    public static SlightGuiModificationsConfig.Cts.TextureProvider resource(String file) {
        return new ResourceTextureProvider(file);
    }
    
    static class ResourceTextureProvider implements SlightGuiModificationsConfig.Cts.TextureProvider {
        private Identifier identifier;
        
        ResourceTextureProvider(String identifier) {
            this.identifier = new Identifier(identifier);
        }
        
        @Override
        public Identifier provide() {
            return identifier;
        }
    }
    
    static class FileTextureProvider implements SlightGuiModificationsConfig.Cts.TextureProvider {
        private File file;
        
        FileTextureProvider(File file) {
            this.file = file;
        }
        
        @Override
        public Identifier provide() {
            try {
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                return textureManager.registerDynamicTexture(file.getName() + "_" + file.length(), new NativeImageBackedTexture(NativeImage.read(new FileInputStream(file.toPath().normalize().toFile()))));
            } catch (IOException e) {
                e.printStackTrace();
                return new Identifier("missingno");
            }
        }
    }
}
