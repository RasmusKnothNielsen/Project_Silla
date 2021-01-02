package dk.dmi.silla.service;

import dk.dmi.silla.config.Config;
import dk.dmi.silla.model.Parameter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.Collections;
import java.util.List;

public class ParameterService {

    public JsonArray getParameterList() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        List<Parameter> parameterList = Config.getParameterList();
        Collections.sort(parameterList);
        for(Parameter parameter : parameterList) {
            jsonArrayBuilder.add(parameter.toJson());
        }
        return jsonArrayBuilder.build();
    }

}
