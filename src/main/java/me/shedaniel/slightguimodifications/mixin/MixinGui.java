package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(Gui.class)
public class MixinGui extends GuiComponent {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private int screenWidth;
    public double offsetBeneficial;
    public double offsetTargetBeneficial;
    
    public double offsetNonBeneficial;
    public double offsetTargetNonBeneficial;
    
    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void preRenderStatusEffectOverlay(CallbackInfo ci) {
        double widthBeneficial = 0;
        double widthNonBeneficial = 0;
        for (MobEffectInstance effect : minecraft.player.getActiveEffects()) {
            if (!effect.showIcon()) continue;
            if (effect.getEffect().isBeneficial()) {
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
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderStatusEffectOverlay(PoseStack matrices, CallbackInfo ci) {
        if (!SlightGuiModifications.getGuiConfig().fluidStatusEffects) return;
        ci.cancel();
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (!collection.isEmpty()) {
            RenderSystem.enableBlend();
            
            double beneficialOffset = 0;
            double nonBeneficialOffset = 0;
            MobEffectTextureManager statusEffectSpriteManager = this.minecraft.getMobEffectTextures();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
            this.minecraft.getTextureManager().bind(AbstractContainerScreen.INVENTORY_LOCATION);
            
            for (MobEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                MobEffect statusEffect = statusEffectInstance.getEffect();
                if (statusEffectInstance.showIcon()) {
                    double[] x = {this.screenWidth};
                    int[] y = {1};
                    if (this.minecraft.isDemo()) {
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
                    
                    matrices.pushPose();
                    float alphaOffset = (float) Math.min(1.0, 1 - EasingMethod.EasingMethodImpl.LINEAR.apply(1 - Mth.clamp(statusEffectInstance.getDuration() / 10.0, 0, 1)));
                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, alphaOffset);
                    matrices.translate(x[0] - this.screenWidth, 0, 0);
                    float[] alpha = {alphaOffset};
                    if (statusEffectInstance.isAmbient()) {
                        this.blit(matrices, this.screenWidth, y[0], 165, 166, 24, 24);
                    } else {
                        this.blit(matrices, this.screenWidth, y[0], 141, 166, 24, 24);
                        if (statusEffectInstance.getDuration() <= 200) {
                            int m = 10 - statusEffectInstance.getDuration() / 20;
                            alpha[0] = alphaOffset * Mth.clamp((float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F) * Mth.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }
                    matrices.popPose();
                    
                    TextureAtlasSprite sprite = statusEffectSpriteManager.get(statusEffect);
                    list.add(() -> {
                        if (alpha[0] <= 0.01) return;
                        this.minecraft.getTextureManager().bind(sprite.atlas().location());
                        matrices.pushPose();
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha[0]);
                        matrices.translate(x[0] - this.screenWidth, 0, 0);
                        blit(matrices, this.screenWidth + 3, y[0] + 3, this.getBlitOffset(), 18, 18, sprite);
                        matrices.popPose();
                    });
                }
            }
            
            list.forEach(Runnable::run);
            
            RenderSystem.clear(256, Minecraft.ON_OSX);
            RenderSystem.matrixMode(5889);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, minecraft.getWindow().getWidth() / minecraft.getWindow().getGuiScale(), minecraft.getWindow().getHeight() / minecraft.getWindow().getGuiScale(), 0.0D, 1000.0D, 3000.0D);
            RenderSystem.matrixMode(5888);
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
            Lighting.setupFor3DItems();
        }
    }
}
