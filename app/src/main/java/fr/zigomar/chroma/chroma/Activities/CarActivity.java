package fr.zigomar.chroma.chroma.Activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.zigomar.chroma.chroma.Adapters.CarTripAdapter;
import fr.zigomar.chroma.chroma.Model.CarTrip;
import fr.zigomar.chroma.chroma.R;

public class CarActivity extends InputActivity {

    // the list holding the data
    private ArrayList<CarTrip> carTrips;
    // the adapter managing the view of the data
    private CarTripAdapter carTripAdapter;
    private EditText origin;
    private EditText startKM;
    private EditText startTime;
    private EditText destination;
    private EditText endKM;
    private EditText endTime;

    private int startHour;
    private int startMinute;

    private int endHour;
    private int endMinute;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout
        setContentView(R.layout.activity_cartrip);

        // call inherited initiating method
        init();

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

        startTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(CarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                                String displayed_Time = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                                startTime.setText(displayed_Time);
                                startHour = selectedHour;
                                startMinute = selectedMinute;
                            }
                        }, hour, minute, true);
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                }
        });

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
                            Log.i("CHROMA", carTrips.get(carTrips.size() - 1).toString());
                            if (carTrips.get(carTrips.size() - 1).getCompleted()) {
                                // safe to create a new one
                                carTripAdapter.add(new CarTrip(origin_str, cal.getTime(), startKM));
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.AlreadyACarTrip, Toast.LENGTH_SHORT).show();
                            }
                        }
                        Log.i("CHROMA", "Currently " + carTrips.size() + " car trips.");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataLocationAndKMRequired, Toast.LENGTH_SHORT).show();
                }

            }
        });

        endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(CarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String displayed_Time = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                            endTime.setText(displayed_Time);
                            endHour = selectedHour;
                            endMinute = selectedMinute;
                        }
                    }, hour, minute, true);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination_str = destination.getText().toString();
                String endKM_str = endKM.getText().toString();

                Calendar cal;
                if (startHour == 0 && startMinute == 0) {
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

        if (this.carTrips.size() > 0) {
            int last = this.carTrips.size() - 1;
            CarTrip lastTrip = this.carTrips.get(last);

            if (!lastTrip.getCompleted()) {
                this.origin.setText(lastTrip.getStartLocation());
                this.startKM.setText(String.valueOf(lastTrip.getStartKM()));
                Date s = lastTrip.getStartDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(s);
                String display_Time = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cal.get(Calendar.MINUTE));
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
                Log.i("CHROMA", "Clicked the " + position + "-th item.");

                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(R.string.DeleteTitle);
                builder.setMessage(R.string.DeleteItemQuestion);

                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        carTripAdapter.remove(carTrips.get(pos));
                        carTripAdapter.notifyDataSetChanged();
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
}