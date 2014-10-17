package com.opower.guilttrip.async;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.opower.guilttrip.model.VehicleInfo;
import com.opower.guilttrip.resource.MPGResource;

import static com.opower.guilttrip.FormActivity.MAKE_LOADER;
import static com.opower.guilttrip.FormActivity.MODEL_LOADER;
import static com.opower.guilttrip.FormActivity.YEAR_LOADER;
import static com.opower.guilttrip.FormActivity.OPTIONS_LOADER;
import static com.opower.guilttrip.FormActivity.MODEL_KEY;
import static com.opower.guilttrip.FormActivity.YEAR_KEY;
import static com.opower.guilttrip.FormActivity.MAKE_KEY;

public class VehicleInfoGetter extends AsyncTaskLoader<VehicleInfo> {

    private Bundle bundle;
    private MPGResource mpgResource;

    public VehicleInfoGetter(Context context, Bundle bundle, MPGResource mpgResource) {
        super(context);
        this.mpgResource = mpgResource;
        this.bundle = bundle;
    }

    @Override
    public VehicleInfo loadInBackground() {
        try {
            switch(getId()) {
                case YEAR_LOADER:
                    return this.mpgResource.listYears();

                case MAKE_LOADER:
                    String yearParam = this.bundle.getString(YEAR_KEY);
                    return this.mpgResource.listMakes(yearParam);

                case MODEL_LOADER:
                    yearParam = this.bundle.getString(YEAR_KEY);
                    String makeParam = this.bundle.getString(MAKE_KEY);
                    return this.mpgResource.listModels(yearParam, makeParam);

                case OPTIONS_LOADER:
                    yearParam = this.bundle.getString(YEAR_KEY);
                    makeParam = this.bundle.getString(MAKE_KEY);
                    String modelParam = this.bundle.getString(MODEL_KEY);
                    return this.mpgResource.listOptions(yearParam, makeParam, modelParam);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }
}
