package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;blit(IIIIFFIIII)V"))
    private void blit(int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texWidth, int texHeight) {
        int tmp = ((AnimationListener) this).slightguimodifications_getAnimationState();
        ((AnimationListener) this).slightguimodifications_setAnimationState(0);
        DrawableHelper.blit(x, y, width, height, u, v, uWidth, vHeight, texWidth, texHeight);
        ((AnimationListener) this).slightguimodifications_setAnimationState(tmp);
    }
}
