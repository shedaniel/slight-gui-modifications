package me.shedaniel.slightguimodifications.listener;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;

public class CoolOptionInstanceSliderButton<T> extends OptionInstance.OptionInstanceSliderButton<T> {
    private final OptionInstance<T> instance;
    private final OptionInstance.SliderableValueSet<T> sliderableValueSet;
    private long lastActive = -1;
    
    public CoolOptionInstanceSliderButton(Options options, int i, int j, int k, int l, OptionInstance<T> instance, OptionInstance.SliderableValueSet<T> sliderableValueSet, OptionInstance.TooltipSupplier<T> tooltipSupplier) {
        super(options, i, j, k, l, instance, sliderableValueSet, tooltipSupplier);
        this.instance = instance;
        this.sliderableValueSet = sliderableValueSet;
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
            this.setValue(this.value + (double) (f / (float) (this.width - 8)));
            this.updateMessage();
        }
        
        applyNew();
        return false;
    }
    
    private void setValue(double d) {
        double e = this.value;
        this.value = Mth.clamp(d, 0.0, 1.0);
        if (e != this.value) {
            this.applyValue();
        }
        
        this.updateMessage();
    }
    
    private void applyNew() {
        this.lastActive = -1;
        OptionInstance.ClampingLazyMaxIntRange set = (OptionInstance.ClampingLazyMaxIntRange) this.sliderableValueSet;
        Integer nv = (int) Math.round(Mth.map(this.value, 0.0, 1.0, set.minInclusive(), set.maxInclusive()));
        Integer old = (Integer) this.instance.get();
        this.instance.set((T) nv);
        this.options.save();
        if (!old.equals(instance.get())) {
            Minecraft.getInstance().resizeDisplay();
        }
        setValue(sliderableValueSet.toSliderValue(instance.get()));
    }
}
