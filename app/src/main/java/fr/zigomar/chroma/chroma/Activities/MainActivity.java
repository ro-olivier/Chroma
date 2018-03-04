package fr.zigomar.chroma.chroma.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.zigomar.chroma.chroma.R;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_DATE = "com.example.chroma.current_date";

    private Date currentDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton forwardButton = findViewById(R.id.ButtonForwardDate);
        forwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementDate();
                updateDateView();
            }
        });

        ImageButton backwardButton = findViewById(R.id.ButtonBackwardDate);
        backwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                decrementDate();
                updateDateView();
            }
        });

        Button moodButton = findViewById(R.id.ButtonToOption1);
        moodButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to mood activity");
                Intent moodIntent = new Intent (MainActivity.this, MoodActivity.class);
                moodIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(moodIntent);
            }
        });


        Button moneyButton = findViewById(R.id.ButtonToOption2);
        moneyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to money activity");
                Intent moneyIntent = new Intent (MainActivity.this, MoneyActivity.class);
                moneyIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(moneyIntent);
            }
        });

        Button alcoholButton = findViewById(R.id.ButtonToOption3);
        alcoholButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to alcohol activity");
                Intent alcoholIntent = new Intent (MainActivity.this, AlcoholActivity.class);
                alcoholIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(alcoholIntent);
            }
        });

        Button transportButton = findViewById(R.id.ButtonToOption4);
        transportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to transport activity");
                Intent transportIntent = new Intent (MainActivity.this, TransportActivity.class);
                transportIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(transportIntent);
            }
        });

        Button carButton = findViewById(R.id.ButtonToOption5);
        carButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to car activity");
                Intent carIntent = new Intent (MainActivity.this, CarActivity.class);
                carIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(carIntent);
            }
        });

        Button moneyInButton = findViewById(R.id.ButtonToOption6);
        moneyInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to money in activity");
                Intent moneyInIntent = new Intent (MainActivity.this, MoneyInActivity.class);
                moneyInIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(moneyInIntent);
            }
        });

        Button movieButton = findViewById(R.id.ButtonToOption7);
        movieButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to money in activity");
                Intent movieIntent = new Intent (MainActivity.this, MovieActivity.class);
                movieIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(movieIntent);
            }
        });

        Button bookButton = findViewById(R.id.ButtonToOption8);
        bookButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to book activity");
                Intent bookIntent = new Intent (MainActivity.this, NewBookActivity.class);
                bookIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(bookIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDateView();
    }

    private void incrementDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 1);
        currentDate = c.getTime();
        Log.i("CHROMA", "Incrementing Date");
    }

    private void decrementDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -1);
        currentDate = c.getTime();
        Log.i("CHROMA", "Decrementing Date");
    }

    private void updateDateView() {
        // simple method to update the date view at the top of the screen
        TextView dateView = findViewById(R.id.DateTextView);
        String formattedDate = (new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(this.currentDate));
        Log.i("CHROMA", "Updating date : " + formattedDate);
        dateView.setText(formattedDate);
    }
}
