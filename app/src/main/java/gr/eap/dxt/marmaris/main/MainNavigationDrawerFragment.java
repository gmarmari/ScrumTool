package gr.eap.dxt.marmaris.main;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.tools.AppShared;
import gr.eap.dxt.marmaris.tools.Keyboard;
import gr.eap.dxt.marmaris.tools.ListMyOptionsItemAdapter;
import gr.eap.dxt.marmaris.tools.MyOtionsItem;
import gr.eap.dxt.marmaris.tools.StoreManagement;

public class MainNavigationDrawerFragment extends Fragment {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";


    public static final String HOME = "gr.eap.dxt.marmaris.main.MainNavigationDrawerFragment.HOME";
    public static final String PROJECTS = "gr.eap.dxt.marmaris.main.MainNavigationDrawerFragment.PROJECTS";
    public static final String PEOPLE = "gr.eap.dxt.marmaris.main.MainNavigationDrawerFragment.PEOPLE";
    public static final String BACKLOG = "gr.eap.dxt.marmaris.main.MainNavigationDrawerFragment.BACKLOG";
    public static final String SPRINTS = "gr.eap.dxt.marmaris.main.MainNavigationDrawerFragment.SPRINTS";
    public static final String ABOUT = "gr.eap.dxt.marmaris.main.MainNavigationDrawerFragment.ABOUT";

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(String itemId);
        void openLoginFragment();
    }
    private NavigationDrawerCallbacks mCallbacks;

    private View rootView;

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private ListView mListView;
    private ArrayList<MyOtionsItem> items;

    private int mCurrentSelectedPosition = 0;
    private boolean mUserLearnedDrawer;

    public MainNavigationDrawerFragment() {
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("context must implement NavigationDrawerCallbacks.");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StoreManagement store = new StoreManagement(getActivity());
        mUserLearnedDrawer = store.userLearnedMainDrawer();

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        }

        createListItems();
        handleItemSelection(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate( R.layout.drawer_main_navigation, container, false);

        setMyList();
        setButtonToogleLogin();

        return rootView;
    }

    private void setMyList() {
        if (rootView == null) return;

        mListView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (mListView == null) return;

        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setItemChecked(mCurrentSelectedPosition, true);
        ListMyOptionsItemAdapter adapter = new ListMyOptionsItemAdapter(getActivity(), items);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - mListView.getHeaderViewsCount();
                handleItemSelection(pos);
            }
        });

    }

    private void setButtonToogleLogin(){
        if (rootView == null) return;

        final Button button = (Button) rootView.findViewById(R.id.my_button_toggle_login);
        if (button == null) return;
        if (AppShared.getUserLogged() != null) {
            button.setText(R.string.logout);
        } else {
            button.setText(R.string.login);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppShared.getUserLogged() != null) {
                    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseAuth.signOut();
                    AppShared.loggout();
                    button.setText(R.string.login);
                } else {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                    }
                    if (mCallbacks != null) {
                        mCallbacks.openLoginFragment();
                    }
                }
            }
        });
    }

    private void createListItems(){
        items = new ArrayList<>();

        items.add(new MyOtionsItem(getString(R.string.home), R.drawable.ic_action_home_purple, HOME));
        items.add(new MyOtionsItem(getString(R.string.projects), R.drawable.ic_action_project_purple, PROJECTS));
        items.add(new MyOtionsItem(getString(R.string.people), R.drawable.ic_action_people_purple, PEOPLE));
        items.add(new MyOtionsItem(getString(R.string.backlog), R.drawable.ic_action_backlog_purple, BACKLOG));
        items.add(new MyOtionsItem(getString(R.string.sprints), R.drawable.ic_action_event_purple, SPRINTS));
        items.add(new MyOtionsItem(getString(R.string.about), R.drawable.ic_action_about_purple, ABOUT));
    }

    private void handleItemSelection(int position){
        if (items == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "items == null");
            return;
        }
        if (items.isEmpty()) {
            AppShared.writeErrorToLogString(getClass().toString(), "items.isEmpty()");
            return;
        }

        if (position < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
            return;
        }
        if (position >= items.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "position >= items.size()");
            return;
        }

        MyOtionsItem item = items.get(position);
        if (item == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "item == null");
            return;
        }

        mCurrentSelectedPosition = position;
        if (mListView != null) {
            mListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(item.getId());
        }
    }

/*
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }
*/
    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                //R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    StoreManagement store = new StoreManagement(getActivity());
                    store.setUserLearnedMainDrawer(true);
                }

                setButtonToogleLogin();
                Keyboard.close(getActivity());
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        /*
        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }
        */

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //noinspection SimplifiableIfStatement
        if (item == null || mDrawerToggle == null) return false;
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
/*
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }
*/

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Called only at the first launch and
     * adds a shortcut on home screen
     */



}
