package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.ConfigButtonWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {
    protected MixinOptionsScreen(Component title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("modmenu")) return;
        this.addRenderableWidget(new ConfigButtonWidget(this.width - 105, this.height - 25, 100, 20, Component.translatable("text.slightguimodifications"), (buttonWidget) -> {
            this.minecraft.setScreen(SlightGuiModifications.getConfigScreen(this));
        }));
    }
}
