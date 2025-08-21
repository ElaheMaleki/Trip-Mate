package ir.shariaty.tripmate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {

    private TextView tvAlarmStartDate, tvAlarmStartTime;
    private Button btnSetAlarm;

    private String startDate, startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tvAlarmStartDate = findViewById(R.id.tvAlarmStartDate);
        tvAlarmStartTime = findViewById(R.id.tvAlarmStartTime);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);

        startDate = getIntent().getStringExtra("start_date");
        startTime = getIntent().getStringExtra("start_time");

        tvAlarmStartDate.setText("تاریخ شروع سفر: " + startDate);
        tvAlarmStartTime.setText("ساعت شروع سفر: " + startTime);

        btnSetAlarm.setOnClickListener(v -> setAlarm24HoursBefore());

        Button btnBack = findViewById(R.id.btnback);
        btnBack.setOnClickListener(v -> finish());

    }

    private void setAlarm24HoursBefore() {
        try {
            String fullDateTime = startDate + " " + startTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
            Date tripDateTime = sdf.parse(fullDateTime);

            if (tripDateTime == null) {
                Toast.makeText(this, "تاریخ یا ساعت نامعتبر است", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTime(tripDateTime);
            alarmTime.add(Calendar.HOUR_OF_DAY, -24);

            long alarmMillis = alarmTime.getTimeInMillis();

            if (alarmMillis < System.currentTimeMillis()) {
                Toast.makeText(this, "زمان آلارم گذشته است", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmMillis, pendingIntent);

            Toast.makeText(this, "آلارم برای روز قبل از سفر تنظیم شد", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در تنظیم آلارم", Toast.LENGTH_SHORT).show();
        }
    }
}
