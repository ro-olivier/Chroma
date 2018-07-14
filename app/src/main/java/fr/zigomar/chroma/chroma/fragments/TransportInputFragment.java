package fr.zigomar.chroma.chroma.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.TransportActivity;
import fr.zigomar.chroma.chroma.listeners.AddTransportStepClickListener;
import fr.zigomar.chroma.chroma.listeners.RemoveTransportStepClickListener;
import fr.zigomar.chroma.chroma.model.Step;
import fr.zigomar.chroma.chroma.model.Trip;

public class TransportInputFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private TransportActivity callingActivity;

    private LinearLayout stepsList;
    private EditText priceField;
    private int position;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.callingActivity = (TransportActivity) getActivity();
        LayoutInflater inflater = LayoutInflater.from(this.callingActivity);
        Bundle data = this.getArguments();

        View v = inflater.inflate(R.layout.input_transport, (ViewGroup) getView(), false);
        this.priceField = v.findViewById(R.id.TripPrice);
        this.priceField.setText(data.getString("cost"));

        this.stepsList = v.findViewById(R.id.StepLinearLayout);
        AutoCompleteTextView station = this.stepsList.findViewById(R.id.Station);
        EditText line = this.stepsList.findViewById(R.id.Line);

        station.setBackgroundColor(this.callingActivity.getResources().getColor(R.color.colorDialogFields));
        line.setBackgroundColor(this.callingActivity.getResources().getColor(R.color.colorDialogFields));

        ArrayList<String> stations = data.getStringArrayList("stations");
        ArrayList<String> lines = data.getStringArrayList("lines");

        if ((stations != null ? stations.size() : 0) == 1) {
            v.findViewById(R.id.AddStep).setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < (stations != null ? stations.size() : 0); i++) {
            station.setText(stations.get(i));
            if (lines != null && lines.size() > 0) {
                line.setText(lines.get(i));
            }

            if (i < stations.size() - 1 ) {
                @SuppressLint("InflateParams") View child = inflater.inflate(R.layout.unit_input_tripstep, null);
                this.stepsList.addView(child);

                station = child.findViewById(R.id.Station);
                line = child.findViewById(R.id.Line);

                station.setBackgroundColor(this.callingActivity.getResources().getColor(R.color.colorDialogFields));
                line.setBackgroundColor(this.callingActivity.getResources().getColor(R.color.colorDialogFields));

                if (i == stations.size() - 2) {
                    ImageView addStepButton = child.findViewById(R.id.AddStep);
                    addStepButton.setOnClickListener(new AddTransportStepClickListener(this.callingActivity, this.stepsList));
                    addStepButton.setVisibility(View.VISIBLE);
                    ImageView removeStepButton = child.findViewById(R.id.RemoveStep);
                    removeStepButton.setOnClickListener(new RemoveTransportStepClickListener(this.callingActivity, this.stepsList));
                    removeStepButton.setVisibility(View.VISIBLE);
                }

            }
        }

        /*
        if (callingActivity.getNumberOfTrips() > 0) {
            Log.i("CHROMA", "Trips is not empty, let's prefill the station");

            station.setText(callingActivity.getLastStation());
            line.requestFocus();
        } else {
            station.requestFocus();
        }
        */

        ArrayAdapter<CharSequence> stationAdapter = ArrayAdapter.createFromResource(this.callingActivity,
                R.array.stations, R.layout.dropdown);
        AutoCompleteTextView textView = v.findViewById(R.id.Station);
        textView.setAdapter(stationAdapter);

        this.position = data.getInt("position", -1);
        if (this.position == -1) {
            ImageView addStepButton = v.findViewById(R.id.AddStep);
            addStepButton.setVisibility(View.VISIBLE);
            addStepButton.setOnClickListener(new AddTransportStepClickListener(this.callingActivity, this.stepsList));
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(R.string.AddTrip)
                .setPositiveButton("OK", this)
                .setView(v)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        String cost_str = this.priceField.getText().toString();
        double cost = 0;

        ArrayList<String> stations = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();

        for (int i = 0; i < this.stepsList.getChildCount(); i++) {
            View child = this.stepsList.getChildAt(i);
            AutoCompleteTextView station = child.findViewById(R.id.Station);
            EditText line = child.findViewById(R.id.Line);
            stations.add(station.getText().toString());
            lines.add(line.getText().toString());
        }

        Log.i("CHROMA", "stations = " + stations.toString());
        Log.i("CHROMA", "lines = " + lines.toString());

        try {
            cost = Double.parseDouble(cost_str);
        } catch (NumberFormatException e){
            Toast.makeText(this.callingActivity.getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
            rebuildDialog(cost_str, stations, lines);
        }

        ArrayList<Step> steps = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            try {
                Log.i("CHROMA", "s = " + stations.get(i));
                Log.i("CHROMA", "l = " + lines.get(i));
                if (i == stations.size() - 1) {
                    steps.add(new Step(stations.get(i), lines.get(i), true));
                } else {
                    steps.add(new Step(stations.get(i), lines.get(i), false));
                }
            } catch (Step.EmptyStationException e) {
                // An exception is thrown if a station is empty
                Toast.makeText(this.callingActivity.getApplicationContext(), R.string.TripStationMandatory, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                rebuildDialog(cost_str, stations, lines);
            } catch (Step.EmptyLineException e) {
                // An exception is thrown if a line is empty and the corresponding step is not end of trip
                Toast.makeText(this.callingActivity.getApplicationContext(), R.string.TripLineMandatory, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                rebuildDialog(cost_str, stations, lines);
            }
        }

        try {
            if (position < 0) {
                this.callingActivity.addTrip(new Trip(steps, cost));
            } else {
                this.callingActivity.updateTrip(position, new Trip(steps, cost));
            }
        } catch (Trip.InvalidTripNoEndException e) {
            Toast.makeText(this.callingActivity.getApplicationContext(), R.string.TripInvalid, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.i("CHROMA", "InvalidTripNoEndException, rebuilding");
            rebuildDialog(cost_str, stations, lines);
        } catch (Trip.InvalidTripOnlyOneStep e) {
            Toast.makeText(this.callingActivity.getApplicationContext(), R.string.TripMinimumTwoStepsRequired, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            rebuildDialog(cost_str, stations, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rebuildDialog(String cost, ArrayList<String> stations, ArrayList<String> lines) {
        Bundle data = new Bundle();
        data.putString("cost", cost);
        data.putStringArrayList("stations", stations);
        data.putStringArrayList("lines", lines);
        data.putInt("position", position);

        this.dismiss();

        TransportInputFragment inputFragment = new TransportInputFragment();
        inputFragment.setArguments(data);
        inputFragment.show(this.callingActivity.getFragmentManager(), "TransportInputDialogFragment");
    }
}
