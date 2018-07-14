package fr.zigomar.chroma.chroma.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import fr.zigomar.chroma.chroma.adapters.speeddialmenuadapters.TransportSpeedDialMenuAdapter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.zigomar.chroma.chroma.adapters.modeladapters.TripAdapter;
import fr.zigomar.chroma.chroma.fragments.TransportInputFragment;
import fr.zigomar.chroma.chroma.model.Step;
import fr.zigomar.chroma.chroma.model.Trip;
import fr.zigomar.chroma.chroma.R;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

public class TransportActivity extends InputListActivity {

    // the list holding the data
    private ArrayList<Trip> trips;
    // the adapter managing the view of the data
    private TripAdapter tripAdapter;

    private boolean inputEnabled = false;

    public int getNumberOfTrips() {
        return trips.size();
    }

    @Override
    public FragmentManager getFragmentManager() {
        return super.getFragmentManager();
    }

    public Trip getLastTrip() {
        return this.trips.get(this.trips.size() - 1);
    }

    @Override
    public TripAdapter getAdapter() {
        return this.tripAdapter;
    }

    @Override
    public ArrayList<Trip> getItems() {
        return this.trips;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // init of the data : fetch trips data in the currentDate file if it exist
        this.trips = getTrips();
        updateSummary();

        SpeedDialMenuItem fab_add = new SpeedDialMenuItem(getApplicationContext());
        fab_add.setIcon(R.drawable.plus_sign_16dp);
        fab_add.setLabel(R.string.addTrip);

        SpeedDialMenuItem fab_revert = new SpeedDialMenuItem(getApplicationContext());
        fab_revert.setIcon(R.drawable.arrow_revert_16dp);
        fab_revert.setLabel(R.string.reverseTrip);

        SpeedDialMenuItem fab_commute = new SpeedDialMenuItem(getApplicationContext());
        fab_commute.setIcon(R.drawable.infinity_16dp);
        fab_commute.setLabel(R.string.commuteTrip);

        this.getFABItems().add(fab_add);
        this.getFABItems().add(fab_revert);
        this.getFABItems().add(fab_commute);

        this.getFAB().setSpeedDialMenuAdapter(new TransportSpeedDialMenuAdapter(this, this.getFABItems()));

        this.tripAdapter = new TripAdapter(TransportActivity.this, this.trips);
        this.getListView().setAdapter(this.tripAdapter);

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

    @Override
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
        Log.i("CHROMA", "onOptionsItemSelected called, this.inputEnabled=" + this.inputEnabled);
        Log.i("CHROMA","item = " + item.getItemId());

        if (android.R.id.home == item.getItemId())
            if (this.inputEnabled) {
                //this.fragment.closeFragment();
                this.inputEnabled = false;
            } else {
                super.onOptionsItemSelected(item);
            }
        else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void addTrip(Trip trip) {
        this.tripAdapter.add(trip);
        updateSummary();
        Log.i("CHROMA", "Currently " + this.trips.size() + " trips.");
    }

    public void updateTrip(int position, Trip trip) {
        this.trips.set(position, trip);
        updateSummary();
        this.tripAdapter.notifyDataSetChanged();
    }

    @Override
    protected void buildInputFragmentForUpdate(int position) {
        TransportInputFragment inputFragment = new TransportInputFragment();
        Bundle data = new Bundle();
        ArrayList<String> stations = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();

        Trip t = this.trips.get(position);
        for (Step s : t.getSteps()) {
            stations.add(s.getStop());
            lines.add(s.getLine());
        }
        data.putStringArrayList("stations", stations);
        data.putStringArrayList("lines", lines);
        data.putString("cost", String.valueOf(t.getCost()));
        data.putInt("position", position);

        inputFragment.setArguments(data);
        inputFragment.show(this.getFragmentManager(), "TransportInputDialogFragment");
    }

}