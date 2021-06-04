package me.shedaniel.slightguimodifications.mixin;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;

@Mixin(CycleButton.Builder.class)
public interface CycleButtonBuilderAccessor {
    @Accessor
    Function getValueStringifier();
}
