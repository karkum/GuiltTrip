package com.opower.guilttrip.model;

import java.io.Serializable;

public class CarbonFootprint implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private Itinerary transit;
    private Itinerary drive;

    public CarbonFootprint(Itinerary transit, Itinerary drive, float highwayMpg) {
        this.transit = transit;
        this.drive = drive;

        for (TransitStep transitStep : this.drive.getSteps()) {
            CarStep carStep = (CarStep)transitStep;
            carStep.setMpg(highwayMpg);
            carStep.setPassengerCount(1);
        }
    }

    public Itinerary getDrive() {
        return this.drive;
    }

    public Itinerary getTransit() {
        return this.transit;
    }

    public double getSavingsPercentage() {
        double transitCarbon = this.transit.getCarbon();
        double carCarbon = this.drive.getCarbon();

        return (1.0d - transitCarbon/carCarbon) * 100d;
    }

    public double getSavingsRate() {
       return this.drive.getPoundsPerWeek() - this.transit.getPoundsPerWeek();
    }

    public double getReadingTime() {
        double durationOfOneTripInSeconds = this.transit.getDuration();
        double totalTripTimeForOneYearInSeconds = durationOfOneTripInSeconds * (5.0 * 2.0 * 49.0);
        return (totalTripTimeForOneYearInSeconds / 3600) / 6;
    }
}
