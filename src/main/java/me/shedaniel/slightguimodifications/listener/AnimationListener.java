package me.shedaniel.slightguimodifications.listener;

import net.minecraft.client.gui.screen.Screen;

public interface AnimationListener {
    float slightguimodifications_getEasedMouseY();
    
    void slightguimodifications_startRendering();
    
    void slightguimodifications_stopRendering();
    
    void slightguimodifications_openScreen(Screen lastScreen);
    
    void slightguimodifications_reset();
    
    float slightguimodifications_getAlpha();
    
    float slightguimodifications_getEasedYOffset();
    
    int slightguimodifications_getAnimationState();
    
    void slightguimodifications_setAnimationState(int stage);
}
