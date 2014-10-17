package com.opower.guilttrip.async;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.google.gson.Gson;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class ResourceGetter<T> extends AsyncTaskLoader<T> {

    private Class<T> clazz;
    private String endpoint;
    private Gson gson;

    public ResourceGetter(Context context, String endpoint, Class<T> clazz, Gson gson) {
        super(context);
        this.endpoint = endpoint;
        this.clazz = clazz;
        this.gson = gson;
    }

    @Override
    public T loadInBackground() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(this.endpoint)
                .setConverter(new GsonConverter(this.gson))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Accept", "application/json");
                    }
                })
                .build();
        return restAdapter.create(this.clazz);
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }
}
