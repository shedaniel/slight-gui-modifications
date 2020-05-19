package me.shedaniel.slightguimodifications.gui.cts;

import io.github.cottonmc.parchment.api.ScriptLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CtsRegistry {
    public static final Logger LOGGER = LogManager.getLogger("CtsRegistry");
    
    public static void loadScriptsAsync() {
        CompletableFuture.runAsync(CtsRegistry::loadScripts);
    }
    
    public static void loadScripts() {
        try {
            LOGGER.info("Start Loading of CTS.");
            File scriptFile = new File(FabricLoader.getInstance().getConfigDirectory(), "slightguimodifications/cts.groovy");
            if (!scriptFile.exists()) {
                FileWriter writer = new FileWriter(scriptFile);
                writer.write("// Here is the groovy file for screen elements\n" +
                             "info(\"Hello From 'Slight' Gui Modifications cts script\")\n" +
                             "\n" +
                             "mainMenu {\n" +
                             "    enabled = false // Set to true to enable this module\n" +
                             "\n" +
                             "    splashText {\n" +
                             "        enabled = false // Set to true to enable this module\n" +
                             "        splashesEnabled = true // Set to false to disable splashes entirely\n" +
                             "\n" +
                             "        customSplashes {\n" +
                             "            enabled = false // Set to true to enable this module\n" +
                             "            // You can put either \"override\" or \"append\" here to declare how you want to provide custom splashes\n" +
                             "            applyMode = \"override\"\n" +
                             "            defineCustom([\"Wood\", \"Potato\", \"Stone\"])\n" +
                             "        }\n" +
                             "    }\n" +
                             "\n" +
                             "    background {\n" +
                             "        clearBackgrounds() // This line removes the rotating cube\n" +
                             "        backgroundStayLength = 10000 // This sets the length a background would stay\n" +
                             "        backgroundFadeLength = 10000 // This sets the fade duration to another background\n" +
                             "        renderGradientShade = false // This sets whether the slight shade should be rendered\n" +
                             "        image {\n" +
                             "            texture = file(\"config/slightguimodifications/background.png\") // Remember to use forward slash to support unix!\n" +
                             "            texture = resource(\"slightguimodifications:background.png\") // Here to use a resource location / identifier\n" +
                             "        }\n" +
                             "    }\n" +
                             "\n" +
                             "    // Uncomment these to remove aspects of the title screen\n" +
                             "    // removeMinecraftLogo()\n" +
                             "    // removeEditionBadge()\n" +
                             "\n" +
                             "    // Clear all buttons already on screen\n" +
                             "    // clearAllButtons()\n" +
                             "\n" +
                             "    label {\n" +
                             "        position {\n" +
                             "            x = 2\n" +
                             "            y { it - 20 }\n" +
                             "        }\n" +
                             "\n" +
                             "        // You can create a text with \"literal\" or \"translatable\" if you want to translate with Resource Packs\n" +
                             "        text = literal(\"Custom Version Here!\")\n" +
                             "        // The alignment here can be \"left\", \"center\" or \"right\", default is \"left\"\n" +
                             "        align = \"left\"\n" +
                             "        // Color of the text, default is 0xFFFFFF\n" +
                             "        color = 0xFFFFFF\n" +
                             "        // Mouse Hovered Color of the text, default is 0xFFFFFF\n" +
                             "        hoveredColor = 0xFFFFFF\n" +
                             "        // Mouse Click Function, default is nothing, here's a list of options\n" +
                             "        onClicked = nothing()\n" +
                             "        onClicked = url(\"https://www.google.com\")\n" +
                             "        onClicked = modMenu()\n" +
                             "        onClicked = language()\n" +
                             "        onClicked = options()\n" +
                             "        onClicked = exit()\n" +
                             "        onClicked = accessibility()\n" +
                             "        onClicked = singleplayer()\n" +
                             "        onClicked = multiplayer()\n" +
                             "        onClicked = realms()\n" +
                             "        onClicked = reloadCts()\n" +
                             "    }\n" +
                             "\n" +
                             "    button {\n" +
                             "        position {\n" +
                             "            x = 5\n" +
                             "            y = 5\n" +
                             "        }\n" +
                             "        width = 200\n" +
                             "        height = 20\n" +
                             "\n" +
                             "        // You can create a text with \"literal\" or \"translatable\" if you want to translate with Resource Packs\n" +
                             "        text = literal(\"Random Button\")\n" +
                             "        // The alignment here can be \"left\", \"center\" or \"right\", default is \"left\"\n" +
                             "        align = \"left\"\n" +
                             "        // Mouse Click Function, default is nothing, look up see the list\n" +
                             "        onClicked = nothing()\n" +
                             "    }\n" +
                             "}");
                writer.close();
            }
            List<String> lines = Files.readAllLines(scriptFile.toPath());
            lines.add(0, "import static me.shedaniel.slightguimodifications.gui.cts.script.ScriptDSL.*\n");
            String content = String.join("\n", lines);
            ScriptLoader.INSTANCE.loadScript(ScriptLoader.ScriptFactory.SIMPLE, new Identifier("config.groovy"), content).getEngine().eval(content);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
