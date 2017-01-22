package gr.eap.dxt.main;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;

public class MainNavigationActivity extends Activity implements MainNavigationDrawerFragment.NavigationDrawerCallbacks{

    private MainNavigationDrawerFragment mMainNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        mMainNavigationDrawerFragment = (MainNavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mMainNavigationDrawerFragment.setUp( R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
/*
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
        */
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

        if (itemId.equals(MainNavigationDrawerFragment.HOME)){
            getFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
        }else if (itemId.equals(MainNavigationDrawerFragment.ABOUT)) {
            getFragmentManager().beginTransaction().replace(R.id.container, AboutFragment.newInstance()).commit();
        }else{
            AppShared.writeErrorToLogString(getClass().toString(), "Not supported itemId: " + itemId);
            getFragmentManager().beginTransaction().replace(R.id.container, new Fragment()).commit();
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

}
