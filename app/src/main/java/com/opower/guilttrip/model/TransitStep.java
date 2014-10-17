package com.opower.guilttrip.model;

import java.io.Serializable;

/**
 * @author chris.phillips
 */
public abstract class TransitStep implements Serializable {

    private static final long serialVersionUID = 752647229522776140L;

    public static final double LBS_PER_KG = 2.204d;
    private double distance;
    private String name;
    private int duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }



    public double getLbsOfCarbon() {
        return this.distance * 0.000621371 * getCarbonConstant() * LBS_PER_KG;
    }

    protected abstract double getCarbonConstant();
}
