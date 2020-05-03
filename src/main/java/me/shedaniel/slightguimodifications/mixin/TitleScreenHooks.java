package me.shedaniel.slightguimodifications.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenHooks {
    @Accessor("doBackgroundFade")
    boolean isDoBackgroundFade();
    
    @Accessor("backgroundFadeStart")
    long getBackgroundFadeStart();
}
