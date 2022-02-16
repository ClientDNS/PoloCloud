package de.polocloud.base.config.editor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConfigSplitSpacer {

    YAML(": "),
    TOML(" = ");

    private final String split;

}
