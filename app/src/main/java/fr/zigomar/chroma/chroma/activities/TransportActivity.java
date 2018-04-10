package fr.zigomar.chroma.chroma.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.adapters.TripAdapter;
import fr.zigomar.chroma.chroma.fragments.TransportInputFragment;
import fr.zigomar.chroma.chroma.model.Step;
import fr.zigomar.chroma.chroma.model.Trip;
import fr.zigomar.chroma.chroma.R;

public class TransportActivity extends InputActivity {

    // the list holding the data
    protected ArrayList<Trip> trips;
    // the adapter managing the view of the data
    protected TripAdapter tripAdapter;

    protected FloatingActionButton fab;
    protected FloatingActionButton fab_revert;
    protected FloatingActionButton fab_commute;

    public int getNumberOfTrips() {
        return trips.size();
    }

    public String getLastStation() {
        if (trips.size() > 0) {
            Trip lastTrip = trips.get(trips.size() - 1);
            return lastTrip.getSteps().get(lastTrip.getNumberOfSteps() - 1).getStop();
        } else {
            return "";
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // init of the data : fetch trips data in the currentDate file if it exist
        this.trips = getTrips();
        updateSummary();

        // finishing up the setting of the adapter for the list view of the retrieved (and
        // new) trips
        ListView tripsListView = findViewById(R.id.ListViewTransport);

        this.fab = findViewById(R.id.fab);
        this.fab_revert = findViewById(R.id.fab_revert);
        this.fab_commute = findViewById(R.id.fab_commute);

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

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                findViewById(R.id.TransportActivityInputContainer).setVisibility(View.VISIBLE);

                TransportInputFragment inputFragment = new TransportInputFragment();
                fragmentTransaction.add(R.id.TransportActivityInputContainer, inputFragment);
                fragmentTransaction.commit();

                hideActionButtons();
            }
        });

        this.fab_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    //resetViews(lastTrip.getSteps().get(0).getStop());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.TripCannotRevertEmptyTrip, Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.fab_commute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String commuteString = sharedPref.getString(SettingsActivity.KEY_PREF_COMMUTE, "");
                String tripString = commuteString.split("€")[0];

                double tripCost = Double.parseDouble(commuteString.split("€")[1]);

                tripAdapter.add(new Trip(tripString, tripCost));
                updateSummary();
            }
        });

    }

    /*
    private void resetViews(String s) {
        stepsList.removeAllViews();
        @SuppressLint("InflateParams") View child = getLayoutInflater().inflate(R.layout.unit_input_tripstep, null);
        stepsList.addView(child);

        AutoCompleteTextView station = child.findViewById(R.id.Station);
        station.setText(s);

        priceField.setText("");
    }
    */

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    public void addTrip(Trip trip) {
        this.tripAdapter.add(trip);
        updateSummary();
        Log.i("CHROMA", "Currently " + this.trips.size() + " trips.");
    }

    public void showActionButtons() {
        this.fab.setVisibility(View.VISIBLE);
        this.fab_revert.setVisibility(View.VISIBLE);
    }

    public void hideActionButtons() {
        this.fab.setVisibility(View.INVISIBLE);
        this.fab_revert.setVisibility(View.INVISIBLE);
        this.fab_commute.setVisibility(View.INVISIBLE);
    }
}