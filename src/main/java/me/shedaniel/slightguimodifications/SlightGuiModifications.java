package me.shedaniel.slightguimodifications;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.resource.ArtificeResource;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import me.shedaniel.cloth.hooks.ClothClientHooks;
import me.shedaniel.cloth.hooks.ScreenHooks;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.gui.cts.CtsRegistry;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.SoundOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.MathHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;

import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.setUnsafely;

public class SlightGuiModifications implements ClientModInitializer {
    public static float backgroundTint = 0;
    public static final Identifier TEXT_FIELD_TEXTURE = new Identifier("textures/gui/text_field.png");
    public static float lastAlpha = -1;
    public static boolean prettyScreenshots = false;
    public static NativeImageBackedTexture prettyScreenshotTexture = null;
    public static NativeImageBackedTexture lastPrettyScreenshotTexture = null;
    public static Identifier prettyScreenshotTextureId = null;
    public static Identifier lastPrettyScreenshotTextureId = null;
    public static long prettyScreenshotTime = -1;
    public static long backgroundTime = -1;
    
    private static final Lazy<Object> COLOR_OBJ = new Lazy<>(() -> {
        try {
            Field field = GlStateManager.class.getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493", "field_20487", "Lnet/minecraft/class_4493$class_1020;"));
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final Lazy<Field> RED_FIELD = new Lazy<>(() -> {
        try {
            Field field = getColorObj().getClass().getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493$class_1020", "field_5057", "F"));
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final Lazy<Field> GREEN_FIELD = new Lazy<>(() -> {
        try {
            Field field = getColorObj().getClass().getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493$class_1020", "field_5056", "F"));
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final Lazy<Field> BLUE_FIELD = new Lazy<>(() -> {
        try {
            Field field = getColorObj().getClass().getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_4493$class_1020", "field_5055", "F"));
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final Lazy<Field> ALPHA_FIELD = new Lazy<>(() -> {
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
        lastAlpha = colorAlpha == -1 ? 1 : MathHelper.clamp(colorAlpha, 0, 1);
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
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getEasedYOffset();
            if (alpha >= 0) return y + (int) ((1 - alpha) * screen.height / 2);
        }
        return y;
    }
    
    public static int applyMouseYAnimation(int y) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getEasedMouseY();
            if (alpha >= 0) return y - (int) ((1 - alpha) * screen.height / 2);
        }
        return y;
    }
    
    public static double reverseYAnimation(double y) {return y - applyYAnimation(y) + y;}
    
    public static double applyYAnimation(double y) {
        if (!RenderSystem.isOnRenderThread()) return y;
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof AnimationListener) {
            float alpha = ((AnimationListener) screen).slightguimodifications_getEasedYOffset();
            if (alpha >= 0) return y + (int) ((1 - alpha) * screen.height / 2);
        }
        return y;
    }
    
    public static int applyAlphaAnimation(int alpha) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
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
            prettyScreenshotTexture = new NativeImageBackedTexture(cloneImage);
            prettyScreenshotTextureId = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("slight-gui-modifications-pretty-screenshots", prettyScreenshotTexture);
        }
    }
    
    @Override
    public void onInitializeClient() {
        AutoConfig.register(SlightGuiModificationsConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        AutoConfig.getGuiRegistry(SlightGuiModificationsConfig.class).registerAnnotationProvider(
                (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ConfigEntryBuilder.create().startIntSlider(new TranslatableText(i13n), (int) (Math.max(1, getUnsafely(field, config, 0.0)) * 100), 100,
                                (MinecraftClient.getInstance().getWindow().calculateScaleFactor(0, false) + 4) * 100)
                                .setDefaultValue(0)
                                .setTextGetter(integer -> {
                                    if (integer <= 100)
                                        return new TranslatableText(i13n + ".text.disabled");
                                    return new TranslatableText(i13n + ".text", integer / 100.0);
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
                return ActionResult.SUCCESS;
            }
            if (SlightGuiModifications.getGuiConfig().rightClickActions && mouseButton == 1) {
                // Pause Menu
                if (screen instanceof GameMenuScreen || screen instanceof TitleScreen) {
                    AbstractButtonWidget optionsButton = screen.buttons.stream().filter(button -> button.getMessage().equals(I18n.translate("menu.options"))).findFirst().get();
                    if (optionsButton.isMouseOver(mouseX, mouseY)) {
                        ((MenuWidgetListener) screen).applyMenu(new MenuWidget(new Point(mouseX + 2, mouseY + 2),
                                ImmutableList.of(
                                        new TextMenuEntry(I18n.translate("options.video").replace("...", ""), () -> {
                                            ((MenuWidgetListener) screen).removeMenu();
                                            client.openScreen(new VideoOptionsScreen(screen, client.options));
                                        }),
                                        new TextMenuEntry(I18n.translate("options.controls").replace("...", ""), () -> {
                                            ((MenuWidgetListener) screen).removeMenu();
                                            client.openScreen(new ControlsOptionsScreen(screen, client.options));
                                        }),
                                        new TextMenuEntry(I18n.translate("options.sounds").replace("...", ""), () -> {
                                            ((MenuWidgetListener) screen).removeMenu();
                                            client.openScreen(new SoundOptionsScreen(screen, client.options));
                                        })
                                )
                        ));
                    }
                }
            }
            return ActionResult.PASS;
        });
        reloadCtsAsync();
        Artifice.registerAssets(new Identifier("slightguimodifications:cts_textures"), builder -> {
            File buttons = new File(FabricLoader.getInstance().getConfigDirectory(), "slightguimodifications/buttons.png");
            if (buttons.exists()) {
                builder.add(new Identifier("minecraft:textures/gui/widgets.png"), new ArtificeResource<FileInputStream>() {
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
                            return new FileInputStream(buttons);
                        } catch (FileNotFoundException e) {
                            return null;
                        }
                    }
                });
            }
            File textField = new File(FabricLoader.getInstance().getConfigDirectory(), "slightguimodifications/text_field.png");
            if (textField.exists()) {
                builder.add(new Identifier("minecraft:textures/gui/text_field.png"), new ArtificeResource<FileInputStream>() {
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
                            return new FileInputStream(textField);
                        } catch (FileNotFoundException e) {
                            return null;
                        }
                    }
                });
            }
        });
    }
    
    public static void reloadCtsAsync() {
        cts = new SlightGuiModificationsConfig.Cts();
        CtsRegistry.loadScriptsAsync();
    }
    
    public static void reloadCts() {
        cts = new SlightGuiModificationsConfig.Cts();
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
    
    public static String getModMenuModsCount() {
        try {
            return (String) Class.forName("me.shedaniel.slightguimodifications.gui.cts.ModMenuCompat").getDeclaredMethod("getDisplayedModCount").invoke(null);
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
    
    private static SlightGuiModificationsConfig.Cts cts = new SlightGuiModificationsConfig.Cts();
    
    public static SlightGuiModificationsConfig.Cts getCtsConfig() {
        return cts;
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
                MinecraftClient.getInstance().onResolutionChanged();
            });
            builder.setAfterInitConsumer(screen -> {
                ((ScreenHooks) screen).cloth_addButton(new ButtonWidget(screen.width - 104, 4, 100, 20, new TranslatableText("text.slightguimodifications.reloadCts"), button -> {
                    SlightGuiModifications.reloadCts();
                }));
            });
            return builder.build();
        });
        return supplier.get();
    }
}
