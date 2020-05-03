package me.shedaniel.slightguimodifications.listener;

import me.shedaniel.slightguimodifications.gui.MenuWidget;

public interface MenuWidgetListener {
    void removeMenu();
    
    void applyMenu(MenuWidget menu);
    
    MenuWidget getMenu();
}
