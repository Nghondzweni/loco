package com.project.loco;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.google.android.libraries.places.api.net.PlacesClient;

public class MapsActivity extends FragmentActivity implements
        View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds  LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168),new LatLng(71, 136));

    //Threads && Handlers
//    private Worker worker;
    private Handler handler;

    //views
    private AutoCompleteTextView mSearchText;
    private TextView t;
    private ImageView mGps;
    private View mapView;
    private Button savePin;
    private EditText loco_name;
    private EditText loco_address;
    private EditText loco_pos;
    private EditText loco_description;
    private PopupWindow popupWindow;
    private View popupView;

    //vars
    private boolean mLocationPermissionGranted = false;
    private LocationViewModel locationViewModel;


    //objects
    private List<LocationData> locations = new ArrayList<>();
    private GoogleMap mMap;
    private Marker mMarker;
    private List<Marker> mMarkersList= new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView auto;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }

        mMap.setOnMarkerClickListener(marker -> {
            marker.remove();
            return true;
        });

        mMap.setOnMapClickListener(latLng -> {
            //for (Marker m : mMarkersList){

            //}
            mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("New Location"));
            mMarkersList.add(mMarker);
            moveCamera(latLng, DEFAULT_ZOOM, "New Location");
            showPopup(mapView, latLng);
        });

    }

    public void savePins(){
        LocationData loco = new LocationData();
        loco.setLatitude(mMarker.getPosition().latitude);
        loco.setLongitude(mMarker.getPosition().longitude);
        loco.setTitle(loco_name.getText().toString());
        loco.setDescription(loco_description.getText().toString());
        loco.setAddress(loco_address.getText().toString());
        locations.add(loco);
        for(LocationData l : locations){
            Log.d(TAG, "NEW LOCATION! : " + locations.toString());
            addLocoLocation(l);
        }
    }

    private void addLocoLocation(LocationData data){
        String latitude = Double.toString(data.getLatitude());
        String longitude = Double.toString(data.getLatitude());
        String name = data.getTitle();
        LocoLocation location = new LocoLocation(name,latitude,longitude);
        locationViewModel.insert(location);
    }

    public void showDatabase(View view){
        Log.d(TAG, "show database called");
        Intent intent = new Intent(this, DatabaseViewer.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        //Views
        auto = findViewById(R.id.input_search);
        t = findViewById(R.id.hint);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.gps);
        mapView = findViewById(R.id.map);

        //Threads && Handlers


        //Initialise PLACES OBJECT FOR API CALL
        String apiKey = getString(R.string.places_api_key);

        if (apiKey.equals("")) {
            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
            return;
        }
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        getLocationPermission();

    }


    private void init(){
        Log.d(TAG, "init: initialising");


        mSearchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAutoCompleteAdapter = new AutoCompleteAdapter(MapsActivity.this, mSearchText.getText().toString(), t);
                mAutoCompleteAdapter.findAutocompletePredictions();
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                    showPopup(mapView, mMap.getCameraPosition().target);
                }
                return false;
            }
        });
        hideSoftKeyboard();
        mGps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick : clicked gps icon");
                getDeviceLocation();
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG, "geo:Locate: Geo Loacting");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
           list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.e(TAG, "geoLocate: found a location: " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: Getting Devices Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if (mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,
                                    "My Location");
                        }
                        else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to find location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLoaction: SecurityException : "+e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to : lat:" + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "intiMap: Initialising map");
        mapFragment.getMapAsync((OnMapReadyCallback) MapsActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting Location Permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        Log.d(TAG, "onRequestPermissionResult: Called");
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = true;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return ;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    //UTIL

    public void showPopup(View anchor, LatLng latLng) {

        View popupView = getLayoutInflater().inflate(R.layout.fragment_new_pin, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        savePin = popupView.findViewById(R.id.save_pin);

        savePin.setOnClickListener(btn ->{
            savePins();
            closePopup(popupWindow);
        });

        loco_name = popupView.findViewById(R.id.location_name);
        loco_address = popupView.findViewById(R.id.location_address);
        loco_pos = popupView.findViewById(R.id.location_pos);
        loco_description = popupView.findViewById(R.id.location_description);

        popupWindow.setFocusable(true);
        ColorDrawable background = new ColorDrawable(000);
        background.setAlpha(50);
        popupWindow.setBackgroundDrawable(background);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        popupWindow.setWidth((int)(width * .8));
        popupWindow.setHeight((int)(height * .8));

        int location[] = new int[2];

        anchor.getLocationOnScreen(location);
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + ((anchor.getWidth() - popupWindow.getWidth()) / 2), location[1]);
        loco_pos.setText(latLng.toString());
    }

    public void closePopup(PopupWindow p){
        p.dismiss();
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public List<LocationData> getLocations(){
        return locations;
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
