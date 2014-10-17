package com.opower.guilttrip.resource;

import com.opower.guilttrip.model.VehicleInfo;
import com.opower.guilttrip.model.VehicleMenuItems;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

public interface MPGResource {

    @GET("/ws/rest/vehicle/menu/year")
    VehicleMenuItems listYears();

    @GET("/ws/rest/vehicle/menu/make")
    VehicleMenuItems listMakes(@Query("year") String year);

    @GET("/ws/rest/vehicle/menu/model")
    VehicleMenuItems listModels(@Query("year") String year, @Query("make") String make);

    @GET("/ws/rest/vehicle/menu/options")
    VehicleInfo listOptions(@Query("year") String year, @Query("make") String make, @Query("model") String model);
}
