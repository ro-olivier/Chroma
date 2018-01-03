package fr.zigomar.chroma.chroma.Activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import fr.zigomar.chroma.chroma.R;



public class TransportActivity extends InputActivity {

    private Button addButton;
    private ListView drinksListView;
    private LinearLayout stepsList;
    private Button addStepButton;
    private Button addTripButton;
    private Button revertTripButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);

        // setting the view's layout, yay, we can see stuff on the screen!
        setContentView(R.layout.activity_transport);

        // call inherited initiating method
        init();

        this.addStepButton = (Button) findViewById(R.id.AddStep);
        this.addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsList = (LinearLayout) findViewById(R.id.StepLinearLayout);
                View child = getLayoutInflater().inflate(R.layout.unit_tripstep, null);
                stepsList.addView(child);

            }
        });

        this.addTripButton = (Button) findViewById(R.id.AddButton);
        // TODO: add Trip Model object
        // TODO: add TripAdapter and corresponding logic

        this.revertTripButton = (Button) findViewById(R.id.RevertTransport);
        // TODO : add revert logic : take the last trip and revert it

    }
}
