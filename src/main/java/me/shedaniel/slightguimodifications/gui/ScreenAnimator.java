package me.shedaniel.slightguimodifications.gui;

import net.minecraft.util.Mth;

import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;

public class ScreenAnimator {
    public static final Animator<Float> ALPHA = new Animator<>(() -> 1f, d -> Mth.clamp(d, 0, 1));
    
    public static class Animator<T extends Number> {
        private final Supplier<T> fullAmountGetter;
        private final DoubleUnaryOperator clamp;
        
        public Animator(Supplier<T> fullAmountGetter, DoubleUnaryOperator clamp) {
            this.fullAmountGetter = fullAmountGetter;
            this.clamp = clamp;
        }
    }
}
