package ir.shariaty.tripmate;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    TextView txtSource, txtDest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtSource = findViewById(R.id.txt_source);
        txtDest = findViewById(R.id.txt_dest);

        double srcLat = getIntent().getDoubleExtra("source_lat", 0);
        double srcLng = getIntent().getDoubleExtra("source_lng", 0);
        double destLat = getIntent().getDoubleExtra("destination_lat", 0);
        double destLng = getIntent().getDoubleExtra("destination_lng", 0);

        Geocoder geocoder = new Geocoder(this, new Locale("fa"));

        try {
            List<Address> srcList = geocoder.getFromLocation(srcLat, srcLng, 1);
            List<Address> destList = geocoder.getFromLocation(destLat, destLng, 1);

            String srcAddr = (srcList != null && !srcList.isEmpty())
                    ? srcList.get(0).getAddressLine(0)
                    : "مختصات: " + srcLat + ", " + srcLng;

            String destAddr = (destList != null && !destList.isEmpty())
                    ? destList.get(0).getAddressLine(0)
                    : "مختصات: " + destLat + ", " + destLng;

            txtSource.setText("مبدا: " + srcAddr);
            txtDest.setText("مقصد: " + destAddr);
        } catch (IOException e) {
            txtSource.setText("خطا در دریافت آدرس مبدا");
            txtDest.setText("خطا در دریافت آدرس مقصد");
            e.printStackTrace();
        }

        Button btnSave = findViewById(R.id.btn_Save);
        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, NewTripActivity.class);
            startActivity(intent);
            finish(); // این خط باعث می‌شود صفحه فعلی بسته شود و با back به آن برنگردی
        });
    }
}
