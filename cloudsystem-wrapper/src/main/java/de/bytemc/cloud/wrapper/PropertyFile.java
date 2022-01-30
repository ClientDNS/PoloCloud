package de.bytemc.cloud.wrapper;

public class PropertyFile {

    private final String node;
    private final String hostname;
    private final String service;
    private final int port;

    public PropertyFile(final String node, final String hostname, final String service, final int port) {
        this.node = node;
        this.hostname = hostname;
        this.service = service;
        this.port = port;
    }

    public String getNode() {
        return this.node;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getService() {
        return this.service;
    }

    public int getPort() {
        return this.port;
    }

}
