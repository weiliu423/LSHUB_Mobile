package ie.mycit.weiliu.lshub.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import ie.mycit.weiliu.lshub.R;

public class AccountHeaderHelper {

    private static AccountHeader headerResult = null;
    private static final int PROFILE_SETTING = 100000;

    public static AccountHeader header(Activity context, Bundle savedInstanceState) {

        headerResult = new AccountHeaderBuilder()
                .withActivity(context)
                .withTranslucentStatusBar(true).withHeaderBackground(R.drawable.header2)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier a add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            //int count = 100 + headerResult.getProfiles().size() + 1;
                            //IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon(R.drawable.profile5).withIdentifier(count);
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                //headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                //headerResult.addProfiles(newProfile);
                            }
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        return headerResult;

    }

}
