package de.polocloud.api.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class ServiceState {

    public static final String PREPARED = "PREPARED";
    public static final String STARTING = "STARTING";
    public static final String ONLINE = "ONLINE";
    public static final String STOPPED = "STOPPED";

}
