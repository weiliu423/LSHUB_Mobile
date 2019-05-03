package ie.mycit.weiliu.lshub;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

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

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private VideoView videoview;
    private Uri uri;
    //---------------------------------------
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;
    String[] animalNameList;
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
        LinearLayout mainLayout = findViewById(R.id.loginBar);
        //---------------------------------------------------------------------------------------------------------
        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        PrimaryDrawerItem login = null;
        PrimaryDrawerItem signupView = null;
        PreferenceUtils utils = new PreferenceUtils();
        IProfile profile = new ProfileDrawerItem();
        if (utils.getEmail(this) != null ){
            profile = new ProfileDrawerItem().withName("Hi "+utils.getEmail(this)).withIcon(getResources().getDrawable(R.drawable.profile3));
            signupView = new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100).withSelectable(false);
            mainLayout.setVisibility(LinearLayout.GONE);
        }else{
            profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
            login = new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false);
            signupView = new PrimaryDrawerItem().withName("Sign up").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2).withSelectable(false);
            mainLayout.setVisibility(LinearLayout.VISIBLE);
        }

        headerResult = accountHeaderHelper.header(this,savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView);

        //--------------------------------------------------------------------------------------------------------

        videoview = (VideoView) findViewById(R.id.videoView);
        uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.star1);
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
        list = (ListView) findViewById(R.id.searchList);
        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                // Storing data into bundle
                b.putString("Name", "Courses");
                Intent coursepage = new Intent(MainActivity.this, serviceListActivity.class);
                coursepage.putExtras(b);
                startActivity(coursepage);
            }
        });
        final Thread thread = new Thread(new Runnable(){

            public void run() {
                try {


                    Data = GetRequest();

                    if( Data == null)
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast.makeText(MainActivity.this.getApplicationContext(), "No service for search!", Toast.LENGTH_LONG).show();

                            }
                        });

                    }else {
                        try{
                            for (int i = 0; i < Data.length(); i++) {
                                JSONObject data = Data.getJSONObject(i);
                                JSONArray serviceInfo = data.getJSONArray("serviceInfo");
                                for (int j = 0; j < serviceInfo.length(); j++) {
                                    JSONObject actor = serviceInfo.getJSONObject(j);
                                    String Name = actor.getString("Name");
                                    arraylist.add(Name);
                                }
                            }
                        }catch (Exception e){

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

        // Locate the ListView in listview_main.xml

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
                    System.out.println("Data array ----------------- "+ Data.length());
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject data = Data.getJSONObject(i);
                        serviceInfo = data.getJSONArray("serviceInfo");
                        ContactName = data.getString("ContactName");
                        ContactEmail = data.getString("ContactEmail");
                        ContactNo = data.getString("ContactNo");
                        for (int j = 0; j < serviceInfo.length(); j++) {
                            all = serviceInfo.getJSONObject(j);
                            String name = all.getString("Name");
                            System.out.println("Name is ----------------- "+ name);
                            if (name.equals(arraylist.get(position))) {
                                i = Data.length();
                                j=serviceInfo.length();
                            }
                        }
                    }
                    System.out.println("ArrayList  ----------------- "+ arraylist.get(position));

                    Bundle b = new Bundle();
                    b.putString("Name", all.getString("Name"));
                    b.putString("Description", all.getString("Description"));
                    b.putString("ImageLink", all.getString("ImageLink"));
                    b.putString("CreateDate", all.getString("CreateDate"));
                    b.putString("ContactName", ContactName);
                    b.putString("ContactEmail", ContactEmail);
                    b.putString("ContactNo", ContactNo);
                    Intent loadService = new Intent(MainActivity.this.getApplicationContext(), serviceInfoActivity.class);
                    loadService.putExtras(b);
                    startActivity(loadService);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
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
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

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
