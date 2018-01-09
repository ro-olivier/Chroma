package fr.zigomar.chroma.chroma.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.zigomar.chroma.chroma.Model.DataHandler;
import fr.zigomar.chroma.chroma.R;


public abstract class InputActivity extends AppCompatActivity {

    protected static final String CURRENT_DATE = "com.example.chroma.current_date";

    protected DataHandler dh;

    protected Date currentDate = new Date();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        // calling inherited class constructor
        super.onCreate(savedInstanceState);
    }

    protected void init() {
        // update the date view at the top of the layout
        this.currentDate.setTime(getIntent().getLongExtra(CURRENT_DATE, -1));
        updateDateView();

        // init the data handler
        this.dh = new DataHandler(this.getApplicationContext(), this.currentDate);

        ImageButton backwardDateButton = (ImageButton) findViewById(R.id.ButtonBackwardDate);
        backwardDateButton.setVisibility(View.INVISIBLE);
        ImageButton forwardDateButton = (ImageButton) findViewById(R.id.ButtonForwardDate);
        forwardDateButton.setVisibility(View.INVISIBLE);
    }

    protected void updateDateView() {
        // simple method to update the date view at the top of the screen
        TextView dateView = (TextView) findViewById(R.id.DateTextView);
        String formattedDate = (new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(this.currentDate));
        Log.i("CHROMA", "Updating date : " + formattedDate);
        dateView.setText(formattedDate);
    }
}
