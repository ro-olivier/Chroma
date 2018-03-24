package fr.zigomar.chroma.chroma.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.SettingsActivity;
import fr.zigomar.chroma.chroma.model.DataHandler;

public class AirplaneBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.AIRPLANE_MODE")) {

            Calendar cal = Calendar.getInstance();
            Date currentDate = new Date();
            Log.i("CHROMA", "CurrentDate: " + currentDate.toString());
            Log.i("CHROMA", "CurrentDate.getTime: " + currentDate.getTime());
            cal.setTime(currentDate);

            int currentHour = cal.get(Calendar.HOUR_OF_DAY);

            long coeff = 1000 * 60 * 5;
            Calendar roundedCal = Calendar.getInstance();

            double correctedDateDouble = (double) currentDate.getTime() / (double) coeff;
            Log.i("CHROMA", "currentDate.getTime() / coeff) = " + currentDate.getTime() / coeff);
            Log.i("CHROMA", "correctedDateDouble = " + correctedDateDouble);
            Log.i("CHROMA", "Math.round(correctedDateDouble) * coeff = " + Math.round(correctedDateDouble) * coeff);
            Date roundedDate = new Date(Math.round(correctedDateDouble) * coeff);
            Log.i("CHROMA", "roundedDate:" + roundedDate.toString());
            Log.i("CHROMA", "roundedDate.getTime: " + roundedDate.getTime());
            roundedCal.setTime(roundedDate);

            int roundedHour = roundedCal.get(Calendar.HOUR_OF_DAY);
            int roundedMinute = roundedCal.get(Calendar.MINUTE);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            int limit_on = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_SLEEP_AIRPLANE_ON, "22"));
            int limit_off = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_PREF_SLEEP_AIRPLANE_OFF, "7"));

            if (intent.toUri(Intent.URI_INTENT_SCHEME).contains("state=false")) {
                Log.i("CHROMA", "Airplane mode is off");
                Log.i("CHROMA", "Limit_off read from sharedPref:" + limit_off);
                Log.i("CHROMA", "CurrentHour is:" + currentHour);
                Log.i("CHROMA", "RoundedMinute is:" + roundedMinute);
                Log.i("CHROMA", "RoundedHour is:" + roundedHour);
                if (currentHour < limit_off) {
                    // Here the current hour is not yet larger than the limit for the airplane "off"
                    // monitoring, so we don't do anything
                    Log.i("CHROMA", "Airplane off detector out of working hours: not doing anything !");
                } else {
                    // Do something !
                    Log.i("CHROMA", "I'm going to write data in today's file ! (wakeup time)");
                    DataHandler dh = new DataHandler(context, new Date());
                    dh.saveWakeuptime(roundedHour, roundedMinute);
                    dh.writeDataToFile(context);
                    Toast.makeText(context, R.string.Saved, Toast.LENGTH_SHORT).show();
                    // create DataHandler with correct date (should be today all the time,
                    // as we will never finished a night before the end of a day!
                    // call dh.saveWakeuptime
                }
            } else if (intent.toUri(Intent.URI_INTENT_SCHEME).contains("state=true")) {
                Log.i("CHROMA", "Airplane mode in on");
                Log.i("CHROMA", "Limit_on read from sharedPref:" + limit_on);
                Log.i("CHROMA", "Limit_off read from sharedPref:" + limit_off);
                Log.i("CHROMA", "CurrentHour is:" + currentHour);
                Log.i("CHROMA", "RoundedMinute is:" + roundedMinute);
                Log.i("CHROMA", "RoundedHour is:" + roundedHour);
                if (currentHour >= limit_on  || currentHour < limit_off) {
                    // do something !
                    Log.i("CHROMA", "I'm going to write data in today's file ! (bedtime)");
                    Date day;
                    if (currentHour >= limit_on) {
                        Calendar calForDH = Calendar.getInstance();
                        day = calForDH.getTime();
                    } else {
                        Calendar calForDH = Calendar.getInstance();
                        calForDH.add(Calendar.DATE, -1);
                        day = calForDH.getTime();
                    }
                    DataHandler dh = new DataHandler(context, day);
                    dh.saveBedtime(roundedHour, roundedMinute);
                    dh.writeDataToFile(context);
                    Toast.makeText(context, R.string.Saved, Toast.LENGTH_SHORT).show();
                    // create DataHandler with correct date (should be today all the time,
                    // as we will never finished a night before the end of a day!
                    // call dh.saveBedtime
                } else {
                    Log.i("CHROMA", "Airplane on detector out of working hours: not doing anything !");
                    // Here the currentHour is either before the begining of the "on" monitor period,
                    // or after the beginning of the "off" monitor period, so we don't do anything
                }
            } else {
                Log.i("CHROMA", "Could not parse Airplane mode data");
            }
        }
    }
}
