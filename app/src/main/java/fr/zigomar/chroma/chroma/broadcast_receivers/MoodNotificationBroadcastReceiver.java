package fr.zigomar.chroma.chroma.broadcast_receivers;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import fr.zigomar.chroma.chroma.R;
import fr.zigomar.chroma.chroma.activities.InputActivity;
import fr.zigomar.chroma.chroma.activities.MainActivity;
import fr.zigomar.chroma.chroma.activities.MoodActivity;

public class MoodNotificationBroadcastReceiver extends BroadcastReceiver {

    public static final int mood_notification_id = 12;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("CHROMA", "Received broadcase with intent = " + intent.toString());
        Log.i("CHROMA", "sameDay: " + intent.getBooleanExtra("sameDay", false));
        Log.i("CHROMA", "idAlarm: " + intent.getIntExtra("idAlarm", 100));
        if (Objects.equals(intent.getAction(), "com.example.chroma.intent.mood_notification")) {


            Intent mood_intent = new Intent(context, MoodActivity.class);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID);

            
            mBuilder.setSmallIcon(R.mipmap.rainbow)
                    .setContentTitle("Chroma mood reminder")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Date currentDate = new Date();
            Calendar cal = Calendar.getInstance();

            if (intent.getBooleanExtra("sameDay", false)) {
                mBuilder.setContentText("Have you written today?");
                Log.i("CHROMA", "MoodIntent build with time = " + currentDate.getTime());
                mood_intent.putExtra(InputActivity.CURRENT_DATE, currentDate.getTime());
            } else {
                mBuilder.setContentText("Have you written about yesterday?");
                cal.setTime(currentDate);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Log.i("CHROMA", "MoodIntent build with time = " + cal.getTime().getTime());
                mood_intent.putExtra(InputActivity.CURRENT_DATE, cal.getTime().getTime());
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(mood_intent);
            PendingIntent moodPendingIntent = stackBuilder.getPendingIntent(mood_notification_id, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(moodPendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(mood_notification_id, mBuilder.build());

        }
    }
}