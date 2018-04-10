package fr.zigomar.chroma.chroma.listeners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import fr.zigomar.chroma.chroma.R;

public class AddTransportStepClickListener implements View.OnClickListener {

    private Context context;
    private LinearLayout stepsList;

    public AddTransportStepClickListener(Context ctx, LinearLayout stepLists) {
        this.context = ctx;
        this.stepsList = stepLists;
    }

    @Override
    public void onClick(View v) {

        LayoutInflater li = LayoutInflater.from(this.context);
        @SuppressLint("InflateParams") View child = li.inflate(R.layout.unit_input_tripstep, null);
        this.stepsList.addView(child);

        ArrayAdapter<CharSequence> stationAdapter = ArrayAdapter.createFromResource(this.context,
                R.array.stations, R.layout.dropdown);
        AutoCompleteTextView textView = child.findViewById(R.id.Station);
        textView.setAdapter(stationAdapter);

        child.findViewById(R.id.AddStep).setOnClickListener(this);
        child.findViewById(R.id.RemoveStep).setOnClickListener(
                new RemoveTransportStepClickListener(this.stepsList));
        child.findViewById(R.id.RemoveStep).setVisibility(View.VISIBLE);

        LinearLayout stepLayout = (LinearLayout) v.getParent();
        View removeButton = stepLayout.findViewById(R.id.RemoveStep);

        v.setVisibility(View.INVISIBLE);
        removeButton.setVisibility(View.INVISIBLE);
    }
}