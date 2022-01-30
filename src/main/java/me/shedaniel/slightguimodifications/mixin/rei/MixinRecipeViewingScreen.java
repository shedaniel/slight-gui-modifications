package me.shedaniel.slightguimodifications.mixin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.rei.impl.client.gui.screen.DefaultDisplayViewingScreen;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("UnstableApiUsage")
@Mixin(DefaultDisplayViewingScreen.class)
public class MixinRecipeViewingScreen extends Screen {
    protected MixinRecipeViewingScreen(Component title) {
        super(title);
    }
    
    @Redirect(method = "render",
              at = @At(value = "INVOKE",
                       target = "Lme/shedaniel/rei/impl/client/gui/screen/DefaultDisplayViewingScreen;renderBackground(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
                       ordinal = 0))
    private void fillGradient(DefaultDisplayViewingScreen screen, PoseStack matrices) {
        int left = 0;
        int top = 0;
        int right = width;
        int bottom = height;
        int color1 = -1072689136, color2 = -804253680;
        if (screen instanceof AnimationListener listener) {
            if (listener.slightguimodifications_getAnimationState() == 2) {
                float alpha = listener.slightguimodifications_getEasedYOffset();
                listener.slightguimodifications_setAnimationState(0);
                if (alpha >= 0) {
                    SlightGuiModifications.backgroundTint = Math.min(SlightGuiModifications.backgroundTint + minecraft.getDeltaFrameTime() * 8, SlightGuiModifications.getSpeed() / 20f);
                    float f = Math.min(SlightGuiModifications.backgroundTint / SlightGuiModifications.getSpeed() * 20f, 1f);
                    fillGradient(matrices, top, SlightGuiModifications.reverseYAnimation(left), right, SlightGuiModifications.reverseYAnimation(bottom),
                            color1 & 16777215 | Mth.ceil(f * (float) (color1 >> 24 & 255)) << 24,
                            color2 & 16777215 | Mth.ceil(f * (float) (color2 >> 24 & 255)) << 24);
                } else fillGradient(matrices, top, left, right, bottom, color1, color2);
                listener.slightguimodifications_setAnimationState(2);
            } else fillGradient(matrices, top, left, right, bottom, color1, color2);
        } else fillGradient(matrices, top, left, right, bottom, color1, color2);
    }
}
