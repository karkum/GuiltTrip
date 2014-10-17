package com.opower.guilttrip.model;

import java.io.Serializable;

/**
 * @author chris.phillips
 */
public class CarStep extends TransitStep implements Serializable {

    private static final long serialVersionUID = 7526472129522776147L;
    private double mpg;
    private int passengerCount = 1;

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public double getMpg() {
        return mpg;
    }

    public void setMpg(float mpg) {
        this.mpg = mpg;
    }

    @Override
    protected double getCarbonConstant() {
        return 8.91d / mpg / passengerCount;
    }
}
