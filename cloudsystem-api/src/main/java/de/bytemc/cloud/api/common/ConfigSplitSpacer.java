package de.bytemc.cloud.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConfigSplitSpacer {

    YAML(": "),
    TOML(" = ");

    private final String split;

}
