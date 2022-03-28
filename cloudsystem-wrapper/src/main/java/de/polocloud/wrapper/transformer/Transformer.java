package de.polocloud.wrapper.transformer;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

public interface Transformer {

    /**
     * credits to @CloudNetServices
     * GitHub: https://github.com/CloudNetService/CloudNet-v3
     */
    void transform(@NotNull String className, @NotNull ClassNode classNode);

}
