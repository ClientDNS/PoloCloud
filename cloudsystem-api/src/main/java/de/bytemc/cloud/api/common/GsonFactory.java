package de.bytemc.cloud.api.common;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@Setter
@NoArgsConstructor
public class GsonFactory<T> {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File file;

    public GsonFactory(String path) {
        this.file = new File(path);
    }

    @SneakyThrows
    public T load(Class<T> clazz){
        return gson.fromJson(new FileReader(file), clazz);
    }

    public List<T> loadAll(Class<T> clazz, List<File> files) {
        List<T> items = Lists.newArrayList();
        for (File fileList : files) {
            setFile(fileList);
            items.add(load(clazz));
        }
        return items;
    }

    public static Gson build(){
        return gson;
    }



}
