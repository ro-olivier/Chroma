package fr.zigomar.chroma.chroma;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodActivity extends AppCompatActivity {

    public static final String CURRENT_DATE = "com.example.chroma.current_date";

    public static final int MIN_MOOD = 1;
    public static final int MAX_MOOD = 9;
    public static final int INITIAL_MOOD = 5;

    private JSONObject moodData;

    private Date currentDate = new Date();
    private String filename;

    private NumberPicker pickerMood1;
    private NumberPicker pickerMood2;
    private NumberPicker pickerMood3;

    private EditText textData;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        this.currentDate.setTime(getIntent().getLongExtra(CURRENT_DATE, -1));
        updateDateView();
        this.filename = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(this.currentDate);

        this.pickerMood1 = (NumberPicker) findViewById(R.id.MoodPicker1);
        this.pickerMood2 = (NumberPicker) findViewById(R.id.MoodPicker2);
        this.pickerMood3 = (NumberPicker) findViewById(R.id.MoodPicker3);

        this.textData = (EditText) findViewById(R.id.TextData);

        Button saveButton = (Button) findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initViews();
        initMoodData();

    }

    private void initMoodData() {
        Log.i("CHROMA", "Data init started.");

        FileInputStream is;

        try {
            // read the file if it exists, and create JSON object
            is  = openFileInput(this.filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            this.moodData = new JSONObject(new String(buffer, "UTF-8"));
            Log.i("CHROMA", "Read currentDate file and obtained following data :" + this.moodData.toString());
            // load the values in the views
            updateViewsWithMoodData();


        } catch (FileNotFoundException e) {
            // the file does exist yet so we load the views with the default values
            Log.i("CHROMA", "File " + this.filename + " was not found");
            e.printStackTrace();
            updateViewWithInitData();

            // we create an empty JSON object for later
            this.moodData = new JSONObject();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            Log.e("CHROMA","Data in file does not seem to be in JSON format");
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("CHORMA","Starting activity closing...");

        try {
            updateMoodData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("CHROMA", "Received the data : " + this.moodData.toString());

        String string = this.moodData.toString();

        FileOutputStream outputStream;

        try {
            Log.i("CHROMA", "Writing new values to file " + this.filename + " : " + string);
            outputStream = openFileOutput(this.filename, MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        Log.i("CHROMA", "Setting min max params for the views");
        this.pickerMood1.setMaxValue(MAX_MOOD);
        this.pickerMood2.setMaxValue(MAX_MOOD);
        this.pickerMood3.setMaxValue(MAX_MOOD);

        this.pickerMood1.setMinValue(MIN_MOOD);
        this.pickerMood2.setMinValue(MIN_MOOD);
        this.pickerMood3.setMinValue(MIN_MOOD);
    }

    private void updateDateView() {
        TextView dateView = (TextView) findViewById(R.id.DateTextView);
        String formattedDate = (new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(this.currentDate));
        Log.i("CHROMA", "Updating date : " + formattedDate);
        dateView.setText(formattedDate);
    }

    private void updateMoodData() throws JSONException {
        Log.i("CHROMA", "Updating the moodData object with current values in the views");
        this.moodData.put("mood_eval1", this.pickerMood1.getValue());
        this.moodData.put("mood_eval2", this.pickerMood2.getValue());
        this.moodData.put("mood_eval3", this.pickerMood3.getValue());
        this.moodData.put("mood_text", this.textData.getText());

    }

    private void updateViewsWithMoodData () throws JSONException {
        Log.i("CHROMA", "Updating the views with the moodData object");
        this.pickerMood1.setValue(this.moodData.getInt("mood_eval1"));
        this.pickerMood2.setValue(this.moodData.getInt("mood_eval2"));
        this.pickerMood3.setValue(this.moodData.getInt("mood_eval3"));

        this.textData.setText(this.moodData.getString("mood_text"));
    }

    private void updateViewWithInitData() {
        Log.i("CHROMA", "Applying default values to the views");
        this.pickerMood1.setValue(INITIAL_MOOD);
        this.pickerMood2.setValue(INITIAL_MOOD);
        this.pickerMood3.setValue(INITIAL_MOOD);
    }

}
