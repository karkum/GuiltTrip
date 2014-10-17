package com.opower.guilttrip.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author chris.phillips
 */
public class Itinerary implements Serializable {

    private static final long serialVersionUID = 852647229522776147L;
    private double distance;
    private double duration;


    public List<TransitStep> getSteps() {
        return steps;
    }

    public void setSteps(List<TransitStep> steps) {
        this.steps = steps;
    }

    private  List<TransitStep> steps;


    public double getCarbon() {
        double sum = 0;
        for(TransitStep ts : steps) {
            sum += ts.getLbsOfCarbon();
        }

        return sum;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration / 60;
    }

    public double getDistance() {
        return distance;
    }

    public double getPoundsPerMinute() {
        double totalCarbon = getCarbon();
        double duration = getDuration();

        if (duration == 0) {
            return 0;
        }
        return totalCarbon/duration;
    }

    public double getPoundsPerWeek() {
        double totalCarbon = getCarbon();
        double duration = getDuration();

        if (duration == 0) {
            return 0;
        }
        return totalCarbon * 10;
    }
}
