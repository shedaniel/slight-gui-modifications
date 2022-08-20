package me.shedaniel.slightguimodifications.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
        @ConfigEntry.Gui.CollapsibleObject
        public TooltipModifications tooltipModifications = new TooltipModifications();
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
        
        public static class TooltipModifications {
            @Comment("Whether this module is enabled.")
            public boolean enabled = false;
            @ConfigEntry.ColorPicker(allowAlpha = true)
            public int backgroundColor = 0xf0100010;
            @ConfigEntry.ColorPicker(allowAlpha = true)
            public int outlineGradientTopColor = 0x505000ff;
            @ConfigEntry.ColorPicker(allowAlpha = true)
            public int outlineGradientBottomColor = 0x5028007f;
        }
        
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface ScaleSlider {}
    }
}
