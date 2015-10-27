package com.opower.guilttrip.resource;

import com.opower.guilttrip.model.Itinerary;

import retrofit.http.GET;
import retrofit.http.Query;

public interface DirectionsResource {

    @GET("/maps/api/place/nearbysearch")
    Itinerary getTransitDirections(@Query("mode") String mode, @Query("departure_time") long departureTime,
                                     @Query("origin") String origin, @Query("destination") String destination);

    @GET("/maps/api/directions/json")
    Itinerary getDrivingDirections(@Query("origin") String origin, @Query("destination") String destination);
}
