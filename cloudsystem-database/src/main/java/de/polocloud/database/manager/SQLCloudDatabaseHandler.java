package de.polocloud.database.manager;

import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.groups.impl.ServiceGroup;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.database.DatabaseConfiguration;
import de.polocloud.database.ICloudDatabaseProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

@RequiredArgsConstructor
public class SQLCloudDatabaseHandler implements ICloudDatabaseProvider {

    private static final String DEFAULT_JDBC = "jdbc:mysql://";
    private static final String DEFAULT_PROPERTIES = "&serverTimezone=UTC&autoReconnect=true&useUnicode=true";

    private static final String TABLE = "cloudsystem_groups";

    private Connection connection;
    private final DatabaseConfiguration config;

    @SneakyThrows
    public void connect() {
        this.connection = DriverManager.getConnection(getConnectionUrl());

        if (connection != null && !doesCloudTableExist()) {
            createTable();
        }

        CloudAPI.getInstance().getLogger().log("The connection is now established to the database.");
    }

    @SneakyThrows
    private boolean doesCloudTableExist() {
        return connection.getMetaData().getTables(null, null, TABLE, new String[]{"TABLE"}).next();
    }

    public void createTable() {
        executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE + "(name VARCHAR(100), template VARCHAR(100), node VARCHAR(100), memory INT, minOnlineService INT, maxOnlineService INT, static INT, fallbackGroup INT, version VARCHAR(100), maxPlayers INT, motd TEXT, maintenance INT, autoUpdating INT, PRIMARY KEY (name))");
    }

    private String getConnectionUrl() {
        return DEFAULT_JDBC + config.getHostname() + ":" + config.getPort() + "/" + config.getDatabase() + "?user" + config.getUsername() + "&password=" + config.getPassword() + DEFAULT_PROPERTIES;
    }

    @Override
    public void removeGroup(final @NotNull IServiceGroup serviceGroup) {
        executeUpdate("DELETE FROM " + TABLE + " WHERE name='" + serviceGroup.getName() + "'");
    }

    @SneakyThrows
    @Override
    public List<IServiceGroup> getAllServiceGroups() {
        List<IServiceGroup> groups = Lists.newArrayList();
        var result = executeQuery("SELECT * FROM " + TABLE);
        while (result.next()) {
            groups.add(new ServiceGroup(
                result.getString("name"), result.getString("template"), result.getString("node"), result.getString("motd"), result.getInt("memory"), result.getInt("maxPlayers"), result.getInt("minOnlineService"), result.getInt("maxOnlineService"), result.getBoolean("static"), result.getBoolean("fallbackGroup"), result.getBoolean("maintenance"), result.getBoolean("autoUpdating"), GameServerVersion.getVersionByName(result.getString("version"))));
        }
        return groups;
    }

    @Override
    public void addGroup(final @NotNull IServiceGroup group) {
        executeUpdate("INSERT INTO " + TABLE + "(name, template, node, memory, minOnlineService, maxOnlineService, static, fallbackGroup, version, maxPlayers, motd, maintenance, autoUpdating) VALUES (" + "'" + group.getName() + "', '" + group.getTemplate() + "', '" + group.getNode() + "', " + group.getMemory() + ", " + group.getMinOnlineService() + ", " + group.getMaxOnlineService() + ", " + conBool(group.isStatic()) + ", " + conBool(group.isFallbackGroup()) + ", '" + group.getGameServerVersion().getName() + "', " + group.getDefaultMaxPlayers() + ",'" + group.getMotd() + "', " + conBool(group.isMaintenance()) + ", " + conBool(group.isAutoUpdating()) + ");");
    }

    private int conBool(final @NotNull boolean state) {
        return (state ? 1 : 0);
    }

    public void executeUpdate(final @NotNull String url) {
        try (var preparedStatement = connection.prepareStatement(url)) {
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private ResultSet executeQuery(final @NotNull String query) {
        try (var preparedStatement = connection.prepareStatement(query); var resultSet = preparedStatement.executeQuery()) {
            return resultSet;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
