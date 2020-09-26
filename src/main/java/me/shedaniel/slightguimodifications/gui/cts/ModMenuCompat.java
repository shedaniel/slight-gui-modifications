package me.shedaniel.slightguimodifications.gui.cts;

import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.gui.ModsScreen;
import net.minecraft.client.Minecraft;

public class ModMenuCompat {
    public static void openModMenu() {
        Minecraft.getInstance().setScreen(new ModsScreen(Minecraft.getInstance().screen));
    }
    
    public static String getDisplayedModCount() {
        return ModMenu.getDisplayedModCount();
    }
}
