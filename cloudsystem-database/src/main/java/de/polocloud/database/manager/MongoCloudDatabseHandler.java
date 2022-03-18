package de.polocloud.database.manager;

import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.groups.impl.SimpleServiceGroup;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.database.CloudDatabaseProvider;
import de.polocloud.database.DatabaseConfiguration;
import de.polocloud.database.SimpleDatabaseManager;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class MongoCloudDatabseHandler implements CloudDatabaseProvider {

    private MongoClient mongoClient;
    private final DatabaseConfiguration config;

    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    public void connect() {

        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

        this.mongoClient = MongoClients.create(new MongoClientURI("mongodb://" + this.config.getUsername() + ":" + this.config.getPassword() + "@" + this.config.getHostname() + ":" + this.config.getPort()).toString());
        this.mongoDatabase = this.mongoClient.getDatabase(config.getDatabase());
        this.mongoCollection = this.mongoDatabase.getCollection(SimpleDatabaseManager.GROUP_TABLE);
    }


    @Override
    public void addGroup(@NotNull ServiceGroup serviceGroup) {
        this.mongoCollection.insertOne(new Document()
            .append("name", serviceGroup.getName())
            .append("template", serviceGroup.getTemplate())
            .append("node", serviceGroup.getNode())
            .append("motd", serviceGroup.getMotd())
            .append("maxMemory", serviceGroup.getMaxMemory())
            .append("defaultMaxPlayers", serviceGroup.getDefaultMaxPlayers())
            .append("minOnlineService", serviceGroup.getMinOnlineService())
            .append("maxOnlineService", serviceGroup.getMaxOnlineService())
            .append("static", serviceGroup.isStatic())
            .append("fallbackGroup",serviceGroup.isFallbackGroup())
            .append("maintenance", serviceGroup.isMaintenance())
            .append("updating", serviceGroup.isAutoUpdating()).append("version", serviceGroup.getGameServerVersion().getName()));
    }

    @Override
    public void removeGroup(@NotNull ServiceGroup serviceGroup) {
        this.mongoCollection.deleteOne(Filters.eq("name", serviceGroup.getName()));
    }

    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        List<ServiceGroup> groups = new ArrayList<>();
        for (Document document : mongoCollection.find()) {
            groups.add(new SimpleServiceGroup(document.getString("name"), document.getString("template"), document.getString("node"), document.getString("motd"),
                document.getInteger("maxMemory"), document.getInteger("defaultMaxPlayers"), document.getInteger("maxOnlineService"), document.getInteger("minOnlineService"),
                document.getBoolean("static"), document.getBoolean("fallbackGroup"), document.getBoolean("maintenance"), document.getBoolean("updating"),
                GameServerVersion.getVersionByName(document.getString("version"))));
        }
        return groups;
    }

    @Override
    public void updateGroupProperty(@NotNull String group, @NotNull String property, @NotNull Object value) {
        Document document = this.mongoCollection.find(Filters.eq("name", group)).first();
        document.replace(property, value);
        mongoCollection.replaceOne(Filters.eq("name", group), document);
    }

    @Override
    public void disconnect() {
        if (this.mongoClient != null) this.mongoClient.close();
    }
}
