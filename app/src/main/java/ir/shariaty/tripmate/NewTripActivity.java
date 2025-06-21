package ir.shariaty.tripmate;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class NewTripActivity extends AppCompatActivity {

    private EditText etDestination, etPeopleCount;
    private TextView tvStartDate, tvEndDate, tvStartTime;
    private Button btnSelectStartDate, btnSelectEndDate, btnSelectStartTime, btnGoToToDo;

    private int startHour = -1;
    private int startMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        etDestination = findViewById(R.id.etDestination);
        etPeopleCount = findViewById(R.id.etPeopleCount);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        btnSelectStartDate = findViewById(R.id.btnSelectStartDate);
        btnSelectEndDate = findViewById(R.id.btnSelectEndDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnGoToToDo = findViewById(R.id.btnGoToToDo);

        btnSelectStartDate.setOnClickListener(v -> showDatePickerDialog(tvStartDate));
        btnSelectEndDate.setOnClickListener(v -> showDatePickerDialog(tvEndDate));
        btnSelectStartTime.setOnClickListener(v -> showTimePickerDialog());

        btnGoToToDo.setOnClickListener(v -> {
            if (validateInputs()) {
                String destination = etDestination.getText().toString().trim();
                String startDate = tvStartDate.getText().toString();
                String endDate = tvEndDate.getText().toString();
                int peopleCount = Integer.parseInt(etPeopleCount.getText().toString());
                String startTime = tvStartTime.getText().toString();

                String tripId = String.valueOf(System.currentTimeMillis());

                saveTrip(tripId, destination, startDate, startTime, endDate, peopleCount);

                scheduleNotification(tripId, startDate, startTime);

                Intent intent = new Intent(NewTripActivity.this, ToDoListActivity.class);
                intent.putExtra("trip_id", tripId);
                startActivity(intent);
            }
        });
    }

    private void showDatePickerDialog(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                NewTripActivity.this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String date = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay;
                    targetTextView.setText(date);
                },
                year, month, day
        );

        dialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                NewTripActivity.this,
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    startHour = selectedHour;
                    startMinute = selectedMinute;
                    String formattedTime = String.format("%02d:%02d", startHour, startMinute);
                    tvStartTime.setText(formattedTime);
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private boolean validateInputs() {
        if (etDestination.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "لطفاً مقصد را وارد کنید", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvStartDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "لطفاً تاریخ شروع را وارد کنید", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvStartTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "لطفاً ساعت شروع را وارد کنید", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvEndDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "لطفاً تاریخ پایان را وارد کنید", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPeopleCount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "لطفاً تعداد افراد را وارد کنید", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveTrip(String tripId, String destination, String startDate, String startTime, String endDate, int peopleCount) {
        SharedPreferences sp = getSharedPreferences("trips_prefs", MODE_PRIVATE);
        String tripsJson = sp.getString("trips", "[]");

        try {
            JSONArray tripsArray = new JSONArray(tripsJson);

            JSONObject tripObj = new JSONObject();
            tripObj.put("tripId", tripId);
            tripObj.put("destination", destination);
            tripObj.put("startDate", startDate);
            tripObj.put("startTime", startTime);
            tripObj.put("endDate", endDate);
            tripObj.put("peopleCount", peopleCount);

            tripsArray.put(tripObj);
            sp.edit().putString("trips", tripsArray.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotification(String tripId, String startDate, String startTime) {
        try {
            String[] dateParts = startDate.split("/");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int day = Integer.parseInt(dateParts[2]);

            String[] timeParts = startTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            calendar.add(Calendar.MINUTE, -1);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                return;
            }

            Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
            intent.putExtra("tripId", tripId);
            intent.putExtra("notificationTitle", "یادآوری سفر");
            intent.putExtra("notificationText", "1 دقیقه مانده به شروع سفر شما!");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, tripId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
