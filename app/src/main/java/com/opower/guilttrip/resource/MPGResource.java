package com.opower.guilttrip.resource;

import com.opower.guilttrip.model.MPGInfo;
import com.opower.guilttrip.model.VehicleInfo;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MPGResource {

    @GET("/ws/rest/vehicle/menu/year")
    VehicleInfo listYears();

    @GET("/ws/rest/vehicle/menu/make")
    VehicleInfo listMakes(@Query("year") String year);

    @GET("/ws/rest/vehicle/menu/model")
    VehicleInfo listModels(@Query("year") String year, @Query("make") String make);

    @GET("/ws/rest/vehicle/menu/options")
    VehicleInfo listOptions(@Query("year") String year, @Query("make") String make, @Query("model") String model);

    @GET("/ws/rest/vehicle/{id}")
    MPGInfo getMPG(@Path("id") int id);
}
