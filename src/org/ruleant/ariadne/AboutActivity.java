package org.ruleant.ariadne;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String dateFormat = "yyyy-MM-dd";
        if (MainActivity.DEBUG_LEVEL >= 1) {
            dateFormat = "yyyy-MM-dd'T'HH:mm:ssz";
        }
        String versionName = "";
        String lastUpdated = "";
        PackageInfo packageInfo;

        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = "v" + packageInfo.versionName;
            Date date = new Date(packageInfo.lastUpdateTime);
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            lastUpdated = formatter.format(date);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // Version text view
        TextView tvVersion = (TextView) findViewById(R.id.textview_version);
        tvVersion.setText(getResources().getString(R.string.version) + ": " + versionName);

        // Updated text view
        TextView tvUpdated = (TextView) findViewById(R.id.textview_updated);
        tvUpdated.setText(getResources().getString(R.string.updated) + ": " + lastUpdated);

        // Copyright text view
        TextView tvCopyright = (TextView) findViewById(R.id.textview_copyright);
        // enable HTML links
        tvCopyright.setMovementMethod(LinkMovementMethod.getInstance());

        // License text view
        TextView tvLicense = (TextView) findViewById(R.id.textview_license);
        // enable HTML links
        tvLicense.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
