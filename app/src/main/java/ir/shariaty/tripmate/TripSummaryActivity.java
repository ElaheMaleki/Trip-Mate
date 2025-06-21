package ir.shariaty.tripmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TripSummaryActivity extends AppCompatActivity {

    private ArrayList<JSONObject> tripObjects;
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private ListView lv;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_summary);

        lv = findViewById(R.id.lvTrips);
        Button btnAddTrip = findViewById(R.id.btnAddTrip);

        sp = getSharedPreferences("trips_prefs", MODE_PRIVATE);

        loadTrips();

        lv.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject selectedTrip = tripObjects.get(position);
                String tripId = selectedTrip.optString("tripId", String.valueOf(position));

                Intent intent = new Intent(TripSummaryActivity.this, ToDoListActivity.class);
                intent.putExtra("trip_id", tripId);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(TripSummaryActivity.this, "خطا در باز کردن لیست کارهای سفر", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDeleteTrip(position);
            return true;
        });

        btnAddTrip.setOnClickListener(v -> {
            Intent intent = new Intent(TripSummaryActivity.this, NewTripActivity.class);
            startActivity(intent);
        });
    }

    private void loadTrips() {
        String json = sp.getString("trips", "[]");
        items = new ArrayList<>();
        tripObjects = new ArrayList<>();

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                tripObjects.add(o);

                String s = "مقصد: " + o.getString("destination") +
                        "\nشروع: " + o.getString("startDate") +
                        "\nپایان: " + o.getString("endDate") +
                        "\nتعداد افراد: " + o.getInt("peopleCount");
                items.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در خواندن اطلاعات سفر", Toast.LENGTH_SHORT).show();
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);
    }

    private void confirmDeleteTrip(int position) {
        new AlertDialog.Builder(this)
                .setTitle("حذف سفر")
                .setMessage("آیا مطمئن هستید که می‌خواهید این سفر را حذف کنید؟")
                .setPositiveButton("بله", (dialog, which) -> deleteTrip(position))
                .setNegativeButton("خیر", null)
                .show();
    }

    private void deleteTrip(int position) {
        try {
            JSONArray arr = new JSONArray(sp.getString("trips", "[]"));
            JSONArray newArr = new JSONArray();

            for (int i = 0; i < arr.length(); i++) {
                if (i != position) {
                    newArr.put(arr.get(i));
                }
            }

            sp.edit().putString("trips", newArr.toString()).apply();

            loadTrips();

            Toast.makeText(this, "سفر حذف شد", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در حذف سفر", Toast.LENGTH_SHORT).show();
        }
    }
}
