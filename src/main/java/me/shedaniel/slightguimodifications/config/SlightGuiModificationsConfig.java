package me.shedaniel.slightguimodifications.config;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.cts.elements.WidgetElement;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Config(name = "slightguimodifications")
public class SlightGuiModificationsConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("config")
    @ConfigEntry.Gui.TransitiveObject
    public Gui gui = new Gui();
    
    @Config(name = "config")
    public static class Gui implements ConfigData {
        public boolean fluidAdvancements = false;
        public boolean fluidStatusEffects = false;
        @Comment("Whether we should unlimit the hard 60 fps limit placed on the title screen.") @ConfigEntry.Gui.Tooltip()
        public boolean unlimitTitleScreenFps = false;
        @ConfigEntry.Gui.CollapsibleObject
        public OpeningAnimation openingAnimation = new OpeningAnimation();
        @ConfigEntry.Gui.CollapsibleObject
        public TextFieldModifications textFieldModifications = new TextFieldModifications();
        @ConfigEntry.Gui.CollapsibleObject
        public SliderModifications sliderModifications = new SliderModifications();
        @ConfigEntry.Gui.CollapsibleObject
        public DebugInformation debugInformation = new DebugInformation();
        @ConfigEntry.Gui.CollapsibleObject
        public CustomScaling customScaling = new CustomScaling();
        @ConfigEntry.Gui.CollapsibleObject
        public SlotHighlight slotHighlight = new SlotHighlight();
        @Comment("Whether GUI should allow right click actions.")
        @ConfigEntry.Gui.Tooltip()
        public boolean rightClickActions = false;
        public boolean satisfyingScreenshots = false;
        
        public static class OpeningAnimation {
            public boolean fluidChatOpening = false;
            public boolean fluidOpenSlideFromBottom = false;
            public boolean fluidOpenFade = false;
            @ConfigEntry.BoundedDiscrete(min = 10, max = 5000)
            public int fluidAnimationDuration = 400;
            public boolean affectsGameMenus = true;
            public boolean affectsInventories = true;
            public boolean ignoreSlideWhenRedirected = true;
            public boolean ignoreFadeWhenRedirected = false;
        }
        
        public static class TextFieldModifications {
            @Comment("Whether this module is enabled.")
            public boolean enabled = false;
            @Comment(
                    "Set to Color to use the Border and Background Color.\nSet to Texture to use resource pack:\n/assets/minecraft/textures/gui/text_field.png\n\nMore documentations on website.")
            @ConfigEntry.Gui.Tooltip(count = 6)
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public BackgroundMode backgroundMode = BackgroundMode.COLOR;
            @ConfigEntry.ColorPicker
            public int borderColor = 0xa0a0a0;
            @ConfigEntry.ColorPicker
            public int backgroundColor = 0x000000;
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public SelectionMode selectionMode = SelectionMode.INVERT;
            @Comment("Whether Text Fields should allow right click actions.")
            @ConfigEntry.Gui.Tooltip() public boolean rightClickActions = false;
            
            public enum BackgroundMode implements SelectionListEntry.Translatable {
                COLOR,
                TEXTURE;
                
                @Override
                public @NotNull String getKey() {
                    return "text.autoconfig.slightguimodifications.option.gui.textFieldModifications.backgroundMode." + name().toLowerCase(Locale.ROOT);
                }
            }
            
            public enum SelectionMode implements SelectionListEntry.Translatable {
                INVERT,
                HIGHLIGHT;
                
                @Override
                public @NotNull String getKey() {
                    return "text.autoconfig.slightguimodifications.option.gui.textFieldModifications.selectionMode." + name().toLowerCase(Locale.ROOT);
                }
            }
        }
        
        public static class SliderModifications {
            @Comment("Whether this module is enabled.")
            public boolean enabled = false;
            public int grabberWidth = 8;
            @Comment(
                    "Whether to use custom texture:\n/assets/slightguimodifications/textures/gui/slider(_hovered).png\nor\n/config/slightguimodifications/slider(_hovered).png")
            @ConfigEntry.Gui.Tooltip(count = 4)
            public boolean customBackgroundTexture = false;
        }
        
        public static class DebugInformation {
            public boolean showFps = false;
        }
        
        public static class CustomScaling {
            public boolean vanillaScaleSlider = false;
            @ScaleSlider
            public double scale = 1;
        }
        
        public static class SlotHighlight {
            @Comment("Whether this module is enabled.")
            public boolean enabled = false;
            @ConfigEntry.ColorPicker(allowAlpha = true)
            public int color = 0x80ffffff;
        }
        
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface ScaleSlider {}
    }
    
    public static class Cts {
        @Comment("Whether this category is enabled.")
        public boolean enabled = false;
        @ConfigEntry.Gui.CollapsibleObject
        public SplashText splashText = new SplashText();
        public boolean removeMinecraftEditionTexture = false;
        public boolean removeMinecraftLogoTexture = false;
        public boolean clearAllButtons = false;
        public boolean clearAllLabels = false;
        public List<WidgetElement> widgetElements = new ArrayList<>();
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
            
            public abstract void render(PoseStack matrices, TitleScreen screen, float delta, float alpha);
        }
        
        public static class DefaultBackgroundInfo extends BackgroundInfo {
            @Override
            public void render(PoseStack matrices, TitleScreen screen, float delta, float alpha) {
                Minecraft.getInstance().getTextureManager().bind(TitleScreen.PANORAMA_OVERLAY);
                screen.panorama.render(delta, Mth.clamp(alpha * getAlpha(), 0.0F, 1.0F));
            }
        }
        
        public static class TextureProvidedBackgroundInfo extends BackgroundInfo {
            private final LazyLoadedValue<ResourceLocation> provider;
            
            public TextureProvidedBackgroundInfo(TextureProvider provider) {
                this.provider = new LazyLoadedValue<>(provider::provide);
            }
            
            @Override
            public void render(PoseStack matrices, TitleScreen screen, float delta, float alpha) {
                Minecraft.getInstance().getTextureManager().bind(provider.get());
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, getAlpha());
                GuiComponent.blit(matrices, 0, 0, screen.width, screen.height, 0.0F, 0.0F, 16, 128, 16, 128);
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
            public CustomSplashesApplyMode customSplashesApplyMode = CustomSplashesApplyMode.OVERRIDE;
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
}
