package ir.shariaty.tripmate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ToDoListActivity extends AppCompatActivity {

    private EditText etNewTask;
    private Button btnAddTask, btnShowSummary;
    private ListView lvTasks;

    private ArrayList<String> taskList;
    private ArrayList<Boolean> checkedList;
    private TaskAdapter adapter;

    private static final String PREFS_NAME = "todo_prefs";
    private String TASKS_KEY;
    private String CHECKED_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        etNewTask = findViewById(R.id.etNewTask);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnShowSummary = findViewById(R.id.btnShowSummary);
        lvTasks = findViewById(R.id.lvTasks);

        String tripId = getIntent().getStringExtra("trip_id");
        if (tripId == null) {
            Toast.makeText(this, "شناسه سفر دریافت نشد", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tripId = tripId.replace("/", "_");

        TASKS_KEY = "tasks_" + tripId;
        CHECKED_KEY = "checked_" + tripId;

        taskList = loadTaskList();
        checkedList = loadCheckedList();

        adapter = new TaskAdapter();
        lvTasks.setAdapter(adapter);

        btnAddTask.setOnClickListener(v -> {
            String task = etNewTask.getText().toString().trim();
            if (task.isEmpty()) {
                Toast.makeText(ToDoListActivity.this, "لطفا یک کار وارد کنید", Toast.LENGTH_SHORT).show();
            } else {
                taskList.add(task);
                checkedList.add(false);
                adapter.notifyDataSetChanged();
                etNewTask.setText("");
                saveTaskList();
                saveCheckedList();
                showToDoNotification();
            }
        });

        btnShowSummary.setOnClickListener(v -> {
            Intent intent = new Intent(ToDoListActivity.this, TripSummaryActivity.class);
            startActivity(intent);
        });

        showToDoNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showToDoNotification();
    }

    private ArrayList<String> loadTaskList() {
        ArrayList<String> list = new ArrayList<>();
        String json = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(TASKS_KEY, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private void saveTaskList() {
        JSONArray jsonArray = new JSONArray();
        for (String task : taskList) {
            jsonArray.put(task);
        }
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(TASKS_KEY, jsonArray.toString())
                .apply();
    }

    private ArrayList<Boolean> loadCheckedList() {
        ArrayList<Boolean> list = new ArrayList<>();
        String json = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(CHECKED_KEY, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getBoolean(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        while (list.size() < taskList.size()) {
            list.add(false);
        }
        return list;
    }

    private void saveCheckedList() {
        JSONArray jsonArray = new JSONArray();
        for (Boolean checked : checkedList) {
            jsonArray.put(checked);
        }
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putString(CHECKED_KEY, jsonArray.toString())
                .apply();
    }

    private class TaskAdapter extends ArrayAdapter<String> {
        public TaskAdapter() {
            super(ToDoListActivity.this, R.layout.list_item_task, R.id.checkBoxTask, taskList);
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            CheckBox checkBoxTask = view.findViewById(R.id.checkBoxTask);
            ImageButton btnDelete = view.findViewById(R.id.btnDeleteTask);

            checkBoxTask.setText(taskList.get(position));
            checkBoxTask.setChecked(checkedList.get(position));

            checkBoxTask.setOnCheckedChangeListener(null);
            checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkedList.set(position, isChecked);
                saveCheckedList();
                showToDoNotification();
            });

            btnDelete.setOnClickListener(v -> {
                taskList.remove(position);
                checkedList.remove(position);
                notifyDataSetChanged();
                saveTaskList();
                saveCheckedList();
                showToDoNotification();
                Toast.makeText(ToDoListActivity.this, "کار حذف شد", Toast.LENGTH_SHORT).show();
            });

            return view;
        }
    }

    private void showToDoNotification() {
        String channelId = "todo_channel";
        String channelName = "اعلان‌های لیست کارها";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        StringBuilder contentBuilder = new StringBuilder();
        if (taskList.isEmpty()) {
            contentBuilder.append("لیست کارها خالی است.");
        } else {
            for (int i = 0; i < taskList.size(); i++) {
                contentBuilder.append("• ").append(taskList.get(i));
                if (checkedList.get(i)) {
                    contentBuilder.append(" ✅");
                }
                contentBuilder.append("\n");
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notifications) // آیکون مناسب را جایگزین کنید
                .setContentTitle("لیست کارهای سفر")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentBuilder.toString()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
