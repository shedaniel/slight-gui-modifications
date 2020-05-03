package me.shedaniel.slightguimodifications.modmenu;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.slightguimodifications.SlightGuiModifications;

public class SlightGuiModificationsMM implements ModMenuApi {
    @Override
    public String getModId() {return "slight-gui-modifications";}
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SlightGuiModifications::getConfigScreen;
    }
}
