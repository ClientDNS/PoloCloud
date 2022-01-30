package de.bytemc.cloud.api.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public class Document {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonObject jsonObject;

    public Document(final File file) {
        this.read(file);
    }

    public Document(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Document(final Object object) {
        this.setJsonObject(GSON.toJsonTree(object));
    }

    public <T> T get(final String key, final Class<T> clazz) {
        return GSON.fromJson(this.jsonObject.get(key), clazz);
    }

    public <T> T get(final String key, final Type type) {
        return GSON.fromJson(this.jsonObject.get(key), type);
    }

    public <T> T get(final Class<T> clazz) {
        return GSON.fromJson(this.jsonObject, clazz);
    }

    public <T> T get(final Type type) {
        return GSON.fromJson(this.jsonObject, type);
    }

    public Document set(final String key, final Object object) {
        this.jsonObject.add(key, GSON.toJsonTree(object, object.getClass()));
        return this;
    }

    public Document read(final File file) {
        try (final FileReader fileReader = new FileReader(file)) {
            this.jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Document write(final File file) {
        try (final FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(GSON.toJson(this.jsonObject));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Document setJsonObject(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }

    public Document setJsonObject(final Object object) {
        this.jsonObject = GSON.toJsonTree(object).getAsJsonObject();
        return this;
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

}
