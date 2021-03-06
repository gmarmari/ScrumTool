package gr.eap.dxt.main;

import android.app.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogDialogActivity;
import gr.eap.dxt.backlog.BacklogEditFragment;
import gr.eap.dxt.backlog.BacklogNewDialogActivity;
import gr.eap.dxt.backlog.BacklogListFragment;
import gr.eap.dxt.board.BoardFragment;
import gr.eap.dxt.login.FirebaseGetUser;
import gr.eap.dxt.login.LoginFragment;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.persons.PersonDialogActivity;
import gr.eap.dxt.persons.PersonListFragment;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.projects.ProjectDialogActivity;
import gr.eap.dxt.projects.ProjectEditFragment;
import gr.eap.dxt.projects.ProjectNewDialogActivity;
import gr.eap.dxt.projects.ProjectListFragment;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.sprints.SprintDialogActivity;
import gr.eap.dxt.sprints.SprintEditFragment;
import gr.eap.dxt.sprints.SprintListFragment;
import gr.eap.dxt.sprints.SprintNewDialogActivity;
import gr.eap.dxt.sprints.SprintNewFragment;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.GooglePlayServices;
import gr.eap.dxt.tools.MyRequestCodes;
import gr.eap.dxt.tools.StoreManagement;

public class MainNavigationActivity extends Activity implements MainNavigationDrawerFragment.NavigationDrawerCallbacks,
        LoginFragment.FragmentInteractionListener,
        PersonListFragment.FragmentInteractionListener,
        HomeFragment.FragmentInteractionListener,
        ProjectListFragment.FragmentInteractionListener,
        BacklogListFragment.FragmentInteractionListener,
        SprintListFragment.FragmentInteractionListener{

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

        if (savedInstanceState == null){
            if (AppShared.getLogginUser() != null){
                openFragmentHome();
            }else{
                new FirebaseGetUser(this, new FirebaseGetUser.Listener() {
                    @Override
                    public void onResponse(Person person, String errorMsg) {

                        AppShared.setLogginUser(person);
                        openFragmentHome();
                        if (person == null)  {
                            openDrawer();
                        }

                    }
                }).execute();
            }
        }
    }

    private void openDrawer(){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) return;
        View mFragmentContainerView = findViewById(R.id.navigation_drawer);
        if (mFragmentContainerView == null) return;

        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    private void closeDrawer(){
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) return;
        View mFragmentContainerView = findViewById(R.id.navigation_drawer);
        if (mFragmentContainerView == null) return;

        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyRequestCodes.PERSON_EDIT_REQUEST){
            if (resultCode == RESULT_OK){
                // Person editted or added
                if (data != null){
                    if (data.getBooleanExtra(PersonDialogActivity.RELOAD, false)){
                        openFragmentPersons();
                    }
                }
            }
        }
        if (requestCode == MyRequestCodes.PROJECT_EDIT_REQUEST){
            if (resultCode == RESULT_OK){
                // Project editted or added
                if (data != null){
                    if (data.getBooleanExtra(ProjectEditFragment.RELOAD, false)){
                        openFragmentProjects();
                    }
                }
            }
        }
        if (requestCode == MyRequestCodes.BACKLOG_EDIT_REQUEST){
            if (resultCode == RESULT_OK){
                // Backlog editted or added
                if (data != null){
                    if (data.getBooleanExtra(BacklogEditFragment.RELOAD, false)){
                        openFragmentBacklogs();
                    }
                }
            }
        }
        if (requestCode == MyRequestCodes.SPRINT_EDIT_REQUEST){
            if (resultCode == RESULT_OK){
                // Sprint editted or added
                if (data != null){
                    if (data.getBooleanExtra(SprintEditFragment.RELOAD, false)
                            || data.getBooleanExtra(SprintNewFragment.RELOAD, false)){
                        openFragmentSprints();
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
            case MainNavigationDrawerFragment.PEOPLE:
                openFragmentPersons();
                break;
            case MainNavigationDrawerFragment.PROJECTS:
                openFragmentProjects();
                break;
            case MainNavigationDrawerFragment.BACKLOG:
                openFragmentBacklogs();
                break;
            case MainNavigationDrawerFragment.SPRINTS:
                openFragmentSprints();
                break;
            case MainNavigationDrawerFragment.BOARD:
                openFragmentBoard();
                break;
            case MainNavigationDrawerFragment.ABOUT:
                openFragmentAbout();
                break;
            default:
                AppShared.writeErrorToLogString(getClass().toString(), "Not supported itemId: " + itemId);
                getFragmentManager().beginTransaction().replace(R.id.container, new Fragment()).commit();
                break;
        }
    }

    private void openFragmentHome(){
        if (getActionBar() != null) getActionBar().setTitle(R.string.app_name);
        getFragmentManager().beginTransaction().replace(R.id.container, HomeFragment.newInstance()).commit();
    }

    private void openFragmentAbout(){
        if (getActionBar() != null) getActionBar().setTitle(R.string.about);
        getFragmentManager().beginTransaction().replace(R.id.container, AboutFragment.newInstance()).commit();
    }

    private void openFragmentBoard(){
        if (getActionBar() != null) getActionBar().setTitle(R.string.board);
        getFragmentManager().beginTransaction().replace(R.id.container, BoardFragment.newInstance()).commit();
    }

    /**  From {@link HomeFragment.FragmentInteractionListener} */
    @Override
    public void openFragmentPersons(){
        if (getActionBar() != null) getActionBar().setTitle(R.string.people);
        getFragmentManager().beginTransaction().replace(R.id.container, PersonListFragment.newInstance()).commit();
    }

    /**  From {@link HomeFragment.FragmentInteractionListener} */
    @Override
    public void openFragmentProjects(){
        if (getActionBar() != null) getActionBar().setTitle(R.string.projects);
        getFragmentManager().beginTransaction().replace(R.id.container, ProjectListFragment.newInstance()).commit();
    }

    private void openFragmentBacklogs(){
        if (getActionBar() != null) getActionBar().setTitle(R.string.backlog);
        getFragmentManager().beginTransaction().replace(R.id.container, BacklogListFragment.newInstance()).commit();
    }

    /**  From {@link HomeFragment.FragmentInteractionListener} */
    @Override
    public void openFragmentSprints(){
        if (getActionBar() != null) getActionBar().setTitle(R.string.sprints);
        getFragmentManager().beginTransaction().replace(R.id.container, SprintListFragment.newInstance()).commit();
    }

    /**  From {@link MainNavigationDrawerFragment.NavigationDrawerCallbacks} */
    @Override
    public void openLoginFragment() {
        if (getActionBar() != null) getActionBar().setTitle(R.string.login);
        getFragmentManager().beginTransaction().replace(R.id.container, LoginFragment.newInstance()).commit();
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

    /** From {@link MainNavigationDrawerFragment.NavigationDrawerCallbacks}*/
    @Override
    public void logout() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signOut();
        AppShared.logout();
        closeDrawer();
        openFragmentHome();
    }

    /** From {@link PersonListFragment.FragmentInteractionListener}
     *  and
     * {@link HomeFragment.FragmentInteractionListener}
     * */
    @Override
    public void onShowPerson(Person person) {
        PersonDialogActivity.setStaticContent(person);
        Intent intent = new Intent(this, PersonDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.PERSON_EDIT_REQUEST);
    }

    /** From {@link ProjectListFragment.FragmentInteractionListener}*/
    @Override
    public void onShowProject(Project project) {
        ProjectDialogActivity.setStaticContent(project);
        Intent intent = new Intent(this, ProjectDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.PROJECT_EDIT_REQUEST);
    }

    /** From {@link ProjectListFragment.FragmentInteractionListener}*/
    @Override
    public void onAddNewProject() {
        Intent intent = new Intent(this, ProjectNewDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.PROJECT_EDIT_REQUEST);
    }

    /** From {@link BacklogListFragment.FragmentInteractionListener} */
    @Override
    public void onShowBacklog(Backlog backlog) {
        BacklogDialogActivity.setStaticContent(backlog);
        Intent intent = new Intent(this, BacklogDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.BACKLOG_EDIT_REQUEST);
    }

    /** From {@link BacklogListFragment.FragmentInteractionListener}*/
    @Override
    public void onAddNewBacklog() {
        Intent intent = new Intent(this, BacklogNewDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.BACKLOG_EDIT_REQUEST);
    }

    /** From {@link SprintListFragment.FragmentInteractionListener}*/
    @Override
    public void onShowSprint(Sprint sprint) {
        SprintDialogActivity.setStaticContent(sprint);
        Intent intent = new Intent(this, SprintDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.SPRINT_EDIT_REQUEST);
    }

    /** From {@link SprintListFragment.FragmentInteractionListener}*/
    @Override
    public void onAddNewSprint(Project project, Date startDate, long durationDays, int oldNumber) {
        SprintNewDialogActivity.setStaticContent(project, startDate, durationDays, oldNumber);
        Intent intent = new Intent(this, SprintNewDialogActivity.class);
        startActivityForResult(intent, MyRequestCodes.SPRINT_EDIT_REQUEST);
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
