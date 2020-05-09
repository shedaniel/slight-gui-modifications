package me.shedaniel.slightguimodifications.mixin;

import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.gui.ConfigButtonWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SettingsScreen.class)
public class MixinSettingsScreen extends Screen {
    protected MixinSettingsScreen(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("modmenu")) return;
        this.addButton(new ConfigButtonWidget(this.width - 105, this.height - 25, 100, 20, I18n.translate("text.slightguimodifications"), (buttonWidget) -> {
            this.minecraft.openScreen(SlightGuiModifications.getConfigScreen(this));
        }));
    }
}
