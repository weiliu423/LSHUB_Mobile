package ie.mycit.weiliu.lshub;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.ListViewAdapter;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final int PROFILE_SETTING = 100000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private VideoView videoview;
    private Uri uri;
    //---------------------------------------
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    JSONArray Data;
    ArrayList<String> arraylist = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //--------------------------------------------------------------------------------------------
        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        checkLocationPermission();
        LinearLayout mainLayout = findViewById(R.id.loginBar);
        Intent i = getIntent();
        try {
            //---------------------------------------------------------------------------------------------------------
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
                utils.saveTextColour("#000000",MainActivity.this);
            }
            if(utils.getColour(this)==null){
                utils.saveColour("#ffffff",MainActivity.this);
            }

            if (utils.getEmail(this) != null) {
                profile = new ProfileDrawerItem().withName("Hi " + utils.getEmail(this)).withIcon(getResources().getDrawable(R.drawable.profile3)).withTextColor(Color.parseColor(utils.getTextColour(this)));
                signupView = new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIconColor(Color.parseColor(utils.getTextColour(this))).withIdentifier(100).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(this)));
                if(utils.getIsProvider(this).equals("1")){
                    addServiceView =  new PrimaryDrawerItem().withName("Add New Service").withIcon(Octicons.Icon.oct_diff_added).withIconColor(Color.parseColor(utils.getTextColour(this))).withIdentifier(11).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(this)));
                }
                mainLayout.setVisibility(LinearLayout.GONE);
            } else {
                PreferenceUtils.savePassword(null, this);
                PreferenceUtils.saveEmail(null, this);
                PreferenceUtils.saveIsProvider(null, this);
                profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
                login = new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
                signupView = new PrimaryDrawerItem().withName("Sign up").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
                mainLayout.setVisibility(LinearLayout.VISIBLE);
            }

            headerResult = accountHeaderHelper.header(this, savedInstanceState);
            headerResult.addProfiles(profile);
            result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView, addServiceView);
            //result = getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView);
            //--------------------------------------------------------------------------------------------------------
            videoview = (VideoView) findViewById(R.id.videoView);
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.star1);
            //videoview.setVideoPath();
            videoview.setVideoURI(uri);
            videoview.start();
            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });
            videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    videoview.setVideoURI(uri);
                    videoview.start();
                }
            });
            Button loginBtn = findViewById(R.id.loginBtn);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signInIntent = new Intent(MainActivity.this.getApplicationContext(), LoginActivity.class);
                    MainActivity.this.startActivity(signInIntent);
                }
            });
            Button course = findViewById(R.id.btn1);
            Button Tutor = findViewById(R.id.btn2);
            Button Repairs = findViewById(R.id.btn3);
            Button Travel = findViewById(R.id.btn4);
            Button Wellness = findViewById(R.id.btn5);
            Button Electrician = findViewById(R.id.btn6);
            Button ChildCare = findViewById(R.id.btn7);
            Button Cleaning = findViewById(R.id.btn8);
            Button Cooks = findViewById(R.id.btn9);
            Button Electrician1 = findViewById(R.id.btn10);
            Button Painters = findViewById(R.id.btn11);
            Button Plumber = findViewById(R.id.btn12);
            list = (ListView) findViewById(R.id.searchList);
            course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Courses");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Tutor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Tutors");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Repairs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Repairs");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Travel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Travel");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Wellness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Wellness");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Electrician.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Electrician");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            ChildCare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "ChildCare");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Cleaning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Cleaning");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Cooks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Cooks");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Electrician1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Electrician");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Painters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Painters");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            Plumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("Name", "Plumber");
                    Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                    coursepage.putExtras(b);
                    startActivity(coursepage);
                }
            });
            //---------------------------------------------------------------------------------------------

            final Thread thread = new Thread(new Runnable() {

                public void run() {
                    try {
                        Data = GetRequest();
                        System.out.println("Data array ----------------- " + Data.toString());
                        if (Data == null) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    Toast.makeText(MainActivity.this.getApplicationContext(), "No service for search!", Toast.LENGTH_LONG).show();

                                }
                            });

                        } else {
                            try {
                                for (int i = 0; i < Data.length(); i++) {
                                    JSONObject data = Data.getJSONObject(i);
                                    JSONArray serviceInfo = data.getJSONArray("serviceInfo");
                                    for (int j = 0; j < serviceInfo.length(); j++) {
                                        JSONObject actor = serviceInfo.getJSONObject(j);
                                        String Name = actor.getString("Name");
                                        arraylist.add(Name);
                                    }
                                }
                            } catch (Exception e) {

                            }
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    adapter = new ListViewAdapter(MainActivity.this, arraylist);

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

            // Pass results to ListViewAdapter Class
            LinearLayout homeLayout = findViewById(R.id.HomeLayout);
            // Locate the EditText in listview_main.xml
            editsearch = (SearchView) findViewById(R.id.searchBox);
            list.setVisibility(ListView.GONE);
            editsearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        homeLayout.setVisibility(LinearLayout.GONE);
                        list.setVisibility(ListView.VISIBLE);
                    } else {
                        homeLayout.setVisibility(LinearLayout.VISIBLE);
                        list.setVisibility(ListView.GONE);
                    }
                }
            });
            editsearch.setOnQueryTextListener(this);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    JSONObject all = new JSONObject();
                    JSONArray serviceInfo = new JSONArray();
                    String ContactName = "";
                    String ContactEmail = "";
                    String ContactNo = "";
                    try {
                        System.out.println("Data array ----------------- " + Data.length());
                        for (int i = 0; i < Data.length(); i++) {
                            JSONObject data = Data.getJSONObject(i);
                            serviceInfo = data.getJSONArray("serviceInfo");
                            ContactName = data.getString("ContactName");
                            ContactEmail = data.getString("ContactEmail");
                            ContactNo = data.getString("ContactNo");
                            for (int j = 0; j < serviceInfo.length(); j++) {
                                all = serviceInfo.getJSONObject(j);
                                String name = all.getString("Name");
                                System.out.println("Name is ----------------- " + name);
                                if (name.equals(arraylist.get(position))) {
                                    i = Data.length();
                                    j = serviceInfo.length();
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
                        Intent loadService = new Intent(MainActivity.this.getApplicationContext(), serviceInfoActivity.class);
                        loadService.putExtras(b);
                        startActivity(loadService);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }catch (Exception e){
        e.printStackTrace();
    }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        return true;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        if(!text.equals("") || !text.isEmpty()) {
            adapter.filter(text);
        }
        return false;
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
                                ActivityCompat.requestPermissions(MainActivity.this,
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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }@Override
    protected void onResume() {
        super.onResume();
        if(videoview!=null){
            videoview = (VideoView) findViewById(R.id.videoView);
            uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.star1);
            //videoview.setVideoPath();
            videoview.setVideoURI(uri);
            videoview.start();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (videoview != null && videoview.isPlaying()) {
            videoview.pause();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*switch (item.getItemId()) {
            case R.id.log_out:
                PreferenceUtils.savePassword(null, this);
                PreferenceUtils.saveEmail(null, this);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

        }
*/
        return super.onOptionsItemSelected(item);
    }



}
