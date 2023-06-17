package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen implements AnimationListener {
    @Shadow protected EditBox input;
    @Unique
    private double start = -1;
    @Unique
    private double offset = -1;
    
    protected MixinChatScreen(Component title) {
        super(title);
    }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(String originalChatText, CallbackInfo ci) {
        start = SlightGuiModifications.getGuiConfig().openingAnimation.fluidChatOpening ? Util.getMillis() : -1;
    }
    
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void preRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.offset = start == -1 ? -1 : Mth.clamp((Util.getMillis() - start) / 300, 0.0, 1.0);
    }
    
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;getMessageTagAt(DD)Lnet/minecraft/client/GuiMessageTag;"))
    private void postRenderCommandSuggestor(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
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
