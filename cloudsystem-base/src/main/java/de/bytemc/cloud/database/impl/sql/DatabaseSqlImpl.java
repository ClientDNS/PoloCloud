package de.bytemc.cloud.database.impl.sql;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.cloud.database.IDatabase;
import de.bytemc.cloud.database.impl.DatabaseFunction;
import lombok.SneakyThrows;

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
        connection = DriverManager.getConnection("jdbc:mysql://37.114.60.81:3306"
            + "/cloudsystem?useUnicode=true&autoReconnect=true", "outroddet_", "BgiPID8KkS");

        executeUpdate("CREATE TABLE IF NOT EXISTS cloudsystem_groups(name VARCHAR(100), template VARCHAR(100), node VARCHAR(100)," +
            " memory INT, minOnlineService INT, maxOnlineService INT, staticService INT, version VARCHAR(100))");

        CloudAPI.getInstance().getLoggerProvider().logMessage("The connection is now established to the database.");
    }

    @SneakyThrows
    @Override
    public void disconnect() {
        if (connection != null) this.connection.close();
    }

    @Override
    public void addGroup(IServiceGroup serviceGroup) {
        executeUpdate("INSERT INTO cloudsystem_groups(name, template, node, memory, minOnlineService, maxOnlineService, staticService, version) VALUES (" +
            "'" + serviceGroup.getGroup() + "', '" + serviceGroup.getTemplate() + "', '" + serviceGroup.getNode() + "', " + serviceGroup.getMemory() + ", " +
            serviceGroup.getMinOnlineService() + ", " + serviceGroup.getMaxOnlineService() + ", " + (serviceGroup.isStaticService() ? 1 : 0 + ", '" +
            serviceGroup.getGameServerVersion().getTitle() + "');"));
    }

    @Override
    public void removeGroup(IServiceGroup serviceGroup) {
        executeUpdate("DELETE FROM cloudsystem_groups WHERE name='" + serviceGroup.getGroup() + "'");
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
                    resultSet.getInt("minOnlineService"),
                    resultSet.getInt("maxOnlineService"),
                    resultSet.getInt("staticService") == 1 ? true : false,
                    GameServerVersion.getVersionByTitle(resultSet.getString("version")));
                groups.add(serviceGroup);
            }
            return groups;
        }, Lists.newArrayList());
    }

    public <T> T executeQuery(String query, DatabaseFunction<ResultSet, T> function, T defaultValue) {
        Objects.requireNonNull(connection, "Try to execute a statement, but the connection is null.");
        try (var preparedStatement = connection.prepareStatement(query)) {
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

    public int executeUpdate(String query) {
        Objects.requireNonNull(connection, "Try to update a statement, but the connection is null.");
        try (var preparedStatement = connection.prepareStatement(query)) {
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }
}
