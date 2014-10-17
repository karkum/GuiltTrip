package com.opower.guilttrip.async;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.opower.guilttrip.model.MPGInfo;
import com.opower.guilttrip.resource.MPGResource;

import static com.opower.guilttrip.FormActivity.VEHICLE_ID;

public class MPGGetter extends AsyncTaskLoader<MPGInfo> {
    private MPGResource mpgResource;
    private Bundle bundle;

    public MPGGetter(Context context, MPGResource mpgResource, Bundle bundle) {
        super(context);
        this.mpgResource = mpgResource;
        this.bundle = bundle;
    }

    @Override
    public MPGInfo loadInBackground() {
        return this.mpgResource.getMPG(this.bundle.getInt(VEHICLE_ID));
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }
}