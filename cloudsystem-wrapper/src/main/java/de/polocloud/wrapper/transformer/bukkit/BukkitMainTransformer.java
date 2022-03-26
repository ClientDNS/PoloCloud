package de.polocloud.wrapper.transformer.bukkit;

import de.polocloud.wrapper.transformer.Transformer;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class BukkitMainTransformer implements Transformer {

    @Override
    public void transform(@NotNull String className, @NotNull ClassNode classNode) {
        var state = SearchingState.SEARCHING;
        for (final var method : classNode.methods) {
            if (method.name.equals("main")) {
                final var index = this.findVersionStoreIndex(method);
                if (index != -1) {
                    for (var instruction : method.instructions) {
                        if (state == SearchingState.SEARCHING
                            && instruction.getOpcode() == Opcodes.FLOAD
                            && instruction instanceof VarInsnNode varInsnNode
                            && varInsnNode.var == index) {
                            state = SearchingState.WAITING;
                            continue;
                        }

                        if (state == SearchingState.WAITING
                            && (instruction.getOpcode() == Opcodes.IFLE || instruction.getOpcode() == Opcodes.IFNE)) {
                            state = SearchingState.REMOVING;
                            continue;
                        }

                        if (state == SearchingState.REMOVING) {
                            method.instructions.remove(instruction);
                            if (instruction.getOpcode() == Opcodes.RETURN) {
                                state = SearchingState.SEARCHING;
                            }
                        }
                    }
                }
            }
        }
    }

    private int findVersionStoreIndex(@NotNull MethodNode methodNode) {
        for (var instruction : methodNode.instructions) {
            if (instruction.getOpcode() == Opcodes.LDC
                && instruction instanceof LdcInsnNode node
                && node.cst instanceof String string
                && string.equals("java.class.version")) {
                var next = node.getNext().getNext().getNext();
                if (next.getOpcode() == Opcodes.FSTORE && next instanceof VarInsnNode varInsnNode) {
                    return varInsnNode.var;
                }
            }
        }
        return -1;
    }

    private enum SearchingState {
        SEARCHING,
        WAITING,
        REMOVING
    }
}
