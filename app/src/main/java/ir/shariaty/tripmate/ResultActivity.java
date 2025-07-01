package ir.shariaty.tripmate;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
        double destLat = getIntent().getDoubleExtra("dest_lat", 0);
        double destLng = getIntent().getDoubleExtra("dest_lng", 0);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> srcList = geocoder.getFromLocation(srcLat, srcLng, 1);
            List<Address> destList = geocoder.getFromLocation(destLat, destLng, 1);

            String srcAddr = srcList != null && !srcList.isEmpty() ? srcList.get(0).getAddressLine(0) : "نامشخص";
            String destAddr = destList != null && !destList.isEmpty() ? destList.get(0).getAddressLine(0) : "نامشخص";

            txtSource.setText("مبدا: " + srcAddr);
            txtDest.setText("مقصد: " + destAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
