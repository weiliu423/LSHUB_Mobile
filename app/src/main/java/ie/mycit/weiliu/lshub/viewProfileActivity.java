package ie.mycit.weiliu.lshub;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;
import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class viewProfileActivity extends AppCompatActivity {
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private VideoView videoview;
    private Uri uri;
    SpotsDialog progress;
    String Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        PrimaryDrawerItem login = null;
        PrimaryDrawerItem signupView = null;
        PrimaryDrawerItem addServiceView = null;
        PreferenceUtils utils = new PreferenceUtils();


        TextView user_profile_name = findViewById(R.id.user_profile_name);
        user_profile_name.setText(PreferenceUtils.getEmail(this));
        RelativeLayout profile_layout_relative = findViewById(R.id.profile_layout_relative);
        LinearLayout mainLayout = findViewById(R.id.loginCheck122);
        Button redirectLogin = findViewById(R.id.redirectLogin122);

        TextView user_full_name = findViewById(R.id.user_full_name);
        TextView user_accountname = findViewById(R.id.user_accountname);
        TextView user_createDate = findViewById(R.id.user_createDate);
        TextView user_email = findViewById(R.id.user_email);
        TextView user_phoneNo = findViewById(R.id.user_phoneNo);


        if (utils.getEmail(this) != null) {
                showLoadingAnimation("Loading info, Please Wait, Thank you");
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            Data = GetRequest();
                            if (Data == null || Data.length() == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        profile_layout_relative.setVisibility(View.INVISIBLE);
                                        hideLoadingAnimation();
                                    }
                                });

                            } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String accountName = Data.split(":")[0].replace("[\"","");
                                            String createDate = Data.split(":")[1];
                                            String fullName = Data.split(":")[2];
                                            String email = Data.split(":")[3];
                                            String phoneNo = Data.split(":")[4].replace("\"]","");

                                            user_full_name.setText("Full Name: " + fullName);
                                            user_accountname.setText("Account Name: "+accountName);
                                            user_createDate.setText("Registered Date: "+createDate);
                                            user_email.setText("Email: "+email);
                                            user_phoneNo.setText("Phone Number: "+phoneNo);
                                            Toast.makeText(getApplicationContext(),
                                                    "Data loaded successfully", Toast.LENGTH_LONG)
                                                    .show();
                                            hideLoadingAnimation();
                                        }
                                    });
                                }
                        } catch (Exception e) {
                            e.printStackTrace();
                            hideLoadingAnimation();
                        }
                    }
                });

                thread.start();
        }else {
            profile_layout_relative.setVisibility(View.INVISIBLE);
            mainLayout.setVisibility(View.VISIBLE);
        }


        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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

    public String GetRequest() throws IOException {
        //ArrayList<String> list = new ArrayList<String>();
        String Data = new String();
        URL obj = new URL("https://lshapi.azurewebsites.net/getUserByName/"+PreferenceUtils.getEmail(this));
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
                Data = mainObject.getString("Data");

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
