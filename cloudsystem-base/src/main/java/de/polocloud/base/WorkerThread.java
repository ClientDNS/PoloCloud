package de.polocloud.base;

import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.service.ServiceState;
import de.polocloud.base.service.LocalService;
import de.polocloud.base.service.SimpleServiceManager;
import de.polocloud.base.service.port.PortHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class WorkerThread extends Thread {

    private final static int MAX_BOOTABLE_SERVICES = 1;

    private final Base base;
    private final StringBuffer stringBuffer = new StringBuffer();
    private final byte[] bytes = new byte[2048];

    public WorkerThread(final Base base) {
        this.base = base;
    }

    @Override
    public void run() {
        while (this.base.isRunning()) {
            this.checkForQueue();

            for (final var service : this.base.getServiceManager().getAllCachedServices()) {
                if (service instanceof LocalService localService) {
                    final var process = localService.getProcess();
                    if (process == null) continue;
                    if (process.isAlive()) {
                        this.readStream(process.getErrorStream(),
                            s -> this.base.getLogger().log("[" + service.getName() + "] " + s, LogType.ERROR));
                        if (localService.isScreen()) this.readStream(process.getInputStream(),
                            s -> this.base.getLogger().log("[" + service.getName() + "] " + s, LogType.INFO));
                    }
                }
            }

            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkForQueue() {
        this.addServiceToQueueWhereProvided();
        if (this.minBootableServiceExists()) return;
        final var services = this.base.getServiceManager().getAllServicesByState(ServiceState.PREPARED)
            .stream().filter(service -> service.getGroup().getNode().equalsIgnoreCase(this.base.getNode().getName())).toList();
        if (services.isEmpty()) return;
        ((SimpleServiceManager) this.base.getServiceManager()).start(services.get(0));
    }

    private void addServiceToQueueWhereProvided() {
        this.base.getGroupManager().getAllCachedServiceGroups().stream()
            .filter(serviceGroup -> serviceGroup.getNode().equalsIgnoreCase(this.base.getNode().getName()))
            .filter(serviceGroup -> this.getAmountOfGroupServices(serviceGroup) < serviceGroup.getMinOnlineService())
            .forEach(serviceGroup -> {
                final var service = new LocalService(serviceGroup, this.getPossibleServiceIDByGroup(serviceGroup),
                    PortHandler.getNextPort(serviceGroup), this.base.getNode().getHostName());
                this.base.getServiceManager().getAllCachedServices().add(service);
                this.base.getNode().sendPacketToAll(new ServiceAddPacket(service));
                this.base.getLogger()
                    .log("The group '§b" + serviceGroup.getName() + "§7' start new instance of '§b" + service.getName()
                        + "§7' (§6Prepared§7)");
            });
    }

    private boolean minBootableServiceExists() {
        return this.getAmountOfBootableServices() >= MAX_BOOTABLE_SERVICES;
    }

    private int getAmountOfBootableServices() {
        return this.base.getServiceManager().getAllServicesByState(ServiceState.STARTING).size();
    }

    private int getAmountOfGroupServices(final ServiceGroup serviceGroup) {
        return (int) this.base.getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getGroup().equals(serviceGroup)).count();
    }

    private int getPossibleServiceIDByGroup(final ServiceGroup serviceGroup) {
        int id = 1;
        while (this.isServiceIdAlreadyExists(serviceGroup, id)) id++;
        return id;
    }

    private boolean isServiceIdAlreadyExists(final ServiceGroup serviceGroup, int id) {
        return this.base.getServiceManager().getAllServicesByGroup(serviceGroup)
            .stream().anyMatch(it -> id == it.getServiceId());
    }

    private void readStream(final InputStream inputStream, final Consumer<String> consumer) {
        int length;
        try {
            while (inputStream.available() > 0
                && (length = inputStream.read(this.bytes, 0, this.bytes.length)) != -1) {
                this.stringBuffer.append(new String(this.bytes, 0, length, StandardCharsets.UTF_8));
            }

            final var string = this.stringBuffer.toString();
            if (string.contains("\n")) {
                for (final var s : string.split("\n")) consumer.accept(s);
            }
            this.stringBuffer.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
