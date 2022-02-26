package de.polocloud.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
@AllArgsConstructor
@Getter
public class PropertyFile {

    private final String node;
    private final String hostname;
    private final String service;
    private final int port;

}
