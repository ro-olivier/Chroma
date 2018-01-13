package fr.zigomar.chroma.chroma.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.HashMap;

import fr.zigomar.chroma.chroma.R;

public class MoodActivity extends InputActivity {

    public static final int MIN_MOOD = 1;
    public static final int MAX_MOOD = 9;

    private NumberPicker pickerMood1;
    private NumberPicker pickerMood2;
    private NumberPicker pickerMood3;

    private EditText textData;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout, yay, we can see stuff on the screen!
        setContentView(R.layout.activity_mood);

        // call inherited initiating method
        init();

        // getting the views from their id
        this.pickerMood1 = (NumberPicker) findViewById(R.id.MoodPicker1);
        this.pickerMood2 = (NumberPicker) findViewById(R.id.MoodPicker2);
        this.pickerMood3 = (NumberPicker) findViewById(R.id.MoodPicker3);

        this.textData = (EditText) findViewById(R.id.TextData);

        // setting the parameters of the views
        initViews();

        // setting the data in the views
        initMoodData();
    }

    private void initViews() {
        // method used to set some parameters to the number picker views
        // making it could be done directly in the layout XML file
        Log.i("CHROMA", "Setting settings for the views.");
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

    private void initMoodData() {
        // initialize the views with data coming from file (or defaults)
        // collection of data is managed by the DataHandler, this method only
        // sets the values of the views
        HashMap<String, String> data = this.dh.getMoodData();

        this.pickerMood1.setValue(Integer.parseInt(data.get("eval1")));
        this.pickerMood2.setValue(Integer.parseInt(data.get("eval2")));
        this.pickerMood3.setValue(Integer.parseInt(data.get("eval3")));

        if (!data.get("txt").isEmpty()) {
            Log.i("CHROMA", "Applying data retrieved from file to text field");
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
