package com.opower.guilttrip.model;

import java.util.List;

public class VehicleInfo {

    private List<NameValuePair> pairs;

    public VehicleInfo(List<NameValuePair> pairs) {
        this.pairs = pairs;
    }

    public List<NameValuePair> getPairs() {
        return this.pairs;
    }
}
