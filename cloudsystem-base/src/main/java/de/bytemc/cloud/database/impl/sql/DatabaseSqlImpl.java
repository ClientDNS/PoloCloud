package de.bytemc.cloud.database.impl.sql;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.cloud.config.NodeConfig;
import de.bytemc.cloud.database.DatabaseConfiguration;
import de.bytemc.cloud.database.IDatabase;
import de.bytemc.cloud.database.impl.DatabaseFunction;
import de.bytemc.network.promise.CommunicationPromise;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DatabaseSqlImpl implements IDatabase {

    private Connection connection;

    @SneakyThrows
    @Override
    public void connect() {
        final DatabaseConfiguration databaseConfiguration = NodeConfig.get().getDatabaseConfiguration();
        this.connection = DriverManager.getConnection("jdbc:mysql://" + databaseConfiguration.getHost() + ":" + databaseConfiguration.getPort()
            + "/" + databaseConfiguration.getDatabase() + "?useUnicode=true&autoReconnect=true",
            databaseConfiguration.getUser(), databaseConfiguration.getPassword());

        executeUpdate("CREATE TABLE IF NOT EXISTS cloudsystem_groups(name VARCHAR(100), template VARCHAR(100), node VARCHAR(100)," +
            " memory INT, minOnlineService INT, maxOnlineService INT, staticService INT, fallbackGroup INT, version VARCHAR(100), maxPlayers INT)");

        CloudAPI.getInstance().getLoggerProvider().logMessage("The connection is now established to the database.");
    }

    @SneakyThrows
    @Override
    public @NotNull ICommunicationPromise<Void> disconnect() {
        final ICommunicationPromise<Void> shutdownPromise = new CommunicationPromise<>();
        if (this.connection != null) this.connection.close();
        shutdownPromise.setSuccess(null);
        return shutdownPromise;
    }

    @Override
    public void addGroup(final @NotNull IServiceGroup serviceGroup) {
        executeUpdate("INSERT INTO cloudsystem_groups(name, template, node, memory, minOnlineService, maxOnlineService, staticService, fallbackGroup, version, maxPlayers) VALUES (" +
            "'" + serviceGroup.getName() + "', '" + serviceGroup.getTemplate() + "', '" + serviceGroup.getNode() + "', " + serviceGroup.getMemory() + ", " +
            serviceGroup.getMinOnlineService() + ", " + serviceGroup.getMaxOnlineService() + ", " + (serviceGroup.isStaticService() ? 1 : 0) +
            ", " + (serviceGroup.isFallbackGroup() ? 1 : 0) + ", '" + serviceGroup.getGameServerVersion().getTitle() + "', 100);");
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
                    resultSet.getInt("memory"),
                    resultSet.getInt("maxPlayers"),
                    resultSet.getInt("minOnlineService"),
                    resultSet.getInt("maxOnlineService"),
                    resultSet.getInt("staticService") == 1,
                    resultSet.getInt("fallbackGroup") == 1,
                    GameServerVersion.getVersionByTitle(resultSet.getString("version")));
                groups.add(serviceGroup);
            }
            return groups;
        }, Lists.newArrayList());
    }

    public <T> T executeQuery(String query, DatabaseFunction<ResultSet, T> function, T defaultValue) {
        Objects.requireNonNull(this.connection, "Try to execute a statement, but the connection is null.");
        try (var preparedStatement = this.connection.prepareStatement(query)) {
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

    public int executeUpdate(final String query) {
        Objects.requireNonNull(this.connection, "Try to update a statement, but the connection is null.");
        try (var preparedStatement = this.connection.prepareStatement(query)) {
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

}
