package me.shedaniel.slightguimodifications.config;

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.cts.elements.WidgetElement;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
public class Cts {
    @Comment("Whether this category is enabled.")
    public boolean enabled = false;
    @ConfigEntry.Gui.CollapsibleObject
    public SplashText splashText = new SplashText();
    public boolean removeMinecraftEditionTexture = false;
    public boolean removeMinecraftLogoTexture = false;
    public boolean clearAllButtons = false;
    public boolean clearAllLabels = false;
    public List<WidgetElement> widgetElements = new CopyOnWriteArrayList<>();
    public List<BackgroundInfo> backgroundInfos = new ArrayList<>(Collections.singletonList(new DefaultBackgroundInfo()));
    public long backgroundStayLength = 10000;
    public long backgroundFadeLength = 1000;
    public boolean renderGradientShade = true;
    
    public static abstract class BackgroundInfo {
        public final int index() {
            return SlightGuiModifications.getCtsConfig().backgroundInfos.indexOf(this);
        }
        
        public final float getAlpha() {
            Cts ctsConfig = SlightGuiModifications.getCtsConfig();
            if (ctsConfig.backgroundInfos.size() == 1) return 1;
            int index = index();
            long fullIterationLength = ctsConfig.backgroundStayLength * ctsConfig.backgroundInfos.size();
            long timePast = (Util.getMillis() - SlightGuiModifications.backgroundTime) % fullIterationLength;
            if (timePast >= index * ctsConfig.backgroundStayLength && timePast < (index + 1) * ctsConfig.backgroundStayLength)
                return 1;
            if (timePast >= (index + 1) * ctsConfig.backgroundStayLength && timePast < (index + 1) * ctsConfig.backgroundStayLength + ctsConfig.backgroundFadeLength)
                return Mth.clamp(((index + 1) * ctsConfig.backgroundStayLength + ctsConfig.backgroundFadeLength - timePast) / (float) ctsConfig.backgroundFadeLength, 0, 1);
            if (index == ctsConfig.backgroundInfos.size() - 1 && timePast <= ctsConfig.backgroundFadeLength)
                return Mth.clamp((ctsConfig.backgroundFadeLength - timePast) / (float) ctsConfig.backgroundFadeLength, 0, 1);
            return 0;
        }
        
        public abstract void render(GuiGraphics graphics, TitleScreen screen, float delta, float alpha);
    }
    
    public static class DefaultBackgroundInfo extends BackgroundInfo {
        @Override
        public void render(GuiGraphics graphics, TitleScreen screen, float delta, float alpha) {
            RenderSystem.setShaderTexture(0, TitleScreen.PANORAMA_OVERLAY);
            screen.panorama.render(delta, Mth.clamp(alpha * getAlpha(), 0.0F, 1.0F));
        }
    }
    
    public static class TextureProvidedBackgroundInfo extends BackgroundInfo {
        private final Supplier<ResourceLocation> provider;
        
        public TextureProvidedBackgroundInfo(TextureProvider provider) {
            this.provider = Suppliers.memoize(provider::provide);
        }
        
        @Override
        public void render(GuiGraphics graphics, TitleScreen screen, float delta, float alpha) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, getAlpha());
            graphics.blit(provider.get(), 0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128);
        }
    }
    
    public interface TextureProvider {
        ResourceLocation provide();
    }
    
    public static class SplashText {
        @Comment("Whether this module is enabled.")
        public boolean enabled = false;
        public boolean removeSplashes = false;
        public boolean customSplashesEnabled = false;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public SplashText.CustomSplashesApplyMode customSplashesApplyMode = SplashText.CustomSplashesApplyMode.OVERRIDE;
        public List<String> customSplashes = Lists.newArrayList();
        
        public enum CustomSplashesApplyMode implements SelectionListEntry.Translatable {
            APPEND,
            OVERRIDE;
            
            @Override
            public @NotNull String getKey() {
                return "text.autoconfig.slightguimodifications.option.cts.splashText.customSplashesApplyMode." + name().toLowerCase(Locale.ROOT);
            }
        }
    }
}
