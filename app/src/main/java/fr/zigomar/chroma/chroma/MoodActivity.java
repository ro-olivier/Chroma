package fr.zigomar.chroma.chroma;

import android.app.Activity;
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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Robin on 25/12/2017.
 */

public class MoodActivity extends AppCompatActivity {

    public static final String CURRENT_DATE = "com.example.chroma.current_date";

    Date currentDate;

    NumberPicker pickerMood1;
    NumberPicker pickerMood2;
    NumberPicker pickerMood3;

    EditText textData;

    Button saveButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        this.currentDate = new Date();
        this.currentDate.setTime(getIntent().getLongExtra(CURRENT_DATE, -1));
        updateDateView();

        this.pickerMood1 = (NumberPicker) findViewById(R.id.MoodPicker1);
        this.pickerMood2 = (NumberPicker) findViewById(R.id.MoodPicker2);
        this.pickerMood3 = (NumberPicker) findViewById(R.id.MoodPicker3);

        initNumberPicker(this.pickerMood1);
        initNumberPicker(this.pickerMood2);
        initNumberPicker(this.pickerMood3);

        this.textData = (EditText) findViewById(R.id.TextData);

        this.saveButton = (Button) findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = null;
                try {
                    data = parseMoodData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("CHROMA", "Received the data : " + data);
                // store data in a bundle, or other...
                // close the activity and go back to the previous one
            }
        });

    }

    @Override
    public void onStop() {

    }

    public void initNumberPicker(NumberPicker np) {
        np.setMaxValue(9);
        np.setMinValue(1);
        np.setValue(5);
    }

    public void updateDateView() {
        TextView dateView = (TextView) findViewById(R.id.DateTextView);
        String formattedDate = (new SimpleDateFormat("yyyy/MM/dd").format(currentDate));
        Log.i("CHROMA", "Updating date : " + formattedDate);
        dateView.setText(formattedDate);
    }

    public String parseMoodData() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("mood1", this.pickerMood1.getValue());
        data.put("mood2", this.pickerMood2.getValue());
        data.put("mood3", this.pickerMood3.getValue());
        data.put("text", this.textData.getText());

        return data.toString();
    }

}
