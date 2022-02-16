package de.bytemc.cloud.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeConfiguration {

    private String nodeName;
    private String hostname;
    private int port;

}
