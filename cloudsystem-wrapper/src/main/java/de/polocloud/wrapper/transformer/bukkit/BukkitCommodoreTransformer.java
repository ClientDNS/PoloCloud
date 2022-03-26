package de.polocloud.wrapper.transformer.bukkit;

import de.polocloud.wrapper.transformer.Transformer;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public final class BukkitCommodoreTransformer implements Transformer {

    @Override
    public void transform(@NotNull String className, @NotNull ClassNode classNode) {
        for (final var method : classNode.methods) {
            if (method.name.equals("convert") && method.desc.equals("([BZ)[B")) {
                int asmVersion = -1;
                for (final var instruction : method.instructions) {
                    if (instruction.getOpcode() == Opcodes.LDC
                        && instruction instanceof LdcInsnNode ldcInsnNode
                        && ldcInsnNode.cst instanceof Integer integer) {
                        asmVersion = integer;
                        break;
                    }
                }

                if (asmVersion < Opcodes.ASM8) {
                    for (var instruction : method.instructions) {
                        if (instruction.getOpcode() == Opcodes.INVOKESPECIAL
                            && instruction instanceof MethodInsnNode methodInsnNode
                            && methodInsnNode.name.equals("<init>")
                            && methodInsnNode.owner.endsWith("org/objectweb/asm/ClassReader")) {
                            final var next = instruction.getNext();
                            if (next != null && next.getOpcode() == Opcodes.ASTORE && next instanceof VarInsnNode varInsnNode) {
                                final var beginLabel = new LabelNode();
                                final var finishLabel = new LabelNode();
                                final var handlerLabel = new LabelNode();
                                method.instructions.insert(beginLabel);
                                method.instructions.insert(next, finishLabel);

                                final var catchDimension = new InsnList();
                                catchDimension.add(handlerLabel);
                                catchDimension.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                catchDimension.add(new InsnNode(Opcodes.ARETURN));
                                method.instructions.add(catchDimension);
                                method.tryCatchBlocks.add(new TryCatchBlockNode(
                                    beginLabel,
                                    finishLabel,
                                    handlerLabel,
                                    Type.getInternalName(IllegalArgumentException.class)));

                                final var nonRecordValidateDimension = new InsnList();
                                final var validationEnvironment = new Label();
                                nonRecordValidateDimension.add(new VarInsnNode(Opcodes.ALOAD, varInsnNode.var));
                                nonRecordValidateDimension.add(new MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    methodInsnNode.owner,
                                    "getAccess",
                                    "()I",
                                    false));
                                nonRecordValidateDimension.add(new LdcInsnNode(Opcodes.ACC_RECORD));
                                nonRecordValidateDimension.add(new InsnNode(Opcodes.IAND));
                                nonRecordValidateDimension.add(new JumpInsnNode(Opcodes.IFEQ, new LabelNode(validationEnvironment)));
                                nonRecordValidateDimension.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                nonRecordValidateDimension.add(new InsnNode(Opcodes.ARETURN));
                                nonRecordValidateDimension.add(new LabelNode(validationEnvironment));
                                method.instructions.insert(next, nonRecordValidateDimension);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
