package me.shedaniel.slightguimodifications.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.components.events.GuiEventListener;

public class SplitterMenuEntry extends MenuEntry {
    private int x, y, width;
    private boolean selected, containsMouse, rendering;
    
    @Override
    public int getEntryWidth() {return 0;}
    
    @Override
    public int getEntryHeight() {return 5;}
    
    @Override
    public List<? extends GuiEventListener> children() {return Collections.emptyList();}
    
    @Override
    public void updateInformation(int xPos, int yPos, boolean selected, boolean containsMouse, boolean rendering, int width) {
        this.x = xPos;
        this.y = yPos;
        this.selected = selected;
        this.containsMouse = containsMouse;
        this.rendering = rendering;
        this.width = width;
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        fillGradient(matrices, x + 3, y + 2, x + width - 3, y + 3, -7829368, -7829368);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (rendering && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + 5) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
