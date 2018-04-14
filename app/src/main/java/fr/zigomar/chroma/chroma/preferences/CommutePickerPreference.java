package fr.zigomar.chroma.chroma.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.listeners.AddTransportStepClickListener;
import fr.zigomar.chroma.chroma.listeners.RemoveTransportStepClickListener;
import fr.zigomar.chroma.chroma.model.Step;
import fr.zigomar.chroma.chroma.model.Trip;

public class CommutePickerPreference extends DialogPreference {

    private static final String DEFAULT_VALUE = "";
    private String value;

    private LinearLayout stepsList;
    private EditText priceField;

    private Context ctx;

    public CommutePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.input_transport);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        this.ctx = context;

        setDialogIcon(null);

    }

    @Override
    protected void onBindDialogView(View view) {

        Button submitButton = view.findViewById(R.id.AddButton);
        submitButton.setVisibility(View.GONE);

        view.setBackgroundColor(Color.WHITE);

        this.stepsList = view.findViewById(R.id.StepLinearLayout);
        this.priceField = view.findViewById(R.id.TripPrice);

        AutoCompleteTextView initial_station = view.findViewById(R.id.Station);
        EditText initial_line = view.findViewById(R.id.Line);

        ArrayAdapter<CharSequence> stationAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.stations, R.layout.dropdown);
        initial_station.setAdapter(stationAdapter);

        initial_station.setBackgroundColor(getContext().getResources().getColor(R.color.colorDialogFields));
        initial_line.setBackgroundColor(getContext().getResources().getColor(R.color.colorDialogFields));
        this.priceField.setBackgroundColor(getContext().getResources().getColor(R.color.colorDialogFields));

        ImageView addStepButton = view.findViewById(R.id.AddStep);
        addStepButton.setVisibility(View.INVISIBLE);
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(getContext());
                @SuppressLint("InflateParams") View child = li.inflate(R.layout.unit_input_tripstep, null);
                stepsList.addView(child);

                AutoCompleteTextView station = child.findViewById(R.id.Station);
                EditText line = child.findViewById(R.id.Line);

                station.setBackgroundColor(getContext().getResources().getColor(R.color.colorDialogFields));
                line.setBackgroundColor(getContext().getResources().getColor(R.color.colorDialogFields));

                ArrayAdapter<CharSequence> stationAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.stations, R.layout.dropdown);
                station.setAdapter(stationAdapter);
            }
        });

        String commuteString = this.value;
        Log.i("CHROMA", commuteString);
        this.priceField.setText(this.value.split("€")[1]);

        String tripString = this.value.split("€")[0];
        int i = 0;
        for (String s : tripString.split(" # ")) {

            Log.i("CHROMA", s);

            int length = s.split("\\(").length;

            AutoCompleteTextView station = this.stepsList.getChildAt(i).findViewById(R.id.Station);
            EditText line = this.stepsList.getChildAt(i).findViewById(R.id.Line);

            station.setBackgroundColor(getContext().getResources().getColor(R.color.colorDialogFields));
            line.setBackgroundColor(getContext().getResources().getColor(R.color.colorDialogFields));

            if (length > 1) {
                String station_str = s.split("\\(")[length - 2];
                station.setText(station_str.substring(0, station_str.length() - 1));

                String line_str = s.split("\\(")[length - 1];
                line_str = line_str.substring(0, line_str.length() - 1);
                line.setText(line_str);

                LayoutInflater li = LayoutInflater.from(getContext());
                @SuppressLint("InflateParams") View child = li.inflate(R.layout.unit_input_tripstep, null);
                stepsList.addView(child);
                child.findViewById(R.id.AddStep).setVisibility(View.INVISIBLE);
                child.findViewById(R.id.AddStep).setOnClickListener(
                        new AddTransportStepClickListener(this.getContext(), this.stepsList));
                child.findViewById(R.id.RemoveStep).setVisibility(View.INVISIBLE);
                child.findViewById(R.id.RemoveStep).setOnClickListener(
                        new RemoveTransportStepClickListener(this.stepsList));

                i++;
            } else {
                String station_str = s.split("\\(")[length - 1];
                station.setText(station_str);

                ImageView addButton = stepsList.getChildAt(stepsList.getChildCount() - 1).findViewById(R.id.AddStep);
                addButton.setVisibility(View.VISIBLE);
                addButton.setOnClickListener(new AddTransportStepClickListener(this.getContext(), this.stepsList));

                ImageView removeButton = stepsList.getChildAt(stepsList.getChildCount() - 1).findViewById(R.id.RemoveStep);
                removeButton.setVisibility(View.VISIBLE);
                removeButton.setOnClickListener(new RemoveTransportStepClickListener(this.stepsList));
            }

            line.requestFocus();
        }

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean result) {

        Log.i("CHROMA", "OnDialogClosed called with result = " + result);

        if (result) {
            Trip commuteTrip = null;

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

                            commuteTrip = new Trip(ar, cost);
                        }
                    } catch (Step.EmptyStationException e) {
                        // An exception is thrown if the name of the station is empty
                        Toast.makeText(this.ctx, R.string.TripStationMandatory, Toast.LENGTH_SHORT).show();
                    } catch (Step.EmptyLineException e) {
                        // An exception is thrown if the name of the line is empty (except for the last step of the trip)
                        Toast.makeText(this.ctx, R.string.TripLineMandatory, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this.ctx, R.string.TripCostMandatory, Toast.LENGTH_SHORT).show();
                }

            if (commuteTrip != null) {
                this.value = commuteTrip.tripString() + "€" + this.priceField.getText().toString();
            }

            persistString(this.value);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            this.value = this.getPersistedString(DEFAULT_VALUE);
        } else {
            this.value = (String) defaultValue;
            persistString(this.value);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
}
