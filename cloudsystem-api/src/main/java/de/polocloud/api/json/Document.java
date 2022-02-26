package de.polocloud.api.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.lang.reflect.Type;

@SuppressWarnings("ALL")
public class Document {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonObject jsonObject;

    public Document() {
        this(new JsonObject());
    }

    public Document(final File file) {
        this.read(file);
    }

    public Document(final Reader reader) {
        this.read(reader);
    }

    public Document(final JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Document(final String json) {
        this.jsonObject = GSON.fromJson(json, JsonObject.class);
    }

    public Document(final Object object) {
        this.setJsonObject(object);
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
        try (final var fileReader = new FileReader(file)) {
            this.jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Document read(final Reader reader) {
        this.jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        return this;
    }

    public Document write(final File file) {
        try (final var fileWriter = new FileWriter(file)) {
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
