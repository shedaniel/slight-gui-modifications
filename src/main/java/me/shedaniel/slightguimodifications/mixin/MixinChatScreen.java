package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen implements AnimationListener {
    @Shadow protected TextFieldWidget chatField;
    @Unique
    private double start = -1;
    @Unique
    private double offset = -1;
    
    protected MixinChatScreen(Text title) {
        super(title);
    }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(String originalChatText, CallbackInfo ci) {
        start = SlightGuiModifications.getConfig().openingAnimation.fluidChatOpening ? Util.getMeasuringTimeMs() : -1;
    }
    
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void preRender(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.offset = start == -1 ? -1 : MathHelper.clamp((Util.getMeasuringTimeMs() - start) / 300, 0.0, 1.0);
    }
    
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getText(DD)Lnet/minecraft/text/Text;"))
    private void postRenderCommandSuggestor(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.offset = -1;
    }
    
    @Override
    public float slightguimodifications_getAlpha() {
        if (offset == -1) return 1;
        return Math.min((float) offset * 1.5f, 1);
    }
    
    @Override
    public float slightguimodifications_getEasedYOffset() {
        if (offset == -1) return 1;
        return 1 - (float) ((12 - EasingMethod.EasingMethodImpl.EXPO.apply(offset) * 12) * 4 / height);
    }
}
