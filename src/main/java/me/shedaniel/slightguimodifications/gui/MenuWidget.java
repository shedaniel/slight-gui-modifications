package me.shedaniel.slightguimodifications.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import me.shedaniel.clothconfig2.api.ScissorsHandler;
import me.shedaniel.clothconfig2.api.ScrollingContainer;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class MenuWidget extends AbstractContainerEventHandler implements Renderable {
    public final Point menuStartPoint;
    private final List<MenuEntry> entries = Lists.newArrayList();
    public final ScrollingContainer scrolling = new ScrollingContainer() {
        @Override
        public int getMaxScrollHeight() {
            int i = 0;
            for (MenuEntry entry : children()) i += entry.getEntryHeight();
            return i;
        }
        
        @Override
        public Rectangle getBounds() {
            return MenuWidget.this.getInnerBounds();
        }
        
        @Override
        public boolean hasScrollBar() {
            return MenuWidget.this.hasScrollBar();
        }
    };
    
    public MenuWidget(Point menuStartPoint, Collection<MenuEntry> entries) {
        buildEntries(entries);
        if (menuStartPoint.y + scrolling.getMaxScrollHeight() >= Minecraft.getInstance().screen.height - 5)
            menuStartPoint.y -= scrolling.getMaxScrollHeight();
        this.menuStartPoint = menuStartPoint;
    }
    
    @SuppressWarnings("deprecation")
    private void buildEntries(Collection<MenuEntry> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
        for (MenuEntry entry : this.entries) entry.parent = this;
    }
    
    public @NotNull Rectangle getBounds() {return new Rectangle(menuStartPoint.x, menuStartPoint.y, getMaxEntryWidth() + 2 + (hasScrollBar() ? 6 : 0), getInnerHeight() + 2);}
    
    public Rectangle getInnerBounds() {return new Rectangle(menuStartPoint.x + 1, menuStartPoint.y + 1, getMaxEntryWidth() + (hasScrollBar() ? 6 : 0), getInnerHeight());}
    
    public boolean hasScrollBar() {return scrolling.getMaxScrollHeight() > getInnerHeight();}
    
    public int getInnerHeight() {return Math.min(scrolling.getMaxScrollHeight(), Minecraft.getInstance().screen.height - 5 - menuStartPoint.y);}
    
    public int getMaxEntryWidth() {
        int i = 0;
        for (MenuEntry entry : children()) if (entry.getEntryWidth() > i) i = entry.getEntryWidth();
        return Math.max(10, i);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Rectangle bounds = getBounds();
        Rectangle innerBounds = getInnerBounds();
        graphics.fill(bounds.x, bounds.y, bounds.getMaxX(), bounds.getMaxY(), -6250336);
        graphics.fill(innerBounds.x, innerBounds.y, innerBounds.getMaxX(), innerBounds.getMaxY(), -16777216);
        boolean contains = innerBounds.contains(mouseX, mouseY);
        MenuEntry focused = getFocused() instanceof MenuEntry ? (MenuEntry) getFocused() : null;
        int currentY = (int) (innerBounds.y - scrolling.scrollAmount);
        for (MenuEntry child : children()) {
            boolean containsMouse = contains && mouseY >= currentY && mouseY < currentY + child.getEntryHeight();
            if (containsMouse) focused = child;
            currentY += child.getEntryHeight();
        }
        currentY = (int) (innerBounds.y - scrolling.scrollAmount);
        ScissorsHandler.INSTANCE.scissor(new Rectangle(scrolling.getScissorBounds()));
        for (MenuEntry child : children()) {
            boolean rendering = currentY + child.getEntryHeight() >= innerBounds.y && currentY <= innerBounds.getMaxY();
            boolean containsMouse = contains && mouseY >= currentY && mouseY < currentY + child.getEntryHeight();
            child.updateInformation(innerBounds.x, currentY, focused == child || containsMouse, containsMouse, rendering, getMaxEntryWidth());
            if (rendering) child.render(graphics, mouseX, mouseY, delta);
            currentY += child.getEntryHeight();
        }
        ScissorsHandler.INSTANCE.removeLastScissor();
        setFocused(focused);
        scrolling.renderScrollBar(graphics);
        scrolling.updatePosition(delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrolling.updateDraggingState(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrolling.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double g) {
        if (getInnerBounds().contains(mouseX, mouseY)) {
            scrolling.offset(ClothConfigInitializer.getScrollStep() * -amount, true);
            return true;
        }
        for (MenuEntry child : children()) if (child instanceof SubMenuEntry && child.mouseScrolled(mouseX, mouseY, amount, g)) return true;
        return super.mouseScrolled(mouseX, mouseY, amount, g);
    }
    
    @Override
    public List<MenuEntry> children() {return entries;}
}
