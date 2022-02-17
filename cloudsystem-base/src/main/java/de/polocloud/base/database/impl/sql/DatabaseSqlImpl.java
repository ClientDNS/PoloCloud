package de.polocloud.base.database.impl.sql;

import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.groups.impl.ServiceGroup;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.base.database.DatabaseConfiguration;
import de.polocloud.base.database.IDatabase;
import de.polocloud.base.database.DatabaseFunction;
import de.polocloud.network.promise.CommunicationPromise;
import de.polocloud.network.promise.ICommunicationPromise;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DatabaseSqlImpl implements IDatabase {

    private HikariDataSource source;

    @SneakyThrows
    @Override
    public void connect(final @NotNull DatabaseConfiguration config) {

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getDatabase() + "?useSSL=false&useUnicode=true&autoReconnect=tru");
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setMaximumPoolSize(5);

        this.source = new HikariDataSource(hikariConfig);

        executeUpdate("CREATE TABLE IF NOT EXISTS cloudsystem_groups(name VARCHAR(100), template VARCHAR(100), node VARCHAR(100)," +
            " memory INT, minOnlineService INT, maxOnlineService INT, static INT, fallbackGroup INT, version VARCHAR(100), maxPlayers INT, motd TEXT, maintenance INT, autoUpdating INT)");
        CloudAPI.getInstance().getLogger().log("The connection is now established to the database.");
    }

    @SneakyThrows
    @Override
    public @NotNull ICommunicationPromise<Void> disconnect() {
        final ICommunicationPromise<Void> shutdownPromise = new CommunicationPromise<>();
        this.source.close();
        shutdownPromise.setSuccess(null);
        return shutdownPromise;
    }

    @Override
    public void addGroup(final @NotNull IServiceGroup serviceGroup) {
        executeUpdate("INSERT INTO cloudsystem_groups(name, template, node, memory, minOnlineService, maxOnlineService, static, fallbackGroup, version, maxPlayers, motd, maintenance, autoUpdating) VALUES (" +
            "'" + serviceGroup.getName() + "', '" + serviceGroup.getTemplate() + "', '" + serviceGroup.getNode() + "', " + serviceGroup.getMemory() + ", " +
            serviceGroup.getMinOnlineService() + ", " + serviceGroup.getMaxOnlineService() + ", " + (serviceGroup.isStatic() ? 1 : 0) +
            ", " + (serviceGroup.isFallbackGroup() ? 1 : 0) + ", '" + serviceGroup.getGameServerVersion().getName() + "', " + serviceGroup.getDefaultMaxPlayers() +
            ",'" + serviceGroup.getMotd() + "', '" + (serviceGroup.isMaintenance() ? 1 : 0) + "', '" + (serviceGroup.isAutoUpdating() ? 1 : 0) + "');");
    }

    @Override
    public void removeGroup(final @NotNull IServiceGroup serviceGroup) {
        executeUpdate("DELETE FROM cloudsystem_groups WHERE name='" + serviceGroup.getName() + "'");
    }

    @Override
    public List<IServiceGroup> getAllServiceGroups() {
        return executeQuery("SELECT * FROM cloudsystem_groups", resultSet -> {
            List<IServiceGroup> groups = Lists.newArrayList();
            while (resultSet.next()) {
                ServiceGroup serviceGroup = new ServiceGroup(
                    resultSet.getString("name"),
                    resultSet.getString("template"),
                    resultSet.getString("node"),
                    resultSet.getString("motd"),
                    resultSet.getInt("memory"),
                    resultSet.getInt("maxPlayers"),
                    resultSet.getInt("minOnlineService"),
                    resultSet.getInt("maxOnlineService"),
                    resultSet.getBoolean("static"),
                    resultSet.getBoolean("fallbackGroup"),
                    resultSet.getBoolean("maintenance"),
                    resultSet.getBoolean("autoUpdating"),
                    GameServerVersion.getVersionByName(resultSet.getString("version")));
                groups.add(serviceGroup);
            }
            return groups;
        }, Lists.newArrayList());
    }

    @SneakyThrows
    public <T> T executeQuery(String query, DatabaseFunction<ResultSet, T> function, T defaultValue) {
        Objects.requireNonNull(this.source.getConnection(), "Try to execute a statement, but the connection is null.");
        try (var preparedStatement = this.source.getConnection().prepareStatement(query)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                return function.apply(resultSet);
            } catch (Exception throwable) {
                return defaultValue;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return defaultValue;
    }

    @SneakyThrows
    public void executeUpdate(final String query) {
        Objects.requireNonNull(this.source.getConnection(), "Try to update a statement, but the connection is null.");
        try (var preparedStatement = this.source.getConnection().prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}
