package ir.shariaty.tripmate;  // نام پکیج را متناسب با پروژه خود تغییر دهید

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "trip_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String tripId = intent.getStringExtra("trip_id");
        if (tripId == null) return;

        String formattedTripId = tripId.replace("/", "_");
        String tasksKey = "tasks_" + formattedTripId;

        SharedPreferences sp = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE);
        String tasksJson = sp.getString(tasksKey, "[]");

        StringBuilder todoListString = new StringBuilder();
        try {
            JSONArray tasksArray = new JSONArray(tasksJson);
            for (int i = 0; i < tasksArray.length(); i++) {
                String task = tasksArray.getString(i);
                todoListString.append("- ").append(task).append("\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (todoListString.length() == 0) {
            todoListString.append("هیچ موردی برای یادآوری وجود ندارد.");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Trip Reminder Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent activityIntent = new Intent(context, ToDoListActivity.class);
        activityIntent.putExtra("trip_id", tripId);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)  // آیکون مناسب پروژه خود را قرار دهید
                .setContentTitle("یادآوری سفر")
                .setContentText("موارد To-Do سفر شما آماده‌اند")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(todoListString.toString()))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
