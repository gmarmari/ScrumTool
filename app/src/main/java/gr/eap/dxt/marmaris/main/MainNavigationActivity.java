package gr.eap.dxt.marmaris.main;

import android.app.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.google.android.gms.common.ConnectionResult;

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.tools.AppShared;
import gr.eap.dxt.marmaris.tools.GooglePlayServices;
import gr.eap.dxt.marmaris.tools.StoreManagement;

public class MainNavigationActivity extends Activity implements MainNavigationDrawerFragment.NavigationDrawerCallbacks{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        MainNavigationDrawerFragment mMainNavigationDrawerFragment = (MainNavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mMainNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        StoreManagement store = new StoreManagement(this);
        if (store.isFirstRunAfterInstall()){
            addShortcut();
            store.setFirstRunAfterInstal(false);
        }

        GooglePlayServices googlePlay = new GooglePlayServices(this);

        // Check for GooglePlayServices and get the Push ID if it is not already gotten
        int resultCode = googlePlay.checkPlayServices(true);
        if (resultCode != ConnectionResult.SUCCESS) {
            // If error try second time
            resultCode = googlePlay.checkPlayServices(false);
        }
        if (resultCode != ConnectionResult.SUCCESS) {
            // If error try third time
            googlePlay.checkPlayServices(false);
        }
        googlePlay.getRegistrationID();
    }

    /**
     * From MainNavigationDrawerFragment.NavigationDrawerCallbacks
     */
    @Override
    public void onNavigationDrawerItemSelected(String itemId) {
        if (itemId == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "itemId == null");
            return;
        }

        switch (itemId) {
            case MainNavigationDrawerFragment.HOME:
                getFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
                break;
            case MainNavigationDrawerFragment.ABOUT:
                getFragmentManager().beginTransaction().replace(R.id.container, AboutFragment.newInstance()).commit();
                break;
            default:
                AppShared.writeErrorToLogString(getClass().toString(), "Not supported itemId: " + itemId);
                getFragmentManager().beginTransaction().replace(R.id.container, new Fragment()).commit();
                break;
        }
    }

/*
    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
*/

    private void addShortcut() {
        Intent shortcutIntent = new Intent(getApplicationContext(), MainNavigationActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

}
