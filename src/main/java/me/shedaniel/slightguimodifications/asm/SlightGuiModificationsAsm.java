package me.shedaniel.slightguimodifications.asm;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class SlightGuiModificationsAsm implements Runnable {
    @Override
    public void run() {
        MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
        ClassTinkerers.addTransformation(resolver.mapClassName("intermediary", "net.minecraft.class_4584"), classNode -> {
            String class_4588 = resolver.mapClassName("intermediary", "net.minecraft.class_4588").replace('.', '/');
            String vertex = resolver.mapMethodName("intermediary", "net.minecraft.class_4588", "method_22912", "(DDD)Lnet/minecraft/class_4588;");
            String color = resolver.mapMethodName("intermediary", "net.minecraft.class_4588", "method_1336", "(IIII)Lnet/minecraft/class_4588;");
            for (MethodNode method : classNode.methods)
                if (method.name.equals(vertex) && method.desc.startsWith("(DDD)L" + class_4588)) {    /* transform vertex y*/
                    AbstractInsnNode first = method.instructions.getFirst();
                    method.instructions.insertBefore(first, new VarInsnNode(24, 3));
                    method.instructions.insertBefore(first, new MethodInsnNode(184, "me/shedaniel/slightguimodifications/SlightGuiModifications", "applyYAnimation", "(D)D", false));
                    method.instructions.insertBefore(first, new VarInsnNode(57, 3));
                } else if (method.name.equals(color) && method.desc.startsWith("(IIII)L" + class_4588)) {    /* transform color alpha*/
                    AbstractInsnNode first = method.instructions.getFirst();
                    method.instructions.insertBefore(first, new VarInsnNode(21, 4));
                    method.instructions.insertBefore(first, new MethodInsnNode(184, "me/shedaniel/slightguimodifications/SlightGuiModifications", "applyAlphaAnimation", "(I)I", false));
                    method.instructions.insertBefore(first, new VarInsnNode(54, 4));
                }
        });
    }
}
