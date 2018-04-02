package fr.zigomar.chroma.chroma.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import fr.zigomar.chroma.chroma.R;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    public static final String KEY_PREF_ENC = "pref_encrypt";
    public static final String KEY_PREF_PWD = "pref_encryptPWD";
    public static final String KEY_PREF_ACTIVATED_ACTIVITIES = "pref_toggle";
    public static final String KEY_PREF_SLEEP_AIRPLANE_OFF = "pref_sleep_off";
    public static final String KEY_PREF_SLEEP_AIRPLANE_ON = "pref_sleep_on";
    public static final String KEY_PREF_SAVE_MODE = "pref_saveMode";
    public static final String KEY_PREF_PERIODIC_EXPORT = "pref_periodExport";

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_toolbar);

        toolbar = findViewById(R.id.toolbar);
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
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }



}
