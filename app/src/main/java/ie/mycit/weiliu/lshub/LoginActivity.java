package ie.mycit.weiliu.lshub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;
import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity {

    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    IProfile profile;
    private static Drawer result = null;
    private VideoView videoview;
    private Uri uri;
    SpotsDialog progress;
    PrimaryDrawerItem signupView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        videoview = (VideoView) findViewById(R.id.videoView1);
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
        Button loginBtn = findViewById(R.id.loginBtnPage);
        PreferenceUtils utils = new PreferenceUtils();
        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
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
        } else {
            profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
            signupView = new PrimaryDrawerItem().withName("Sign up").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
        }

        headerResult = accountHeaderHelper.header(this,savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult,null, signupView, null);

        //Create the drawer


        result.getActionBarDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.getActionBarDrawerToggle().getToolbarNavigationClickListener().onClick(view);
                //Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
                hideKeyboard(view);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Thread thread = new Thread(new Runnable(){
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    showLoadingAnimation("Validating, Thank you!");

                                }
                            });
                            String UserName = username.getText().toString();
                            String Password = password.getText().toString();


                            String results = POSTRequest(UserName, Password);
                            System.out.println("------------"+ result + "-------------");
                            if( results == null)
                            {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(LoginActivity.this.getApplicationContext(), "Error occurred/User doesn't exists, Please check input", Toast.LENGTH_LONG).show();
                                        hideLoadingAnimation();

                                    }
                                });

                            }else {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        //Toast.makeText(LoginActivity.this.getApplicationContext(), "Account Login Successfully", Toast.LENGTH_LONG).show();
                                        hideLoadingAnimation();
                                        // Create the AccountHeaderHelper
                                        headerResult.addProfiles(new ProfileDrawerItem().withName("Hi "+UserName).withIcon(getResources().getDrawable(R.drawable.profile3)));
                                        PreferenceUtils.saveEmail(username.getText().toString(), LoginActivity.this);
                                        PreferenceUtils.savePassword(password.getText().toString(), LoginActivity.this);
                                        PreferenceUtils.saveIsProvider(results.split(":")[1], LoginActivity.this);
                                        username.getText().clear();
                                        password.getText().clear();
                                        signupView.withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(LoginActivity.this)));
                                        result.updateItem(signupView);
                                        Intent logged = new Intent(LoginActivity.this.getApplicationContext(), MainActivity.class);
                                        startActivity(logged);
                                        finish();

                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
                hideKeyboard(v);
            }
        });
        TextView signup = findViewById(R.id.signup);
        SpannableString content = new SpannableString(" Sign up!");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        signup.setText(content);
        signup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent signUpIntent = new Intent(LoginActivity.this.getApplicationContext(), SignupActivity.class);
                startActivity(signUpIntent);
            }
        });
        //-----------------------------------------------------

        username.setFocusableInTouchMode(false);
        username.setFocusable(false);
        username.setFocusableInTouchMode(true);
        username.setFocusable(true);


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
    public String POSTRequest(String UserName, String Password) throws IOException {
        String Data = new String();
        final String POST_PARAMS = "{\n" +
                "    \"UserName\": \""+UserName+"\",\r\n" +
                "    \"Password\": \""+Password+"\"" + "\n}";
        System.out.println(POST_PARAMS);
        URL obj = new URL("https://lshapi.azurewebsites.net/accountValidation");
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        //Header--------------------------------------------------------
        //postConnection.setRequestProperty("userId", "a1bcdefgh");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code :  " + responseCode);
        System.out.println("POST Response Message : " + postConnection.getResponseMessage());
        if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();
            // print result
            try {
                JSONObject mainObject = new JSONObject(response.toString());
                Data = mainObject.getString("Data");

            } catch (JSONException e) {

            }
            return Data;

        } else {
            System.out.println("POST NOT WORKED");
            return null;
        }
    }
    //--------------------------------------------------------------
    public void hideKeyboard(View view) {
        Log.e("", "hideKeyboard: ");
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };*/

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
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(videoview!=null){
            videoview = (VideoView) findViewById(R.id.videoView1);
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
