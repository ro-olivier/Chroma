package fr.zigomar.chroma.chroma.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.TransportActivity;
import fr.zigomar.chroma.chroma.listeners.AddTransportStepClickListener;
import fr.zigomar.chroma.chroma.model.Step;
import fr.zigomar.chroma.chroma.model.Trip;

public class TransportInputFragment extends Fragment {

    private TransportActivity callingActivity;

    private LinearLayout stepsList;
    private EditText priceField;
    private InputMethodManager inputMethodManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.callingActivity = (TransportActivity) getActivity();
        this.inputMethodManager = (InputMethodManager) this.callingActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_transport, container, false);

        this.priceField = v.findViewById(R.id.TripPrice);
        this.stepsList = v.findViewById(R.id.StepLinearLayout);
        AutoCompleteTextView station = this.stepsList.findViewById(R.id.Station);

        if (callingActivity.getNumberOfTrips() > 0) {
            Log.i("CHROMA", "Trips is not empty, let's prefill the station");

            station.setText(callingActivity.getLastStation());

            EditText line = this.stepsList.findViewById(R.id.Line);
            line.requestFocus();
            if (this.inputMethodManager != null) {
                this.inputMethodManager.toggleSoftInput(0,0);
            }
        } else {
            station.requestFocus();
            if (this.inputMethodManager != null) {
                this.inputMethodManager.toggleSoftInput(0,0);
            }
        }

        ArrayAdapter<CharSequence> stationAdapter = ArrayAdapter.createFromResource(callingActivity,
                R.array.stations, R.layout.dropdown);
        AutoCompleteTextView textView = v.findViewById(R.id.Station);
        textView.setAdapter(stationAdapter);

        ImageButton addStepButton = v.findViewById(R.id.AddStep);
        addStepButton.setOnClickListener(new AddTransportStepClickListener(getContext(), this.stepsList));

        Button addTripButton = v.findViewById(R.id.AddButton);
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

                            callingActivity.addTrip(new Trip(ar, cost));
                            closeFragment();

                        } else {
                            Toast.makeText(callingActivity.getApplicationContext(), R.string.TripMinimumTwoStepsRequired, Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(callingActivity.getApplicationContext(), R.string.UnableToParse, Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException e) {
                        // NullPointer Exception can be thrown by the getChildCount method if no child has been added
                        // which happens if the "submit" button if clicked with data only for one step of the trip
                        Toast.makeText(callingActivity.getApplicationContext(), R.string.TripMinimumTwoStepsRequired, Toast.LENGTH_SHORT).show();
                    } catch (Step.EmptyStationException e){
                        // An exception is thrown if the name of the station is empty
                        Toast.makeText(callingActivity.getApplicationContext(), R.string.TripStationMandatory, Toast.LENGTH_SHORT).show();
                    } catch (Step.EmptyLineException e) {
                        // An exception is thrown if the name of the line is empty (except for the last step of the trip)
                        Toast.makeText(callingActivity.getApplicationContext(), R.string.TripLineMandatory, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(callingActivity.getApplicationContext(), R.string.TripCostMandatory, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void closeFragment() {
        if (this.inputMethodManager != null) {
            this.inputMethodManager.toggleSoftInput(0,0);
        }

        this.callingActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        this.callingActivity.findViewById(R.id.TransportActivityInputContainer).setVisibility(View.GONE);
        this.callingActivity.showFAB();
    }
}
