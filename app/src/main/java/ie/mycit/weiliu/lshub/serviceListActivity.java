package ie.mycit.weiliu.lshub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class serviceListActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_service_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        ListView serviceLists = findViewById(R.id.serviceLists);
        Button uploadServiceBtn = findViewById(R.id.uploadServiceBtn);

        showLoadingAnimation("Loading services, Please Wait, Thank you");
        Intent in = getIntent();
        Bundle b = in.getExtras();
        String Name = b.getString("Name");
        TextView check = findViewById(R.id.checkService);
        check.setVisibility(View.INVISIBLE);
        uploadServiceBtn.setVisibility(View.INVISIBLE);

        Thread thread = new Thread(new Runnable(){
            public void run() {
                        list = new ArrayList<String>();
                        list.add("Select an item");
                        try {
                            Data = GetRequest(Name);
                            if(Data == null || Data.length() == 0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        check.setText("No services submitted, be the first one !");
                                        check.setVisibility(View.VISIBLE);
                                        uploadServiceBtn.setVisibility(View.VISIBLE);
                                        hideLoadingAnimation();
                                    }
                                });

                            }else {
                                for (int i = 0; i < Data.length(); i++) {
                                    JSONObject data = Data.getJSONObject(i);
                                    JSONArray serviceInfo = data.getJSONArray("serviceInfo");
                                    for (int j = 0; j < serviceInfo.length(); j++) {
                                        JSONObject actor = serviceInfo.getJSONObject(j);
                                        String name = actor.getString("Name");
                                        list.add(name);
                                    }
                                }

                                if(!list.isEmpty()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            check.setVisibility(View.INVISIBLE);
                                            ArrayAdapter adapter = new ArrayAdapter<String>(serviceListActivity.this, R.layout.listtextsize, list);
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

                        }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
            }
        });

        thread.start();
        uploadServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadService = new Intent(serviceListActivity.this.getApplicationContext(), uploadServiceActivity.class);
                startActivity(uploadService);
            }
        });
        serviceLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject all = new JSONObject();
                JSONArray serviceInfo = new JSONArray();
                String ContactName = "";
                String ContactEmail = "";
                String ContactNo = "";
                try {
                    for (int i = 0; i < Data.length(); i++) {
                        JSONObject data = Data.getJSONObject(i);
                        serviceInfo = data.getJSONArray("serviceInfo");
                        ContactName = data.getString("ContactName");
                        ContactEmail = data.getString("ContactEmail");
                        ContactNo = data.getString("ContactNo");
                        for (int j = 0; j < serviceInfo.length(); j++) {
                            all = serviceInfo.getJSONObject(j);
                            String name = all.getString("Name");
                            if (name.equals(list.get(position))) {
                                i = Data.length();
                                j=serviceInfo.length();
                            }
                        }
                    }

                    Bundle b = new Bundle();
                    b.putString("Name", all.getString("Name"));
                    b.putString("Description", all.getString("Description"));
                    b.putString("ImageLink", all.getString("ImageLink"));
                    b.putString("CreateDate", all.getString("CreateDate"));
                    b.putString("ContactName", ContactName);
                    b.putString("ContactEmail", ContactEmail);
                    b.putString("ContactNo", ContactNo);
                    Intent loadService = new Intent(serviceListActivity.this.getApplicationContext(), serviceInfoActivity.class);
                    loadService.putExtras(b);
                    startActivity(loadService);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        PrimaryDrawerItem login = null;
        PrimaryDrawerItem signupView = null;
        PreferenceUtils utils = new PreferenceUtils();
        IProfile profile = new ProfileDrawerItem();
        if (utils.getEmail(this) != null ){
            profile = new ProfileDrawerItem().withName("Hi "+utils.getEmail(this)).withIcon(getResources().getDrawable(R.drawable.profile3));
            signupView = new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100).withSelectable(false);
        }else{
            profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
            login = new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false);
            signupView = new PrimaryDrawerItem().withName("Sign up").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2).withSelectable(false);
        }

        headerResult = accountHeaderHelper.header(this,savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView);
    }

    public JSONArray GetRequest(String category) throws IOException {
        //ArrayList<String> list = new ArrayList<String>();
        JSONArray Data = new JSONArray();
        System.out.println("TTTTTTTTTTTTTTTTTT :: " + category);
        URL obj = new URL("https://serviceinfo.azurewebsites.net/getServicesByName/"+ category);
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
            return null;
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
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


}