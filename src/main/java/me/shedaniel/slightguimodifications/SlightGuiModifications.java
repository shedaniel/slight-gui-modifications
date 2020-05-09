package me.shedaniel.slightguimodifications;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.gui.ConfigScreenProvider;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.hooks.ClothClientHooks;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.api.Point;
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig;
import me.shedaniel.slightguimodifications.gui.MenuWidget;
import me.shedaniel.slightguimodifications.gui.TextMenuEntry;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import me.shedaniel.slightguimodifications.listener.MenuWidgetListener;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.SoundOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;

import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.getUnsafely;
import static me.sargunvohra.mcmods.autoconfig1u.util.Utils.setUnsafely;

public class SlightGuiModifications implements ClientModInitializer {
    public static float backgroundTint = 0;
    public static final Identifier TEXT_FIELD_TEXTURE = new Identifier("textures/gui/text_field.png");
    public static float lastAlpha = -1;
    
    public static void setAlpha(float alpha) {
        if (lastAlpha >= 0) new IllegalStateException().fillInStackTrace();
        lastAlpha = GlStateManager.COLOR.alpha == -1 ? 1 : MathHelper.clamp(GlStateManager.COLOR.alpha, 0, 1);
//        System.out.println(lastAlpha * alpha);
//        if (lastAlpha * alpha == 0) new RuntimeException(lastAlpha + " " + alpha).printStackTrace();
        RenderSystem.color4f(GlStateManager.COLOR.red == -1 ? 1 : GlStateManager.COLOR.red,
                GlStateManager.COLOR.green == -1 ? 1 : GlStateManager.COLOR.green,
                GlStateManager.COLOR.blue == -1 ? 1 : GlStateManager.COLOR.blue,
                lastAlpha * alpha);
    }
    
    public static void restoreAlpha() {
        if (lastAlpha < 0) return;
        RenderSystem.color4f(GlStateManager.COLOR.red == -1 ? 1 : GlStateManager.COLOR.red,
                GlStateManager.COLOR.green == -1 ? 1 : GlStateManager.COLOR.green,
                GlStateManager.COLOR.blue == -1 ? 1 : GlStateManager.COLOR.blue,
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
    
    @Override
    public void onInitializeClient() {
        AutoConfig.register(SlightGuiModificationsConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getGuiRegistry(SlightGuiModificationsConfig.class).registerAnnotationProvider(
                (i13n, field, config, defaults, guiProvider) -> Collections.singletonList(
                        ConfigEntryBuilder.create().startIntSlider(i13n, (int) (Math.max(1, getUnsafely(field, config, 0.0)) * 100), 100,
                                (MinecraftClient.getInstance().getWindow().calculateScaleFactor(0, false) + 4) * 100)
                                .setDefaultValue(0)
                                .setTextGetter(integer -> {
                                    if (integer <= 100)
                                        return I18n.translate(i13n + ".text.disabled");
                                    return I18n.translate(i13n + ".text", integer / 100.0);
                                })
                                .setSaveConsumer(integer -> setUnsafely(field, config, integer / 100.0))
                                .build()
                ),
                SlightGuiModificationsConfig.ScaleSlider.class
        );
        ClothClientHooks.SCREEN_MOUSE_CLICKED.register((client, screen, mouseX, mouseY, mouseButton) -> {
            if (((MenuWidgetListener) screen).getMenu() != null) {
                if (!((MenuWidgetListener) screen).getMenu().mouseClicked(mouseX, mouseY, mouseButton)) {
                    ((MenuWidgetListener) screen).removeMenu();
                }
                return ActionResult.SUCCESS;
            }
            if (SlightGuiModifications.getConfig().rightClickActions && mouseButton == 1) {
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
    }
    
    public static SlightGuiModificationsConfig getConfig() {return AutoConfig.getConfigHolder(SlightGuiModificationsConfig.class).getConfig();}
    
    public static float getSpeed() {
        return getConfig().openingAnimation.fluidAnimationDuration;
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
            return builder.build();
        });
        return supplier.get();
    }
}
