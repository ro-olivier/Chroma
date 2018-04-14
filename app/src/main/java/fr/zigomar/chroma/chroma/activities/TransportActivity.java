package fr.zigomar.chroma.chroma.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import fr.zigomar.chroma.chroma.adapters.speeddialmenuadapters.TransportSpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.FloatingActionButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.adapters.modeladapters.TripAdapter;
import fr.zigomar.chroma.chroma.model.Trip;
import fr.zigomar.chroma.chroma.R;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class TransportActivity extends InputActivity {

    // the list holding the data
    private ArrayList<Trip> trips;
    // the adapter managing the view of the data
    private TripAdapter tripAdapter;

    private FloatingActionButton fab;

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

    public Trip getLastTrip() {
        return this.trips.get(this.trips.size() - 1);
    }

    public TripAdapter getTripAdapter() {
        return tripAdapter;
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

        SpeedDialMenuItem fab_add = new SpeedDialMenuItem(getApplicationContext());
        fab_add.setIcon(R.drawable.plus_sign_16dp);
        fab_add.setLabel(R.string.addTrip);

        SpeedDialMenuItem fab_revert = new SpeedDialMenuItem(getApplicationContext());
        fab_revert.setIcon(R.drawable.arrow_revert_16dp);
        fab_revert.setLabel(R.string.reverseTrip);

        SpeedDialMenuItem fab_commute = new SpeedDialMenuItem(getApplicationContext());
        fab_commute.setIcon(R.drawable.infinity_16dp);
        fab_commute.setLabel(R.string.commuteTrip);

        ArrayList<SpeedDialMenuItem> menu_items = new ArrayList<>();
        menu_items.add(fab_add);
        menu_items.add(fab_revert);
        menu_items.add(fab_commute);

        this.fab = findViewById(R.id.fab);
        this.fab.setContentCoverColour(0x88ffffff);
        this.fab.setElevation(tripsListView.getElevation() + 1);
        this.fab.setSpeedDialMenuAdapter(new TransportSpeedDialMenuAdapter(this, menu_items));

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
    }

    public void hideFAB() {
        this.fab.hide(true);
    }

    public void showFAB() {
        this.fab.show();
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

    public void updateSummary() {
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
}