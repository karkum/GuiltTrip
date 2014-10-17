package com.opower.guilttrip;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.opower.guilttrip.model.CarbonFootprint;
import com.opower.guilttrip.model.Itinerary;

import static com.opower.guilttrip.FormActivity.CARBON_FOOTPRINT_KEY;

public class ShowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_stuff);

        CarbonFootprint carbonFootprint = (CarbonFootprint)getIntent().getSerializableExtra(CARBON_FOOTPRINT_KEY);

        TextView carTimeTextView = (TextView)findViewById(R.id.carTimeTxt);
        TextView carCO2TextView = (TextView)findViewById(R.id.carco2Txt);
        TextView busTimeTextView = (TextView)findViewById(R.id.busTxt);
        TextView busCO2TextView = (TextView)findViewById(R.id.co2BusTxt);
//        TextView savingsRateTextView = (TextView)findViewById(R.id.savingsRate);
        TextView savingsTotalTextView = (TextView)findViewById(R.id.savingsTotal);
        TextView booksTextView = (TextView)findViewById(R.id.books);

        Itinerary car = carbonFootprint.getDrive();
        if (car == null) {
            Toast.makeText(this, "Could not find driving directions", Toast.LENGTH_LONG).show();
        }
        else {
            carCO2TextView.setText("" + String.format("%.1f lbs CO2", car.getCarbon()));
            carTimeTextView.setText(String.format("%.1f min", car.getDuration()));
        }

        Itinerary transit = carbonFootprint.getTransit();
        if (transit != null) {
            busCO2TextView.setText("" + String.format("%.1f lbs CO2", transit.getCarbon()));
            busTimeTextView.setText(String.format("%.1f min", transit.getDuration()));
        }
        else {
            Toast.makeText(this, "Could not find transit directions", Toast.LENGTH_LONG).show();
        }

//        savingsRateTextView.setText(String.format("%.1f%% lbs CO2 saved", carbonFootprint.getSavingsPercentage()));
        savingsTotalTextView.setText(String.format("%.1f lbs CO2/week saved", carbonFootprint.getSavingsRate()));
        booksTextView.setText(String.format("%.1f extra books read/year", carbonFootprint.getReadingTime()));
    }
}
