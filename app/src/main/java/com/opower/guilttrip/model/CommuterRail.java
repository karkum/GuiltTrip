package com.opower.guilttrip.model;

import java.io.Serializable;

/**
 * @author chris.phillips
 */
public class CommuterRail extends TransitStep implements Serializable {

    private static final long serialVersionUID = 722647229522776147L;

    @Override
    public double getCarbonConstant() {
        return 0.172d;
    }
}
