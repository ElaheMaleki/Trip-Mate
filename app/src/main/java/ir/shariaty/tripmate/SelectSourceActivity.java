package ir.shariaty.tripmate;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectSourceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng sourceLatLng;
    private EditText etSourceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_source);

        etSourceAddress = findViewById(R.id.et_source_address);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_source);
        mapFragment.getMapAsync(this);

        findViewById(R.id.btn_confirm_source).setOnClickListener(v -> {
            if (sourceLatLng != null) {
                Intent intent = new Intent(this, SelectDestinationActivity.class);
                intent.putExtra("source_lat", sourceLatLng.latitude);
                intent.putExtra("source_lng", sourceLatLng.longitude);
                startActivity(intent);
            } else {
                Toast.makeText(this, "لطفاً یک مبدا انتخاب کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng iranCenter = new LatLng(32.4279, 53.6880);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iranCenter, 5.5f));

        LatLng southwest = new LatLng(24.396308, 44.031890);
        LatLng northeast = new LatLng(39.781812, 63.333336);
        LatLngBounds iranBounds = new LatLngBounds(southwest, northeast);
        mMap.setLatLngBoundsForCameraTarget(iranBounds);

        mMap.setOnMapClickListener(latLng -> {
            if (iranBounds.contains(latLng)) {
                mMap.clear();
                sourceLatLng = latLng;
                mMap.addMarker(new MarkerOptions().position(latLng).title("مبدا"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                // گرفتن آدرس فارسی و نمایش در EditText
                Geocoder geocoder = new Geocoder(this, new Locale("fa"));
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        etSourceAddress.setText(addresses.get(0).getAddressLine(0));
                    } else {
                        etSourceAddress.setText("آدرس پیدا نشد");
                    }
                } catch (IOException e) {
                    etSourceAddress.setText("خطا در دریافت آدرس");
                }
            } else {
                Toast.makeText(this, "لطفاً نقطه‌ای داخل ایران انتخاب کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
