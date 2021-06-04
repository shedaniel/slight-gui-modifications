package me.shedaniel.slightguimodifications.gui.cts;

import com.google.common.base.Suppliers;
import groovy.lang.GroovyShell;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CtsRegistry {
    public static final Logger LOGGER = LogManager.getLogger("CtsRegistry");
    
    private static final Supplier<GroovyShell> SHELL = Suppliers.memoize(() -> new GroovyShell(FabricLauncherBase.getLauncher().getTargetClassLoader()));
    
    public static void loadScriptsAsync() {
        CompletableFuture.runAsync(CtsRegistry::loadScripts);
    }
    
    public static void loadScripts() {
        try {
            LOGGER.info("Start Loading of CTS.");
            File scriptFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "slightguimodifications/cts.groovy");
            if (!scriptFile.exists()) {
                FileWriter writer = new FileWriter(scriptFile);
                writer.write("""
                        // Here is the groovy file for screen elements
                        info("Hello From 'Slight' Gui Modifications cts script")
                                
                        mainMenu {
                            enabled = false // Set to true to enable this module
                                
                            splashText {
                                enabled = false // Set to true to enable this module
                                splashesEnabled = true // Set to false to disable splashes entirely
                                
                                customSplashes {
                                    enabled = false // Set to true to enable this module
                                    // You can put either "override" or "append" here to declare how you want to provide custom splashes
                                    applyMode = "override"
                                    defineCustom(["Wood", "Potato", "Stone"])
                                }
                            }
                                
                            background {
                                clearBackgrounds() // This line removes the rotating cube
                                backgroundStayLength = 10000 // This sets the length a background would stay
                                backgroundFadeLength = 10000 // This sets the fade duration to another background
                                renderGradientShade = false // This sets whether the slight shade should be rendered
                                image {
                                    texture = file("config/slightguimodifications/background.png") // Remember to use forward slash to support unix!
                                    texture = resource("slightguimodifications:background.png") // Here to use a resource location / identifier
                                }
                            }
                                
                            // Uncomment these to remove aspects of the title screen
                            // removeMinecraftLogo()
                            // removeEditionBadge()
                                
                            // Clear all buttons already on screen
                            // clearAllButtons()
                                
                            label {
                                position {
                                    x = 2
                                    y { it - 20 }
                                }
                                
                                // You can create a text with "literal" or "translatable" if you want to translate with Resource Packs
                                text = literal("Custom Version Here!")
                                // The alignment here can be "left", "center" or "right", default is "left"
                                align = "left"
                                // Color of the text, default is 0xFFFFFF
                                color = 0xFFFFFF
                                // Whether the label has a shadow, default is false
                                shadow = true
                                // Mouse Hovered Color of the text, default is 0xFFFFFF
                                hoveredColor = 0xFFFFFF
                                // Mouse Click Function, default is nothing, here's a list of options
                                onClicked = nothing()
                                onClicked = url("https://www.google.com")
                                onClicked = modMenu()
                                onClicked = language()
                                onClicked = options()
                                onClicked = exit()
                                onClicked = accessibility()
                                onClicked = singleplayer()
                                onClicked = multiplayer()
                                onClicked = realms()
                                onClicked = reloadCts()
                            }
                                
                            button {
                                position {
                                    x = 5
                                    y = 5
                                }
                                width = 200
                                height = 20
                                
                                // You can create a text with "literal" or "translatable" if you want to translate with Resource Packs
                                text = literal("Random Button")
                                // The alignment here can be "left", "center" or "right", default is "left"
                                align = "left"
                                // Mouse Click Function, default is nothing, look up see the list
                                onClicked = nothing()
                            }
                        }""");
                writer.close();
            }
            List<String> lines = Files.readAllLines(scriptFile.toPath());
            lines.add(0, "import static me.shedaniel.slightguimodifications.gui.cts.script.ScriptDSL.*\n");
            String content = String.join("\n", lines);
            SHELL.get().evaluate(content);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
