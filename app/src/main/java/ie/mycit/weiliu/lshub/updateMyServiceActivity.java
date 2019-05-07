package ie.mycit.weiliu.lshub;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ie.mycit.weiliu.lshub.utils.AccountHeaderHelper;
import ie.mycit.weiliu.lshub.utils.DrawerHelper;
import ie.mycit.weiliu.lshub.utils.GPSTracker;
import ie.mycit.weiliu.lshub.utils.PreferenceUtils;

public class updateMyServiceActivity extends AppCompatActivity {

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
    //String CLOUDINARY_UPLOAD_URL = "https://api.cloudinary.com/v1_1/predator423/image/upload/";
    Uri imageUri;
    Intent intent;
    String imgUrl;
    String ServiceTitleEdit;
    String ServiceDescriptionEdit;
    String SpinnerText;
    List<String> categories;
    String validate = "";
    String city = "";
    String country = "";
    Bundle b;
    Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_my_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        PreferenceUtils utils = new PreferenceUtils();
        GPSTracker mGPS = new GPSTracker(this);
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

        if(mGPS.canGetLocation ){
            mGPS.getLocation();
            try {
                List<Address> addresses = geoCoder.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 1);

                if (addresses.size() > 0) {
                    city = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                }

            }catch (IOException e1) {
                e1.printStackTrace();
            }
        }else{
            System.out.println("Unable");
        }
        Intent in = getIntent();
        b = in.getExtras();

        updateMyServiceActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        //Connecting the view variables with xml views
        bSelectImage = (Button) findViewById(R.id.bSelectImage2);
        ivLogoWiseL = (ImageView) findViewById(R.id.ivLogoWiseL2);
        ivLogoWiseL.setImageResource(R.drawable.ic_defaultimg);
        ivLogoWiseL.getLayoutParams().height = 600;
        ivLogoWiseL.getLayoutParams().width = 900;
        ivLogoWiseL.requestLayout();

        videoview =  findViewById(R.id.videoView323);
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
                Intent intent = new Intent(updateMyServiceActivity.this, SelectImageActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });

        EditText ServiceTitle = findViewById(R.id.ServiceTitle2);
        EditText ServiceDescription = findViewById(R.id.ServiceDescription2);

        final Spinner spinner = findViewById(R.id.spinner2);
        categories = new ArrayList<String>();
        categories.add("Select an item...");
        final Thread thread = new Thread(new Runnable(){
            JSONArray Data;
            public void run() {
                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            showLoadingAnimation("Validating, Thank you!");

                        }
                    });

                    Data = GetRequest();
                    if( Data == null)
                    {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Toast.makeText(updateMyServiceActivity.this.getApplicationContext(), "An Error occurred", Toast.LENGTH_LONG).show();
                                hideLoadingAnimation();

                            }
                        });

                    }else {
                        try{
                            for (int i = 0; i < Data.length(); i++) {
                                String data = Data.getString(i);
                                categories.add(data);
                            }
                        }catch (Exception e){

                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                        updateMyServiceActivity.this, android.R.layout.simple_spinner_dropdown_item ,categories){
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
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                hideLoadingAnimation();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        if (b != null) {
            try {
                String Name = b.getString("Name");
                String Description = b.getString("Description");
                String ImageLink = b.getString("ImageLink");
                String CreateDate = b.getString("CreateDate");
                String ServiceLocation1 = b.getString("ServiceLocation");
                String ContactName = b.getString("ContactName");
                Date dateObj = curFormater.parse(CreateDate);
                String newDateStr = curFormater.format(dateObj);
                ServiceTitle.setText(Name);
                ServiceDescription.setText(Description);
                new DownloadImageFromInternet( findViewById(R.id.ivLogoWiseL2))
                        .execute(ImageLink);

            }catch (Exception e){

            }
        }
        //----------------------------------------------------------------------------------------------
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        DrawerHelper drawer = new DrawerHelper();
        AccountHeaderHelper accountHeaderHelper = new AccountHeaderHelper();
        PrimaryDrawerItem login = null;
        PrimaryDrawerItem signupView = null;
        PrimaryDrawerItem addServiceView = null;
        LinearLayout mainLayout = findViewById(R.id.loginCheck2);
        TextView checkService = findViewById(R.id.checkService2);
        LinearLayout uploadLayout = findViewById(R.id.uploadLayout2);
        Button redirectLogin = findViewById(R.id.redirectLogin2);

        //-----------------------------------------------------------------------------------------------
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
        if (utils.getEmail(this) != null ){
            profile = new ProfileDrawerItem().withName("Hi " + utils.getEmail(this)).withIcon(getResources().getDrawable(R.drawable.profile3)).withTextColor(Color.parseColor(utils.getTextColour(this)));
            signupView = new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIconColor(Color.parseColor(utils.getTextColour(this))).withIdentifier(100).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(this)));
            mainLayout.setVisibility(LinearLayout.GONE);
            if(utils.getIsProvider(this).equals("1")){
                addServiceView =  new PrimaryDrawerItem().withName("Add New Service").withIcon(Octicons.Icon.oct_diff_added).withIconColor(Color.parseColor(utils.getTextColour(this))).withIdentifier(11).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(this)));
            }
        }else{
            profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle);
            login = new PrimaryDrawerItem().withName("Login").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
            signupView = new PrimaryDrawerItem().withName("Sign up").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2).withSelectable(false).withIconColor(Color.parseColor(utils.getTextColour(this))).withTextColor(Color.parseColor(utils.getTextColour(this)));
            checkService.setText("Please login first before upload your services!");
            ivLogoWiseL.setVisibility(ImageView.GONE);
            mainLayout.setVisibility(LinearLayout.VISIBLE);
            uploadLayout.setVisibility(LinearLayout.GONE);
        }
        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(updateMyServiceActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        headerResult = accountHeaderHelper.header(this,savedInstanceState);
        headerResult.addProfiles(profile);
        result = drawer.getDrawer(this, savedInstanceState, toolbar, profile, headerResult, login, signupView, addServiceView);

        Button ServiceUpdateBtn = findViewById(R.id.ServiceUpdateBtn);

        ServiceUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingAnimation("Loading Please wait!");
                ServiceTitleEdit = ServiceTitle.getText().toString();
                ServiceDescriptionEdit = ServiceDescription.getText().toString();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Thread thread1 = new Thread(new Runnable(){
                    public void run() {
                        try {
                            if(spinner.getSelectedItem() !=null) {

                                String response = POSTRequest(b.getString("ServiceID"));

                                if (response != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Service successfully updated!", Toast.LENGTH_LONG)
                                                    .show();
                                            finish();
                                            hideLoadingAnimation();
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Failed to update, please check input!", Toast.LENGTH_LONG)
                                                    .show();
                                            hideLoadingAnimation();
                                        }
                                    });
                                }
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Failed to update, please check input!", Toast.LENGTH_LONG)
                                                .show();
                                        hideLoadingAnimation();
                                    }
                                });
                            }
                        }catch (Exception e){
                            System.out.println(e);
                        }
                    }
                });

                thread1.start();
            }
        });


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
    public String POSTRequest(String serviceId) throws IOException {

        PreferenceUtils utils = new PreferenceUtils();
        if(imageUri == null) {
            try{
                final String POST_PARAMS = "{\n" +
                        "    \"ServiceID\": \"" + serviceId + "\",\r\n" +
                        "    \"Name\": \"" + ServiceTitleEdit + "\",\r\n" +
                        "    \"TypeName\": \"" + SpinnerText + "\",\r\n" +
                        "    \"Description\": \"" + ServiceDescriptionEdit + "\",\r\n" +
                        "    \"ImageLink\": \"" + b.getString("ImageLink") + "\",\r\n" +
                        "    \"AccountName\": \"" + utils.getEmail(updateMyServiceActivity.this) + "\",\r\n" +
                        "    \"ServiceLocation\": \"" + city + ", " + country + "\"" + "\n}";

                System.out.println("TTTTTTTTTTTTT: " + POST_PARAMS);
                URL obj = new URL("https://serviceinfo.azurewebsites.net/updateService");
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
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    // print result
                    System.out.println(response.toString());
                    validate = "true";

                } else {
                    System.out.println("POST NOT WORKED");
                    validate = null;
                }

            } catch (Exception o){
                o.printStackTrace();

            }
        }else {
            MediaManager.get().upload(imageUri)
                    .unsigned(CLOUDINARY_UPLOAD_PRESET).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    // your code here
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    // example code starts here
                    Double progress = (double) bytes / totalBytes;
                    // post progress to app UI (e.g. progress bar, notification)
                    // example code ends here
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    try {
                        JSONObject mainObject = new JSONObject(resultData);
                        imgUrl = mainObject.getString("secure_url");

                        final String POST_PARAMS = "{\n" +
                                "    \"ServiceID\": \"" + serviceId + "\",\r\n" +
                                "    \"Name\": \"" + ServiceTitleEdit + "\",\r\n" +
                                "    \"TypeName\": \"" + SpinnerText + "\",\r\n" +
                                "    \"Description\": \"" + ServiceDescriptionEdit + "\",\r\n" +
                                "    \"ImageLink\": \"" + imgUrl + "\",\r\n" +
                                "    \"AccountName\": \"" + utils.getEmail(updateMyServiceActivity.this) + "\",\r\n" +
                                "    \"ServiceLocation\": \"" + city + ", " + country + "\"" + "\n}";

                        System.out.println("TTTTTTTTTTTTT: " + POST_PARAMS);
                        URL obj = new URL("https://serviceinfo.azurewebsites.net/updateService");
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
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            // print result
                            System.out.println(response.toString());
                            validate = "true";

                        } else {
                            System.out.println("POST NOT WORKED");
                            validate = null;
                        }

                    } catch (Exception e) {
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
                }
            })
                    .dispatch();
        }

        return validate;
    }
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take few second to load", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
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
            videoview = (VideoView) findViewById(R.id.videoView323);
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}