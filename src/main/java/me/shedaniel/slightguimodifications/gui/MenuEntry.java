package me.shedaniel.slightguimodifications.gui;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;

public abstract class MenuEntry extends AbstractContainerEventHandler implements Widget {
    @Deprecated MenuWidget parent = null;
    
    public final MenuWidget getParent() {return parent;}
    
    public abstract int getEntryWidth();
    
    public abstract int getEntryHeight();
    
    public abstract void updateInformation(int xPos, int yPos, boolean selected, boolean containsMouse, boolean rendering, int width);
}
