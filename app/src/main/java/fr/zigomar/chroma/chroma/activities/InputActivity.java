package fr.zigomar.chroma.chroma.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.zigomar.chroma.chroma.model.DataHandler;
import fr.zigomar.chroma.chroma.R;

public abstract class InputActivity extends AppCompatActivity {

    public static final String CURRENT_DATE = "com.example.chroma.current_date";

    DataHandler dh;

    private boolean cancelDoNotSave = false;

    final Date currentDate = new Date();
    private String formattedDate;
    private String formattedDay;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("list_activity", false)) {
            setContentView(R.layout.listinput_activity);
            Log.i("CHROMA", "opened activity with listinput generic layout");
        } else {
            Log.i("CHROMA", "opened activity with specific layout");
            String className = this.getClass().getSimpleName();
            String cleanClassName = className.replace("Activity", "");
            String layoutResName = "activity_" + cleanClassName.toLowerCase();

            setContentView(getResources().getIdentifier(layoutResName, "layout", this.getApplicationContext().getPackageName()));
        }

        // update the date view at the top of the layout
        this.currentDate.setTime(getIntent().getLongExtra(CURRENT_DATE, -1));
        Log.i("CHROMA", "Activity created with Intent time: " + this.currentDate.getTime());
        this.formattedDate = (new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(this.currentDate));
        this.formattedDay = (new SimpleDateFormat("EEEE", Locale.ENGLISH).format(this.currentDate));

        try {
            updateDateView();

            // hiding the buttons
            ImageView backwardDateButton = findViewById(R.id.ButtonBackwardDate);
            backwardDateButton.setVisibility(View.INVISIBLE);
            ImageView forwardDateButton = findViewById(R.id.ButtonForwardDate);
            forwardDateButton.setVisibility(View.INVISIBLE);
        } catch (NullPointerException ex) {
            Log.i("CHROMA", "Could not update the date and day views, maybe they are missing from the activity layout.");
        }

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(this.formattedDate + " (" + this.formattedDay + ")");
        }


        // init the data handler if not already set in the child class
        if (this.dh == null) {
            this.dh = new DataHandler(this.getApplicationContext(), this.currentDate);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.input_toolbar, menu);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPref.getBoolean(SettingsActivity.KEY_PREF_SAVE_MODE, true)) {
            Log.i("CHROMA", "pref is true");
            menu.findItem(R.id.explicit_save).setEnabled(false);
            menu.findItem(R.id.explicit_save).setVisible(false);
        } else {
            Log.i("CHROMA", "pref is false");
            menu.findItem(R.id.cancel_activity).setEnabled(false);
            menu.findItem(R.id.cancel_activity).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_activity:
                Log.i("CHROMA", "User canceled the current activity, do not save");
                this.cancelDoNotSave = true;
                this.finish();
                return true;

            case R.id.explicit_save:
                Log.i("CHROMA", "Explicit save requested by the user");
                saveData();
                this.dh.writeDataToFile();
                Toast.makeText(getApplicationContext(), R.string.Saved, Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        // surcharge the onStop() method to include a call to the method updating the data and then
        // using the DataHandler to write it to file before closing
        super.onStop();
        Log.i("CHROMA", "Starting activity closing... : " + this.getClass().getSimpleName());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPref.getBoolean(SettingsActivity.KEY_PREF_SAVE_MODE, true)) {
            if (!cancelDoNotSave) {
                saveData();
                this.dh.writeDataToFile();
                Toast.makeText(getApplicationContext(), R.string.Saved, Toast.LENGTH_SHORT).show();
            } else {
                Log.i("CHROMA", "SaveData aborted because cancel button");
            }
        } else {
            Log.i("CHROMA", "OnStop didn't save because the pref explicit save is off");
        }
    }

        // simply fetch the data from the views and save it into the DataHandler with the dedicated method
        // each class inheriting from InputActivity has its own way of doing this and thus must override this method
        //Log.i("CHROMA", "Updating the data object with current values in the views");
    protected abstract void saveData();

    private void updateDateView() throws NullPointerException{
        // simple method to update the date view at the top of the screen
        TextView dateView = findViewById(R.id.DateTextView);
        dateView.setText(formattedDate);

        TextView dayView = findViewById(R.id.DayTextView);
        dayView.setText(formattedDay);

        Log.i("CHROMA", "Updating date : " + formattedDate + " (" + formattedDay + ")");
    }
}
