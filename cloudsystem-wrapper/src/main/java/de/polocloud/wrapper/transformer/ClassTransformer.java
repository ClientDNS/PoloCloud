package de.polocloud.wrapper.transformer;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.function.Predicate;

public record ClassTransformer(
    @NotNull Transformer transformer,
    @NotNull Predicate<String> predicate,
    @NotNull Instrumentation instrumentation
) implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> clazz, ProtectionDomain protectionDomain, byte[] classBuffer) {
        if (clazz == null && this.predicate.test(className)) {
                final var node = new ClassNode();
                final var reader = new ClassReader(classBuffer);
                reader.accept(node, 0);
                this.transformer.transform(className, node);
                this.instrumentation.removeTransformer(this);
                var writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                node.accept(writer);
                return writer.toByteArray();
        }
        return null;
    }

}
