package fr.zigomar.chroma.chroma.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Objects;

import fr.zigomar.chroma.chroma.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_ENC = "pref_encrypt";
    public static final String KEY_PREF_PWD = "pref_encryptPWD";
    public static final String KEY_PREF_ACTIVATED_ACTIVITIES = "pref_toggle";
    public static final String KEY_PREF_SLEEP_AIRPLANE_OFF = "pref_sleep_off";
    public static final String KEY_PREF_SLEEP_AIRPLANE_ON = "pref_sleep_on";
    public static final String KEY_PREF_SAVE_MODE = "pref_saveMode";
    public static final String KEY_PREF_PERIODIC_EXPORT = "pref_periodExport";
    public static final String KEY_PREF_COMMUTE = "pref_commute";
    public static final String KEY_PREF_APP_PASSWORD = "pref_appPassword";
    public static final String KEY_PREF_APP_PASSWORD_PWD = "pref_appPasswordPWD";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        Log.i("CHROMA-------------->", "Created SettingsActivity: "+ this.toString());
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyPreferenceFragment()).commit();


    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        customOnSharedPreferenceChangeListener listener;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            managePasswordPreferences();
            Log.i("CHROMA-------------->", "Created MyPreferenceFragment: "+ this.toString());

            this.listener = new customOnSharedPreferenceChangeListener(getActivity());

        }

        private void managePasswordPreferences() {
            if (getPreferenceManager().getSharedPreferences().getBoolean(SettingsActivity.KEY_PREF_ENC, false)) {
                Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_PWD)).setEnabled(true);
            } else {
                Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_PWD)).setEnabled(false);
            }

            if (getPreferenceManager().getSharedPreferences().getBoolean(SettingsActivity.KEY_PREF_APP_PASSWORD, false)) {
                Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD)).setEnabled(true);
            } else {
                Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD)).setEnabled(false);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.i("CHROMA-------------->","getActivity: " + getActivity().toString());
            Log.i("CHROMA-------------->","Resumed MyPreferenceFragment " + this.toString());
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this.listener);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this.listener);
            Log.i("CHROMA-------------->","getActivity: " + getActivity().toString());
            Log.i("CHROMA-------------->","Paused MyPreferenceFragment " + this.toString());
            super.onPause();
        }

        @Override
        public void onDestroy() {
            Log.i("CHROMA-------------->","getActivity: " + getActivity().toString());
            Log.i("CHROMA-------------->","Destroyed MyPreferenceFragment " + this.toString());
            super.onDestroy();
        }

        private class customOnSharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

            private Context ctx;

            private customOnSharedPreferenceChangeListener(Context ctx) {
                this.ctx = ctx;
                Log.i("CHROMA-------------->", "customOnSharedPreferenceChangeListener created: "+ this.toString());
                Log.i("CHROMA-------------->", "Listener created with context: " + this.ctx);
            }

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (Objects.equals(key, SettingsActivity.KEY_PREF_ENC)) {
                    if (sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_ENC, false)) {
                        Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_PWD)).setEnabled(true);
                    } else {
                        Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_PWD)).setEnabled(false);
                    }
                }

                if (Objects.equals(key, SettingsActivity.KEY_PREF_APP_PASSWORD)) {
                    Log.i("CHROMA-------------->", "Listener activated with context: " + this.ctx);
                    if (sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_APP_PASSWORD, false)) {
                        Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD)).setEnabled(true);
                    } else {
                        Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD)).setEnabled(false);
                    }
                }

                if (Objects.equals(key, SettingsActivity.KEY_PREF_APP_PASSWORD_PWD)) {
                    Log.i("CHROMA-------------->", "Listener activated with context: " + this.ctx);
                    if (Objects.requireNonNull(getPreferenceManager().findPreference(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD)).isEnabled()
                            && sharedPreferences.getString(SettingsActivity.KEY_PREF_APP_PASSWORD_PWD, "").length() > 0) {
                        AlertDialog dialog = new AlertDialog.Builder(this.ctx)
                                .setTitle(R.string.app_name)
                                .setMessage(R.string.RestartRequiredDoItNowQuestion)
                                .setPositiveButton(R.string.RestartNow, new onRestartSelected())
                                .setNegativeButton(R.string.RestartLater, null)
                                .create();

                        dialog.show();
                    }
                }
            }
        }

        class onRestartSelected implements AlertDialog.OnClickListener {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent mStartActivity = new Intent(getActivity(), MainActivity.class);
                PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(),
                        888,
                        mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                Objects.requireNonNull(mgr).set(AlarmManager.RTC, System.currentTimeMillis() + 150, mPendingIntent);
                System.exit(0);
            }
        }
    }
}