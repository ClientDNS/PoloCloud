package de.polocloud.database.manager;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.groups.impl.SimpleServiceGroup;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.database.DatabaseConfiguration;
import de.polocloud.database.CloudDatabaseProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SQLCloudDatabaseHandler implements CloudDatabaseProvider {

    private static final String DEFAULT_JDBC = "jdbc:mysql://";
    private static final String DEFAULT_PROPERTIES = "&serverTimezone=UTC&autoReconnect=true";

    private static final String GROUP_TABLE = "cloudsystem_groups";

    private Connection connection;
    private final DatabaseConfiguration config;

    @SneakyThrows
    public void connect() {
        this.connection = DriverManager.getConnection(getConnectionUrl());

        this.createTable();

        CloudAPI.getInstance().getLogger().log("The connection is now established to the database.");
    }

    @SneakyThrows
    private boolean doesCloudTableExist() {
        return this.connection.getMetaData().getTables(null, null, GROUP_TABLE, new String[]{"TABLE"}).next();
    }

    public void createTable() {
        this.executeUpdate("CREATE TABLE IF NOT EXISTS " + GROUP_TABLE + "(" +
            "name VARCHAR(100), " +
            "template VARCHAR(100), " +
            "node VARCHAR(100), " +
            "maxMemory INT, " +
            "minOnlineService INT, " +
            "maxOnlineService INT, " +
            "static BOOL, " +
            "fallback BOOL, " +
            "version VARCHAR(100), " +
            "maxPlayers INT, " +
            "motd TEXT, " +
            "maintenance BOOL, " +
            "autoUpdating BOOL, " +
            "PRIMARY KEY (name))");
    }

    private String getConnectionUrl() {
        return DEFAULT_JDBC + this.config.getHostname() + ":" + this.config.getPort() + "/" + this.config.getDatabase()
            + "?user=" + this.config.getUsername() + "&password=" + this.config.getPassword() + DEFAULT_PROPERTIES;
    }

    @Override
    public void removeGroup(final @NotNull ServiceGroup serviceGroup) {
        this.executeUpdate("DELETE FROM " + GROUP_TABLE + " WHERE name ='" + serviceGroup.getName() + "'");
    }

    @SneakyThrows
    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        final List<ServiceGroup> groups = new ArrayList<>();
        try (final var preparedStatement = this.connection
            .prepareStatement("SELECT * FROM " + GROUP_TABLE); var result = preparedStatement.executeQuery()) {
            while (result.next()) {
                groups.add(new SimpleServiceGroup(
                    result.getString("name"),
                    result.getString("template"),
                    result.getString("node"),
                    result.getString("motd"),
                    result.getInt("maxMemory"),
                    result.getInt("maxPlayers"),
                    result.getInt("minOnlineService"),
                    result.getInt("maxOnlineService"),
                    result.getBoolean("static"),
                    result.getBoolean("fallback"),
                    result.getBoolean("maintenance"),
                    result.getBoolean("autoUpdating"),
                    GameServerVersion.getVersionByName(result.getString("version"))));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return groups;
    }

    @Override
    public void updateGroupProperty(@NotNull String group, @NotNull String property, @NotNull Object value) {
        this.executeUpdate("UPDATE " + GROUP_TABLE + " SET " + property + " = '" + value + "' WHERE name = '" + group + "'");
    }

    @SneakyThrows
    @Override
    public void disconnect() {
        this.connection.close();
    }

    @Override
    public void addGroup(final @NotNull ServiceGroup group) {
        this.executeUpdate("INSERT INTO " + GROUP_TABLE +
            "(name, template, node, maxMemory, minOnlineService, maxOnlineService, static, fallback, version, maxPlayers, motd, maintenance, autoUpdating) " +
            "VALUES (" + "'" + group.getName() + "', '" + group.getTemplate() + "', '" + group.getNode() + "', " + group.getMaxMemory() + ", " + group.getMinOnlineService() + ", " + group.getMaxOnlineService() + ", " + conBool(group.isStatic()) + ", " + conBool(group.isFallbackGroup()) + ", '" + group.getGameServerVersion().getName() + "', " + group.getDefaultMaxPlayers() + ",'" + group.getMotd() + "', " + conBool(group.isMaintenance()) + ", " + conBool(group.isAutoUpdating()) + ");");
    }

    private int conBool(final boolean state) {
        return (state ? 1 : 0);
    }

    public void executeUpdate(final @NotNull String url) {
        try (final var preparedStatement = this.connection.prepareStatement(url)) {
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
