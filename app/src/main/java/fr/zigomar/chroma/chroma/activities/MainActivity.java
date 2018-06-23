package fr.zigomar.chroma.chroma.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
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
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.zigomar.chroma.chroma.adapters.ImageAdapter;
import fr.zigomar.chroma.chroma.asynctasks.ExportDataTask;
import fr.zigomar.chroma.chroma.broadcast_receivers.AirplaneBroadcastReceiver;
import fr.zigomar.chroma.chroma.fragments.ExportDateFragment;
import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.fragments.PasswordFragment;

public class MainActivity extends AppCompatActivity {

    private static final String CURRENT_DATE = "com.example.chroma.current_date";
    private static final int CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_FULL_EXPORT = 100;
    private static final int CHROMA_WRITE_EXTERNAL_STORAGE_PERMISSION_DATE_EXPORT = 101;

    private Date currentDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean(SettingsActivity.KEY_PREF_APP_PASSWORD, false) &&
                !Objects.equals(sharedPref.getString(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD, ""), "")) {

            PasswordFragment pwdFragment = new PasswordFragment();
            getFragmentManager().beginTransaction().add(pwdFragment, "NewPasswordAttempt").commit();
            // yes, I know this first passwordOK=false; is quite useless, but this is just to be on the safe side
            // if something goes wrong in showDialogPassword and it doesn't return a value for some reason.
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        ImageView forwardButton = findViewById(R.id.ButtonForwardDate);
        forwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                incrementDate();
                updateDateView();
            }
        });

        ImageView backwardButton = findViewById(R.id.ButtonBackwardDate);
        backwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                decrementDate();
                updateDateView();
            }
        });

        LinearLayout displayDateLayout = findViewById(R.id.display_date);
        displayDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(currentDate);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        currentDate = c.getTime();
                        updateDateView();
                    }
                }, year, month, day);
                datePicker.setTitle("Select Date");
                datePicker.show();
            }
        });

        BroadcastReceiver br = new AirplaneBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        getApplicationContext().registerReceiver(br, filter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
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

                    new ExportDataTask(getApplicationContext()).execute();
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

                    new ExportDataTask(getApplicationContext()).execute();

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
        Pattern validDataFilename = Pattern.compile("((?:19|20)\\d\\d)-(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01]).json");

        for (File f: getFilesDir().listFiles()) {
            if (f.isFile()) {
                Matcher matcher = validDataFilename.matcher(f.getName());

                if (matcher.matches()) {
                    Log.i("CHROMA", f.getName());
                    available_dates.add(f.getName().substring(0,10));
                }
            }
        }

        String[] available_dates_array = new String[available_dates.size()];
        Collections.sort(available_dates, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                int val1 = dataFilenameFromStringToInt(o1);
                int val2 = dataFilenameFromStringToInt(o2);

                return Integer.compare(val1, val2);
            }
        });

        ExportDateFragment exportDateFragment = new ExportDateFragment();
        String[] sorted_available_dates = (available_dates.toArray(available_dates_array));
        exportDateFragment.setMinDate(sorted_available_dates[0]);
        exportDateFragment.setMaxDate(sorted_available_dates[sorted_available_dates.length - 1]);
        getFragmentManager().beginTransaction().add(exportDateFragment, "ExportDateSelector").commit();
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
        //        R.drawable.transportactivity, R.drawable.caractivity, R.drawable.incomeactivity,
        //        R.drawable.movieactivity, R.drawable.bookactivity };

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
                String className = "fr.zigomar.chroma.chroma.activities." + capitalized_choice;

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

        checkPeriodExport();
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

    public static int dataFilenameFromStringToInt(String filename) {
        return Integer.parseInt(filename.split("-")[0] + filename.split("-")[1] + filename.split("-")[2]);
    }

    private void checkPeriodExport() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.i("CHROMA", "The period export policy is: " + sharedPref.getString(SettingsActivity.KEY_PREF_PERIODIC_EXPORT, ""));
        int periodExportInt = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_PERIODIC_EXPORT, "0"));
        if (periodExportInt > 0) {
            // must check and do periodic export
            String filename;
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            File f;

            switch (periodExportInt) {
                case 3:
                    //yearly
                    filename = getApplicationContext().getString(R.string.YearlyExportFilenamePrefix);

                    cal.add(Calendar.YEAR, -1);

                    filename += cal.get(Calendar.YEAR);
                    filename += getApplicationContext().getString(R.string.JSONExtension);
                    f = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS), filename);

                    if (f.exists()) {
                        Log.i("CHROMA", "Yearly export file is already there, see you next year!");
                    } else {
                        Log.i("CHROMA", "Happy new year, let's export the data from last year");
                        String first_day = cal.get(Calendar.YEAR) + "-01-01";
                        String last_day = cal.get(Calendar.YEAR) + "-12-31";
                        Log.i("CHROMA", "first_day= " + first_day + " || last_day= " + last_day);
                        Log.i("CHROMA", "Period export filename = " + filename);
                        new ExportDataTask(getApplicationContext()).execute(first_day, last_day, filename);
                    }
                    break;
                case 2:
                    //monthly
                    filename = getApplicationContext().getString(R.string.MonthlyExportFilenamePrefix);

                    @SuppressLint("DefaultLocale") String month = String.format("%02d", cal.get(Calendar.MONTH));

                    filename += month;
                    filename += getApplicationContext().getString(R.string.JSONExtension);
                    f = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS), filename);

                    if (f.exists()) {
                        Log.i("CHROMA", "Monthly export file is already there, see you next month!");
                    } else {
                        Log.i("CHROMA", "A new month begins, let's export the data from last month");
                        String first_day = cal.get(Calendar.YEAR) + "-" + month + "-01";
                        String last_day = cal.get(Calendar.YEAR) + "-" + month + "-31";
                        Log.i("CHROMA", "first_day= " + first_day + " || last_day= " + last_day);
                        Log.i("CHROMA", "Period export filename = " + filename);
                        new ExportDataTask(getApplicationContext()).execute(first_day, last_day, filename);
                    }
                    break;
                case 1:
                    //weekly

                    filename = getApplicationContext().getString(R.string.WeeklyExportFilenamePrefix);

                    cal.add(Calendar.WEEK_OF_YEAR, -1);

                    filename += cal.get(Calendar.WEEK_OF_YEAR);
                    filename += getApplicationContext().getString(R.string.JSONExtension);
                    f = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS), filename);

                    if (f.exists()) {
                        Log.i("CHROMA", "Weekly export file is already there, see you next week!");
                    } else {
                        Log.i("CHROMA", "A new week begins, let's export the data from last week");
                        Calendar yesterday = Calendar.getInstance();
                        yesterday.setTime(currentDate);
                        yesterday.add(Calendar.DAY_OF_YEAR, -1);

                        Calendar seven_day_ago = Calendar.getInstance();
                        seven_day_ago.setTime(currentDate);
                        seven_day_ago.add(Calendar.DAY_OF_YEAR, -7);

                        String first_day = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(seven_day_ago.getTime());
                        String last_day = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(yesterday.getTime());
                        Log.i("CHROMA", "first_day= " + first_day + " || last_day= " + last_day);
                        Log.i("CHROMA", "Period export filename = " + filename);
                        new ExportDataTask(getApplicationContext()).execute(first_day, last_day, filename);
                    }
                    break;
            }
        }
    }
}