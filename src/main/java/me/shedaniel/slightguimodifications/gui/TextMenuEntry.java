package me.shedaniel.slightguimodifications.gui;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public class TextMenuEntry extends MenuEntry {
    public final String text;
    private int x, y, width;
    private boolean selected, containsMouse, rendering;
    private int textWidth = -69;
    private Runnable runnable;
    
    public TextMenuEntry(String string, Runnable runnable) {
        this.text = I18n.get(string);
        this.runnable = runnable;
    }
    
    private int getTextWidth() {
        if (textWidth == -69) this.textWidth = Math.max(0, Minecraft.getInstance().font.width(text));
        return this.textWidth;
    }
    
    @Override
    public int getEntryWidth() {return getTextWidth() + 4;}
    
    @Override
    public int getEntryHeight() {return 12;}
    
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (selected) graphics.fill(x, y, x + width, y + 12, -12237499);
        graphics.drawString(Minecraft.getInstance().font, text, x + 2, y + 2, selected ? 16777215 : 8947848, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (rendering && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + 12) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            runnable.run();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}