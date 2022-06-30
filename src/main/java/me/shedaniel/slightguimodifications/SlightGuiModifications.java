package me.shedaniel.slightguimodifications;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.hooks.client.screen.ScreenHooks;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.cloth.api.client.events.v0.ClothClientHooks;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.LazyResettable;
import me.shedaniel.math.Point;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.gui.cts.CtsRegistry;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
    
    public static float[] getColorObj() {
        return RenderSystem.getShaderColor();
    }
    
    public static float getColorRed(float[] colorObj) {
        return colorObj[0];
    }
    
    public static float getColorGreen(float[] colorObj) {
        return colorObj[1];
    }
    
    public static float getColorBlue(float[] colorObj) {
        return colorObj[2];
    }
    
    public static float getColorAlpha(float[] colorObj) {
        return colorObj[3];
    }
    
    public static void setAlpha(float alpha) {
        if (lastAlpha >= 0) new IllegalStateException().printStackTrace();
        float[] colorObj = getColorObj();
        float colorRed = getColorRed(colorObj);
        float colorGreen = getColorGreen(colorObj);
        float colorBlue = getColorBlue(colorObj);
        float colorAlpha = getColorAlpha(colorObj);
        lastAlpha = colorAlpha == -1 ? 1 : Mth.clamp(colorAlpha, 0, 1);
        RenderSystem.setShaderColor(colorRed == -1 ? 1 : colorRed,
                colorGreen == -1 ? 1 : colorGreen,
                colorBlue == -1 ? 1 : colorBlue,
                lastAlpha * alpha);
    }
    
    public static void restoreAlpha() {
        if (lastAlpha < 0) return;
        float[] colorObj = getColorObj();
        float colorRed = getColorRed(colorObj);
        float colorGreen = getColorGreen(colorObj);
        float colorBlue = getColorBlue(colorObj);
        RenderSystem.setShaderColor(colorRed == -1 ? 1 : colorRed,
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
                        ConfigEntryBuilder.create().startIntSlider(Component.translatable(i13n), (int) (Math.max(1, getUnsafely(field, config, 0.0)) * 100), 100,
                                (Minecraft.getInstance().getWindow().calculateScale(0, false) + 4) * 100)
                                .setDefaultValue(0)
                                .setTextGetter(integer -> {
                                    if (integer <= 100)
                                        return Component.translatable(i13n + ".text.disabled");
                                    return Component.translatable(i13n + ".text", integer / 100.0);
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
                    Optional<Widget> optionsButton = ScreenHooks.getRenderables(screen).stream()
                            .filter(button -> button instanceof AbstractWidget widget && widget.getMessage().getString().equals(I18n.get("menu.options"))).findFirst();
                    if (optionsButton.isPresent() && ((AbstractWidget) optionsButton.get()).isMouseOver(mouseX, mouseY)) {
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
        ClientTooltipEvent.RENDER_MODIFY_COLOR.register((poseStack, x, y, colorContext) -> {
            SlightGuiModificationsConfig.Gui config = SlightGuiModifications.getGuiConfig();
            SlightGuiModificationsConfig.Gui.TooltipModifications modifications = config.tooltipModifications;
            if (modifications.enabled) {
                colorContext.setBackgroundColor(modifications.backgroundColor);
                colorContext.setOutlineGradientTopColor(modifications.outlineGradientTopColor);
                colorContext.setOutlineGradientBottomColor(modifications.outlineGradientBottomColor);
            }
        });
        reloadCtsAsync();
        RRPCallback.AFTER_VANILLA.register(packs -> {
            RuntimeResourcePack pack = RuntimeResourcePack.create("slightguimodifications:cts_textures");
            Path buttons = FabricLoader.getInstance().getConfigDir().resolve("slightguimodifications/buttons.png");
            if (Files.exists(buttons)) {
                try {
                    pack.addAsset(new ResourceLocation("minecraft:textures/gui/widgets.png"), Files.readAllBytes(buttons));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Path textField = FabricLoader.getInstance().getConfigDir().resolve("slightguimodifications/text_field.png");
            if (Files.exists(textField)) {
                try {
                    pack.addAsset(new ResourceLocation("minecraft:textures/gui/text_field.png"), Files.readAllBytes(textField));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Path slider = FabricLoader.getInstance().getConfigDir().resolve("slightguimodifications/slider.png");
            if (Files.exists(slider)) {
                try {
                    pack.addAsset(new ResourceLocation("slightguimodifications:textures/gui/slider.png"), Files.readAllBytes(slider));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Path sliderHovered = FabricLoader.getInstance().getConfigDir().resolve("slightguimodifications/slider_hovered.png");
            if (Files.exists(sliderHovered)) {
                try {
                    pack.addAsset(new ResourceLocation("slightguimodifications:textures/gui/slider_hovered.png"), Files.readAllBytes(sliderHovered));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            packs.add(pack);
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
                ScreenHooks.addRenderableWidget(screen, new Button(screen.width - 104, 4, 100, 20, Component.translatable("text.slightguimodifications.reloadCts"), button -> {
                    SlightGuiModifications.resetCts();
                    SlightGuiModifications.reloadCts();
                }));
            });
            return builder.build();
        });
        return supplier.get();
    }
}
