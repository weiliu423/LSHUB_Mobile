package ie.mycit.weiliu.lshub;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
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

import dmax.dialog.SpotsDialog;
import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class ListOfCategoryActivity extends AppCompatActivity {

    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private VideoView videoview;
    private Uri uri;
    SpotsDialog progress;
    JSONArray Data;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        ListView serviceLists = findViewById(R.id.serviceLists1);

        showLoadingAnimation("Loading services, Please Wait, Thank you");
        Thread thread = new Thread(new Runnable(){
            public void run() {
                list = new ArrayList<String>();
                try {
                    Data = GetRequest();
                    for (int i = 0; i < Data.length(); i++) {
                        String data = Data.getString(i);
                        list.add(data);
                    }

                    if(!list.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter adapter = new ArrayAdapter<String>(ListOfCategoryActivity.this, R.layout.listtextsize, list);
                                serviceLists.setAdapter(adapter);
                                hideLoadingAnimation();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Error loading data!", Toast.LENGTH_LONG)
                                        .show();
                                hideLoadingAnimation();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        serviceLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String name = serviceLists.getAdapter().getItem(position).toString();
                    Bundle b = new Bundle();
                    b.putString("Name", name);
                    Intent loadService = new Intent(ListOfCategoryActivity.this.getApplicationContext(), serviceListActivity.class);
                    loadService.putExtras(b);
                    startActivity(loadService);



            }
        });

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

        headerResult = accountHeaderHelper.header(this,savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView, addServiceView);
    }

    public JSONArray GetRequest() throws IOException {
        //ArrayList<String> list = new ArrayList<String>();
        JSONArray Data = new JSONArray();
        URL obj = new URL("https://serviceinfo.azurewebsites.net/getAllCategories");
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

            //list.add("test1");
            return Data;

        } else {
            return Data;
        }
    }
    void showLoadingAnimation(String loadingMessage){
        //ProgressDialog.show(this, "Loading", "Wait while loading...");
        progress = new SpotsDialog(this,  R.style.Custom);
        progress.setTitle("Loading");
        progress.setMessage(loadingMessage);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

    }

    void hideLoadingAnimation(){
        // dismiss the dialog
        progress.dismiss();

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


}