package fr.zigomar.chroma.chroma.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import fr.zigomar.chroma.chroma.R;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_DATE = "com.example.chroma.current_date";
    private static final int CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;

    private Date currentDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(myToolbar);

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
                Log.i("CHROMA", "Switching to movie activity");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION);
                } else {
                    export();
                }

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    export();

                } else {

                    Log.i("CHROMA", "Permission refused, aborting.");
                }
            }
        }
    }

    private void export() {


        JSONArray data = new JSONArray();
        for (File f: getFilesDir().listFiles()) {
            if (f.isFile()) {
                if (!Objects.equals(f.getName(), "openbooks.json")) {
                    Log.i("CHROMA", "Processing " + f.getName());
                    try {
                        InputStream is = getApplicationContext().openFileInput(f.getName());
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        int byte_read = is.read(buffer);
                        if (byte_read != size) { Log.i("CHROMA", "Did not read the complete file, or something else went wrong"); }
                        is.close();
                        JSONObject temp = new JSONObject(new String(buffer, "UTF-8"));
                        temp.put("date", f.getName().substring(0,10));
                        data.put(temp);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        File exportFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "chroma_export.json");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(exportFile);
            stream.write(data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                    Toast.makeText(getApplicationContext(), R.string.ExportOK, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    }

    private void decrementDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -1);
        currentDate = c.getTime();
    }

    private void updateDateView() {
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
