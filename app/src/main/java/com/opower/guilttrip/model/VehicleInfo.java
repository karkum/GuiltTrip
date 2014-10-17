package com.opower.guilttrip.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VehicleInfo {

    private Map<String, String> map;

    public VehicleInfo(Map<String, String> map) {
        this.map = map;
    }

    public List<String> getInfoAsString() {
        ArrayList<String> strings = new ArrayList<String>();
        for (Map.Entry<String, String> entry : this.map.entrySet()) {
            strings.add(entry.getKey() + " | " + entry.getValue());
        }

        return strings;
    }
}
