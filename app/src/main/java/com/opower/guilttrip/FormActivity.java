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
import com.opower.guilttrip.model.VehicleInfo;
import com.opower.guilttrip.model.VehicleMenuItems;
import com.opower.guilttrip.serialization.VehicleInfoDeserializer;
import com.opower.guilttrip.serialization.VehicleMenuItemsDeserializer;
import com.opower.guilttrip.resource.MPGResource;

import java.util.ArrayList;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class FormActivity extends Activity {

    private static final int YEAR_LOADER = 0;
    private static final int MAKE_LOADER = 1;
    private static final int MODEL_LOADER = 2;
    private static final int OPTIONS_LOADER = 3;

    private static final String MPG_ENDPOINT = "http://www.fueleconomy.gov";

    private static final String YEAR_KEY = "year";
    private static final String MAKE_KEY = "make";
    private static final String MODEL_KEY = "model";

    private Spinner optionsSpinner;
    private EditText startTextView;
    private EditText endTextView;
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        EditText startTextView = ((EditText)findViewById(R.id.start_text));
        EditText endTextView = ((EditText)findViewById(R.id.end_text));

        goButton = (Button)findViewById(R.id.go_button);

        Spinner yearSpinner = (Spinner)findViewById(R.id.yearSpinner);
        Spinner makeSpinner = (Spinner)findViewById(R.id.makeSpinner);
        Spinner modelSpinner = (Spinner)findViewById(R.id.modelSpinner);
        optionsSpinner = (Spinner)findViewById(R.id.optionsSpinner);

        Bundle bundle = new Bundle();
        yearSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                MAKE_LOADER, makeSpinner, YEAR_KEY));
        makeSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                MODEL_LOADER, modelSpinner, MAKE_KEY));
        modelSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener(bundle,
                OPTIONS_LOADER, optionsSpinner, MODEL_KEY));

        getLoaderManager().initLoader(YEAR_LOADER, bundle, new SpinnerLoader(yearSpinner));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //call google
        this.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object selection = FormActivity.this.optionsSpinner.getSelectedItem();
                if (selection == null) {
                    Toast.makeText(FormActivity.this, "Please input everything", Toast.LENGTH_LONG).show();
                    return;
                }

                String selectedCode = selection.toString();
                int code = Integer.valueOf(selectedCode.substring(0, selectedCode.indexOf("|") - 1));
                Toast.makeText(FormActivity.this, "Doing this guy: " + code, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static class VehicleInfoGetterer extends AsyncTaskLoader<List<String>> {

        private Bundle bundle;

        public VehicleInfoGetterer(Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        public List<String> loadInBackground() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(VehicleMenuItems.class, new VehicleMenuItemsDeserializer())
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
            MPGResource mpgResource = restAdapter.create(MPGResource.class);
            try {
                switch(getId()) {
                    case YEAR_LOADER:
                        return mpgResource.listYears().getItems();

                    case MAKE_LOADER:
                        String yearParam = this.bundle.getString(YEAR_KEY);
                        return mpgResource.listMakes(yearParam).getItems();

                    case MODEL_LOADER:
                        yearParam = this.bundle.getString(YEAR_KEY);
                        String makeParam = this.bundle.getString(MAKE_KEY);
                        return mpgResource.listModels(yearParam, makeParam).getItems();

                    case OPTIONS_LOADER:
                        yearParam = this.bundle.getString(YEAR_KEY);
                        makeParam = this.bundle.getString(MAKE_KEY);
                        String modelParam = this.bundle.getString(MODEL_KEY);
                        VehicleInfo items = mpgResource.listOptions(yearParam, makeParam, modelParam);
                        if (items == null) {
                            return new ArrayList<String>();
                        }
                        return items.getInfoAsString();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return new ArrayList<String>();
        }

        @Override
        public void onStartLoading() {
            forceLoad();
        }
    }

    private class SpinnerLoader implements LoaderManager.LoaderCallbacks<List<String>> {
        private final Spinner spinner;

        private SpinnerLoader(Spinner spinner) {
            this.spinner = spinner;
        }

        @Override
        public Loader<List<String>> onCreateLoader(int i, Bundle bundle) {
            return new VehicleInfoGetterer(FormActivity.this, bundle);
        }

        @Override
        public void onLoadFinished(Loader<List<String>> listLoader, List<String> strings) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FormActivity.this,
                    android.R.layout.simple_spinner_item, strings);
            this.spinner.setAdapter(arrayAdapter);
        }

        @Override
        public void onLoaderReset(Loader<List<String>> listLoader) {

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
            getLoaderManager().initLoader(this.loaderToTrigger, this.bundle,
                    new SpinnerLoader(this.spinner)).forceLoad();
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
