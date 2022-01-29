package de.bytemc.cloud.api.services.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceState {

    PREPARED("§6Prepared"),
    STARTING("§eStarting"),
    ONLINE("§aOnline"),
    STOPPING("§cStopping");

    private final String name;

}
