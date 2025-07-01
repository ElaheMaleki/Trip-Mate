package ir.shariaty.tripmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class SelectDestinationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng destinationLatLng;
    private LatLng sourceLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);

        double srcLat = getIntent().getDoubleExtra("source_lat", 0);
        double srcLng = getIntent().getDoubleExtra("source_lng", 0);
        sourceLatLng = new LatLng(srcLat, srcLng);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_destination);
        mapFragment.getMapAsync(this);

        findViewById(R.id.btn_confirm_destination).setOnClickListener(v -> {
            if (destinationLatLng != null) {
                Intent intent = new Intent(SelectDestinationActivity.this, ResultActivity.class);
                intent.putExtra("source_lat", sourceLatLng.latitude);
                intent.putExtra("source_lng", sourceLatLng.longitude);
                intent.putExtra("destination_lat", destinationLatLng.latitude);
                intent.putExtra("destination_lng", destinationLatLng.longitude);
                startActivity(intent);
            } else {
                Toast.makeText(this, "لطفاً مقصد را انتخاب کنید", Toast.LENGTH_SHORT).show();
            }


    });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // نشان دادن مبدا
        mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("مبدا"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, 14));

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("مبدا"));
            destinationLatLng = latLng;
            mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("مقصد"));

            // رسم خط بین مبدا و مقصد
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(sourceLatLng)
                    .add(destinationLatLng)
                    .color(0xFF00796B) // رنگ سبز تیره
                    .width(8);
            mMap.addPolyline(polylineOptions);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 14));
        });
    }
}

