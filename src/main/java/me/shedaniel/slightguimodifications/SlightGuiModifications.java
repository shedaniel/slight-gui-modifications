package me.shedaniel.slightguimodifications;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.resource.ArtificeResource;
import me.shedaniel.architectury.event.events.TooltipEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.api.client.events.v0.ClothClientHooks;
import me.shedaniel.cloth.api.client.events.v0.ScreenHooks;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.LazyResettable;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.gui.cts.CtsRegistry;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static me.shedaniel.autoconfig.util.Utils.getUnsafely;
import static me.shedaniel.autoconfig.util.Utils.setUnsafely;

public class SlightGuiModifications implements ClientModInitializer {
    public static float backgroundTint = 0;
    public static final ResourceLocation TEXT_FIELD_TEXTURE = new ResourceLocation("textures/gui/text_field.png");
    public static float lastAlpha = -1;
    public static boolean prettyScreenshots = false;
    public static DynamicTexture prettyScreenshotTexture = null;
    public static DynamicTexture lastPrettyScreenshotTexture = null;
    public static ResourceLocation prettyScreenshotTextureId = null;
    public static ResourceLocation lastPrettyScreenshotTextureId = null;
    public static long prettyScreenshotTime = -1;
    public static long backgroundTime = -1;
    public static final Logger LOGGER = LogManager.getLogger("SlightGuiModifications");
    
    private static final LazyLoadedValue<Object> COLOR_OBJ = new LazyLoadedValue<>(() -> {
        try {
            Field field = GlStateManager.class.getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493", "field_20487", "Lnet/minecraft/class_4493$class_1020;"));
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final LazyLoadedValue<Field> RED_FIELD = new LazyLoadedValue<>(() -> {
        try {
            Field field = getColorObj().getClass().getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493$class_1020", "field_5057", "F"));
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final LazyLoadedValue<Field> GREEN_FIELD = new LazyLoadedValue<>(() -> {
        try {
            Field field = getColorObj().getClass().getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493$class_1020", "field_5056", "F"));
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final LazyLoadedValue<Field> BLUE_FIELD = new LazyLoadedValue<>(() -> {
        try {
            Field field = getColorObj().getClass().getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493$class_1020", "field_5055", "F"));
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final LazyLoadedValue<Field> ALPHA_FIELD = new LazyLoadedValue<>(() -> {
        try {
            Field field = getColorObj().getClass().getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493$class_1020", "field_5054", "F"));
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    
    public static Object getColorObj() {
        return COLOR_OBJ.get();
    }
    
    public static float getColorRed(Object colorObj) {
        try {
            return (float) RED_FIELD.get().get(colorObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static float getColorGreen(Object colorObj) {
        try {
            return (float) GREEN_FIELD.get().get(colorObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static float getColorBlue(Object colorObj) {
        try {
            return (float) BLUE_FIELD.get().get(colorObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static float getColorAlpha(Object colorObj) {
        try {
            return (float) ALPHA_FIELD.get().get(colorObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void setAlpha(float alpha) {
        if (lastAlpha >= 0) new IllegalStateException().printStackTrace();
        Object colorObj = getColorObj();
        float colorRed = getColorRed(colorObj);
        float colorGreen = getColorGreen(colorObj);
        float colorBlue = getColorBlue(colorObj);
        float colorAlpha = getColorAlpha(colorObj);
        lastAlpha = colorAlpha == -1 ? 1 : Mth.clamp(colorAlpha, 0, 1);
        RenderSystem.color4f(colorRed == -1 ? 1 : colorRed,
                colorGreen == -1 ? 1 : colorGreen,
                colorBlue == -1 ? 1 : colorBlue,
                lastAlpha * alpha);
    }
    
    public static void restoreAlpha() {
        if (lastAlpha < 0) return;
        Object colorObj = getColorObj();
        float colorRed = getColorRed(colorObj);
        float colorGreen = getColorGreen(colorObj);
        float colorBlue = getColorBlue(colorObj);
        RenderSystem.color4f(colorRed == -1 ? 1 : colorRed,
                colorGreen == -1 ? 1 : colorGreen,
                colorBlue == -1 ? 1 : colorBlue,
                lastAlpha);
        lastAlpha = -1;
    }
    
    public static float ease(float t) {
        return (float) (1f * (-Math.pow(2, -10 * t / 1f) + 1));
    }
    
    public static int reverseYAnimation(int y) {
        return y - applyYAnimation(y) + y;
    }
    
    public static int applyYAnimation(int y) {
        if (!RenderSystem.isOnRenderThread()) return y;
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getEasedYOffset();
            if (alpha >= 0) return y + (int) ((1 - alpha) * screen.height / 2);
        }
        return y;
    }
    
    public static int applyMouseYAnimation(int y) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getEasedMouseY();
            if (alpha >= 0) return y - (int) ((1 - alpha) * screen.height / 2);
        }
        return y;
    }
    
    public static double reverseYAnimation(double y) {return y - applyYAnimation(y) + y;}
    
    public static double applyYAnimation(double y) {
        if (!RenderSystem.isOnRenderThread()) return y;
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getEasedYOffset();
            if (alpha >= 0) return y + (int) ((1 - alpha) * screen.height / 2);
        }
        return y;
    }
    
    public static int applyAlphaAnimation(int alpha) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AnimationListener) {
            float animatedAlpha = ((AnimationListener) screen).slightguimodifications_getAlpha();
            if (animatedAlpha >= 0) return (int) (animatedAlpha * alpha);
        }
        return alpha;
    }
    
    public static void startPrettyScreenshot(NativeImage cloneImage) {
        if (prettyScreenshotTexture != null) {
            lastPrettyScreenshotTexture = prettyScreenshotTexture;
            lastPrettyScreenshotTextureId = prettyScreenshotTextureId;
        }
        prettyScreenshotTexture = null;
        prettyScreenshotTextureId = null;
        prettyScreenshotTime = -1;
        if (cloneImage != null) {
            prettyScreenshotTexture = new DynamicTexture(cloneImage);
            prettyScreenshotTextureId = Minecraft.getInstance().getTextureManager().register("slight-gui-modifications-pretty-screenshots", prettyScreenshotTexture);
        }
    }
    
    @Override
    public void onInitializeClient() {
        AutoConfig.register(SlightGuiModificationsConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        AutoConfig.getGuiRegistry(SlightGuiModificationsConfig.class).registerAnnotationProvider(
                (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ConfigEntryBuilder.create().startIntSlider(new TranslatableComponent(i13n), (int) (Math.max(1, getUnsafely(field, config, 0.0)) * 100), 100,
                                (Minecraft.getInstance().getWindow().calculateScale(0, false) + 4) * 100)
                                .setDefaultValue(0)
                                .setTextGetter(integer -> {
                                    if (integer <= 100)
                                        return new TranslatableComponent(i13n + ".text.disabled");
                                    return new TranslatableComponent(i13n + ".text", integer / 100.0);
                                })
                                .setSaveConsumer(integer -> setUnsafely(field, config, integer / 100.0))
                                .build()
                ),
                SlightGuiModificationsConfig.Gui.ScaleSlider.class
        );
        ClothClientHooks.SCREEN_MOUSE_CLICKED.register((client, screen, mouseX, mouseY, mouseButton) -> {
            if (((MenuWidgetListener) screen).getMenu() != null) {
                if (!((MenuWidgetListener) screen).getMenu().mouseClicked(mouseX, mouseY, mouseButton)) {
                    ((MenuWidgetListener) screen).removeMenu();
                }
                return InteractionResult.SUCCESS;
            }
            if (SlightGuiModifications.getGuiConfig().rightClickActions && mouseButton == 1) {
                // Pause Menu
                if (screen instanceof PauseScreen || screen instanceof TitleScreen) {
                    Optional<AbstractWidget> optionsButton = screen.buttons.stream().filter(button -> button != null && button.getMessage().getString().equals(I18n.get("menu.options"))).findFirst();
                    if (optionsButton.isPresent() && optionsButton.get().isMouseOver(mouseX, mouseY)) {
                        ((MenuWidgetListener) screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2),
                                ImmutableList.of(
                                        new TextMenuEntry(I18n.get("options.video").replace("...", ""), () -> {
                                            ((MenuWidgetListener) screen).removeMenu();
                                            client.setScreen(new VideoSettingsScreen(screen, client.options));
                                        }),
                                        new TextMenuEntry(I18n.get("options.controls").replace("...", ""), () -> {
                                            ((MenuWidgetListener) screen).removeMenu();
                                            client.setScreen(new ControlsScreen(screen, client.options));
                                        }),
                                        new TextMenuEntry(I18n.get("options.sounds").replace("...", ""), () -> {
                                            ((MenuWidgetListener) screen).removeMenu();
                                            client.setScreen(new SoundOptionsScreen(screen, client.options));
                                        })
                                )
                        ));
                    }
                }
            }
            return InteractionResult.PASS;
        });
        TooltipEvent.RENDER_MODIFY_COLOR.register((poseStack, x, y, colorContext) -> {
            SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
            SlightGuiModificationsConfig.Gui.TooltipModifications modifications = config.tooltipModifications;
            if (modifications.enabled) {
                colorContext.setBackgroundColor(modifications.backgroundColor);
                colorContext.setOutlineGradientTopColor(modifications.outlineGradientTopColor);
                colorContext.setOutlineGradientBottomColor(modifications.outlineGradientBottomColor);
            }
        });
        reloadCtsAsync();
        Artifice.registerAssetPack(new ResourceLocation("slightguimodifications:cts_textures"), builder -> {
            builder.shouldOverwrite();
            Path buttons = FabricLoader.getInstance().getConfigDir().resolve( "slightguimodifications/buttons.png");
            if (Files.exists(buttons)) {
                builder.add(new ResourceLocation("minecraft:textures/gui/widgets.png"), new ArtificeResource<FileInputStream>() {
                    @Override
                    public FileInputStream getData() {
                        return null;
                    }
                    
                    @Override
                    public String toOutputString() {
                        return null;
                    }
                    
                    @Override
                    public InputStream toInputStream() {
                        try {
                            return new ByteArrayInputStream(Files.readAllBytes(buttons));
                        } catch (IOException e) {
                            return null;
                        }
                    }
                });
            }
            Path textField = FabricLoader.getInstance().getConfigDir().resolve( "slightguimodifications/text_field.png");
            if (Files.exists(textField)) {
                builder.add(new ResourceLocation("minecraft:textures/gui/text_field.png"), new ArtificeResource<FileInputStream>() {
                    @Override
                    public FileInputStream getData() {
                        return null;
                    }
                    
                    @Override
                    public String toOutputString() {
                        return null;
                    }
                    
                    @Override
                    public InputStream toInputStream() {
                        try {
                            return new ByteArrayInputStream(Files.readAllBytes(textField));
                        } catch (IOException e) {
                            return null;
                        }
                    }
                });
            }
            Path slider = FabricLoader.getInstance().getConfigDir().resolve( "slightguimodifications/slider.png");
            if (Files.exists(slider)) {
                builder.add(new ResourceLocation("slightguimodifications:textures/gui/slider.png"), new ArtificeResource<FileInputStream>() {
                    @Override
                    public FileInputStream getData() {
                        return null;
                    }
                    
                    @Override
                    public String toOutputString() {
                        return null;
                    }
                    
                    @Override
                    public InputStream toInputStream() {
                        try {
                            return new ByteArrayInputStream(Files.readAllBytes(slider));
                        } catch (IOException e) {
                            return null;
                        }
                    }
                });
            }
            Path sliderHovered = FabricLoader.getInstance().getConfigDir().resolve( "slightguimodifications/slider_hovered.png");
            if (Files.exists(sliderHovered)) {
                builder.add(new ResourceLocation("slightguimodifications:textures/gui/slider_hovered.png"), new ArtificeResource<FileInputStream>() {
                    @Override
                    public FileInputStream getData() {
                        return null;
                    }
                    
                    @Override
                    public String toOutputString() {
                        return null;
                    }
                    
                    @Override
                    public InputStream toInputStream() {
                        try {
                            return new ByteArrayInputStream(Files.readAllBytes(sliderHovered));
                        } catch (IOException e) {
                            return null;
                        }
                    }
                });
            }
        });
    }
    
    public static final LazyResettable<SlightGuiModificationsConfig.Cts> CTS = new LazyResettable<>(SlightGuiModificationsConfig.Cts::new);
    
    public static void reloadCtsAsync() {
        CtsRegistry.loadScriptsAsync();
    }
    
    public static void reloadCts() {
        CtsRegistry.loadScripts();
    }
    
    public static void openModMenu() {
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            try {
                Class.forName("me.shedaniel.slightguimodifications.gui.cts.ModMenuCompat").getDeclaredMethod("openModMenu").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static String getModMenuText() {
        try {
            return (String) Class.forName("me.shedaniel.slightguimodifications.gui.cts.ModMenuCompat").getDeclaredMethod("getModMenuText").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
    
    public static double bezierEase(double value, double[] points) {
        return bezierEase(value, points[0], points[1], points[2], points[3]);
    }
    
    public static float bezierEase(float value, double[] points) {
        return (float) bezierEase(value, points[0], points[1], points[2], points[3]);
    }
    
    private static double bezierEase(double value, double point1, double point2, double point3, double point4) {
        return point1 * Math.pow(1 - value, 3) + 3 * point2 * Math.pow(1 - value, 2) * value + 3 * point2 * (1 - value) * Math.pow(value, 2) + point4 * Math.pow(value, 3);
    }
    
    public static SlightGuiModificationsConfig.Gui getGuiConfig() {
        return AutoConfig.getConfigHolder(SlightGuiModificationsConfig.class).getConfig().gui;
    }
    
    public static SlightGuiModificationsConfig.Cts getCtsConfig() {
        return CTS.get();
    }
    
    public static void resetCts() {
        CTS.reset();
    }
    
    public static float getSpeed() {
        return getGuiConfig().openingAnimation.fluidAnimationDuration;
    }
    
    @SuppressWarnings("deprecation")
    public static Screen getConfigScreen(Screen parent) {
        ConfigScreenProvider<SlightGuiModificationsConfig> supplier = (ConfigScreenProvider<SlightGuiModificationsConfig>) AutoConfig.getConfigScreen(SlightGuiModificationsConfig.class, parent);
        supplier.setBuildFunction(builder -> {
            Runnable runnable = builder.getSavingRunnable();
            builder.setSavingRunnable(() -> {
                runnable.run();
                Minecraft.getInstance().resizeDisplay();
            });
            builder.setAfterInitConsumer(screen -> {
                ((ScreenHooks) screen).cloth$addButtonWidget(new Button(screen.width - 104, 4, 100, 20, new TranslatableComponent("text.slightguimodifications.reloadCts"), button -> {
                    SlightGuiModifications.resetCts();
                    SlightGuiModifications.reloadCts();
                }));
            });
            return builder.build();
        });
        return supplier.get();
    }
}
