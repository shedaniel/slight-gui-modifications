package me.shedaniel.slightguimodifications.asm;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SlightGuiModificationsMixinPlugin implements IMixinConfigPlugin {
    private static final ImmutableMap<String, String[]> MIXIN_REQUIRES_MODS = ImmutableMap.<String, String[]>builder()
            .put("MixinOverlaySearchField", new String[]{"roughlyenoughitems"})
            .put("MixinRecipeViewingScreen", new String[]{"roughlyenoughitems"})
            .put("MixinVillagerRecipeViewingScreen", new String[]{"roughlyenoughitems"})
            .put("MixinREITextFieldWidget", new String[]{"roughlyenoughitems"})
            .put("MixinScissorsHandlerImpl", new String[]{"cloth-config2"})
            .put("MixinModsScreen", new String[]{"modmenu"})
            .put("MixinModListWidget", new String[]{"modmenu"})
            .build();
    
    @Override
    public void onLoad(String mixinPackage) {}
    
    @Override
    public String getRefMapperConfig() {return null;}
    
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String className = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        String[] mods = MIXIN_REQUIRES_MODS.get(className);
        if (mods == null) return true;
        for (String mod : mods) if (!FabricLoader.getInstance().isModLoaded(mod)) return false;
        return true;
    }
    
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    
    @Override
    public List<String> getMixins() {return null;}
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
