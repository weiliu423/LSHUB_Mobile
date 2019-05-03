package ie.mycit.weiliu.lshub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class uploadServiceActivity extends AppCompatActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    //Creating view variables
    private Button bSelectImage;
    private ImageView ivLogoWiseL;
    private ProgressBar spinner;
    private VideoView videoview;
    private Uri uri;
    SpotsDialog progress;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private static final int PROFILE_SETTING = 100000;
    String CLOUDINARY_UPLOAD_PRESET = "lshserviceupload";
    String CLOUDINARY_UPLOAD_URL = "https://api.cloudinary.com/v1_1/predator423/image/upload/";
    Uri imageUri;
    Intent intent;
    String imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);


        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        //Connecting the view variables with xml views
        bSelectImage = (Button) findViewById(R.id.bSelectImage);
        ivLogoWiseL = (ImageView) findViewById(R.id.ivLogoWiseL);
        videoview = (VideoView) findViewById(R.id.videoView3);
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
        //Setting click listener on button
        bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(uploadServiceActivity.this, SelectImageActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });
        Intent in = getIntent();
        imageUri = in.getData();
        System.out.println("VVVVVVVV: "+ imageUri);
        final Spinner spinner = findViewById(R.id.spinner);

        // Spinner click listener
        //spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("Select an item...");
        categories.add("Course");
        categories.add("Tutor");
        categories.add("Repairs");

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item ,categories){
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
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        PrimaryDrawerItem login = null;
        PrimaryDrawerItem signupView = null;
        PreferenceUtils utils = new PreferenceUtils();
        LinearLayout mainLayout = findViewById(R.id.loginCheck);
        TextView checkService = findViewById(R.id.checkService);
        LinearLayout uploadLayout = findViewById(R.id.uploadLayout);
        Button redirectLogin = findViewById(R.id.redirectLogin);
        IProfile profile = new ProfileDrawerItem();
        if (utils.getEmail(this) != null ){
            profile = new ProfileDrawerItem().withName("Hi "+utils.getEmail(this)).withIcon(getResources().getDrawable(R.drawable.profile3));
            signupView = new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100).withSelectable(false);
            mainLayout.setVisibility(LinearLayout.GONE);
        }else{
            profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
            login = new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false);
            signupView = new PrimaryDrawerItem().withName("Sign up").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2).withSelectable(false);
            checkService.setText("Please login first before upload your services!");
            ivLogoWiseL.setVisibility(ImageView.GONE);
            mainLayout.setVisibility(LinearLayout.VISIBLE);
            uploadLayout.setVisibility(LinearLayout.GONE);
        }
        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(uploadServiceActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        headerResult = accountHeaderHelper.header(this,savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView);

        Button ServiceUploadBtn = findViewById(R.id.ServiceUploadBtn);
        ServiceUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingAnimation("Loading Please wait!");
                try{
                    POSTRequest();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    public String POSTRequest() throws IOException {
        Map config = new HashMap();
        config.put("cloud_name", "predator423");
        MediaManager.init(this, config);
        MediaManager.get().upload(imageUri)
                .unsigned(CLOUDINARY_UPLOAD_PRESET).callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // your code here
                    }
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // example code starts here
                        Double progress = (double) bytes/totalBytes;
                        // post progress to app UI (e.g. progress bar, notification)
                        // example code ends here
                    }
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        try {
                            JSONObject mainObject = new JSONObject(resultData);
                            imgUrl = mainObject.getString("secure_url");
                            System.out.println("WWWWWWWWWWWWWW: " + imgUrl);
                            hideLoadingAnimation();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        // your code here
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // your code here
                    }})
                .dispatch();


        /*final String POST_PARAMS = "{\n" +
                "    \"upload_preset\": \""+CLOUDINARY_UPLOAD_PRESET+"\",\r\n" +
                "    \"file\": \""+imageUri+"\"" + "\n}";
        System.out.println("TTTTTTTTTTTTT: "+POST_PARAMS);
        URL obj = new URL(CLOUDINARY_UPLOAD_URL);
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
            hideLoadingAnimation();
            return response.toString();

        } else {
            System.out.println("POST NOT WORKED");
            hideLoadingAnimation();
            return null;
        }*/
       return imgUrl;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checking the requestCode and resultCode to perform a specific task
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK){
            // If image is selected successfully, set the image URI.
            imageUri = data.getData();
            if (imageUri != null) {
                // Show the image on screen.
                ivLogoWiseL.setImageURI(imageUri);
            }
        }
    }
    public void hideKeyboard(View view) {
        Log.e("", "hideKeyboard: ");
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(videoview!=null){
            videoview = (VideoView) findViewById(R.id.videoView3);
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
