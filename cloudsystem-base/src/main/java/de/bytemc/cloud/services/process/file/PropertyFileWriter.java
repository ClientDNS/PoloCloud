package de.bytemc.cloud.services.process.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.services.IService;
import lombok.SneakyThrows;

import java.io.FileWriter;

public class PropertyFileWriter {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SneakyThrows
    public PropertyFileWriter(IService service) {

        var company = new JsonObject();
        company.addProperty("service.name", service.getName());
        company.addProperty("service.node", service.getServiceGroup().getNode());
        company.addProperty("service.node.hostname", Base.getInstance().getNode().getHostname());
        company.addProperty("service.node.port", Base.getInstance().getNode().getPort());


        var fileWriter = new FileWriter("tmp/" + service.getName() + "/property.json");
        fileWriter.write(gson.toJson(company));
        fileWriter.flush();
        fileWriter.close();

    }

}
