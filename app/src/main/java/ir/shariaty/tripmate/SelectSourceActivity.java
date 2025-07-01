package ir.shariaty.tripmate;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class SelectSourceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng sourceLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_source);

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
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            sourceLatLng = latLng;
            mMap.addMarker(new MarkerOptions().position(latLng).title("مبدا"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });
    }
}
