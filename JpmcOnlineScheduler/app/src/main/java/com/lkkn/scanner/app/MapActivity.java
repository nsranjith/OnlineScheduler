package com.lkkn.scanner.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.lkkn.scanner.app.WorkFlow.DirectionFinder;
import com.lkkn.scanner.app.WorkFlow.DirectionFinderListener;
import com.lkkn.scanner.app.WorkFlow.Route;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends Fragment implements OnMapReadyCallback,LocationListener,DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    String origin="hi";
    private static String mode="";
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider,destination="hi";
    CountDownTimer countDownTimer;
    private double latitude,longitude;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_maps, container, false);
        getActivity().setTitle("Get Your Path");
        SharedPreferences shared = getActivity().getSharedPreferences("loginData", Context.MODE_PRIVATE);


        if (!shared.getString("m_entered","").equals("yes")) {
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("m_entered","yes");
            editor.commit();

            TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                    TapTarget.forView(view.findViewById(R.id.walk), "Modes of travelling", "Know your walking time and distance from the source to destination")
                            .tintTarget(true)
                            .outerCircleColor(R.color.outer)
                            .targetCircleColor(android.R.color.white)
                            .transparentTarget(true)
                            .outerCircleAlpha(0.96f));
        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

//        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(MapActivity.this);
        destination= getArguments().getString("destination");


        getLocation();
        ImageView imageView=(ImageView)view.findViewById(R.id.maps);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLocationEnabled(getActivity().getBaseContext())) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + destination);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
                else {
                    Toast.makeText(getActivity(), "Please enable GPS", Toast.LENGTH_SHORT).show();
                }

            }
        });



        return view;


    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button walk=(Button)view.findViewById(R.id.walk);
        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination= getArguments().getString("destination");
                mode="walking";
                sendRequest();

            }
        });

        final Button drive=(Button)view.findViewById(R.id.driving);
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination= getArguments().getString("destination");
                mode="driving";
                sendRequest();
            }
        });

    }



    private void getLocation() {

        locationManager = (LocationManager)  getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;
        }
        //You can still do this if you like, you might get lucky:
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            Log.e("TAG", "GPS is on");

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            origin=location.getLatitude()+","+location.getLongitude();
            // Toast.makeText("hello",origin);
            Log.d("NIce",origin);
           // Toast.makeText(getActivity(), "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();

        }
        else{

            //This is what you need:
            Toast.makeText(getActivity(), "Check your Gps...", Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        }

    }private void sendRequest() {

        if (origin.equals("hi")) {
            Toast.makeText(getActivity(), "Failed retrieve Gps Info...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.equals("hi")) {
            Toast.makeText(getActivity(), "Failed retrieve Gps Info...", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin,destination,mode).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String add="";
        mMap = googleMap;
        getLocation();
        LatLng hcmus = new LatLng(latitude, longitude);
        //Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(add)
                .position(hcmus)));

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) view.findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView)  view.findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.des))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
    public void walk(){


        switch (view.getId()) {
            case R.id.walk:

                mode="walking";
                sendRequest();


                // do something
                break;
            case R.id.driving:
                // do something else
                mode="driving";
                sendRequest();
                break;

        }


    }

    @Override
    public void onLocationChanged(Location location) {
//
        //   origin=location.getLatitude()+","+location.getLongitude();
//        Log.d("hi",origin);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}