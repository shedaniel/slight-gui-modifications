package me.shedaniel.slightguimodifications.gui.cts.script

import me.shedaniel.slightguimodifications.SlightGuiModifications
import me.shedaniel.slightguimodifications.config.SlightGuiModificationsConfig
import me.shedaniel.slightguimodifications.gui.cts.CtsRegistry
import me.shedaniel.slightguimodifications.gui.cts.Position
import me.shedaniel.slightguimodifications.gui.cts.elements.Text
import net.fabricmc.loader.api.FabricLoader

@SuppressWarnings(["unused", "GrMethodMayBeStatic"])
class ScriptDSL {
    static final def mainMenu = new MainMenuBuilder()

    static def trace(s) { CtsRegistry.LOGGER.trace(s) }

    static def debug(s) { CtsRegistry.LOGGER.debug(s) }

    static def info(s) { CtsRegistry.LOGGER.info(s) }

    static def warn(s) { CtsRegistry.LOGGER.warn(s) }

    static def error(s) { CtsRegistry.LOGGER.error(s) }

    static def fatal(s) { CtsRegistry.LOGGER.fatal(s) }

    static def mainMenu(Closure configure) {
        mainMenu.tap(configure)
    }

    static def file(file) {
        Middleman.file(file)
    }

    static def resource(id) {
        Middleman.resource(id)
    }

    static def literal(string) {
        Text.literal(string)
    }

    static def translatable(string, Object... args) {
        Text.translatable(string, args)
    }

    static def modMenuText() {
        Middleman.modMenuText()
    }

    static Closure nothing() {
        return {}
    }

    static Closure url(String string) {
        return { Middleman.url(string) }
    }

    static Closure modMenu() {
        return {
            if (FabricLoader.getInstance().isModLoaded("modmenu")) {
                SlightGuiModifications.openModMenu()
            }
        }
    }

    static Closure language() {
        return { Middleman.language() }
    }

    static Closure options() {
        return { Middleman.options() }
    }

    static Closure exit() {
        return { Middleman.exit() }
    }

    static Closure accessibility() {
        return { Middleman.accessibility() }
    }

    static Closure singleplayer() {
        return { Middleman.singleplayer() }
    }

    static Closure multiplayer() {
        return { Middleman.multiplayer() }
    }

    static Closure realms() {
        return { Middleman.realms() }
    }

    static Closure reloadCts() {
        return { Middleman.reloadCts() }
    }

    static class MainMenuBuilder {
        static final def splashText = new SplashTextBuilder()
        static final def background = new BackgroundBuilder()

        def setEnabled(boolean enabled) {
            SlightGuiModifications.getCtsConfig().enabled = enabled
        }

        boolean isEnabled() {
            SlightGuiModifications.getCtsConfig().enabled
        }

        def removeMinecraftLogo() {
            SlightGuiModifications.getCtsConfig().removeMinecraftLogoTexture = true
        }

        def removeEditionBadge() {
            SlightGuiModifications.getCtsConfig().removeMinecraftEditionTexture = true
        }

        def clearAllButtons() {
            SlightGuiModifications.getCtsConfig().clearAllButtons = true
        }

        def splashText(Closure configure) {
            splashText.tap(configure)
        }

        def background(Closure configure) {
            background.tap(configure)
        }

        def label(Closure configure) {
            SlightGuiModifications.getCtsConfig().widgetElements.add(Middleman.buildFromLabel(new LabelBuilder().tap(configure)))
        }

        def button(Closure configure) {
            SlightGuiModifications.getCtsConfig().widgetElements.add(Middleman.buildFromButton(new ButtonBuilder().tap(configure)))
        }
    }

    static class SplashTextBuilder {
        def customSplashes = new CustomSplashesBuilder()

        def setEnabled(boolean enabled) {
            SlightGuiModifications.getCtsConfig().splashText.enabled = enabled
        }

        boolean isEnabled() {
            SlightGuiModifications.getCtsConfig().splashText.enabled
        }

        def setSplashesEnabled(boolean enabled) {
            SlightGuiModifications.getCtsConfig().splashText.removeSplashes = !enabled
        }

        boolean isSplashesEnabled() {
            !SlightGuiModifications.getCtsConfig().splashText.removeSplashes
        }

        def customSplashes(Closure configure) {
            customSplashes.tap(configure)
        }
    }

    static class CustomSplashesBuilder {
        def customSplashes = SlightGuiModifications.getCtsConfig().splashText.customSplashes

        def setEnabled(boolean enabled) {
            SlightGuiModifications.getCtsConfig().splashText.customSplashesEnabled = enabled
        }

        boolean isEnabled() {
            SlightGuiModifications.getCtsConfig().splashText.customSplashesEnabled
        }

        def setApplyMode(mode) {
            SlightGuiModifications.getCtsConfig().splashText.customSplashesApplyMode = SlightGuiModificationsConfig.Cts.SplashText.CustomSplashesApplyMode.valueOf(mode.toUpperCase(Locale.ROOT))
        }

        String getApplyMode() {
            SlightGuiModifications.getCtsConfig().splashText.customSplashesApplyMode.toString().toLowerCase(Locale.ROOT)
        }

        def defineCustom(custom) {
            custom.each { customSplashes.add(it) }
        }
    }

    static class BackgroundBuilder {
        def setBackgroundStayLength(long backgroundStayLength) {
            SlightGuiModifications.getCtsConfig().backgroundStayLength = backgroundStayLength
        }

        long getBackgroundStayLength() {
            SlightGuiModifications.getCtsConfig().backgroundStayLength
        }

        def setBackgroundFadeLength(long backgroundFadeLength) {
            SlightGuiModifications.getCtsConfig().backgroundFadeLength = backgroundFadeLength
        }

        long getBackgroundFadeLength() {
            SlightGuiModifications.getCtsConfig().backgroundFadeLength
        }

        def setRenderGradientShade(boolean renderGradientShade) {
            SlightGuiModifications.getCtsConfig().renderGradientShade = renderGradientShade
        }

        boolean isRenderGradientShade() {
            SlightGuiModifications.getCtsConfig().renderGradientShade
        }

        def clearBackgrounds() {
            SlightGuiModifications.getCtsConfig().backgroundInfos.clear()
        }

        def image(Closure configure) {
            SlightGuiModifications.getCtsConfig().backgroundInfos.add(new BackgroundImageBuilder().tap(configure).build())
        }

        static class BackgroundImageBuilder {
            def texture

            def build() {
                new SlightGuiModificationsConfig.Cts.TextureProvidedBackgroundInfo(texture)
            }
        }
    }

    static class PositionBuilder {
        Closure<Integer> x = { -1 }
        Closure<Integer> y = { -1 }

        def setX(int x) {
            this.x = { x }
        }

        def setY(int y) {
            this.y = { y }
        }

        def x(Closure<Integer> closure) {
            x = closure
        }

        def y(Closure<Integer> closure) {
            y = closure
        }

        Position build() {
            new Position({ (int) it.with(x) }, { (int) it.with(y) })
        }
    }

    static class LabelBuilder implements Middleman.LabelBuilder {
        Text text = literal("")
        String align = "left"
        PositionBuilder position = new PositionBuilder()
        int color = 0xFFFFFF
        int hoveredColor = 0xFFFFFF
        Closure onClicked = {}

        def position(configure) {
            position.with(configure)
        }

        @Override
        Position getPositionBuilt() {
            position.build()
        }
    }

    static class ButtonBuilder implements Middleman.ButtonBuilder {
        Text text = literal("")
        String align = "left"
        PositionBuilder position = new PositionBuilder()
        Closure onClicked = {}
        int width = 200
        int height = 20

        def position(configure) {
            position.with(configure)
        }

        @Override
        Position getPositionBuilt() {
            position.build()
        }
    }
}
