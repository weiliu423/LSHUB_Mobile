package ie.mycit.weiliu.lshub;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class refreshActivity extends AppCompatActivity {
;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reload_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                finish();
                this.startActivity(new Intent(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)));
                this.overridePendingTransition(0,0);
                //startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
