package de.polocloud.base.command;

import de.polocloud.base.Base;
import lombok.Getter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Getter
public abstract class CloudCommand {

    private final String name, description;
    private final String[] aliases;

    public CloudCommand() {
        final var annotation = getClass().getAnnotation(Command.class);
        this.name = annotation.name();
        this.description = annotation.description();
        this.aliases = annotation.aliases();
    }

    public abstract void execute(Base base, String[] args);

    public List<String> tabComplete(final String[] arguments) {
        return null;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {

        String name();
        String description() default "";
        String[] aliases() default {};

    }

}
