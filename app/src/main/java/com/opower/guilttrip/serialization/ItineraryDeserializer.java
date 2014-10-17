package com.opower.guilttrip.serialization;

import com.google.gson.*;
import com.opower.guilttrip.model.BusStep;
import com.opower.guilttrip.model.CarStep;
import com.opower.guilttrip.model.CommuterRail;
import com.opower.guilttrip.model.Itinerary;
import com.opower.guilttrip.model.SubwayStep;
import com.opower.guilttrip.model.TransitStep;
import com.opower.guilttrip.model.WalkStep;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chris.phillips
 */
public class ItineraryDeserializer implements JsonDeserializer<Itinerary> {
    public enum TravelMode {

        WALKING, TRANSIT, DRIVING, BICYCLING;

        public static TravelMode fromString(String s) {
            return valueOf(s.toUpperCase());
        }
    }

    public enum VehicleType {

        RAIL, METRO_RAIL, SUBWAY, TRAM, MONORAIL, HEAVY_RAIL, COMMUTER_TRAIN, HIGH_SPEED_TRAIN, BUS,
        INTERCITY_BUS, TROLLEYBUS, SHARE_TAXI, FERRY, CABLE_CAR, GONDOLA_LIFT, FUNICULAR, OTHER;

        public static VehicleType fromString(String s) {
            return valueOf(s.toUpperCase());
        }
    }

    @Override
    public Itinerary deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Itinerary result = new Itinerary();

        if (jsonElement.getAsJsonObject().get("status").getAsString().equals("NOT_FOUND")) {
            return null;
        }

        JsonObject legObject = jsonElement.getAsJsonObject().getAsJsonArray("routes").get(0).getAsJsonObject().getAsJsonArray("legs").get(0).getAsJsonObject();
        double distanceTotal = getDistance(legObject);
        int durationTotal = getDuration(legObject);

        result.setDistance(distanceTotal);
        result.setDuration(durationTotal);

        JsonArray stepsJson = legObject.getAsJsonObject().getAsJsonArray("steps");

        List<TransitStep> stepList = new ArrayList<TransitStep>();

        for(JsonElement element : stepsJson) {
            JsonObject stepObject = element.getAsJsonObject();
            TravelMode tm = TravelMode.fromString(stepObject.get("travel_mode").getAsString());
            TransitStep parsedStep = null;
            switch (tm) {
                case WALKING:
                    parsedStep = new WalkStep();
                    break;
                case TRANSIT:
                    String vtString = stepObject.get("transit_details").getAsJsonObject().get("line").getAsJsonObject().get("vehicle").getAsJsonObject().get("type").getAsString();
                    VehicleType vt = VehicleType.fromString(vtString);
                    switch(vt) {
                        case SUBWAY:
                        case HEAVY_RAIL:
                            parsedStep = new SubwayStep();
                            break;
                        case BUS:
                            parsedStep = new BusStep();
                            break;
                        case COMMUTER_TRAIN:
                            parsedStep = new CommuterRail();
                            break;
                        default:
                            throw new RuntimeException("unsupported type: " + vtString);
                    }
                    break;
                case DRIVING:
                    parsedStep = new CarStep();
                    break;
                case BICYCLING:
                    break;
            }
            parsedStep.setDistance(getDistance(stepObject));
            parsedStep.setName(stepObject.get("html_instructions").getAsString());
            parsedStep.setDuration(getDuration(stepObject));
            stepList.add(parsedStep);
        }

        result.setSteps(stepList);
        return result;
    }

    private double getDistance(JsonObject stepObject) {
        return stepObject.get("distance").getAsJsonObject().get("value").getAsDouble();
    }

    private int getDuration(JsonObject stepObject) {
        return stepObject.get("duration").getAsJsonObject().get("value").getAsInt();
    }
}
