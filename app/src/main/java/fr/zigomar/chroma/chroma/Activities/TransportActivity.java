package fr.zigomar.chroma.chroma.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.Adapters.TripAdapter;
import fr.zigomar.chroma.chroma.Model.Step;
import fr.zigomar.chroma.chroma.Model.Trip;
import fr.zigomar.chroma.chroma.R;

public class TransportActivity extends InputActivity {

    // the list holding the data
    private ArrayList<Trip> trips;
    // the adapter managing the view of the data
    private TripAdapter tripAdapter;

    private LinearLayout stepsList;
    private EditText priceField;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        this.priceField = findViewById(R.id.TripPrice);

        // init of the data : fetch trips data in the currentDate file if it exist
        this.trips = getTrips();
        updateSummary();

        if (trips.size() > 0) {
            Log.i("CHROMA", "Trips is not empty, let's prefill the station");
            stepsList = findViewById(R.id.StepLinearLayout);
            View child = stepsList.findViewById(R.id.Station);
            AutoCompleteTextView station = child.findViewById(R.id.Station);
            Trip lastTrip = trips.get(trips.size() - 1);
            station.setText(lastTrip.getSteps().get(lastTrip.getNumberOfSteps() - 1).getStop());
        }

        ArrayAdapter<CharSequence> stationAdapter = ArrayAdapter.createFromResource(TransportActivity.this,
                R.array.stations, R.layout.dropdown);
        AutoCompleteTextView textView = findViewById(R.id.Station);
        textView.setAdapter(stationAdapter);

        Button addStepButton = findViewById(R.id.AddStep);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsList = findViewById(R.id.StepLinearLayout);
                @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.unit_input_tripstep, null);
                stepsList.addView(child);

                ArrayAdapter<CharSequence> stationAdapter = ArrayAdapter.createFromResource(TransportActivity.this,
                        R.array.stations, R.layout.dropdown);
                AutoCompleteTextView textView = child.findViewById(R.id.Station);
                textView.setAdapter(stationAdapter);
            }
        });

        Button addTripButton = findViewById(R.id.AddButton);
        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cost_str = priceField.getText().toString();
                if (cost_str.length() > 0) {
                    try {
                        double cost = Double.parseDouble(cost_str);
                        int childCount = stepsList.getChildCount();
                        if (childCount > 1) {
                            ArrayList<Step> ar = new ArrayList<>();

                            for (int i = 0; i < childCount - 1; i++) {
                                View child = stepsList.getChildAt(i);
                                AutoCompleteTextView station = child.findViewById(R.id.Station);
                                EditText line = child.findViewById(R.id.Line);
                                ar.add(new Step(station.getText().toString(), line.getText().toString()));
                            }

                            View child = stepsList.getChildAt(childCount - 1);
                            AutoCompleteTextView station = child.findViewById(R.id.Station);
                            ar.add(new Step(station.getText().toString()));

                            tripAdapter.add(new Trip(ar, cost));
                            updateSummary();
                            Log.i("CHROMA", "Currently " + trips.size() + " trips.");
                            resetViews(station.getText().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.TripMinimumTwoStepsRequired, Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException e) {
                        // NullPointer Exception can be thrown by the getChildCount method if no child has been added
                        // which happens if the "submit" button if clicked with data only for one step of the trip
                        Toast.makeText(getApplicationContext(), R.string.TripMinimumTwoStepsRequired, Toast.LENGTH_SHORT).show();
                    } catch (Step.EmptyStationException e){
                        // An exception is thrown if the name of the station is empty
                        Toast.makeText(getApplicationContext(), R.string.TripStationMandatory, Toast.LENGTH_SHORT).show();
                    } catch (Step.EmptyLineException e) {
                        // An exception is thrown if the name of the line is empty (except for the last step of the trip)
                        Toast.makeText(getApplicationContext(), R.string.TripLineMandatory, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.TripCostMandatory, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // finishing up the setting of the adapter for the list view of the retrieved (and
        // new) trips
        ListView tripsListView = findViewById(R.id.ListViewTransport);

        this.tripAdapter = new TripAdapter(TransportActivity.this, this.trips);
        tripsListView.setAdapter(this.tripAdapter);

        tripsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
                        tripAdapter.remove(trips.get(pos));
                        tripAdapter.notifyDataSetChanged();
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

        Button revertTripButton = findViewById(R.id.RevertTransport);
        revertTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trips.size() > 0) {
                    Trip lastTrip = trips.get(trips.size() - 1);

                    ArrayList<Step> backward_steps = new ArrayList<>();

                    try {
                        for (int index = lastTrip.getNumberOfSteps() - 1; index > 0; index--) {

                            backward_steps.add(new Step(lastTrip.getSteps().get(index).getStop(),
                                    lastTrip.getSteps().get(index - 1).getLine()));
                        }

                        backward_steps.add(new Step(lastTrip.getSteps().get(0).getStop()));
                    } catch (Step.EmptyStationException | Step.EmptyLineException e) {
                        e.printStackTrace();
                    }

                    tripAdapter.add(new Trip(backward_steps, lastTrip.getCost()));
                    updateSummary();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.TripCannotRevertEmptyTrip, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void resetViews(String s) {
        stepsList.removeAllViews();
        @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.unit_input_tripstep, null);
        stepsList.addView(child);

        AutoCompleteTextView station = child.findViewById(R.id.Station);
        station.setText(s);

        priceField.setText("");
    }

    private void updateSummary() {
        LinearLayout data_summary = findViewById(R.id.data_summary);
        data_summary.setVisibility(View.VISIBLE);

        TextView field0_data = findViewById(R.id.data_summary_field0);
        TextView field1_data = findViewById(R.id.data_summary_field1);
        TextView field2_data = findViewById(R.id.data_summary_field2);
        TextView field1_text = findViewById(R.id.data_summary_text1);
        TextView field2_text = findViewById(R.id.data_summary_text2);

        if (this.trips.size() > 0) {

            field0_data.setText(R.string.Summary);

            field1_data.setVisibility(View.INVISIBLE);
            field1_text.setVisibility(View.INVISIBLE);

            double amount_total = 0;
            for (Trip d : this.trips) {
                amount_total += d.getCost();
            }
            DecimalFormat df = new DecimalFormat("0.00");
            field2_data.setText(df.format(amount_total));

            field2_text.setText(R.string.Currency);

        } else {
            data_summary.setVisibility(View.INVISIBLE);
        }
    }

    private ArrayList<Trip> getTrips(){
        // getting the data is handled by the DataHandler
        return this.dh.getTripsList();
    }

    @Override
    protected void saveData() {
        // simply pass the data to the DataHandler with the dedicated method
        Log.i("CHROMA", "Updating the data object with current trips");
        this.dh.saveTransportData(this.trips);
    }

}