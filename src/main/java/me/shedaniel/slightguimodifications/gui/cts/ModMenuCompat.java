package me.shedaniel.slightguimodifications.gui.cts;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;

public class ModMenuCompat {
    public static void openModMenu() {
        Minecraft.getInstance().setScreen(ModMenuApi.createModsScreen(Minecraft.getInstance().screen));
    }
    
    public static String getModMenuText() {
        return ModMenuApi.createModsButtonText().getString();
    }
}
