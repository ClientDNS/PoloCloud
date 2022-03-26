package de.polocloud.wrapper.transformer.bukkit;

import de.polocloud.wrapper.transformer.Transformer;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;

public final class PaperConfigTransformer implements Transformer {

    @Override
    public void transform(@NotNull String className, @NotNull ClassNode classNode) {
        for (final var method : classNode.methods) {
            if (method.name.equals("stackableBuckets")) {
                method.instructions.insert(new InsnNode(Opcodes.RETURN));
            }
        }
    }

}
