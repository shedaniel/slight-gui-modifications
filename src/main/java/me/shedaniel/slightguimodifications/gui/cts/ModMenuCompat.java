package me.shedaniel.slightguimodifications.gui.cts;

import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.gui.ModsScreen;
import net.minecraft.client.MinecraftClient;

public class ModMenuCompat {
    public static void openModMenu() {
        MinecraftClient.getInstance().openScreen(new ModsScreen(MinecraftClient.getInstance().currentScreen));
    }
    
    public static String getDisplayedModCount() {
        return ModMenu.getDisplayedModCount();
    }
}
