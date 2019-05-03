package ie.mycit.weiliu.lshub.utils;

import android.app.Activity;
import android.content.Intent;
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
import ie.mycit.weiliu.lshub.SignupActivity;
import ie.mycit.weiliu.lshub.uploadServiceActivity;

public class DrawerHelper {

    private static Drawer result = null;
    private static AccountHeader headerResult = null;
    static IProfile profile;
    private static final int PROFILE_SETTING = 100000;

    public static Drawer getDrawer(Activity context, Bundle savedInstanceState,  Toolbar toolbar, IProfile profile, AccountHeader headerResult, PrimaryDrawerItem item, PrimaryDrawerItem item1) {
        //profile = new ProfileDrawerItem().withName("Welcome").withEmail("Guest").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(100);


        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(context)
                .withTranslucentNavigationBar(true)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withHasStableIds(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeaderHelper we created earlier for the header
                .addDrawerItems(
                        new SectionDrawerItem().withName("Account").withDivider(false),
                        item,
                        item1,
                        new SectionDrawerItem().withName("My dashboard"),
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(3).withSelectable(false),
                        new ExpandableDrawerItem().withName("Dashboard").withIcon(GoogleMaterial.Icon.gmd_dashboard).withIdentifier(19).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("Add my service").withLevel(2).withIcon(Octicons.Icon.oct_tools).withIdentifier(11).withSelectable(false),
                                new SecondaryDrawerItem().withName("View my profile").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_supervisor_account).withIdentifier(2002).withSelectable(false),
                                new SecondaryDrawerItem().withName("View my reviews").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_rate_review).withIdentifier(2003).withSelectable(false)
                        ),
                        new SectionDrawerItem().withName("Contact us"),
                        new PrimaryDrawerItem().withName("Chat with us").withIcon(GoogleMaterial.Icon.gmd_report_problem).withIdentifier(20).withSelectable(false),
                        new PrimaryDrawerItem().withName("Feedback").withIcon(GoogleMaterial.Icon.gmd_feedback).withIdentifier(21).withSelectable(false).withTag("Bullhorn"),
                        new SectionDrawerItem().withName("Services"),
                        new PrimaryDrawerItem().withName("View services").withIcon(Octicons.Icon.oct_tools).withIdentifier(10).withSelectable(false),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Location").withIcon(Octicons.Icon.oct_location).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)
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
                            } else if (drawerItem.getIdentifier() == 10) {
                                intent = new Intent(context, ListOfCategoryActivity.class);
                            } else if (drawerItem.getIdentifier() == 11) {
                                intent = new Intent(context, uploadServiceActivity.class);
                            } else if (drawerItem.getIdentifier() == 12) {
                                intent = new Intent(context, uploadServiceActivity.class);
                            }else if (drawerItem.getIdentifier() == 100) {
                                PreferenceUtils.savePassword(null, context);
                                PreferenceUtils.saveEmail(null, context);
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


    private static OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };
}
