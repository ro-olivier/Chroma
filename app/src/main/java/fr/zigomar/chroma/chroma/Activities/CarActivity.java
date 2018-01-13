package fr.zigomar.chroma.chroma.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    ArrayList<CarTrip> carTrips;
    // the adapter managing the view of the data
    private CarTripAdapter carTripAdapter;
    private EditText origin;
    private EditText startKM;
    private TimePicker startTime;
    private EditText destination;
    private EditText endKM;
    private TimePicker endTime;
    private Button startButton;
    private Button endButton;
    private TextView tripNotification;

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
        this.origin = (EditText) findViewById(R.id.carTrip_origin);
        this.startKM = (EditText) findViewById(R.id.carTrip_KMbegin);
        this.startTime = (TimePicker) findViewById(R.id.carTrip_TimeBegin);
        this.startButton = (Button) findViewById(R.id.carTrip_SubmitStart);

        this.destination = (EditText) findViewById(R.id.carTrip_destination);
        this.endKM = (EditText) findViewById(R.id.carTrip_KMEnd);
        this.endTime = (TimePicker) findViewById(R.id.carTrip_TimeEnd);
        this.endButton = (Button) findViewById(R.id.carTrip_SubmitEnd);

        this.tripNotification = (TextView) findViewById(R.id.TripNotification);

        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int currentMinute = rightNow.get(Calendar.MINUTE);
        if (Build.VERSION.SDK_INT < 23) {
            this.startTime.setCurrentHour(currentHour);
            this.startTime.setCurrentMinute(currentMinute);
        } else {
            this.startTime.setHour(currentHour);
            this.startTime.setMinute(currentMinute);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin_str = origin.getText().toString();
                String startKM_str = startKM.getText().toString();
                int startTime_hours = 0;
                int startTime_minutes = 0;
                if (Build.VERSION.SDK_INT < 23) {
                    startTime_minutes = startTime.getCurrentMinute();
                    startTime_hours = startTime.getCurrentHour();
                } else {
                    startTime_minutes = startTime.getMinute();
                    startTime_hours = startTime.getHour();
                }

                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Calendar.YEAR);
                cal.set(Calendar.MONTH, Calendar.MONTH);
                cal.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH);
                cal.set(Calendar.HOUR_OF_DAY, startTime_hours);
                cal.set(Calendar.MINUTE, startTime_minutes);
                cal.set(Calendar.SECOND, Calendar.SECOND);
                cal.set(Calendar.MILLISECOND, Calendar.MILLISECOND);


                if (origin_str.length() > 0 && startKM_str.length() > 0) {
                    try {
                        double startKM = Double.parseDouble(startKM_str);


                        if (carTrips.size() == 0) {
                            // no car trip registered, we can safely create a new one
                            tripNotification.setText(R.string.OngoingTrip);
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
                        Log.i("CHROMA", "Currently " + carTrips.size() + " car trips.");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataThreeValuesRequired, Toast.LENGTH_SHORT).show();
                }

            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination_str = destination.getText().toString();
                String endKM_str = endKM.getText().toString();
                int endTime_hours = 0;
                int endTime_minutes = 0;
                if (Build.VERSION.SDK_INT < 23) {
                    endTime_minutes = endTime.getCurrentMinute();
                    endTime_hours = endTime.getCurrentHour();
                } else {
                    endTime_minutes = endTime.getMinute();
                    endTime_hours = endTime.getHour();
                }

                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Calendar.YEAR);
                cal.set(Calendar.MONTH, Calendar.MONTH);
                cal.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH);
                cal.set(Calendar.HOUR_OF_DAY, endTime_hours);
                cal.set(Calendar.MINUTE, endTime_minutes);
                cal.set(Calendar.SECOND, Calendar.SECOND);
                cal.set(Calendar.MILLISECOND, Calendar.MILLISECOND);


                if (destination_str.length() > 0 && endKM_str.length() > 0) {
                    try {
                        double endKM = Double.parseDouble(endKM_str);

                        tripNotification.setText("");
                        carTrips.get(carTrips.size() - 1).endTrip(destination_str, cal.getTime(), endKM);
                        carTripAdapter.notifyDataSetChanged();

                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.MissingDataThreeValuesRequired, Toast.LENGTH_SHORT).show();
                }

            }
        });

        this.carTrips = getCarTrips();

        // finishing up the setting of the adapter for the list view of the retrieve (and
        // new) drinks
        ListView carTripsListView = (ListView) findViewById(R.id.ListViewCarTrips);

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