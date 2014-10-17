package com.opower.guilttrip.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.opower.guilttrip.model.VehicleMenuItems;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class VehicleMenuItemsDeserializer implements JsonDeserializer<VehicleMenuItems> {

    @Override
    public VehicleMenuItems deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        ArrayList<String> menuItems = new ArrayList<String>();
        try {
            JsonElement jsonObject = ((JsonObject)json).get("menuItem");
            if (jsonObject.isJsonArray()) {
                for (JsonElement element : jsonObject.getAsJsonArray()) {
                    menuItems.add(element.getAsJsonObject().get("value").getAsString());
                }
            }
            else {
                menuItems.add(jsonObject.getAsJsonObject().get("text").getAsString());
            }
            return new VehicleMenuItems(menuItems);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
