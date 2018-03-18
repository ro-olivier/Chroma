package fr.zigomar.chroma.chroma.Activities;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.GridView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import fr.zigomar.chroma.chroma.Adapters.ImageAdapter;
import fr.zigomar.chroma.chroma.Fragments.ExportDateFragment;
import fr.zigomar.chroma.chroma.R;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_DATE = "com.example.chroma.current_date";
    private static final int CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_FULL_EXPORT = 100;
    private static final int CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_DATE_EXPORT = 101;

    private Date currentDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

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
            case R.id.action_export_all:
                Log.i("CHROMA", "User request full export");
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.i("CHROMA", "Request code : CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_FULL_EXPORT");
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_FULL_EXPORT);
                } else {
                    Log.i("CHROMA", "Permission already granted, proceeding with export()");
                    export();
                }

                return true;

            case R.id.action_export:
                Log.i("CHROMA", "User request date export");
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.i("CHROMA", "Requesting permission WRITE_EXTERNAL_STORAGE");
                    Log.i("CHROMA", "Request code : CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_DATE_EXPORT");
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_DATE_EXPORT);
                } else {
                    Log.i("CHROMA", "Permission already granted, proceeding with getDateAndExport()");
                    getDateAndExport();
                }

                return true;

            case R.id.action_settings:
                Log.i("CHROMA", "Switching to settings activity");
                Intent settingIntent = new Intent (MainActivity.this, SettingsActivity.class);
                startActivity(settingIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_FULL_EXPORT: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("CHROMA", "Received permission CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_FULL_EXPORT");
                    export();

                } else {

                    Log.i("CHROMA", "Permission refused, aborting full export.");
                }

                break;
            }

            case CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_DATE_EXPORT: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("CHROMA", "Received permission CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_DATE_EXPORT");
                    getDateAndExport();

                } else {

                    Log.i("CHROMA", "Permission refused, aborting full export.");
                }

                break;
            }
        }
    }

    private void getDateAndExport() {
        ArrayList<String> available_dates = new ArrayList<>();

        for (File f: getFilesDir().listFiles()) {
            if (f.isFile()) {
                if (!Objects.equals(f.getName(), getString(R.string.OpenBooksFileName))) {
                    Log.i("CHROMA", f.getName());
                    available_dates.add(f.getName().substring(0,10));
                }
            }
        }

        String[] available_dates_array = new String[available_dates.size()];
        Collections.sort(available_dates, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                int val1 = Integer.parseInt(o1.split("-")[0] + o1.split("-")[1] + o1.split("-")[2]);
                int val2 = Integer.parseInt(o2.split("-")[0] + o2.split("-")[1] + o2.split("-")[2]);

                if (val1 < val2) {
                    return -1;
                } else if (val1 == val2) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        DialogFragment newFragment = ExportDateFragment.newInstance(available_dates.toArray( available_dates_array ));
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void exportOnReceiveExportDates(String beginDate, String endDate) {

        Log.i("CHROMA", "exportOnReceiveExportDates was called");

        int beginDateInt = Integer.parseInt(beginDate.split("-")[0] + beginDate.split("-")[1] + beginDate.split("-")[2]);
        int endDateInt = Integer.parseInt(endDate.split("-")[0] + endDate.split("-")[1] + endDate.split("-")[2]);

        JSONArray data = new JSONArray();
        for (File f: getFilesDir().listFiles()) {
            if (f.isFile()) {
                String filename = f.getName();
                String filenameIntStr = filename.substring(0, 10);

                if (!Objects.equals(filename, getString(R.string.OpenBooksFileName))) {
                    Log.i("CHROMA", "Processing file date : " + filename);
                    int filenameInt = Integer.parseInt(filenameIntStr.split("-")[0] + filenameIntStr.split("-")[1] + filenameIntStr.split("-")[2]);

                    if (filenameInt >= beginDateInt && filenameInt <= endDateInt) {
                        Log.i("CHROMA", "Reading data from file : " + filename);
                        try {
                            InputStream is = getApplicationContext().openFileInput(filename);
                            int size = is.available();
                            byte[] buffer = new byte[size];
                            int byte_read = is.read(buffer);
                            if (byte_read != size) {
                                Log.i("CHROMA", "Did not read the complete file, or something else went wrong");
                            }
                            is.close();
                            JSONObject temp = new JSONObject(new String(buffer, "UTF-8"));
                            temp.put("date", filename.substring(0, 10));
                            data.put(temp);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("CHROMA", "Dismissing file date : " + filename);
                    }
                }
            }
        }

        String exportFilename;
        if (beginDateInt != endDateInt) {
            exportFilename = "chroma_export_" + beginDate + "_" + endDate + ".json";
        } else {
            exportFilename = "chroma_export_" + beginDate + ".json";
        }

        Log.i("CHROMA", "Writing to : " + exportFilename);

        File exportFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), exportFilename);

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

    private void export() {

        Log.i("CHROMA", "export was called");

        JSONArray data = new JSONArray();
        for (File f: getFilesDir().listFiles()) {
            if (f.isFile()) {
                String filename = f.getName();
                if (!Objects.equals(filename, getString(R.string.OpenBooksFileName))) {
                    Log.i("CHROMA", "Processing " + filename);
                    try {
                        InputStream is = getApplicationContext().openFileInput(filename);
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        int byte_read = is.read(buffer);
                        if (byte_read != size) { Log.i("CHROMA", "Did not read the complete file, or something else went wrong"); }
                        is.close();
                        JSONObject temp = new JSONObject(new String(buffer, "UTF-8"));
                        temp.put("date", filename.substring(0,10));
                        data.put(temp);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        File exportFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), getString(R.string.FullExportFilename));

        Log.i("CHROMA", "Writing to : chroma_full_export.json");

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
        Log.i("CHROMA", "Resume main activity !");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> activated_activities = sharedPref.getStringSet(SettingsActivity.KEY_PREF_ACTIVATED_ACTIVITIES, new HashSet<String>());

        Log.i("CHROMA", "Size of set returned from Preferences: " + activated_activities.size());

        ArrayList<Integer> ids_array = new ArrayList<>();
        for (String s: activated_activities) {
            int id = getResources().getIdentifier(s.toLowerCase(), "drawable", this.getApplicationContext().getPackageName());
            ids_array.add(id);
            Log.i("CHROMA", "Adding the following activity class the the list from Preferences: " + s);
            Log.i("CHROMA", "id: " + id);
        }

        //Integer[] ids = {
        //        R.drawable.moodactivity, R.drawable.moneyactivity, R.drawable.alcoholactivity,
        //        R.drawable.transportactivity, R.drawable.caractivity, R.drawable.moneyinactivity,
        //        R.drawable.movieactivity, R.drawable.newbookactivity };

        int[] ids = new int[ids_array.size()];
        for (int i=0; i < ids.length; i++) {
            ids[i] = ids_array.get(i);
        }

        final int[] ids_clone = ids.clone();

        GridView gridview = findViewById(R.id.ButtonsGridView);
        gridview.setAdapter(new ImageAdapter(this, ids));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String choice = getResources().getResourceName(ids_clone[position]).split("/")[1];

                String capitalized_activity = choice.replaceFirst("activity", "Activity");
                String capitalized_choice = capitalized_activity.substring(0, 1).toUpperCase() + capitalized_activity.substring(1);
                String className = "fr.zigomar.chroma.chroma.Activities." + capitalized_choice;

                Log.i("CHROMA", "Switching to " + choice + " (transformed to " + capitalized_choice + ")");

                Intent newIntent;
                try {
                    newIntent = new Intent(MainActivity.this, Class.forName(className));
                    newIntent.putExtra(CURRENT_DATE, currentDate.getTime());
                    startActivity(newIntent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

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
