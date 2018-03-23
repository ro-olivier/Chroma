package fr.zigomar.chroma.chroma.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.zigomar.chroma.chroma.R;

public class SleepActivity extends InputActivity {

    private NumberPicker beginHour;
    private NumberPicker beginMinute;
    private NumberPicker endHour;
    private NumberPicker endMinute;

    private boolean pickersVisible;

    private EditText notes;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        this.beginHour = findViewById(R.id.SleepStartHourPicker);
        this.beginMinute = findViewById(R.id.SleepStartMinutePicker);
        this.endHour = findViewById(R.id.SleepEndHourPicker);
        this.endMinute = findViewById(R.id.SleepEndMinutePicker);

        confPicker(this.beginMinute, 60, 5);
        confPicker(this.beginHour, 24, 1);
        confPicker(this.endMinute, 60, 5);
        confPicker(this.endHour, 24, 1);

        this.pickersVisible = true;

        this.notes = findViewById(R.id.TextData);

        this.notes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    beginMinute.setVisibility(View.GONE);
                    beginHour.setVisibility(View.GONE);
                    endHour.setVisibility(View.GONE);
                    endMinute.setVisibility(View.GONE);
                    pickersVisible = true;
                }
            }
        });

        initSleepData();

     }

    @SuppressLint("DefaultLocale")
    private void confPicker(NumberPicker picker, int max, int interval) {
        picker.setMinValue(0);
        picker.setMaxValue((max / interval) - 1);
        List<String> displayedValues = new ArrayList<>();
        for (int i = 0; i < max; i += interval) {
            displayedValues.add(String.format("%02d", i));
        }
        picker.setDisplayedValues(displayedValues
                .toArray(new String[displayedValues.size()]));

        picker.setWrapSelectorWheel(false);
    }

    private void initSleepData() {
        // initialize the views with data coming from file (or defaults)
        // collection of data is managed by the DataHandler, this method only
        // sets the values of the views
        HashMap<String, String> data = this.dh.getSleepData();

        if (data.get("sleep_begin_hour") != null) {
            this.beginHour.setValue(Integer.parseInt(data.get("sleep_begin_hour")));
        } else {
            this.beginHour.setValue(23);
        }

        if (data.get("sleep_begin_minute") != null) {
            this.beginMinute.setValue(Integer.parseInt(data.get("sleep_begin_minute")));
        } else {
            this.beginMinute.setValue(30);
        }

        if (data.get("sleep_end_hour") != null) {
            this.endHour.setValue(Integer.parseInt(data.get("sleep_end_hour")));
        } else {
            this.endHour.setValue(7);
        }

        if (data.get("sleep_end_minute") != null) {
            this.endMinute.setValue(Integer.parseInt(data.get("sleep_end_minute")));
        } else {
            this.endMinute.setValue(30);
        }

        if (!data.get("txt").isEmpty()) {
            this.notes.setText(data.get("txt"));
        }
    }

    @Override
    public void onBackPressed() {
        if (!this.pickersVisible) {
            this.beginMinute.setVisibility(View.VISIBLE);
            this.beginHour.setVisibility(View.VISIBLE);
            this.endHour.setVisibility(View.VISIBLE);
            this.endMinute.setVisibility(View.VISIBLE);
            this.pickersVisible = true;
        } else {
            super.onBackPressed();
        }
    }

    private int computeSleepTime() {

        int result = 0;

        if (this.beginHour.getValue() > this.endHour.getValue()) {
            // couché avant minuit
            // on complète jusqu'à minuit
            result += 60*(23 - this.beginHour.getValue()) + (60 - 5*this.beginMinute.getValue());
            // on complète ensuite les heures dormies
            result += 60*this.endHour.getValue();
            // et on complète les minutes dormies
            result += 5*this.endMinute.getValue();
        } else {
            // on complète jusqu'à la prochaine heure
            result += (60 - 5*this.beginMinute.getValue());
            // on complète les heures dormies
            result += 60*(this.endHour.getValue() - this.beginHour.getValue());
            // on complète les minutes dormies
            result += 5*this.endMinute.getValue();
        }

        return result;
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current sleep data");
        this.dh.saveSleepData(this.beginHour.getValue(),
                this.beginMinute.getValue(),
                this.endHour.getValue(),
                this.endMinute.getValue(),
                this.notes.getText().toString(),
                computeSleepTime());
    }
}
