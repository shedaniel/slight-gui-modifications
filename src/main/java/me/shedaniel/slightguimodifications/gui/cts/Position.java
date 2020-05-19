package me.shedaniel.slightguimodifications.gui.cts;

import java.util.function.Function;

public final class Position {
    private final Function<Integer, Integer> x;
    private final Function<Integer, Integer> y;
    
    public Position(Function<Integer, Integer> x, Function<Integer, Integer> y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX(int width) {
        return this.x.apply(width);
    }
    
    public int getY(int height) {
        return this.y.apply(height);
    }
}
