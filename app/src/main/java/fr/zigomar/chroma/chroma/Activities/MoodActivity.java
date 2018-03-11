package fr.zigomar.chroma.chroma.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.HashMap;

import fr.zigomar.chroma.chroma.R;

public class MoodActivity extends InputActivity {

    private static final int MIN_MOOD = 1;
    private static final int MAX_MOOD = 9;

    private NumberPicker pickerMood1;
    private NumberPicker pickerMood2;
    private NumberPicker pickerMood3;
    private EditText textData;

    private boolean pickersVisible = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting the views from their id
        this.pickerMood1 = findViewById(R.id.MoodPicker1);
        this.pickerMood2 = findViewById(R.id.MoodPicker2);
        this.pickerMood3 = findViewById(R.id.MoodPicker3);

        this.pickerMood3.setFocusable(true);
        this.pickerMood3.setFocusableInTouchMode(true);

        this.textData = findViewById(R.id.TextData);

        // setting the parameters of the views
        initViews();

        // setting the data in the views
        initMoodData();

        textData.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pickerMood1.setVisibility(View.GONE);
                    pickerMood2.setVisibility(View.GONE);
                    pickerMood3.setVisibility(View.GONE);
                    pickersVisible = false;
                }
            }
        });
    }

    private void initViews() {
        this.pickerMood1.setMaxValue(MAX_MOOD);
        this.pickerMood2.setMaxValue(MAX_MOOD);
        this.pickerMood3.setMaxValue(MAX_MOOD);

        this.pickerMood1.setMinValue(MIN_MOOD);
        this.pickerMood2.setMinValue(MIN_MOOD);
        this.pickerMood3.setMinValue(MIN_MOOD);

        this.pickerMood1.setWrapSelectorWheel(false);
        this.pickerMood2.setWrapSelectorWheel(false);
        this.pickerMood3.setWrapSelectorWheel(false);

    }

    @Override
    public void onBackPressed() {
        if (!this.pickersVisible) {
            pickerMood1.setVisibility(View.VISIBLE);
            pickerMood2.setVisibility(View.VISIBLE);
            pickerMood3.setVisibility(View.VISIBLE);
            pickersVisible = true;
        }
    }

    private void initMoodData() {
        // initialize the views with data coming from file (or defaults)
        // collection of data is managed by the DataHandler, this method only
        // sets the values of the views
        HashMap<String, String> data = this.dh.getMoodData();

        this.pickerMood1.setValue(Integer.parseInt(data.get("eval1")));
        this.pickerMood2.setValue(Integer.parseInt(data.get("eval2")));
        this.pickerMood3.setValue(Integer.parseInt(data.get("eval3")));

        if (!data.get("txt").isEmpty()) {
            this.textData.setText(data.get("txt"));
        }
    }

    @Override
    protected void saveData() {
        this.dh.saveMoodData(this.pickerMood1.getValue(),
                this.pickerMood2.getValue(),
                this.pickerMood3.getValue(),
                this.textData.getText().toString());
    }
}
