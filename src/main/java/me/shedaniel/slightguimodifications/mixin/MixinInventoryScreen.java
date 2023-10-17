package me.shedaniel.slightguimodifications.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.slightguimodifications.SlightGuiModifications;
import me.shedaniel.slightguimodifications.listener.AnimationListener;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {
    @Inject(method = "renderEntityInInventory", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER))
    private static void drawEntityChangeY(GuiGraphics guiGraphics, float f, float g, int i, Vector3f vector3f, Quaternionf quaternionf, Quaternionf quaternionf2, LivingEntity livingEntity, CallbackInfo ci) {
        guiGraphics.pose().translate(0, SlightGuiModifications.applyYAnimation(0), 0);
    }
}
