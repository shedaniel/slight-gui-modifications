package me.shedaniel.slightguimodifications.gui.cts.elements;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

public interface WidgetElement {
    AbstractWidget build(Screen screen);
}
