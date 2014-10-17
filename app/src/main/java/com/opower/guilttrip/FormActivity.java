package com.opower.guilttrip;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.SparseArray;
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
import com.opower.guilttrip.async.ItineraryGetter;
import com.opower.guilttrip.async.MPGGetter;
import com.opower.guilttrip.async.ResourceGetter;
import com.opower.guilttrip.async.VehicleInfoGetter;
import com.opower.guilttrip.model.CarbonFootprint;
import com.opower.guilttrip.model.Itinerary;
import com.opower.guilttrip.model.MPGInfo;
import com.opower.guilttrip.model.NameValuePair;
import com.opower.guilttrip.model.VehicleInfo;
import com.opower.guilttrip.resource.DirectionsResource;
import com.opower.guilttrip.resource.MPGResource;
import com.opower.guilttrip.serialization.ItineraryDeserializer;
import com.opower.guilttrip.serialization.VehicleInfoDeserializer;

import java.util.Date;

public class FormActivity extends Activity implements LoaderManager.LoaderCallbacks {

    public static final int YEAR_LOADER = 0;
    public static final int MAKE_LOADER = 1;
    public static final int MODEL_LOADER = 2;
    public static final int OPTIONS_LOADER = 3;
    private static final int DIRECTIONS_RESOURCE_LOADER = 4;
    private static final int MPG_RESOURCE_LOADER = 5;
    private static final int MPG_LOADER = 6;
    private static final int DIRECTIONS_LOADER = 7;

    private static final String MPG_ENDPOINT = "http://www.fueleconomy.gov";
    private static final String GOOGLE_ENDPOINT = "https://maps.googleapis.com";

    public static final String YEAR_KEY = "year";
    public static final String MAKE_KEY = "make";
    public static final String MODEL_KEY = "model";
    public static final String VEHICLE_ID = "id";
    public static final String START_KEY = "start";
    public static final String END_KEY = "end";
    public static final String DEPARTURE_TIME = "departure_time";
    public static final String MPG_KEY = "mpg";
    public static final String CARBON_FOOTPRINT_KEY = "carbonKey";

    private Spinner optionsSpinner;
    private EditText startTextView;
    private EditText endTextView;
    private Button goButton;
    private MPGResource mpgResource;
    private DirectionsResource directionsResource;
    private MPGInfo mpginfo;

    SparseArray<Spinner> spinnerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diff);

        this.startTextView = ((EditText) findViewById(R.id.start_text));
        this.endTextView = ((EditText) findViewById(R.id.end_text));

        goButton = (Button) findViewById(R.id.go_button);

        getLoaderManager().initLoader(MPG_RESOURCE_LOADER, null, this);
        getLoaderManager().initLoader(DIRECTIONS_RESOURCE_LOADER, null, this);
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
                FormActivity.this.getLoaderManager().restartLoader(MPG_LOADER, bundle, FormActivity.this);
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
                MAKE_LOADER, YEAR_KEY));
        makeSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                MODEL_LOADER, MAKE_KEY));
        modelSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                OPTIONS_LOADER, MODEL_KEY));

        this.spinnerMap = new SparseArray<Spinner>();
        this.spinnerMap.put(YEAR_LOADER, yearSpinner);
        this.spinnerMap.put(MAKE_LOADER, makeSpinner);
        this.spinnerMap.put(MODEL_LOADER, modelSpinner);
        this.spinnerMap.put(OPTIONS_LOADER, this.optionsSpinner);

        getLoaderManager().restartLoader(YEAR_LOADER, bundle, this);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(VehicleInfo.class, new VehicleInfoDeserializer())
                .registerTypeAdapter(Itinerary.class, new ItineraryDeserializer())
                .create();
        switch (i) {
            case YEAR_LOADER:
            case MAKE_LOADER:
            case MODEL_LOADER:
            case OPTIONS_LOADER:
                return new VehicleInfoGetter(this, bundle, this.mpgResource);
            case MPG_RESOURCE_LOADER:
                return new ResourceGetter<MPGResource>(this, MPG_ENDPOINT, MPGResource.class, gson);
            case MPG_LOADER:
                return new MPGGetter(this, this.mpgResource, bundle);
            case DIRECTIONS_RESOURCE_LOADER:
                return new ResourceGetter<DirectionsResource>(this, GOOGLE_ENDPOINT, DirectionsResource.class, gson);
            case DIRECTIONS_LOADER:
                return new ItineraryGetter(this, bundle, this.directionsResource);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        switch (loader.getId()) {
            case YEAR_LOADER:
            case MAKE_LOADER:
            case MODEL_LOADER:
            case OPTIONS_LOADER:
                VehicleInfo info = (VehicleInfo)o;
                if (info == null) {
                    return;
                }
                ArrayAdapter<NameValuePair> arrayAdapter = new ArrayAdapter<NameValuePair>(this,
                        android.R.layout.simple_spinner_item, info.getPairs());
                this.spinnerMap.get(loader.getId()).setAdapter(arrayAdapter);
                break;
            case MPG_RESOURCE_LOADER:
                this.mpgResource = (MPGResource)o;
                initSpinner();
                break;
            case MPG_LOADER:
                this.mpginfo = (MPGInfo)o;
                doMapsStuff();
                break;
            case DIRECTIONS_RESOURCE_LOADER:
                this.directionsResource = (DirectionsResource)o;
                break;
            case DIRECTIONS_LOADER:
                CarbonFootprint carbonFootprint = (CarbonFootprint)o;
                startNextActivity(carbonFootprint);
                break;
        }
    }

    private void startNextActivity(CarbonFootprint carbonFootprint) {
        Intent intent = new Intent(this, ShowActivity.class);
        intent.putExtra(CARBON_FOOTPRINT_KEY, carbonFootprint);

        startActivity(intent);
        finish();
    }

    private void doMapsStuff() {
        String start = this.startTextView.getText().toString();
        if (start == null || start.isEmpty()) {
            Toast.makeText(this, "Please input everything", Toast.LENGTH_LONG).show();
            return;
        }

        String end = this.endTextView.getText().toString();
        if (end == null || end.isEmpty()) {
            Toast.makeText(this, "Please input everything", Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(START_KEY, start);
        bundle.putString(END_KEY, end);
        bundle.putLong(DEPARTURE_TIME, new Date().getTime());
        bundle.putFloat(MPG_KEY, this.mpginfo.getHighway08());

        getLoaderManager().restartLoader(DIRECTIONS_LOADER, bundle, this);

        //call google with start/end point
        // - loader, callbacks, model, serialize?
        // - new activity, given it the data from google, mpg (in a bundle), send it through an intent
        // - new activity, calculates the values and shows stuff on the screen
        // - integrate views with cori's stuff
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private class SpinnerOnSelectedItemListener implements AdapterView.OnItemSelectedListener {
        private Bundle bundle;
        private int loaderToTrigger;
        private String key;

        public SpinnerOnSelectedItemListener(Bundle bundle, int loaderToTrigger, String key) {
            this.bundle = bundle;
            this.loaderToTrigger = loaderToTrigger;
            this.key = key;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String item = adapterView.getAdapter().getItem(i).toString();
            this.bundle.putString(this.key, item);
            getLoaderManager().restartLoader(this.loaderToTrigger, this.bundle, FormActivity.this);
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
}
