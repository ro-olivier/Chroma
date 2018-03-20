package fr.zigomar.chroma.chroma.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import fr.zigomar.chroma.chroma.adapters.CarTripAdapter;
import fr.zigomar.chroma.chroma.model.CarTrip;
import fr.zigomar.chroma.chroma.R;

public class CarActivity extends InputActivity {

    private EditText origin;
    private EditText startKM;
    private EditText startTime;
    private EditText destination;
    private EditText endKM;
    private EditText endTime;

    private ArrayList<CarTrip> carTrips;
    private CarTripAdapter carTripAdapter;

    private int startHour;
    private int startMinute;

    private int endHour;
    private int endMinute;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init of the data : fetch drinks data in the currentDate file if it exist
        this.carTrips = getCarTrips();

        // getting the views from their id
        this.origin = findViewById(R.id.carTrip_origin);
        this.startKM = findViewById(R.id.carTrip_KMbegin);
        this.startTime = findViewById(R.id.carTrip_TimeBeginDisplay);
        Button startButton = findViewById(R.id.carTrip_SubmitStart);

        this.destination = findViewById(R.id.carTrip_destination);
        this.endKM = findViewById(R.id.carTrip_KMEnd);
        this.endTime = findViewById(R.id.carTrip_TimeEndDisplay);
        Button endButton = findViewById(R.id.carTrip_SubmitEnd);

        resetView();

        this.startTime.setOnFocusChangeListener(new customOnFocusChangeListenerForTime());

        this.startKM.setOnFocusChangeListener(new customOnFocusChangeListenerForDistance());

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin_str = origin.getText().toString();
                String startKM_str = startKM.getText().toString();

                Calendar cal;
                if (startHour == 0 && startMinute == 0) {
                    // this ensures that if the TimePicker was not used we use "now" as the time for the trip
                    cal = Calendar.getInstance();
                    Toast.makeText(getApplicationContext(), R.string.UsingCurrentTime, Toast.LENGTH_SHORT).show();
                } else {
                    cal = getCalWithTime(startHour, startMinute);
                }

                if (origin_str.length() > 0 && startKM_str.length() > 0) {
                    try {
                        double startKM = Double.parseDouble(startKM_str);

                        if (carTrips.size() == 0) {
                            // no car trip registered, we can safely create a new one
                            carTripAdapter.add(new CarTrip(origin_str, cal.getTime(), startKM));
                        } else {
                            // if there is at least a car trip, we need to check that the last one
                            // is complete : if it's not then we already have an ongoing trip and the user
                            // clicked the start button by mistake
                            if (carTrips.get(carTrips.size() - 1).getCompleted()) {
                                // safe to create a new one
                                carTripAdapter.add(new CarTrip(origin_str, cal.getTime(), startKM));
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.AlreadyACarTrip, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataLocationAndKMRequired, Toast.LENGTH_SHORT).show();
                }

            }
        });

        this.endTime.setOnFocusChangeListener(new customOnFocusChangeListenerForTime());

        this.endKM.setOnFocusChangeListener(new customOnFocusChangeListenerForDistance());

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination_str = destination.getText().toString();
                String endKM_str = endKM.getText().toString();

                Calendar cal;
                if (endHour == 0 && endMinute == 0) {
                    // this ensures that if the TimePicker was not used we use "now" as the time for the trip
                    cal = Calendar.getInstance();
                    Toast.makeText(getApplicationContext(), R.string.UsingCurrentTime, Toast.LENGTH_SHORT).show();

                } else {
                    cal = getCalWithTime(endHour, endMinute);
                }

                if (destination_str.length() > 0 && endKM_str.length() > 0) {
                    try {
                        double endKM = Double.parseDouble(endKM_str);

                        try {
                            carTrips.get(carTrips.size() - 1).endTrip(destination_str, cal.getTime(), endKM);
                            carTripAdapter.notifyDataSetChanged();
                            updateSummary();
                            resetView();
                        } catch (CarTrip.TripEndingError e) {
                            Toast.makeText(getApplicationContext(), R.string.UnableToEndTrip, Toast.LENGTH_SHORT).show();
                        } catch (CarTrip.InvalidKMError e) {
                            Toast.makeText(getApplicationContext(), R.string.InvalidEndKM, Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataLocationAndKMRequired, Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.carTrips = getCarTrips();
        updateSummary();

        if (this.carTrips.size() > 0) {
            int last = this.carTrips.size() - 1;
            CarTrip lastTrip = this.carTrips.get(last);

            if (!lastTrip.getCompleted()) {
                this.origin.setText(lastTrip.getStartLocation());
                this.startKM.setText(String.valueOf(lastTrip.getStartKM()));
                Date s = lastTrip.getStartDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(s);
                String display_Time = getDisplayTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                this.startTime.setText(display_Time);
            }
        }

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) drinks
        ListView carTripsListView = findViewById(R.id.ListViewCarTrips);

        this.carTripAdapter = new CarTripAdapter(CarActivity.this, this.carTrips);
        carTripsListView.setAdapter(this.carTripAdapter);

        carTripsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(R.string.DeleteTitle);
                builder.setMessage(R.string.DeleteItemQuestion);

                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        carTripAdapter.remove(carTrips.get(pos));
                        carTripAdapter.notifyDataSetChanged();
                        updateSummary();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
    }

    private Calendar getCalWithTime(int startHour, int startMinute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Calendar.YEAR);
        cal.set(Calendar.MONTH, Calendar.MONTH);
        cal.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH);
        cal.set(Calendar.HOUR_OF_DAY, startHour);
        cal.set(Calendar.MINUTE, startMinute);
        cal.set(Calendar.SECOND, Calendar.SECOND);
        cal.set(Calendar.MILLISECOND, Calendar.MILLISECOND);
        return cal;
    }

    private void resetView() {
        this.destination.setText("");
        this.origin.setText("");
        this.endKM.setText("");
        this.startKM.setText("");
        this.startTime.setText("");
        this.endTime.setText("");
    }

    @SuppressLint("DefaultLocale")
    private void updateSummary() {
        LinearLayout data_summary = findViewById(R.id.data_summary);
        data_summary.setVisibility(View.VISIBLE);

        TextView field0_data = findViewById(R.id.data_summary_field0);
        TextView field1_data = findViewById(R.id.data_summary_field1);
        TextView field2_data = findViewById(R.id.data_summary_field2);
        TextView field2_text = findViewById(R.id.data_summary_text2);

        if (this.carTrips.size() > 0) {

            field0_data.setText(R.string.Summary);

            double distance_total = 0;
            long duration_total = 0;
            for (CarTrip d : this.carTrips) {
                if (d.getCompleted()) {
                    duration_total += d.getDuration();
                    distance_total += d.getDistance();
                }
            }

            long h = TimeUnit.MILLISECONDS.toHours(duration_total);
            long m = TimeUnit.MILLISECONDS.toMinutes(duration_total) - TimeUnit.HOURS.toMinutes(h);
            //long s = TimeUnit.MILLISECONDS.toSeconds(duration_total)
            //        - TimeUnit.MINUTES.toSeconds(m)
            //        - TimeUnit.HOURS.toSeconds(h);

            field1_data.setText(String.format("%02d:%02d",h,m));

            DecimalFormat df = new DecimalFormat("0.0");
            field2_data.setText(df.format(distance_total));

            field2_text.setText(R.string.KilometersUnit);

        } else {
            data_summary.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<CarTrip> getCarTrips(){
        // getting the data is handled by the DataHandler
        return this.dh.getCarTripsList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current car trips");
        this.dh.saveCarTripData(this.carTrips);
    }

    private class customOnFocusChangeListenerForDistance implements View.OnFocusChangeListener {

        EditText edtxt;
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            this.edtxt = (EditText) v;
            String resultString;
            if (!hasFocus) {
                String distanceStr = this.edtxt.getText().toString();
                if (distanceStr.split("\\.").length > 1) {
                    String wholePart = distanceStr.split("\\.")[0];
                    String decimalPart = distanceStr.split("\\.")[1];

                    resultString = wholePart + "." + decimalPart.substring(0, 1);
                } else {
                    resultString = distanceStr + ".0";
                }
                this.edtxt.setText(resultString);
            }
        }
    }

    private class customOnFocusChangeListenerForTime implements View.OnFocusChangeListener {

        protected View v;
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            this.v = v;
            if (hasFocus) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CarActivity.this,
                        new customOnTimeSetListener(), hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        }

        private class customOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String displayed_Time = getDisplayTime(selectedHour, selectedMinute);

                if (v.getId() == R.id.carTrip_TimeBeginDisplay) {
                    startTime.setText(displayed_Time);
                    startHour = selectedHour;
                    startMinute = selectedMinute;
                } else if (v.getId() == R.id.carTrip_TimeEndDisplay) {
                    endTime.setText(displayed_Time);
                    endHour = selectedHour;
                    endMinute = selectedMinute;
                }
            }
        }
    }

    public String getDisplayTime(int hour, int minute) {
        if (hour < 10) {
            if (minute < 10) {
                return "0" + String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else {
                return "0" + String.valueOf(hour) + ":" + String.valueOf(minute);
            }
        } else if (minute < 10) {
            return String.valueOf(hour) + ":0" + String.valueOf(minute);
        } else {
            return String.valueOf(hour) + ":" + String.valueOf(minute);
        }
    }
}