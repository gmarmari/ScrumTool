package gr.eap.dxt.marmaris.main;

import android.app.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.login.LoginFragment;
import gr.eap.dxt.marmaris.persons.Person;
import gr.eap.dxt.marmaris.persons.PersonDialogActivity;
import gr.eap.dxt.marmaris.persons.PersonsFragment;
import gr.eap.dxt.marmaris.tools.AppShared;
import gr.eap.dxt.marmaris.tools.GooglePlayServices;
import gr.eap.dxt.marmaris.tools.MyRequestCodes;
import gr.eap.dxt.marmaris.tools.StoreManagement;

public class MainNavigationActivity extends Activity implements MainNavigationDrawerFragment.NavigationDrawerCallbacks,
        LoginFragment.FragmentInteractionListener,
        PersonsFragment.FragmentInteractionListener{

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

        if (AppShared.getUserLogged() == null){
            openDrawer();
        }
    }

    private void openDrawer(){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) return;
        View mFragmentContainerView = findViewById(R.id.navigation_drawer);
        if (mFragmentContainerView == null) return;

        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyRequestCodes.PERSON_EDIT_REQUEST){
            if (resultCode == RESULT_OK){
                // Person Editted
                if (data != null){
                    if (data.getBooleanExtra(PersonDialogActivity.RELOAD, false)){
                        openFragmentPerson();
                    }
                }
            }
        }
    }

    /**  From {@link MainNavigationDrawerFragment.NavigationDrawerCallbacks} */
    @Override
    public void onNavigationDrawerItemSelected(String itemId) {
        if (itemId == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "itemId == null");
            return;
        }

        switch (itemId) {
            case MainNavigationDrawerFragment.HOME:
                openFragmentHome();
                break;
            case MainNavigationDrawerFragment.ABOUT:
                openFragmentAbout();
                break;
            case MainNavigationDrawerFragment.PEOPLE:
                openFragmentPerson();
                break;
            default:
                AppShared.writeErrorToLogString(getClass().toString(), "Not supported itemId: " + itemId);
                getFragmentManager().beginTransaction().replace(R.id.container, new Fragment()).commit();
                break;
        }
    }

    private void openFragmentHome(){
        getFragmentManager().beginTransaction().replace(R.id.container, MainFragment.newInstance()).commit();
    }

    private void openFragmentAbout(){
        getFragmentManager().beginTransaction().replace(R.id.container, AboutFragment.newInstance()).commit();
    }

    private void openFragmentPerson(){
        getFragmentManager().beginTransaction().replace(R.id.container, PersonsFragment.newInstance()).commit();
    }


    /**  From {@link MainNavigationDrawerFragment.NavigationDrawerCallbacks} */
    @Override
    public void openLoginFragment() {
        getFragmentManager().beginTransaction().replace(R.id.container, LoginFragment.newInstance()).commit();
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

    /** From {@link LoginFragment.FragmentInteractionListener}*/
    @Override
    public void onDidLogIn(Task<AuthResult> task) {
        if (task != null){
            if (task.isSuccessful()){
                AppShared.writeInfoToLogString(getClass().toString(), "Logged in to firebase");
            }
        }

        openFragmentHome();
    }

    /** From {@link PersonsFragment.FragmentInteractionListener}*/
    @Override
    public void onShowPerson(Person person) {
        PersonDialogActivity.setStaticContent(person);
        Intent intent = new Intent(this, PersonDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.PERSON_EDIT_REQUEST);
    }
}
