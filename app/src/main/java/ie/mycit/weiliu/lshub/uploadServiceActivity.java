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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class uploadServiceActivity extends AppCompatActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    //Creating view variables
    private Button bSelectImage;
    private ImageView ivLogoWiseL;
    private ProgressBar spinner;
    private VideoView videoview;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_service);

        //Connecting the view variables with xml views
        bSelectImage = (Button) findViewById(R.id.bSelectImage);
        ivLogoWiseL = (ImageView) findViewById(R.id.ivLogoWiseL);
        spinner = (ProgressBar)findViewById(R.id.progressBar3);
        spinner.setVisibility(View.GONE);

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

        /*String requestId = MediaManager.get().upload("dog.mp4")
                .unsigned("preset1")
                .option("resource_type", "video")
                .option("folder", "my_folder/my_sub_folder/")
                .option("public_id", "my_dog")
                .option("overwrite", true)
                .option("notification_url", "https://mysite.example.com/notify_endpoint")
                .dispatch();*/
    }

    /*private void uploadToServer(String filePath) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Call call = uploadAPIs.uploadImage(part, description);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
            }
            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checking the requestCode and resultCode to perform a specific task
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK){
            // If image is selected successfully, set the image URI.
            Uri imageUri = data.getData();
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
