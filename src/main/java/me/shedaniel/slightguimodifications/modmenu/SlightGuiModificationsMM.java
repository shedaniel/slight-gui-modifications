package me.shedaniel.slightguimodifications.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.slightguimodifications.SlightGuiModifications;

public class SlightGuiModificationsMM implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SlightGuiModifications::getConfigScreen;
    }
}
