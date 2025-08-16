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

public class SelectDestinationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng destinationLatLng;
    private LatLng sourceLatLng;
    private EditText etDestAddress;

    private static final LatLng SOUTHWEST_IRAN = new LatLng(24.5, 43.5);
    private static final LatLng NORTHEAST_IRAN = new LatLng(40.0, 64.0);
    private static final LatLngBounds IRAN_BOUNDS = new LatLngBounds(SOUTHWEST_IRAN, NORTHEAST_IRAN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);

        etDestAddress = findViewById(R.id.et_dest_address);

        double srcLat = getIntent().getDoubleExtra("source_lat", 0);
        double srcLng = getIntent().getDoubleExtra("source_lng", 0);
        sourceLatLng = new LatLng(srcLat, srcLng);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_destination);
        mapFragment.getMapAsync(this);

        findViewById(R.id.btn_confirm_destination).setOnClickListener(v -> {
            if (destinationLatLng != null) {
                Intent intent = new Intent(SelectDestinationActivity.this, NewTripActivity.class); // این خط تغییر کرد
                intent.putExtra("source_lat", sourceLatLng.latitude);
                intent.putExtra("source_lng", sourceLatLng.longitude);
                intent.putExtra("destination_lat", destinationLatLng.latitude);
                intent.putExtra("destination_lng", destinationLatLng.longitude);
                startActivity(intent);
                // finish(); // اگر لازم بود
            } else {
                Toast.makeText(this, "لطفاً مقصد را انتخاب کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("مبدا"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.4279, 53.6880), 5.5f));
        mMap.setLatLngBoundsForCameraTarget(IRAN_BOUNDS);

        mMap.setOnMapClickListener(latLng -> {
            if (IRAN_BOUNDS.contains(latLng)) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("مبدا"));
                destinationLatLng = latLng;
                mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("مقصد"));

                PolylineOptions polylineOptions = new PolylineOptions()
                        .add(sourceLatLng)
                        .add(destinationLatLng)
                        .color(0xFF00796B)
                        .width(8);
                mMap.addPolyline(polylineOptions);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 14));

                // گرفتن آدرس فارسی مقصد و نمایش در EditText
                Geocoder geocoder = new Geocoder(this, new Locale("fa"));
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        etDestAddress.setText(addresses.get(0).getAddressLine(0));
                    } else {
                        etDestAddress.setText("آدرس پیدا نشد");
                    }
                } catch (IOException e) {
                    etDestAddress.setText("خطا در دریافت آدرس");
                }
            } else {
                Toast.makeText(this, "لطفاً نقطه‌ای داخل ایران انتخاب کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
