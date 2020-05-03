package me.shedaniel.slightguimodifications.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.clothconfig2.gui.entries.SelectionListEntry;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

@Config(name = "slightguimodifications/config")
public class SlightGuiModificationsConfig implements ConfigData {
    public boolean fluidAdvancements = false;
    public boolean fluidStatusEffects = false;
    @Comment("Whether we should unlimit the hard 60 fps limit placed on the title screen.") @ConfigEntry.Gui.Tooltip()
    public boolean unlimitTitleScreenFps = false;
    @ConfigEntry.Gui.CollapsibleObject public OpeningAnimation openingAnimation = new OpeningAnimation();
    @ConfigEntry.Gui.CollapsibleObject public TextFieldModifications textFieldModifications = new TextFieldModifications();
    @ConfigEntry.Gui.CollapsibleObject public DebugInformation debugInformation = new DebugInformation();
    @ConfigEntry.Gui.CollapsibleObject public CustomScaling customScaling = new CustomScaling();
    @Comment("Whether GUI should allow right click actions.") @ConfigEntry.Gui.Tooltip() public boolean rightClickActions = false;
    
    public static class OpeningAnimation {
        public boolean fluidOpenSlideFromBottom = false;
        public boolean fluidOpenFade = false;
        @ConfigEntry.BoundedDiscrete(min = 10, max = 1000) @ConfigEntry.Gui.Excluded public int fluidAnimationDuration = 400;
        public boolean affectsGameMenus = true;
        public boolean affectsInventories = true;
        public boolean ignoreSlideWhenRedirected = true;
        public boolean ignoreFadeWhenRedirected = false;
    }
    
    public static class TextFieldModifications {
        public boolean enabled = false;
        @Comment(
                "Set to Color to use the Border and Background Color.\nSet to Texture to use resource pack:\n/assets/minecraft/textures/gui/text_field.png\n\nMore documentations on website.")
        @ConfigEntry.Gui.Tooltip(count = 6) @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        public BackgroundMode backgroundMode = BackgroundMode.COLOR;
        @ConfigEntry.ColorPicker public int borderColor = 0xa0a0a0;
        @ConfigEntry.ColorPicker public int backgroundColor = 0x000000;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON) public SelectionMode selectionMode = SelectionMode.INVERT;
        @Comment("Whether Text Fields should allow right click actions.") @ConfigEntry.Gui.Tooltip() public boolean rightClickActions = false;
        
        public enum BackgroundMode implements SelectionListEntry.Translatable {
            COLOR,
            TEXTURE;
            
            @Override
            public @NotNull String getKey() {return "text.autoconfig.slightguimodifications/config.option.textFieldModifications.backgroundMode." + name().toLowerCase(Locale.ROOT);}
        }
        
        public enum SelectionMode implements SelectionListEntry.Translatable {
            INVERT,
            HIGHLIGHT;
            
            @Override
            public @NotNull String getKey() {return "text.autoconfig.slightguimodifications/config.option.textFieldModifications.selectionMode." + name().toLowerCase(Locale.ROOT);}
        }
    }
    
    public static class DebugInformation {
        public boolean showFps = false;
    }
    
    public static class CustomScaling {
        @ScaleSlider public double scale = 1;
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ScaleSlider {}
}
