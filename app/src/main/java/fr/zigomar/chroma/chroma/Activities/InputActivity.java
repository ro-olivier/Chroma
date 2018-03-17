package fr.zigomar.chroma.chroma.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.zigomar.chroma.chroma.Model.DataHandler;
import fr.zigomar.chroma.chroma.R;

public abstract class InputActivity extends AppCompatActivity {

    protected static final String CURRENT_DATE = "com.example.chroma.current_date";

    protected DataHandler dh;

    protected final Date currentDate = new Date();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (this.getClass().getName()) {
            case "fr.zigomar.chroma.chroma.Activities.MoodActivity":
                setContentView(R.layout.activity_mood);
                break;

            case "fr.zigomar.chroma.chroma.Activities.MoneyActivity":
                setContentView(R.layout.activity_money);
                break;

            case "fr.zigomar.chroma.chroma.Activities.MoneyInActivity":
                setContentView(R.layout.activity_money_in);
                break;

            case "fr.zigomar.chroma.chroma.Activities.AlcoholActivity":
                setContentView(R.layout.activity_alcohol);
                break;

            case "fr.zigomar.chroma.chroma.Activities.CarActivity":
                setContentView(R.layout.activity_cartrip);
                break;

            case "fr.zigomar.chroma.chroma.Activities.NewBookActivity":
                setContentView(R.layout.activity_newbook);
                break;

            case "fr.zigomar.chroma.chroma.Activities.TransportActivity":
                setContentView(R.layout.activity_transport);
                break;

            case "fr.zigomar.chroma.chroma.Activities.MovieActivity":
                setContentView(R.layout.activity_movie);
                break;
        }

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // update the date view at the top of the layout
        this.currentDate.setTime(getIntent().getLongExtra(CURRENT_DATE, -1));
        updateDateView();

        // init the data handler if not already set in the child class
        if (this.dh == null) {
            this.dh = new DataHandler(this.getApplicationContext(), this.currentDate);
        }

        // hiding the buttons
        ImageButton backwardDateButton = findViewById(R.id.ButtonBackwardDate);
        backwardDateButton.setVisibility(View.INVISIBLE);
        ImageButton forwardDateButton = findViewById(R.id.ButtonForwardDate);
        forwardDateButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        // surcharge the onStop() method to include a call to the method updating the data and then
        // using the DataHandler to write it to file before closing
        super.onStop();
        Log.i("CHROMA","Starting activity closing... : " + this.getClass().getSimpleName());

        saveData();
        this.dh.writeDataToFile(getApplicationContext());
        Toast.makeText(getApplicationContext(), R.string.Saved, Toast.LENGTH_SHORT).show();
    }

        // simply fetch the data from the views and save it into the DataHandler with the dedicated method
        // each class inheriting from InputActivity has its own way of doing this and thus must override this method
        //Log.i("CHROMA", "Updating the data object with current values in the views");
    protected abstract void saveData();

    protected void updateDateView() {
        // simple method to update the date view at the top of the screen
        TextView dateView = findViewById(R.id.DateTextView);
        String formattedDate = (new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(this.currentDate));
        dateView.setText(formattedDate);

        TextView dayView = findViewById(R.id.DayTextView);
        String formattedDay = (new SimpleDateFormat("EEEE", Locale.ENGLISH).format(this.currentDate));
        dayView.setText(formattedDay);

        Log.i("CHROMA", "Updating date : " + formattedDate + " (" + formattedDay + ")");
    }
}
