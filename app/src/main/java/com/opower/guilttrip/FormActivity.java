package com.opower.guilttrip;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opower.guilttrip.model.MPGInfo;
import com.opower.guilttrip.model.NameValuePair;
import com.opower.guilttrip.model.VehicleInfo;
import com.opower.guilttrip.serialization.VehicleInfoDeserializer;
import com.opower.guilttrip.resource.MPGResource;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class FormActivity extends Activity {

    private static final int YEAR_LOADER = 0;
    private static final int MAKE_LOADER = 1;
    private static final int MODEL_LOADER = 2;
    private static final int OPTIONS_LOADER = 3;
    private static final int RESOURCE_LOADER = 4;
    private static final int MPG_LOADER = 5;

    private static final String MPG_ENDPOINT = "http://www.fueleconomy.gov";

    private static final String YEAR_KEY = "year";
    private static final String MAKE_KEY = "make";
    private static final String MODEL_KEY = "model";
    private static final String VEHICLE_ID = "id";

    private Spinner optionsSpinner;
    private EditText startTextView;
    private EditText endTextView;
    private Button goButton;
    private MPGResource mpgResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        this.startTextView = ((EditText)findViewById(R.id.start_text));
        this.endTextView = ((EditText)findViewById(R.id.end_text));

        goButton = (Button)findViewById(R.id.go_button);

        getLoaderManager().initLoader(RESOURCE_LOADER, null, new ResourceLoader());
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NameValuePair selection = (NameValuePair)FormActivity.this.optionsSpinner.getSelectedItem();
                if (selection == null) {
                    Toast.makeText(FormActivity.this, "Please input everything", Toast.LENGTH_LONG).show();
                    return;
                }

                String code = selection.getCode();
                Bundle bundle = new Bundle();
                bundle.putInt(VEHICLE_ID, Integer.valueOf(code));
                FormActivity.this.getLoaderManager().restartLoader(MPG_LOADER, bundle, new MPGLoader(mpgResource));

                String start = FormActivity.this.startTextView.getText().toString();
                if (start == null) {
                    Toast.makeText(FormActivity.this, "Please input everything", Toast.LENGTH_LONG).show();
                    return;
                }

                String end = FormActivity.this.endTextView.getText().toString();
                if (end == null) {
                    Toast.makeText(FormActivity.this, "Please input everything", Toast.LENGTH_LONG).show();
                    return;
                }

                //call google with start/end point
                // - loader, callbacks, model, serialize?
                // - new activity, given it the data from google, mpg (in a bundle), send it through an intent
                // - new activity, calculates the values and shows stuff on the screen
                // - integrate views with cori's stuff
            }
        });
    }

    private void initSpinner() {
        Spinner yearSpinner = (Spinner)findViewById(R.id.yearSpinner);
        Spinner makeSpinner = (Spinner)findViewById(R.id.makeSpinner);
        Spinner modelSpinner = (Spinner)findViewById(R.id.modelSpinner);
        this.optionsSpinner = (Spinner)findViewById(R.id.optionsSpinner);

        Bundle bundle = new Bundle();
        yearSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                MAKE_LOADER, makeSpinner, YEAR_KEY));
        makeSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                MODEL_LOADER, modelSpinner, MAKE_KEY));
        modelSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                OPTIONS_LOADER, optionsSpinner, MODEL_KEY));

        getLoaderManager().restartLoader(YEAR_LOADER, bundle, new SpinnerLoader(yearSpinner));
    }

    private static class VehicleInfoGetterer extends AsyncTaskLoader<VehicleInfo> {

        private Bundle bundle;
        private MPGResource mpgResource;

        public VehicleInfoGetterer(Context context, Bundle bundle, MPGResource mpgResource) {
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

    private class SpinnerLoader implements LoaderManager.LoaderCallbacks<VehicleInfo> {
        private final Spinner spinner;

        private SpinnerLoader(Spinner spinner) {
            this.spinner = spinner;
        }

        @Override
        public Loader<VehicleInfo> onCreateLoader(int i, Bundle bundle) {
            return new VehicleInfoGetterer(FormActivity.this, bundle, FormActivity.this.mpgResource);
        }

        @Override
        public void onLoadFinished(Loader<VehicleInfo> listLoader, VehicleInfo info) {
            if (info == null) {
                return;
            }
            ArrayAdapter<NameValuePair> arrayAdapter = new ArrayAdapter<NameValuePair>(FormActivity.this,
                    android.R.layout.simple_spinner_item, info.getPairs());
            this.spinner.setAdapter(arrayAdapter);
        }

        @Override
        public void onLoaderReset(Loader<VehicleInfo> listLoader) {

        }
    }

    private class ResourceLoader implements LoaderManager.LoaderCallbacks<MPGResource> {

        @Override
        public Loader<MPGResource> onCreateLoader(int i, Bundle bundle) {
            return new AsyncTaskLoader<MPGResource>(FormActivity.this) {

                @Override
                public MPGResource loadInBackground() {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(VehicleInfo.class, new VehicleInfoDeserializer())
                            .create();

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint(MPG_ENDPOINT)
                            .setConverter(new GsonConverter(gson))
                            .setRequestInterceptor(new RequestInterceptor() {
                                @Override
                                public void intercept(RequestFacade request) {
                                    request.addHeader("Accept", "application/json");
                                }
                            })
                            .build();
                    return restAdapter.create(MPGResource.class);
                }

                @Override
                public void onStartLoading() {
                    forceLoad();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<MPGResource> listLoader, MPGResource info) {
            FormActivity.this.mpgResource = info;
            initSpinner();
        }

        @Override
        public void onLoaderReset(Loader<MPGResource> listLoader) {

        }

    }

    private class SpinnerOnSelectedItemListener implements AdapterView.OnItemSelectedListener {
        private Bundle bundle;
        private int loaderToTrigger;
        private Spinner spinner;
        private String key;

        public SpinnerOnSelectedItemListener(Bundle bundle, int loaderToTrigger, Spinner spinner,
                                                 String key) {
            this.bundle = bundle;
            this.loaderToTrigger = loaderToTrigger;
            this.spinner = spinner;
            this.key = key;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String item = adapterView.getAdapter().getItem(i).toString();
            this.bundle.putString(this.key, item);
            getLoaderManager().restartLoader(this.loaderToTrigger, this.bundle,
                    new SpinnerLoader(this.spinner));
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MPGLoader implements LoaderManager.LoaderCallbacks<MPGInfo> {
        private MPGResource mpgResource;

        public MPGLoader(MPGResource mpgResource) {
            this.mpgResource = mpgResource;
        }

        @Override
        public Loader<MPGInfo> onCreateLoader(int i, Bundle bundle) {
            return new MPGGetterer(FormActivity.this, this.mpgResource, bundle);
        }

        @Override
        public void onLoadFinished(Loader<MPGInfo> listLoader, MPGInfo info) {
            Toast.makeText(FormActivity.this, "Found MPG: " + info.getHighway08(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLoaderReset(Loader<MPGInfo> listLoader) {

        }
    }

    private static class MPGGetterer extends AsyncTaskLoader<MPGInfo> {
        private MPGResource mpgResource;
        private Bundle bundle;

        public MPGGetterer(Context context, MPGResource mpgResource, Bundle bundle) {
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
}
