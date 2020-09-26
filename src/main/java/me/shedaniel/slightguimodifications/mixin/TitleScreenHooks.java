package me.shedaniel.slightguimodifications.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenHooks {
    @Accessor("fading")
    boolean isDoBackgroundFade();
    
    @Accessor("fadeInStart")
    long getBackgroundFadeStart();
}
