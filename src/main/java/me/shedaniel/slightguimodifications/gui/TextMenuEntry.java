package me.shedaniel.slightguimodifications.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.Collections;
import java.util.List;

public class TextMenuEntry extends MenuEntry {
    public final String text;
    private int x, y, width;
    private boolean selected, containsMouse, rendering;
    private int textWidth = -69;
    private Runnable runnable;
    
    public TextMenuEntry(String string, Runnable runnable) {
        this.text = I18n.translate(string);
        this.runnable = runnable;
    }
    
    private int getTextWidth() {
        if (textWidth == -69) this.textWidth = Math.max(0, MinecraftClient.getInstance().textRenderer.getStringWidth(text));
        return this.textWidth;
    }
    
    @Override
    public int getEntryWidth() {return getTextWidth() + 4;}
    
    @Override
    public int getEntryHeight() {return 12;}
    
    @Override
    public List<? extends Element> children() {return Collections.emptyList();}
    
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
    public void render(int mouseX, int mouseY, float delta) {
        if (selected) fill(x, y, x + width, y + 12, -12237499);
        MinecraftClient.getInstance().textRenderer.draw(text, x + 2, y + 2, selected ? 16777215 : 8947848);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (rendering && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + 12) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            runnable.run();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}