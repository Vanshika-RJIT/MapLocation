package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;

    private List<AutocompletePrediction> predictionList;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;


    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;


    private final float Default_zoom=18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        materialSearchBar =findViewById(R.id.searchBar);
        btnFind=findViewById(R.id.button1);
        btnFind=findViewById(R.id.button2);

        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
      mapFragment.getMapAsync(this);
      mapView = mapFragment.getView();
      mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this,"AIzaSyDs4jcH9Qv3Csg_SBazOA6rdSExSjdiYX8");
        placesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
     mMap=googleMap;
     mMap.setMyLocationEnabled(true);
     mMap.getUiSettings().setMyLocationButtonEnabled(true);
     if(mapView!=null && mapView.findViewById(Integer.parseInt("1"))!=null)
     {
         View locationButton=((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
         RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
         layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
         layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
         layoutParams.setMargins(0,0,40,180);

     }
        LocationRequest locationRequest= LocationRequest.create();
     locationRequest.setInterval(10000);
     locationRequest.setFastestInterval(5000);
     locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient= LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task =settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });
        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              if(e instanceof ResolvableApiException)
              {
                  ResolvableApiException resolvable= (ResolvableApiException) e;

                      try {
                          resolvable.startResolutionForResult(MapActivity.this, 51);
                      } catch (IntentSender.SendIntentException ex) {
                          ex.printStackTrace();
                      }
                  }
              }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==51)
        {
            if(resultCode==RESULT_OK)
            {
                getDeviceLOcation();
            }
        }
    }
    @SuppressLint("Missing permission")
    private void getDeviceLOcation()
    {
      mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
          @Override
          public void onComplete(@NonNull Task<Location> task) {
              if (task.isSuccessful())
                  mLastKnownLocation = task.getResult();
              if (mLastKnownLocation != null) {
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), Default_zoom));
              } else {
                  LocationRequest locationRequest = LocationRequest.create();
                  locationRequest.setInterval(10000);
                  locationRequest.setFastestInterval(5000);
                  locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                  locationCallback = new LocationCallback() {
                      @Override
                      public void onLocationResult(LocationResult locationResult) {
                          super.onLocationResult(locationResult);
                          if (locationResult == null) {
                              return;

                          }
                          mLastKnownLocation = locationResult.getLastLocation();
                          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), Default_zoom));
                      }
                  };

                  mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);


              }
          }
      });
    }
}
