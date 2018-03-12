package fr.zigomar.chroma.chroma.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import fr.zigomar.chroma.chroma.Activities.MainActivity;
import fr.zigomar.chroma.chroma.R;

public class ExportDateFragment extends DialogFragment {

    String[] valid_dates;

    public static ExportDateFragment newInstance(String[] dates) {
        ExportDateFragment edf = new ExportDateFragment();

        Bundle args = new Bundle();
        args.putStringArray("dates", dates);
        edf.setArguments(args);

        return edf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.valid_dates = getArguments().getStringArray("dates");

        View v = inflater.inflate(R.layout.export_date_selector, container, false);

        final NumberPicker startPicker = v.findViewById(R.id.startPicker);
        final NumberPicker endPicker= v.findViewById(R.id.endPicker);

        startPicker.setMinValue(0);
        endPicker.setMinValue(0);

        startPicker.setMaxValue(this.valid_dates.length - 1);
        endPicker.setMaxValue(this.valid_dates.length - 1);

        startPicker.setDisplayedValues(this.valid_dates);
        endPicker.setDisplayedValues(this.valid_dates);

        startPicker.setWrapSelectorWheel(false);
        endPicker.setWrapSelectorWheel(false);

        Button submit = v.findViewById(R.id.submitExportDates);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int begin = startPicker.getValue();
                int end = endPicker.getValue();

                if (begin > end) {
                    Toast.makeText(getActivity(), R.string.WrongExportDates, Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity callingActivity = (MainActivity) getActivity();
                    callingActivity.exportOnReceiveExportDates(valid_dates[begin], valid_dates[end]);
                    getDialog().dismiss();
                }


            }
        });


        return v;
    }
}