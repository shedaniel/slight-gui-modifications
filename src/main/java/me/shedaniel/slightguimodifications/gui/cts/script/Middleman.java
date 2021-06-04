package me.shedaniel.slightguimodifications.gui.cts.script;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.realmsclient.RealmsMainScreen;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.cts.Position;
import me.shedaniel.slightguimodifications.gui.cts.elements.Text;
import me.shedaniel.slightguimodifications.gui.cts.elements.WidgetElement;
import me.shedaniel.slightguimodifications.gui.cts.widgets.CustomizedButtonWidget;
import me.shedaniel.slightguimodifications.gui.cts.widgets.LabelWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Middleman {
    public static Text modMenuText() {
        return Text.literal(SlightGuiModifications.getModMenuText());
    }
    
    public static void url(String string) {
        Minecraft client = Minecraft.getInstance();
        Screen screen = client.screen;
        client.setScreen(new ConfirmLinkScreen(open -> {
            if (open) {
                Util.getPlatform().openUri(string);
            }
            client.setScreen(screen);
        }, string, false));
    }
    
    public static void language() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(new LanguageSelectScreen(client.screen, client.options, client.getLanguageManager()));
    }
    
    public static void options() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(new OptionsScreen(client.screen, client.options));
    }
    
    public static void exit() {
        Minecraft.getInstance().stop();
    }
    
    public static void accessibility() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(new AccessibilityOptionsScreen(client.screen, client.options));
    }
    
    public static void singleplayer() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(new SelectWorldScreen(client.screen));
    }
    
    public static void multiplayer() {
        Minecraft client = Minecraft.getInstance();
        if (client.options.skipMultiplayerWarning) {
            client.setScreen(new JoinMultiplayerScreen(client.screen));
        } else {
            client.setScreen(new SafetyScreen(client.screen));
        }
    }
    
    public static void realms() {
        Minecraft client = Minecraft.getInstance();
        client.setScreen(new RealmsMainScreen(client.screen));
    }
    
    public static void reloadCts() {
        SlightGuiModifications.resetCts();
        SlightGuiModifications.reloadCts();
        Minecraft.getInstance().setScreen(new TitleScreen());
    }
    
    public static WidgetElement buildFromLabel(LabelBuilder builder) {
        return screen -> {
            Window window = Minecraft.getInstance().getWindow();
            Position position = builder.getPositionBuilt();
            int alignInt = -1;
            if (builder.getAlign().equals("left")) alignInt = 0;
            if (builder.getAlign().equals("center")) alignInt = 1;
            if (builder.getAlign().equals("right")) alignInt = 2;
            if (alignInt == -1) throw new IllegalArgumentException("Illegal alignment: $align");
            return new LabelWidget(position.getX(window.getGuiScaledWidth()), position.getY(window.getGuiScaledHeight()), alignInt, builder.getText(), builder.getColor(), builder.getHoveredColor(), builder.isShadow(), builder.getOnClicked());
        };
    }
    
    public interface LabelBuilder {
        String getAlign();
        
        Position getPositionBuilt();
        
        Text getText();
        
        int getColor();
        
        int getHoveredColor();
        
        boolean isShadow();
        
        Runnable getOnClicked();
    }
    
    public static WidgetElement buildFromButton(ButtonBuilder builder) {
        return screen -> {
            Window window = Minecraft.getInstance().getWindow();
            Position position = builder.getPositionBuilt();
            double alignInt = -1.0;
            if (builder.getAlign().equals("left")) alignInt = 0;
            if (builder.getAlign().equals("center")) alignInt = -0.5;
            if (builder.getAlign().equals("right")) alignInt = -1;
            if (alignInt == -1.0) throw new IllegalArgumentException("Illegal alignment: $align");
            return new CustomizedButtonWidget((int) (position.getX(window.getGuiScaledWidth()) + builder.getWidth() * alignInt), position.getY(window.getGuiScaledHeight()), builder.getWidth(), builder.getHeight(), builder.getText().unwrap(), button -> builder.getOnClicked().run(), Suppliers.memoize(builder.getTexture()::provide), Suppliers.memoize(builder.getHoveredTexture()::provide));
        };
    }
    
    public interface ButtonBuilder {
        String getAlign();
        
        Position getPositionBuilt();
        
        Text getText();
        
        int getWidth();
        
        int getHeight();
        
        Runnable getOnClicked();
        
        SlightGuiModificationsConfig.Cts.TextureProvider getTexture();
        
        SlightGuiModificationsConfig.Cts.TextureProvider getHoveredTexture();
    }
    
    public static SlightGuiModificationsConfig.Cts.TextureProvider file(String file) {
        return new FileTextureProvider(FabricLoader.getInstance().getGameDir().resolve(file));
    }
    
    public static SlightGuiModificationsConfig.Cts.TextureProvider resource(String file) {
        return new ResourceTextureProvider(file);
    }
    
    static class ResourceTextureProvider implements SlightGuiModificationsConfig.Cts.TextureProvider {
        private ResourceLocation identifier;
        
        ResourceTextureProvider(String identifier) {
            this.identifier = new ResourceLocation(identifier);
        }
        
        @Override
        public ResourceLocation provide() {
            return identifier;
        }
    }
    
    static class FileTextureProvider implements SlightGuiModificationsConfig.Cts.TextureProvider {
        private Path file;
        
        FileTextureProvider(Path file) {
            this.file = file;
        }
        
        @Override
        public ResourceLocation provide() {
            try {
                if (!Files.exists(file)) throw new NoSuchFileException(file.toAbsolutePath().toString());
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                return textureManager.register(file.getFileName().toString() + "_" + Files.size(file), new DynamicTexture(NativeImage.read(new FileInputStream(file.normalize().toFile()))));
            } catch (IOException e) {
                SlightGuiModifications.LOGGER.error("Failed to load image at " + file.toAbsolutePath(), e);
                return new ResourceLocation("missingno");
            }
        }
    }
}
