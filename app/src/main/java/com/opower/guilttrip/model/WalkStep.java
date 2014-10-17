package com.opower.guilttrip.model;

import java.io.Serializable;

/**
 * @author chris.phillips
 */
public class WalkStep extends TransitStep implements Serializable {

    private static final long serialVersionUID = 752997229522776147L;
    @Override
    protected double getCarbonConstant() {
        return 0;
    }
}
