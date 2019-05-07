package ie.mycit.weiliu.lshub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class SignupActivity extends AppCompatActivity {

    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private VideoView videoview;
    private Uri uri;
    SpotsDialog progress;
    PrimaryDrawerItem signupView = null;
    String SpinnerText;
    List<String> accountType;
    int isProvider = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        // Handle Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        videoview = (VideoView) findViewById(R.id.videoView2);
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
        Button signupBtn = findViewById(R.id.signupBtnPage);
        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        PrimaryDrawerItem login = null;
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
        } else {
            profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
            login = new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
        }

        headerResult = accountHeaderHelper.header(this,savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView, null);

        result.getActionBarDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.getActionBarDrawerToggle().getToolbarNavigationClickListener().onClick(view);
                Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
                hideKeyboard(view);
            }
        });

        final EditText firstName = (EditText)findViewById(R.id.firstName);
        final EditText lastName = (EditText)findViewById(R.id.surName);
        final EditText email = (EditText)findViewById(R.id.emailSignUp);
        final EditText phoneNo = (EditText)findViewById(R.id.phoneNo);
        final EditText password = (EditText)findViewById(R.id.passwordSignUp);
        final EditText username = findViewById(R.id.usernameSignUp);
        final Spinner spinner = findViewById(R.id.spinnerAccount);


        accountType = new ArrayList<String>();
        accountType.add("Choose an account type:");
        accountType.add("User");
        accountType.add("Provider");
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item ,accountType){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    SpinnerText = selectedItemText;
                    isProvider = position - 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
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
                            String FirstName = firstName.getText().toString();
                            String LastName = lastName.getText().toString();
                            String Email = email.getText().toString();
                            String PhoneNo = phoneNo.getText().toString();

                            String result1 = POSTRequest(UserName, Password, FirstName, LastName, Email,PhoneNo, isProvider );
                            System.out.println("------------"+ result + "-------------");
                            if( result1 == null)
                            {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(SignupActivity.this.getApplicationContext(), "Error occurred/User exists, Please check input", Toast.LENGTH_LONG).show();
                                        hideLoadingAnimation();

                                    }
                                });

                            }else {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        Toast.makeText(SignupActivity.this.getApplicationContext(), "Success, Account created", Toast.LENGTH_LONG).show();
                                        hideLoadingAnimation();

                                        headerResult.addProfiles(new ProfileDrawerItem().withName("Hi "+UserName).withIcon(getResources().getDrawable(R.drawable.profile3)));
                                        PreferenceUtils.saveEmail(username.getText().toString(), SignupActivity.this);
                                        PreferenceUtils.savePassword(password.getText().toString(), SignupActivity.this);
                                        signupView.withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100).withSelectable(false);
                                        result.updateItem(signupView);
                                        username.getText().clear();
                                        password.getText().clear();
                                        firstName.getText().clear();
                                        lastName.getText().clear();
                                        email.getText().clear();
                                        phoneNo.getText().clear();
                                        Intent logged = new Intent(SignupActivity.this.getApplicationContext(), MainActivity.class);
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

        username.setFocusableInTouchMode(false);
        username.setFocusable(false);
        username.setFocusableInTouchMode(true);
        username.setFocusable(true);


        //only set the active selection or active profile if we do not recreate the activity


    }
    void showLoadingAnimation(String loadingMessage){
        //ProgressDialog.show(this, "Loading", "Wait while loading...");
        progress = new SpotsDialog(this,  R.style.Custom);
        progress.setTitle("Loading");
        progress.setMessage(loadingMessage);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

    }
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    void hideLoadingAnimation(){
        // dismiss the dialog
        progress.dismiss();

    }
    public String POSTRequest(String UserName, String Password,String FirstName,String LastName,String Email, String PhoneNo, int isProvider) throws IOException {

        final String POST_PARAMS = "{\n" +
                "    \"UserName\": \""+UserName+"\",\r\n" +
                "    \"Password\": "+ Password +",\r\n" +
                "    \"FirstName\": \""+FirstName+"\",\r\n" +
                "    \"LastName\": \""+LastName+"\",\r\n" +
                "    \"PhoneNo\": \""+PhoneNo+"\",\r\n" +
                "    \"isProvider\": \""+isProvider+"\",\r\n" +
                "    \"Email\": \""+Email+"\"" + "\n}";
        System.out.println(POST_PARAMS);
        URL obj = new URL("https://lshapi.azurewebsites.net/createNewAccount");
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
            System.out.println(response.toString());
            return response.toString();

        } else {
            System.out.println("POST NOT WORKED");
            return null;
        }
    }
    //------------------------------------------------------------------------------------------------------------------------
    public void hideKeyboard(View view) {
        Log.e("", "hideKeyboard: ");
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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
    }

}
