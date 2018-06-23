package fr.zigomar.chroma.chroma.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fr.zigomar.chroma.chroma.R;

public class SleepActivity extends InputActivity {

    private NumberPicker bedtimeHour;
    private NumberPicker bedtimeMinute;
    private NumberPicker wakeupHour;
    private NumberPicker wakeupMinute;

    private TextView bedtimeText;
    private TextView wakeupText;

    private TextView bedtimeDay;
    private TextView wakeupDay;

    private boolean pickersVisible;

    private EditText notes;

    private static final int MINUTES_INTERVAL = 5;
    private static final int HOURS_INTERVAL = 1;
    private static final int HOURS_MAXIMUM = 24;
    private static final int MINUTES_MAXIMUM = 60;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        this.bedtimeHour = findViewById(R.id.SleepStartHourPicker);
        this.bedtimeMinute = findViewById(R.id.SleepStartMinutePicker);
        this.wakeupHour = findViewById(R.id.SleepEndHourPicker);
        this.wakeupMinute = findViewById(R.id.SleepEndMinutePicker);

        this.wakeupText = findViewById(R.id.wakeupTextView);
        this.bedtimeText = findViewById(R.id.bedtimeTextView);

        this.bedtimeDay = findViewById(R.id.SleepStartDayTextView);
        this.wakeupDay = findViewById(R.id.SleepEndDayTextView);

        this.bedtimeDay.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(this.currentDate));
        this.wakeupDay.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(this.currentDate));

        confMinutePicker(this.bedtimeMinute, R.id.SleepStartHourPicker, R.id.SleepStartDayTextView);
        confHourPicker(this.bedtimeHour, R.id.SleepStartDayTextView);
        confMinutePicker(this.wakeupMinute, R.id.SleepEndHourPicker, R.id.SleepEndDayTextView);
        confHourPicker(this.wakeupHour, R.id.SleepEndDayTextView);

        this.pickersVisible = true;

        this.notes = findViewById(R.id.TextData);
        EditText placeholder = findViewById(R.id.placeHolder);

        placeholder.requestFocus();

        this.notes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    bedtimeMinute.setVisibility(View.GONE);
                    bedtimeHour.setVisibility(View.GONE);
                    bedtimeText.setVisibility(View.GONE);
                    bedtimeDay.setVisibility(View.GONE);
                    wakeupHour.setVisibility(View.GONE);
                    wakeupMinute.setVisibility(View.GONE);
                    wakeupText.setVisibility(View.GONE);
                    wakeupDay.setVisibility(View.GONE);

                    //notes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                     //       notes.getMaxHeight()));
                    notes.setLines(12);

                    pickersVisible = false;

                    notes.post(new Runnable() {
                        @Override
                        public void run() {
                            notes.setSelection(notes.getText().length());
                        }
                    });
                }
            }
        });

        initSleepData();

     }

    @SuppressLint("DefaultLocale")
    private void confHourPicker(NumberPicker picker, final int txt_id) {
        picker.setMinValue(0);
        picker.setMaxValue((HOURS_MAXIMUM / HOURS_INTERVAL) - 1);
        List<String> displayedValues = new ArrayList<>();
        for (int i = 0; i < HOURS_MAXIMUM; i += HOURS_INTERVAL) {
            displayedValues.add(String.format("%02d", i));
        }
        picker.setDisplayedValues(displayedValues
                .toArray(new String[displayedValues.size()]));

        picker.setWrapSelectorWheel(true);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal - newVal == HOURS_MAXIMUM - 1) {
                    //went one day forward
                    TextView txt = findViewById(txt_id);
                    incDate(txt);
                } else if (oldVal - newVal == -(HOURS_MAXIMUM - 1)) {
                    //went on day backward
                    TextView txt = findViewById(txt_id);
                    decDate(txt);
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void confMinutePicker(NumberPicker picker, final int picker_id, final int txt_id) {
        picker.setMinValue(0);
        picker.setMaxValue((MINUTES_MAXIMUM / MINUTES_INTERVAL) - 1);
        List<String> displayedValues = new ArrayList<>();
        for (int i = 0; i < MINUTES_MAXIMUM; i += MINUTES_INTERVAL) {
            displayedValues.add(String.format("%02d", i));
        }
        picker.setDisplayedValues(displayedValues
                .toArray(new String[displayedValues.size()]));

        picker.setWrapSelectorWheel(true);

        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal - newVal == (MINUTES_MAXIMUM / MINUTES_INTERVAL) - 1) {
                    //went one hour forward
                    NumberPicker np = findViewById(picker_id);
                    np.setValue(np.getValue() + 1);
                    if (np.getValue() == 0) {
                        TextView txt = findViewById(txt_id);
                        incDate(txt);
                    }
                } else if (oldVal - newVal == -((MINUTES_MAXIMUM / MINUTES_INTERVAL) - 1)) {
                    //went one hour backward
                    NumberPicker np = findViewById(picker_id);
                    np.setValue(np.getValue() - 1);
                    if (np.getValue() == HOURS_MAXIMUM - 1) {
                        TextView txt = findViewById(txt_id);
                        decDate(txt);
                    }
                }
            }
        });
    }

    private void incDate(TextView txt) {
        String currentTxt = txt.getText().toString();
        Date currentTxtDate = null;
        try {
            currentTxtDate = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(currentTxt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTxtDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(cal.getTime());
        txt.setText(formattedDate);
    }

    private void decDate(TextView txt) {
        String currentTxt = txt.getText().toString();
        Date currentTxtDate = null;
        try {
            currentTxtDate = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).parse(currentTxt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTxtDate);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(cal.getTime());
        txt.setText(formattedDate);
    }

    private void initSleepData() {
        // initialize the views with data coming from file (or defaults)
        // collection of data is managed by the DataHandler, this method only
        // sets the values of the views
        HashMap<String, String> data = this.dh.getSleepData();

        /*
        if (data.get("sleep_begin_hour") != null) {
            this.bedtimeHour.setValue(Integer.parseInt(data.get("sleep_begin_hour")));
        } else {
            this.bedtimeHour.setValue(23);
        }

        if (data.get("sleep_begin_minute") != null) {
            int value = Integer.parseInt(data.get("sleep_begin_minute"));
            int index = value / MINUTES_INTERVAL;
            this.bedtimeMinute.setValue(index);
        } else {
            this.bedtimeMinute.setValue(Integer.parseInt(this.bedtimeMinute.getDisplayedValues()[6]));
        }

        if (data.get("sleep_end_hour") != null) {
            this.wakeupHour.setValue(Integer.parseInt(data.get("sleep_end_hour")));
        } else {
            this.wakeupHour.setValue(7);
        }

        if (data.get("sleep_end_minute") != null) {
            int value = Integer.parseInt(data.get("sleep_end_minute"));
            int index = value / MINUTES_INTERVAL;
            this.wakeupMinute.setValue(index);
        } else {
            this.bedtimeMinute.setValue(Integer.parseInt(this.wakeupMinute.getDisplayedValues()[6]));
        }
        */

        if (data.containsKey("bedtimeData")) {
            String bedtime_str = data.get("bedtimeData");
            this.bedtimeDay.setText(bedtime_str.split(" ")[0]);
            this.bedtimeHour.setValue(Integer.parseInt(bedtime_str.split(" ")[1].split(":")[0]));
            this.bedtimeMinute.setValue(Integer.parseInt(bedtime_str.split(":")[1]) / MINUTES_INTERVAL);
        }

        if (data.containsKey("wakeupData")) {
            String bedtime_str = data.get("wakeupData");
            this.wakeupDay.setText(bedtime_str.split(" ")[0]);
            this.wakeupHour.setValue(Integer.parseInt(bedtime_str.split(" ")[1].split(":")[0]));
            this.wakeupMinute.setValue(Integer.parseInt(bedtime_str.split(":")[1]) / MINUTES_INTERVAL);
        }

        if (!data.get("txt").isEmpty()) {
            this.notes.setText(data.get("txt"));
        }
    }

    @Override
    public void onBackPressed() {
        if (!this.pickersVisible) {
            this.bedtimeMinute.setVisibility(View.VISIBLE);
            this.bedtimeHour.setVisibility(View.VISIBLE);
            this.bedtimeText.setVisibility(View.VISIBLE);
            this.bedtimeDay.setVisibility(View.VISIBLE);
            this.wakeupHour.setVisibility(View.VISIBLE);
            this.wakeupMinute.setVisibility(View.VISIBLE);
            this.wakeupText.setVisibility(View.VISIBLE);
            this.wakeupDay.setVisibility(View.VISIBLE);

            //notes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
             //       notes.getMinHeight()));
            notes.setLines(5);

            this.pickersVisible = true;
        } else {
            super.onBackPressed();
        }
    }

    /*private int computeSleepTime() {

        int result = 0;

        if (this.bedtimeHour.getValue() > this.wakeupHour.getValue()) {
            // couché avant minuit
            // on complète jusqu'à minuit
            result += 60*(23 - this.bedtimeHour.getValue()) + (60 - 5*this.bedtimeMinute.getValue());
            // on complète ensuite les heures dormies
            result += 60*this.wakeupHour.getValue();
            // et on complète les minutes dormies
            result += 5*this.wakeupMinute.getValue();
        } else {
            // on complète jusqu'à la prochaine heure
            result += (60 - 5*this.bedtimeMinute.getValue());
            // on complète les heures dormies
            result += 60*(this.wakeupHour.getValue() - this.bedtimeHour.getValue());
            // on complète les minutes dormies
            result += 5*this.wakeupMinute.getValue();
        }

        return result;
    }
    */

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current sleep data");
        /*
        this.dh.saveSleepData(this.bedtimeHour.getValue(),
                Integer.parseInt(this.bedtimeMinute.getDisplayedValues()[this.bedtimeMinute.getValue()]),
                this.wakeupHour.getValue(),
                Integer.parseInt(this.wakeupMinute.getDisplayedValues()[this.wakeupMinute.getValue()]),
                this.notes.getText().toString());
                */
        String wakeupData = this.wakeupDay.getText().toString() + " " + this.wakeupHour.getValue()
                + ":" + this.wakeupMinute.getDisplayedValues()[this.wakeupMinute.getValue()];
        String bedtimeData = this.bedtimeDay.getText().toString() + " " + this.bedtimeHour.getValue()
                + ":" + this.bedtimeMinute.getDisplayedValues()[this.bedtimeMinute.getValue()];

        this.dh.saveSleepData(wakeupData, bedtimeData, this.notes.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

}
