package ie.mycit.weiliu.lshub;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.io.InputStream;

public class serviceInfoActivity extends AppCompatActivity {
    private VideoView videoview;
    private Uri uri;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    String ContactEmail;
    String ContactNo;
    private static final int PROFILE_SETTING = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        videoview = (VideoView) findViewById(R.id.videoView5);
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
        TextView serviceTitle = findViewById(R.id.ServiceTitle1);
        serviceTitle.setBackgroundColor(Color.TRANSPARENT);
        TextView ServiceDescription = findViewById(R.id.ServiceDescription1);

        Intent in = getIntent();
        Bundle b = in.getExtras();
        if (b != null) {
            String Name = b.getString("Name");
            String Description = b.getString("Description");
            String ImageLink = b.getString("ImageLink");
            String CreateDate = b.getString("CreateDate");
            String ContactName = b.getString("ContactName");
            ContactEmail = b.getString("ContactEmail");
            ContactNo = b.getString("ContactNo");

            serviceTitle.setText(Name + "\nby: " + ContactName);
            ServiceDescription.setText(Description);
            new DownloadImageFromInternet((ImageView) findViewById(R.id.image_view))
                    .execute(ImageLink);


        }
        Button contactUs = findViewById(R.id.ServiceUploadBtn);

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] type = {"By Email", "By Phone"};
                AlertDialog.Builder builder = new AlertDialog.Builder(serviceInfoActivity.this);
                builder.setTitle("Choose a way to contact");
                builder.setItems(type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{ContactEmail});
                            i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                            i.putExtra(Intent.EXTRA_TEXT, "body of email");
                            try {
                                startActivity(Intent.createChooser(i, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(serviceInfoActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + ContactNo));
                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(intent);
                        }
                    }
                });
                builder.show();
            }
        });
        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100);


        // Create the AccountHeader
         headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .addProfiles(
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Join us").withDescription("Create new Account")
                                .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBar().paddingDp(5)
                                        .colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING)
                        //new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier a add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            int count = 100 + headerResult.getProfiles().size() + 1;
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon(R.drawable.profile5).withIdentifier(count);
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                // headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                headerResult.addProfiles(newProfile);
                            }
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentNavigationBar(true)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withHasStableIds(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new SectionDrawerItem().withName("Account").withDivider(false),
                        new PrimaryDrawerItem().withName("Login").withDescription("Access more features").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(1).withSelectable(false),
                        new SectionDrawerItem().withName("My dashboard"),
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(2).withSelectable(false),
                        new ExpandableDrawerItem().withName("Dashboard").withIcon(GoogleMaterial.Icon.gmd_dashboard).withIdentifier(19).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("Add my service").withLevel(2).withIcon(Octicons.Icon.oct_tools).withIdentifier(2001).withSelectable(false),
                                new SecondaryDrawerItem().withName("View my profile").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_supervisor_account).withIdentifier(2002).withSelectable(false),
                                new SecondaryDrawerItem().withName("View my reviews").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_rate_review).withIdentifier(2003).withSelectable(false)
                        ),
                        new SectionDrawerItem().withName("Contact us"),
                        new PrimaryDrawerItem().withName("Chat with us").withIcon(GoogleMaterial.Icon.gmd_report_problem).withIdentifier(20).withSelectable(false),
                        new PrimaryDrawerItem().withName("Feedback").withIcon(GoogleMaterial.Icon.gmd_feedback).withIdentifier(21).withSelectable(false).withTag("Bullhorn"),
                        new SectionDrawerItem().withName("Services"),
                        new PrimaryDrawerItem().withName("View services").withIcon(Octicons.Icon.oct_tools).withIdentifier(22).withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Location").withIcon(Octicons.Icon.oct_location).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)
                        /*,
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Switch").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Switch2").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener).withSelectable(false),
                        new ToggleDrawerItem().withName("Toggle").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
                        new DividerDrawerItem(),
                        new SecondarySwitchDrawerItem().withName("Secondary switch").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SecondarySwitchDrawerItem().withName("Secondary Switch2").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener).withSelectable(false),
                        new SecondaryToggleDrawerItem().withName("Secondary toggle").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)
                        */
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(serviceInfoActivity.this, LoginActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(serviceInfoActivity.this, MainActivity.class);
                            } /*else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(DrawerActivity.this, MultiDrawerActivity.class);
                            } else if (drawerItem.getIdentifier() == 20) {
                                intent = new LibsBuilder()
                                        .withFields(R.string.class.getFields())
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .intent(DrawerActivity.this);
                            }*/
                            if (intent != null) {
                                startActivity(intent);
                                finish();
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                //.withShowDrawerUntilDraggedOpened(true)
                .build();

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(1, false);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }else{
            headerResult.setActiveProfile(profile);
        }

        result.updateBadge(4, new StringHolder(10 + ""));


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

}
