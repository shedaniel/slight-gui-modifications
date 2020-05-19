package me.shedaniel.slightguimodifications.gui.cts.elements;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

public interface WidgetElement {
    AbstractButtonWidget build(Screen screen);
}
