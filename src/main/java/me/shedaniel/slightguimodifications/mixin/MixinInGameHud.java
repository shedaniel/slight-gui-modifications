package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(InGameHud.class)
public class MixinInGameHud extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledWidth;
    public double offsetBeneficial;
    public double offsetTargetBeneficial;
//    public long startBeneficial;
//    public long durationBeneficial;
    
    public double offsetNonBeneficial;
    public double offsetTargetNonBeneficial;
//    public long startNonBeneficial;
//    public long durationNonBeneficial;
    
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"))
    private void preRenderStatusEffectOverlay(CallbackInfo ci) {
        double widthBeneficial = 0;
        double widthNonBeneficial = 0;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            if (!effect.shouldShowIcon()) continue;
            if (effect.getEffectType().isBeneficial()) {
                widthBeneficial += 25;
            } else {
                widthNonBeneficial += 25;
            }
        }
        offsetTargetBeneficial = widthBeneficial;
        offsetTargetNonBeneficial = widthNonBeneficial;
    }
    
    /**
     * fuck mod compatibility
     *
     * @author shedaniel
     */
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderStatusEffectOverlay(CallbackInfo ci) {
        if (!SlightGuiModifications.getConfig().fluidStatusEffects) return;
        ci.cancel();
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (!collection.isEmpty()) {
            RenderSystem.enableBlend();
            
            double beneficialOffset = 0;
            double nonBeneficialOffset = 0;
            StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
            this.client.getTextureManager().bindTexture(ContainerScreen.BACKGROUND_TEXTURE);
            
            for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                if (statusEffectInstance.shouldShowIcon()) {
                    double[] x = {this.scaledWidth};
                    int[] y = {1};
                    if (this.client.isDemo()) {
                        y[0] += 15;
                    }
                    
                    if (statusEffect.isBeneficial()) {
                        beneficialOffset += 25 * Math.min(1.0, EasingMethod.EasingMethodImpl.EXPO.apply(statusEffectInstance.getDuration() / 10.0));
                        x[0] -= beneficialOffset;
                    } else {
                        nonBeneficialOffset += 25 * Math.min(1.0, EasingMethod.EasingMethodImpl.EXPO.apply(statusEffectInstance.getDuration() / 10.0));
                        x[0] -= nonBeneficialOffset;
                        y[0] += 26;
                    }
                    
                    RenderSystem.pushMatrix();
                    float alphaOffset = (float) Math.min(1.0, 1 - EasingMethod.EasingMethodImpl.LINEAR.apply(1 - MathHelper.clamp(statusEffectInstance.getDuration() / 10.0, 0, 1)));
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, alphaOffset);
                    RenderSystem.translated(x[0] - this.scaledWidth, 0, 0);
                    float[] alpha = {alphaOffset};
                    if (statusEffectInstance.isAmbient()) {
                        this.blit(this.scaledWidth, y[0], 165, 166, 24, 24);
                    } else {
                        this.blit(this.scaledWidth, y[0], 141, 166, 24, 24);
                        if (statusEffectInstance.getDuration() <= 200) {
                            int m = 10 - statusEffectInstance.getDuration() / 20;
                            alpha[0] = alphaOffset * MathHelper.clamp((float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F) * MathHelper.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }
                    RenderSystem.popMatrix();
                    
                    Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
                    list.add(() -> {
                        if (alpha[0] <= 0.01) return;
                        this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());
                        RenderSystem.pushMatrix();
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha[0]);
                        RenderSystem.translated(x[0] - this.scaledWidth, 0, 0);
                        blit(this.scaledWidth + 3, y[0] + 3, this.getBlitOffset(), 18, 18, sprite);
                        RenderSystem.popMatrix();
                    });
                }
            }
            
            list.forEach(Runnable::run);
            
            RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            RenderSystem.matrixMode(5889);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, client.getWindow().getFramebufferWidth() / client.getWindow().getScaleFactor(), client.getWindow().getFramebufferHeight() / client.getWindow().getScaleFactor(), 0.0D, 1000.0D, 3000.0D);
            RenderSystem.matrixMode(5888);
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
            DiffuseLighting.enableGuiDepthLighting();
        }
    }
}
