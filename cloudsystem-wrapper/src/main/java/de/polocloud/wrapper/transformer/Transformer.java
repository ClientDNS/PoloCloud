package de.polocloud.wrapper.transformer;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

public interface Transformer {

    void transform(@NotNull String className, @NotNull ClassNode classNode);

}
