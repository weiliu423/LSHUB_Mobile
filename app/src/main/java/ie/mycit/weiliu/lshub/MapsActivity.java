package ie.mycit.weiliu.lshub;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.GPSTracker;
import ie.mycit.weiliu.lshub.utils.ListViewAdapter;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class MapsActivity extends refreshActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private VideoView videoview;
    private Uri uri;
    ListView list;
    ListViewAdapter adapter;
    JSONArray Data;
    ArrayList<String> arraylist = new ArrayList<String>();
    String city = "";
    String country = "";
    List<Address> addresses;
    Geocoder geoCoder;


    private final String TAG = "LSH";

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar40);
        toolbar.setTitle("LSH");
        setSupportActionBar(toolbar);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        checkLocationPermission();

        GPSTracker mGPS = new GPSTracker(this);
        geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
        if (mGPS.canGetLocation) {
            mGPS.getLocation();
            try {
               addresses = geoCoder.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 1);

                if (addresses.size() > 0) {
                    city = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            System.out.println("Unable");
        }
        //----------------------------------------------------------------------------------------------
        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        PrimaryDrawerItem login = null;
        PrimaryDrawerItem signupView = null;
        PrimaryDrawerItem addServiceView = null;
        PreferenceUtils utils = new PreferenceUtils();
        IProfile profile = new ProfileDrawerItem();
        if(utils.getBarColour(this) != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(utils.getBarColour(this))));
        }else{
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0091EA")));
        }
        if(utils.getTextColour(this) == null) {
            utils.saveTextColour("#000000",this);
        }
        if(utils.getColour(this)==null){
            utils.saveColour("#ffffff",this);
        }
        if (utils.getEmail(this) != null) {
            profile = new ProfileDrawerItem().withName("Hi " + utils.getEmail(this)).withIcon(getResources().getDrawable(R.drawable.profile3)).withTextColor(Color.parseColor(utils.getTextColour(this)));
            signupView = new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIconColor(Color.parseColor(utils.getTextColour(this))).withIdentifier(100).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(this)));
            if(utils.getIsProvider(this).equals("1")){
                addServiceView =  new PrimaryDrawerItem().withName("Add New Service").withIcon(Octicons.Icon.oct_diff_added).withIconColor(Color.parseColor(utils.getTextColour(this))).withIdentifier(11).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(this)));
            }
        } else {
            profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
            login = new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
            signupView = new PrimaryDrawerItem().withName("Sign up").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
        }
        headerResult = accountHeaderHelper.header(this, savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView, addServiceView);
        //----------------------------------------------------------------------------------------------
        list = findViewById(R.id.serviceLists2);

        setListViewHeightBasedOnChildren(list);
        final Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    Data = GetRequest();
                    if (Data == null) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                arraylist.add("No service found in your area!");
                                Toast.makeText(MapsActivity.this.getApplicationContext(), "No service for search!", Toast.LENGTH_LONG).show();

                            }
                        });

                    } else {
                        try {
                            int count = 1;
                            for (int i = 0; i < Data.length(); i++) {
                                JSONObject data = Data.getJSONObject(i);
                                JSONArray serviceInfo = data.getJSONArray("serviceInfo");
                                for (int j = 0; j < serviceInfo.length(); j++) {
                                    JSONObject actor = serviceInfo.getJSONObject(j);
                                    String Name = actor.getString("Name");
                                    String Location = actor.getString("ServiceLocation");
                                    if (city.contains(Location.split(", ")[0])) {
                                        arraylist.add(count + ". " + Name + " - " + Location);
                                        count++;
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                adapter = new ListViewAdapter(MapsActivity.this, arraylist);

                                // Binds the Adapter to the ListView
                                list.setAdapter(adapter);

                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Toast.makeText(MapsActivity.this.getApplicationContext(), "You clicked on " + position, Toast.LENGTH_LONG).show();

                JSONObject all = new JSONObject();
                JSONArray serviceInfo = new JSONArray();
                String ContactName = "";
                String ContactEmail = "";
                String ContactNo = "";
                try {
                    System.out.println("Data array ----------------- " + Data.length());
                    int count = 1;
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject data = Data.getJSONObject(i);
                        serviceInfo = data.getJSONArray("serviceInfo");
                        ContactName = data.getString("ContactName");
                        ContactEmail = data.getString("ContactEmail");
                        ContactNo = data.getString("ContactNo");
                        for (int j = 0; j < serviceInfo.length(); j++) {
                            all = serviceInfo.getJSONObject(j);
                            String name = all.getString("Name");
                            String Location = all.getString("ServiceLocation");
                            if ((city.contains(Location.split(", ")[0]) || Location.split(", ")[0].contains(city))) {
                                String valdateName = count + ". " + name + " - " + Location;
                                count++;
                                if (valdateName.equals(arraylist.get(position))) {
                                    i = Data.length();
                                    j = serviceInfo.length();
                                }
                            }
                        }
                    }

                    Bundle b = new Bundle();
                    b.putString("Name", all.getString("Name"));
                    b.putString("Description", all.getString("Description"));
                    b.putString("ImageLink", all.getString("ImageLink"));
                    b.putString("CreateDate", all.getString("CreateDate"));
                    b.putString("ServiceLocation", all.getString("ServiceLocation"));
                    b.putString("ContactName", ContactName);
                    b.putString("ContactEmail", ContactEmail);
                    b.putString("ContactNo", ContactNo);
                    Intent loadService = new Intent(MapsActivity.this.getApplicationContext(), serviceInfoActivity.class);
                    loadService.putExtras(b);
                    startActivity(loadService);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG,"Location received: " + location.toString());
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location")
                        .setMessage("Required")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    public JSONArray GetRequest() throws IOException {
        //ArrayList<String> list = new ArrayList<String>();
        JSONArray Data = new JSONArray();
        URL obj = new URL("https://serviceinfo.azurewebsites.net/getAllServices");
        HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setUseCaches(false);
        httpConnection.setAllowUserInteraction(false);
        httpConnection.setConnectTimeout(100000);
        httpConnection.setReadTimeout(100000);
        httpConnection.connect();
        int responseCode = httpConnection.getResponseCode();
        System.out.println("Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
            BufferedReader in = new BufferedReader(new InputStreamReader( httpConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            try {
                JSONObject mainObject = new JSONObject(response.toString());
                Data = mainObject.getJSONArray("Data");

            } catch (JSONException e) {

            }
            return Data;

        } else {
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GPSTracker mGPS = new GPSTracker(this);
        mMap = googleMap;
        LatLng p1 = null;
        try {
            addresses = geoCoder.getFromLocationName(city + " city, " + country, 5);
            List<LatLng> ll = new ArrayList<LatLng>(addresses.size());
            for(Address location : addresses){
                if(location.hasLatitude() && location.hasLongitude()){
                    if (addresses != null) {
                        p1 = new LatLng(location.getLatitude(), location.getLongitude() );
                        mMap.addMarker(new MarkerOptions().position(p1).title("Service within " + city));
                        mMap.addCircle(new CircleOptions()
                                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                                .radius(50000)
                                .strokeColor(Color.BLACK)
                                .fillColor(0x30ff0000))
                                .setStrokeWidth(1);
                    }
                }
            }

        }catch (Exception e){

        }
        if(mGPS.canGetLocation ){
            mGPS.getLocation();
            LatLng cork = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
            mMap.addMarker(new MarkerOptions().position(cork).title("Your location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mGPS.getLatitude(), mGPS.getLongitude()), 9.0f));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(cork));
        }else{
            System.out.println("Unable");
        }


    }
}
