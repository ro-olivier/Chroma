package fr.zigomar.chroma.chroma.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.Adapters.TripAdapter;
import fr.zigomar.chroma.chroma.Model.Step;
import fr.zigomar.chroma.chroma.Model.Trip;
import fr.zigomar.chroma.chroma.R;

public class TransportActivity extends InputActivity {

    // the list holding the data
    ArrayList<Trip> trips;
    // the adapter managing the view of the data
    private TripAdapter tripAdapter;

    private LinearLayout stepsList;
    private EditText priceField;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout, yay, we can see stuff on the screen!
        setContentView(R.layout.activity_transport);

        // call inherited initiating method
        init();

        this.priceField = (EditText) findViewById(R.id.TripPrice);

        Button addStepButton = (Button) findViewById(R.id.AddStep);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsList = (LinearLayout) findViewById(R.id.StepLinearLayout);
                View child = getLayoutInflater().inflate(R.layout.unit_input_tripstep, null);
                stepsList.addView(child);

            }
        });

        Button addTripButton = (Button) findViewById(R.id.AddButton);
        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cost_str = priceField.getText().toString();
                if (cost_str.length() > 0) {
                    try {
                        double cost = Double.parseDouble(cost_str);
                        int childCount = stepsList.getChildCount();
                        if ( childCount > 1) {
                            ArrayList<Step> ar = new ArrayList<>();

                            for (int i = 0; i < childCount - 1; i++) {
                                View child = stepsList.getChildAt(i);
                                EditText station = (EditText) child.findViewById(R.id.Station);
                                EditText line = (EditText) child.findViewById(R.id.Line);
                                ar.add(new Step(station.getText().toString(), line.getText().toString()));
                            }

                            View child = stepsList.getChildAt(childCount - 1);
                            EditText station = (EditText) child.findViewById(R.id.Station);
                            ar.add(new Step(station.getText().toString()));

                            tripAdapter.add(new Trip(ar, cost));
                            Log.i("CHROMA", "Currently " + trips.size() + " trips.");
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

        // init of the data : fetch trips data in the currentDate file if it exist
        this.trips = getTrips();

        // finishing up the setting of the adapter for the list view of the retrieved (and
        // new) trips
        ListView tripsListView = (ListView) findViewById(R.id.ListViewTransport);

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

        Button revertTripButton = (Button) findViewById(R.id.RevertTransport);
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
                } else {
                    Toast.makeText(getApplicationContext(), R.string.TripCannotRevertEmptyTrip, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
