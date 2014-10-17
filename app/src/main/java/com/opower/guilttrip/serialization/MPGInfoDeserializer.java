package com.opower.guilttrip.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.opower.guilttrip.model.MPGInfo;

import java.lang.reflect.Type;

public class MPGInfoDeserializer implements JsonDeserializer<MPGInfo> {
    @Override
    public MPGInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            json.getAsJsonObject();
        }
        return null;
    }
}
