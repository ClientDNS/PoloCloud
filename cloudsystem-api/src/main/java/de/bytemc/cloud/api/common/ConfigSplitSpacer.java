package de.bytemc.cloud.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConfigSplitSpacer {

    YAML(": "),
    PROPERTIES("=");
    private final String split;

}
