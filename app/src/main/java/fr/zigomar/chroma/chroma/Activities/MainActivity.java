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

    public static final String CURRENT_DATE = "com.example.chroma.current_date";

    Date currentDate = new Date();

    ImageButton forwardButton;
    ImageButton backwardButton;

    Button moodButton;
    Button moneyButton;
    Button alcoholButton;
    Button transportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.forwardButton = (ImageButton) findViewById(R.id.ButtonForwardDate);
        this.forwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementDate();
                updateDateView();
            }
        });

        this.backwardButton = (ImageButton) findViewById(R.id.ButtonBackwardDate);
        this.backwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                decrementDate();
                updateDateView();
            }
        });

        this.moodButton = (Button) findViewById(R.id.ButtonToOption1);
        this.moodButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to mood activity");
                Intent moodIntent = new Intent (MainActivity.this, MoodActivity.class);
                moodIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(moodIntent);
            }
        });


        this.moneyButton = (Button) findViewById(R.id.ButtonToOption2);
        this.moneyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to money activity");
                Intent moneyIntent = new Intent (MainActivity.this, MoneyActivity.class);
                moneyIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(moneyIntent);
            }
        });

        this.alcoholButton = (Button) findViewById(R.id.ButtonToOption3);
        this.alcoholButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to money activity");
                Intent alcoholIntent = new Intent (MainActivity.this, AlcoholActivity.class);
                alcoholIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(alcoholIntent);
            }
        });

        this.transportButton = (Button) findViewById(R.id.ButtonToOption4);
        this.transportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("CHROMA", "Switching to transport activity");
                Intent transportIntent = new Intent (MainActivity.this, TransportActivity.class);
                transportIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                startActivity(transportIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDateView();
    }

    public void incrementDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 1);
        currentDate = c.getTime();
        Log.i("CHROMA", "Incrementing Date");
    }

    public void decrementDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -1);
        currentDate = c.getTime();
        Log.i("CHROMA", "Decrementing Date");
    }

    private void updateDateView() {
        // simple method to update the date view at the top of the screen
        TextView dateView = (TextView) findViewById(R.id.DateTextView);
        String formattedDate = (new SimpleDateFormat("yyyy/MM/dd", Locale.FRANCE).format(this.currentDate));
        Log.i("CHROMA", "Updating date : " + formattedDate);
        dateView.setText(formattedDate);
    }
}
