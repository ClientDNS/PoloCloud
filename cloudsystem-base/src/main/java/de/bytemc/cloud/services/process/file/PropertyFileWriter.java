package de.bytemc.cloud.services.process.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.services.IService;
import lombok.SneakyThrows;

import java.io.FileWriter;

public class PropertyFileWriter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @SneakyThrows
    public PropertyFileWriter(final IService service) {

        final var company = new JsonObject();
        company.addProperty("service.name", service.getName());
        company.addProperty("service.node", service.getServiceGroup().getNode());
        company.addProperty("service.node.hostname", Base.getInstance().getNode().getHostName());
        company.addProperty("service.node.port", Base.getInstance().getNode().getPort());


        final var fileWriter = new FileWriter("tmp/" + service.getName() + "/property.json");
        fileWriter.write(GSON.toJson(company));
        fileWriter.flush();
        fileWriter.close();

    }

}
