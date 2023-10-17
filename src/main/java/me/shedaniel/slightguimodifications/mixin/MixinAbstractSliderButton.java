package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSliderButton.class)
public abstract class MixinAbstractSliderButton extends AbstractWidget {
    public MixinAbstractSliderButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
    
    @Shadow
    protected abstract void setValue(double mouseX);
    
    @Shadow protected double value;

    @Shadow protected abstract ResourceLocation getSprite();

    @Shadow protected abstract ResourceLocation getHandleSprite();

    @Inject(method = "setValueFromMouse", at = @At("HEAD"), cancellable = true)
    private void setValueFromMouse(double mouseX, CallbackInfo ci) {
        SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
        if (config.sliderModifications.enabled) {
            int grabberWidth = config.sliderModifications.grabberWidth;
            this.setValue((mouseX - (double) (this.getX() + grabberWidth / 2)) / (double) (this.width - grabberWidth));
            ci.cancel();
        }
    }
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void preKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
        if (config.sliderModifications.enabled) {
            int grabberWidth = config.sliderModifications.grabberWidth;
            this.width += 8;
            this.width -= grabberWidth;
        }
    }
    
    @Inject(method = "keyPressed", at = @At("RETURN"), cancellable = true)
    private void postKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
        if (config.sliderModifications.enabled) {
            int grabberWidth = config.sliderModifications.grabberWidth;
            this.width += grabberWidth;
            this.width -= 8;
        }
    }
    
    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void renderBg(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
        if (config.sliderModifications.enabled) {
            int grabberWidth = config.sliderModifications.grabberWidth;
            if (grabberWidth != 8) {
                var handleLocation = new ResourceLocation("minecraft:textures/gui/sprites/widget/slider_handle.png");
                var handleHighlightLocation = new ResourceLocation("minecraft:textures/gui/sprites/widget/slider_handle_highlighted.png");
                var finalLocation = this.isHoveredOrFocused() ? handleHighlightLocation : handleLocation;

                RenderSystem.setShaderTexture(0, finalLocation);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                graphics.blitSprite(this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
                graphics.blitSprite(this.getHandleSprite(), this.getX() + (int) (this.value * (double) (this.width - grabberWidth)), this.getY(), grabberWidth, 20);
            }
        }
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void renderBg(GuiGraphics graphics, ResourceLocation los, int i, int j, int k, int l) {
        SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
        if (config.sliderModifications.enabled) {
            int grabberWidth = config.sliderModifications.grabberWidth;
            if (grabberWidth != 8) {
                return;
            }
        }

        //TODO
        graphics.blitSprite(los, i, j, k, l);
    }
}
