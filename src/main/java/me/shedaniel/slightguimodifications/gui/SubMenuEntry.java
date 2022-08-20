package me.shedaniel.slightguimodifications.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.impl.client.gui.InternalTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SubMenuEntry extends MenuEntry {
    public final String text;
    private int textWidth = -69;
    private int x, y, width;
    private boolean selected, containsMouse, rendering;
    private List<MenuEntry> entries;
    private MenuWidget menuWidget;
    
    public SubMenuEntry(String text) {this(text, Collections.emptyList());}
    
    public SubMenuEntry(String text, Supplier<List<MenuEntry>> entries) {this(text, entries.get());}
    
    public SubMenuEntry(String text, List<MenuEntry> entries) {
        this.text = text;
        this.entries = entries;
    }
    
    private int getTextWidth() {
        if (textWidth == -69) this.textWidth = Math.max(0, Minecraft.getInstance().font.width(text));
        return this.textWidth;
    }
    
    public MenuWidget getMenuWidget() {
        if (menuWidget == null) this.menuWidget = new MenuWidget(new Point(getParent().getBounds().getMaxX() - 1, y - 1), entries);
        return menuWidget;
    }
    
    @Override
    public int getEntryWidth() {return 12 + getTextWidth() + 4;}
    
    @Override
    public int getEntryHeight() {return 12;}
    
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
        if (selected) fill(matrices, x, y, x + width, y + 12, -12237499);
        if (selected && !entries.isEmpty()) {
            MenuWidget menu = getMenuWidget();
            menu.menuStartPoint.x = getParent().getBounds().getMaxX() - 1;
            menu.menuStartPoint.y = y - 1;
            List<Rectangle> areas = Lists.newArrayList(ScissorsHandler.INSTANCE.getScissorsAreas());
            ScissorsHandler.INSTANCE.clearScissors();
            menu.render(matrices, mouseX, mouseY, delta);
            for (Rectangle area : areas) ScissorsHandler.INSTANCE.scissor(area);
        }
        Minecraft.getInstance().font.draw(matrices, text, x + 2, y + 2, selected ? 16777215 : 8947848);
        if (!entries.isEmpty()) {
            RenderSystem.setShaderTexture(0, InternalTextures.CHEST_GUI_TEXTURE);
            blit(matrices, x + width - 15, y - 2, 0, 28, 18, 18);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (rendering && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + 12) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {return menuWidget != null && !menuWidget.children().isEmpty() && selected && menuWidget.mouseScrolled(mouseX, mouseY, amount);}
    
    @Override
    public List<? extends GuiEventListener> children() {
        if (menuWidget != null && !menuWidget.children().isEmpty() && selected) return Collections.singletonList(menuWidget);
        return Collections.emptyList();
    }
}