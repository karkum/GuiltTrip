package com.opower.guilttrip.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.opower.guilttrip.model.NameValuePair;
import com.opower.guilttrip.model.VehicleInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VehicleInfoDeserializer implements JsonDeserializer<VehicleInfo> {

    @Override
    public VehicleInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        List<NameValuePair> infos = new ArrayList<NameValuePair>();
        try {
            JsonElement jsonObject = ((JsonObject)json).get("menuItem");
            if (jsonObject.isJsonArray()) {
                for (JsonElement element : jsonObject.getAsJsonArray()) {
                    String value = element.getAsJsonObject().get("value").getAsString();
                    String text = element.getAsJsonObject().get("text").getAsString();

                    infos.add(new NameValuePair(text, value));
                }
            }
            else {
                String value = jsonObject.getAsJsonObject().get("value").getAsString();
                String text = jsonObject.getAsJsonObject().get("text").getAsString();
                infos.add(new NameValuePair(text, value));
            }
            return new VehicleInfo(infos);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
