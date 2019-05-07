package ie.mycit.weiliu.lshub.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.Toolbar;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.octicons_typeface_library.Octicons;

import ie.mycit.weiliu.lshub.ListOfCategoryActivity;
import ie.mycit.weiliu.lshub.LoginActivity;
import ie.mycit.weiliu.lshub.MainActivity;
import ie.mycit.weiliu.lshub.MapsActivity;
import ie.mycit.weiliu.lshub.SignupActivity;
import ie.mycit.weiliu.lshub.contactUsActivity;
import ie.mycit.weiliu.lshub.uploadServiceActivity;
import ie.mycit.weiliu.lshub.viewMyServiceListActivity;
import ie.mycit.weiliu.lshub.viewProfileActivity;

public class DrawerHelper{

    private static Drawer result = null;
    private static AccountHeader headerResult = null;
    static IProfile profile;
    private static final int PROFILE_SETTING = 100000;
    PreferenceUtils utils = new PreferenceUtils();
    Activity _context;
    reloadDrawerActivity reloadDrawerActivity = new reloadDrawerActivity();

    public Drawer getDrawer(Activity context, Bundle savedInstanceState,  Toolbar toolbar, IProfile profile, AccountHeader headerResult, PrimaryDrawerItem item, PrimaryDrawerItem item1, PrimaryDrawerItem item2) {
        //profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100);
        _context = context;
        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(context)
                .withTranslucentNavigationBar(true)
                .withSliderBackgroundColor(Color.parseColor(utils.getColour(context)))
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withHasStableIds(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeaderHelper we created earlier for the header
                .addDrawerItems(
                        new SectionDrawerItem().withName("Account").withDivider(false).withTextColor(Color.parseColor(utils.getTextColour(context))),
                        item,
                        item1,
                        new SectionDrawerItem().withName("My Dashboard").withTextColor(Color.parseColor(utils.getTextColour(context))),
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(3).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(context))),
                        new ExpandableDrawerItem().withName("Dashboard").withIcon(GoogleMaterial.Icon.gmd_dashboard).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(19).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("View My Profile").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_supervisor_account).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(4).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(context)))
                                , new SecondaryDrawerItem().withName("View My Service").withLevel(2).withIcon(Octicons.Icon.oct_tools).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(12).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(context)))
                                //,new SecondaryDrawerItem().withName("View my reviews").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_rate_review).withIdentifier(2003).withSelectable(false)
                        ).withArrowColor(Color.parseColor(utils.getTextColour(context))).withTextColor(Color.parseColor(utils.getTextColour(context))),
                        new SectionDrawerItem().withName("Services").withTextColor(Color.parseColor(utils.getTextColour(context))),
                        new PrimaryDrawerItem().withName("View All Services").withIcon(Octicons.Icon.oct_eye).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(10).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(context))),
                        item2,
                        new PrimaryDrawerItem().withName("Services In Your Area").withIcon(Octicons.Icon.oct_location).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(13).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(context))),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Dark Mode").withIcon(Octicons.Icon.oct_color_mode).withIconColor(Color.parseColor(utils.getTextColour(context))).withChecked(Boolean.parseBoolean(utils.getChecked(context))).withOnCheckedChangeListener(onCheckedChangeListener).withTextColor(Color.parseColor(utils.getTextColour(context))),
                        //new PrimaryDrawerItem().withName("Face Detection").withIcon(Octicons.Icon.oct_tools).withIdentifier(12).withSelectable(false),
                        new SectionDrawerItem().withName("Support").withTextColor(Color.parseColor(utils.getTextColour(context))),
                        new PrimaryDrawerItem().withName("Email Us").withIcon(GoogleMaterial.Icon.gmd_email).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(20).withSelectable(false).withTextColor(Color.parseColor(utils.getTextColour(context)))
                        //, new PrimaryDrawerItem().withName("Feedback").withIcon(GoogleMaterial.Icon.gmd_feedback).withIconColor(Color.parseColor(utils.getTextColour(context))).withIdentifier(21).withSelectable(false).withTag("Bullhorn").withTextColor(Color.parseColor(utils.getTextColour(context)))
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                }).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
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
                                intent = new Intent(context, LoginActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(context, SignupActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(context, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(context, viewProfileActivity.class);
                            } else if (drawerItem.getIdentifier() == 10) {
                                intent = new Intent(context, ListOfCategoryActivity.class);
                            } else if (drawerItem.getIdentifier() == 11) {
                                intent = new Intent(context, uploadServiceActivity.class);
                            } else if (drawerItem.getIdentifier() == 12) {
                                intent = new Intent(context, viewMyServiceListActivity.class);
                            }else if (drawerItem.getIdentifier() == 13) {
                                intent = new Intent(context, MapsActivity.class);
                            }else if (drawerItem.getIdentifier() == 20) {
                                intent = new Intent(context, contactUsActivity.class);
                            }else if (drawerItem.getIdentifier() == 100) {
                                PreferenceUtils.savePassword(null, context);
                                PreferenceUtils.saveEmail(null, context);
                                PreferenceUtils.saveIsProvider(null, context);
                                intent = new Intent(context, MainActivity.class);
                            }/*else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(DrawerActivity.this, AdvancedActivity.class);
                            } else if (drawerItem.getIdentifier() == 20) {
                                intent = new LibsBuilder()
                                        .withFields(R.string.class.getFields())
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .intent(DrawerActivity.this);
                            }*/
                            if (intent != null) {
                                context.startActivity(intent);
                                context.finish();
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                //.withShowDrawerUntilDraggedOpened(true)
                .build();
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);

            //set the active profile
            //headerResult.setActiveProfile(profile);
        }else{

            //headerResult.setActiveProfile(profile);

        }

        return result;

    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked + " - Colour is "+ utils.getColour(_context));
                if(isChecked){
                    //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                    utils.saveBarColour("#000000",_context);
                    utils.saveColour("#000000",_context);
                    utils.saveTextColour("#FFD700",_context);
                    utils.saveChecked(Boolean.toString(isChecked),_context);
                    result.getDrawerItems();
                    reloadDrawerActivity.refreshActivity(_context);

                }else {
                    //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0091EA")));
                    utils.saveColour("#ffffff",_context);
                    utils.saveBarColour("#0091EA",_context);
                    utils.saveTextColour("#000000",_context);
                    utils.saveChecked(Boolean.toString(isChecked),_context);
                    reloadDrawerActivity.refreshActivity(_context);
                }

            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);

            }
        }
    };
}
