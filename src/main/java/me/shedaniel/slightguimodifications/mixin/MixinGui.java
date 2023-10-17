package me.shedaniel.slightguimodifications.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import me.shedaniel.clothconfig2.impl.EasingMethod;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(Gui.class)
public class MixinGui {
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

    @Unique
    private static final ResourceLocation AMBIENT_EFFECT_BACKGROUND_LOCATION = new ResourceLocation("minecraft:textures/gui/sprites/hud/effect_background_ambient.png");
    @Unique
    private static final ResourceLocation EFFECT_BACKGROUND_LOCATION = new ResourceLocation("minecraft:textures/gui/sprites/hud/effect_background.png");

    /**
     * fuck mod compatibility
     *
     * @author shedaniel
     */
    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderStatusEffectOverlay(GuiGraphics graphics, CallbackInfo ci) {
        if (!SlightGuiModifications.getGuiConfig().fluidStatusEffects) return;
        ci.cancel();
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (!collection.isEmpty()) {
            RenderSystem.enableBlend();

            var shaderColorPrev = RenderSystem.getShaderColor();
            shaderColorPrev = new float[]
            {
                    shaderColorPrev[0],
                    shaderColorPrev[1],
                    shaderColorPrev[2],
                    shaderColorPrev[3]
            };
            float[] finalShaderColorPrev = shaderColorPrev;

            double beneficialOffset = 0;
            double nonBeneficialOffset = 0;
            MobEffectTextureManager statusEffectSpriteManager = this.minecraft.getMobEffectTextures();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
            RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
            
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
                    
                    graphics.pose().pushPose();
                    float alphaOffset = (float) Math.min(1.0, 1 - EasingMethod.EasingMethodImpl.LINEAR.apply(1 - Mth.clamp(statusEffectInstance.getDuration() / 10.0, 0, 1)));
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alphaOffset);
                    graphics.pose().translate(x[0] - this.screenWidth, 0, 0);
                    float[] alpha = {alphaOffset};
                    if (statusEffectInstance.isAmbient()) {
                        graphics.blit(AMBIENT_EFFECT_BACKGROUND_LOCATION, this.screenWidth, y[0], 0, 0, 24, 24, 24, 24);
                    } else {
                        graphics.blit(EFFECT_BACKGROUND_LOCATION, this.screenWidth, y[0], 0, 0, 24, 24, 24, 24);
                        if (statusEffectInstance.getDuration() <= 200) {
                            int m = 10 - statusEffectInstance.getDuration() / 20;
                            alpha[0] = alphaOffset * Mth.clamp((float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F) * Mth.clamp((float) m / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }
                    graphics.pose().popPose();

                    TextureAtlasSprite sprite = statusEffectSpriteManager.get(statusEffect);
                    list.add(() -> {
                        if (alpha[0] <= 0.01) return;
                        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
                        graphics.pose().pushPose();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha[0]);
                        graphics.pose().translate(x[0] - this.screenWidth, 0, 0);
                        graphics.blit(this.screenWidth + 3, y[0] + 3, 0, 18, 18, sprite);
                        graphics.pose().popPose();
                        RenderSystem.setShaderColor(finalShaderColorPrev[0], finalShaderColorPrev[1], finalShaderColorPrev[2], finalShaderColorPrev[3]);
                    });
                }
            }
            
            list.forEach(Runnable::run);
        }
    }
}
