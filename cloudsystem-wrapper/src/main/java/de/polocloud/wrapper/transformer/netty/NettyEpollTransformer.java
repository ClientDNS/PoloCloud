package de.polocloud.wrapper.transformer.netty;

import de.polocloud.wrapper.transformer.Transformer;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

/**
 *
 * This code was only created with the help of another person.
 * Credits go to @derklaro
 * GitHub: https://github.com/derklaro
 *
 */

public final class NettyEpollTransformer implements Transformer {

    @Override
    public void transform(@NotNull String className, @NotNull ClassNode classNode) {
        for (final var method : classNode.methods) {
            if (method.name.equals("<clinit>")) {
                for (var instruction : method.instructions) {
                    if (instruction instanceof MethodInsnNode methodInsnNode && methodInsnNode.name.equals("epollCreate")) {
                        var instructions = new InsnList();
                        instructions.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(UnsupportedOperationException.class)));
                        instructions.add(new InsnNode(Opcodes.DUP));
                        instructions.add(new LdcInsnNode("Netty 4.0.X is incompatible with Java 9+"));
                        instructions.add(new MethodInsnNode(
                            Opcodes.INVOKESPECIAL,
                            Type.getInternalName(UnsupportedOperationException.class),
                            "<init>",
                            "(Ljava/lang/String;)V",
                            false));
                        instructions.add(new FieldInsnNode(
                            Opcodes.PUTSTATIC,
                            String.join("/", "io", "netty", "channel", "epoll", "Epoll"),
                            "UNAVAILABILITY_CAUSE",
                            Type.getDescriptor(Throwable.class)));
                        instructions.add(new InsnNode(Opcodes.RETURN));
                        method.instructions.insert(instructions);
                        return;
                    }
                }
            }
        }
    }
}
