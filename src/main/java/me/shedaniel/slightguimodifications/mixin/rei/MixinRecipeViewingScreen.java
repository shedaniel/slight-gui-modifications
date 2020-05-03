package me.shedaniel.slightguimodifications.mixin.rei;

import me.shedaniel.rei.gui.RecipeViewingScreen;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnstableApiUsage")
@Mixin(RecipeViewingScreen.class)
public class MixinRecipeViewingScreen extends Screen {
    protected MixinRecipeViewingScreen(Text title) {
        super(title);
    }
    
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/gui/RecipeViewingScreen;fillGradient(IIIIII)V", ordinal = 0))
    private void fillGradient(RecipeViewingScreen screen, int top, int left, int right, int bottom, int color1, int color2) {
        if (screen instanceof AnimationListener) {
            if (((AnimationListener) screen).slightguimodifications_getAnimationState() == 2) {
                float alpha = ((AnimationListener) screen).slightguimodifications_getEasedYOffset();
                ((AnimationListener) screen).slightguimodifications_setAnimationState(0);
                if (alpha >= 0) {
                    SlightGuiModifications.backgroundTint = Math.min(SlightGuiModifications.backgroundTint + minecraft.getLastFrameDuration() * 8, SlightGuiModifications.getSpeed() / 20f);
                    float f = Math.min(SlightGuiModifications.backgroundTint / SlightGuiModifications.getSpeed() * 20f, 1f);
                    fillGradient(top, SlightGuiModifications.reverseYAnimation(left), right, SlightGuiModifications.reverseYAnimation(bottom),
                            color1 & 16777215 | MathHelper.ceil(f * (float) (color1 >> 24 & 255)) << 24,
                            color2 & 16777215 | MathHelper.ceil(f * (float) (color2 >> 24 & 255)) << 24);
                } else fillGradient(top, left, right, bottom, color1, color2);
                ((AnimationListener) screen).slightguimodifications_setAnimationState(2);
            } else fillGradient(top, left, right, bottom, color1, color2);
        } else fillGradient(top, left, right, bottom, color1, color2);
    }
}
