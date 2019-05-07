package ie.mycit.weiliu.lshub.utils;

import android.app.Activity;
import android.content.Intent;

public class reloadDrawerActivity  {


    public void refreshActivity(Activity context) {
        context.finish();
        context.startActivity(new Intent(context.getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)));
        context.overridePendingTransition(0,0);
    }
}
