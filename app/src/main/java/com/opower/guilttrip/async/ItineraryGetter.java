package com.opower.guilttrip.async;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.opower.guilttrip.model.CarbonFootprint;
import com.opower.guilttrip.model.Itinerary;
import com.opower.guilttrip.resource.DirectionsResource;

import static com.opower.guilttrip.FormActivity.START_KEY;
import static com.opower.guilttrip.FormActivity.END_KEY;
import static com.opower.guilttrip.FormActivity.DEPARTURE_TIME;
import static com.opower.guilttrip.FormActivity.MPG_KEY;

public class ItineraryGetter extends AsyncTaskLoader<CarbonFootprint> {
    private DirectionsResource directionsResource;
    private Bundle bundle;

    public ItineraryGetter(Context context, Bundle bundle, DirectionsResource directionsResource) {
        super(context);
        this.directionsResource = directionsResource;
        this.bundle = bundle;
    }

    @Override
    public CarbonFootprint loadInBackground() {
        String start = this.bundle.getString(START_KEY);
        String end = this.bundle.getString(END_KEY);
        long departureTime = this.bundle.getLong(DEPARTURE_TIME) / 1000;
        float mpg = this.bundle.getFloat(MPG_KEY);

        Itinerary transitStep = this.directionsResource.getTransitDirections("transit", departureTime, start, end);
        Itinerary drivingStep = this.directionsResource.getDrivingDirections(start, end);

        return new CarbonFootprint(transitStep, drivingStep, mpg);
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }
}
