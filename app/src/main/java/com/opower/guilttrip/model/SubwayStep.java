package com.opower.guilttrip.model;

import java.io.Serializable;

/**
 * @author chris.phillips
 */
public class SubwayStep extends TransitStep implements Serializable {

    private static final long serialVersionUID = 752647229522776047L;

    @Override
    public double getCarbonConstant() {
        return 0.163d;
    }
}
