package me.shedaniel.slightguimodifications.gui.scale;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.IntSupplier;

public class GuiScaleSliderButton extends AbstractSliderButton {
    private final CycleOption option;
    private final Options options;
    private final IntSupplier maxScale;
    private long lastActive = -1;
    
    public GuiScaleSliderButton(CycleOption option, Options options, int i, int j, int k, int l, Component component, IntSupplier maxScale, double d) {
        super(i, j, k, l, component, d);
        this.option = option;
        this.options = options;
        this.maxScale = maxScale;
    }
    
    @Override
    protected void updateMessage() {
        int s = this.options.guiScale;
        this.options.guiScale = Math.round((float) value * maxScale.getAsInt());
        this.setMessage(this.option.getMessage(options));
        this.options.guiScale = s;
    }
    
    @Override
    protected void applyValue() {
    }
    
    @Override
    public void onRelease(double d, double e) {
        super.onRelease(d, e);
        applyNew();
    }
    
    @Override
    protected void onDrag(double d, double e, double f, double g) {
        super.onDrag(d, e, f, g);
        this.lastActive = Util.getMillis();
    }
    
    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        
        if (lastActive >= 0 && Util.getMillis() - this.lastActive > 500 && !isHovered) {
            applyNew();
        }
    }
    
    @Override
    public boolean keyPressed(int i, int j, int k) {
        boolean bl = i == 263;
        if (bl || i == 262) {
            float f = bl ? -1.0F : 1.0F;
            this.value = Mth.clamp((double) (Math.round((float) value * maxScale.getAsInt()) + f) / maxScale.getAsInt(), 0.0D, 1.0D);
            this.updateMessage();
        }
        
        applyNew();
        return false;
    }
    
    private void applyNew() {
        this.lastActive = -1;
        int s = Math.round((float) value * maxScale.getAsInt());
        
        if (s != this.options.guiScale) {
            this.options.guiScale = s;
            this.options.save();
            Minecraft.getInstance().resizeDisplay();
        }
    }
}
