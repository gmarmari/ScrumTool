package gr.eap.dxt.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.persons.FirebasePersonGetNumber;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.projects.FirebaseProjectGetNumberInProgress;
import gr.eap.dxt.sprints.FirebaseSprintGetNumberInProgress;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.ListMyOptionsItemAdapter;
import gr.eap.dxt.tools.MyOtionsItem;
import gr.eap.dxt.tools.MyProgressDialog;


public class HomeFragment extends Fragment {

    public interface FragmentInteractionListener {
        void openFragmentPersons();
        void openFragmentProjects();
        void openFragmentSprints();
    }
    private FragmentInteractionListener mListener;

    private static final String LIST_ITEM_USER = "gr.eap.dxt.main.HomeFragment.LIST_ITEM_USER";
    private static final String LIST_ITEM_GROUP = "gr.eap.dxt.main.HomeFragment.LIST_ITEM_GROUP";
    private static final String LIST_ITEM_PROJECT = "gr.eap.dxt.main.HomeFragment.LIST_ITEM_PROJECT";
    private static final String LIST_ITEM_SPRINT = "gr.eap.dxt.main.HomeFragment.LIST_ITEM_SPRINT";

    public HomeFragment() { }

    private Person person;
    private long personsNumber;
    private long projectsNumber;
    private long sprintsNumber;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private ListView listView;
    private ListMyOptionsItemAdapter adapter;
    private ArrayList<MyOtionsItem> items;

    private MyProgressDialog myDialog;

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("context must implement FragmentInteractionListener.");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement FragmentInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        person = AppShared.getLogginUser();
        items = new ArrayList<>(); // only at start initilized, so the adapter.notifyDataChanged works
        createListItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        @SuppressLint("InflateParams") View header = inflater.inflate(R.layout.fragment_home_header, null, false);

        listView = (ListView) rootView.findViewById(R.id.my_list_view);

        if (listView != null){
            listView.addHeaderView(header);
            adapter = new ListMyOptionsItemAdapter(getActivity(), items);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    int position = pos - listView.getHeaderViewsCount();

                    if (items == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "items == null");
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
                    if (item == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "item == null");
                        return;
                    }
                    if (item.getId() == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "item.getId() == null");
                        return;
                    }

                    switch (item.getId()) {
                        case LIST_ITEM_USER:
                            if (person != null) {
                                if (mListener != null) mListener.openFragmentPersons();
                            }
                            break;
                        case LIST_ITEM_GROUP:
                            if (mListener != null) {
                                mListener.openFragmentPersons();
                            }
                            break;
                        case LIST_ITEM_PROJECT:
                            if (mListener != null) {
                                mListener.openFragmentProjects();
                            }
                            break;
                        case LIST_ITEM_SPRINT:
                            if (mListener != null) {
                                mListener.openFragmentSprints();
                            }
                            break;
                        default:
                            AppShared.writeErrorToLogString(getClass().toString(), "Not supported item id: " + item.getId());
                            break;
                    }
                }
            });
        }


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (person != null){
            // only if logged in
            getPersonNumber();
        }
    }

    private void getPersonNumber(){
        myDialog = new MyProgressDialog(getActivity(), null, true);
        myDialog.start();

        new FirebasePersonGetNumber(getActivity(), false, new FirebasePersonGetNumber.Listener() {
            @Override
            public void onResponse(long number, String errorMsg) {

                if (errorMsg != null){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                }

                personsNumber = number > 0 ? number : 0;

                getProjectNumber();
            }
        }).execute();
    }

    private void getProjectNumber(){
        new FirebaseProjectGetNumberInProgress(getActivity(), false, new FirebaseProjectGetNumberInProgress.Listener() {
            @Override
            public void onResponse(long number, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                }

                projectsNumber = number > 0 ? number : 0;

                getSprintsNumber();
            }
        }).execute();
    }

    private void getSprintsNumber(){
        new FirebaseSprintGetNumberInProgress(getActivity(), false, new FirebaseSprintGetNumberInProgress.Listener() {
            @Override
            public void onResponse(long number, String errorMsg) {
                if (myDialog != null) {
                    myDialog.stop();
                    if (myDialog.isCanceled()) return;
                }

                if (errorMsg != null && !errorMsg.isEmpty()){
                    AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
                }

                sprintsNumber = number > 0 ? number : 0;

                createListItems();
                if (listView != null){
                    adapter.notifyDataSetChanged();
                }
            }
        }).execute();

        new FirebaseProjectGetNumberInProgress(getActivity(), false, new FirebaseProjectGetNumberInProgress.Listener() {
            @Override
            public void onResponse(long number, String errorMsg) {

            }
        }).execute();
    }

    private void createListItems(){
        items.clear();

        if (person == null) return;

        String user = "";
        if (person.getName() != null && !person.getName().isEmpty()) {
            user += person.getName() + ", " + getString(R.string.welcome);
        } else if (person.getEmail() != null && !person.getEmail().isEmpty()) {
            user += person.getEmail() + ", " + getString(R.string.welcome);
        }
        items.add(new MyOtionsItem(user, R.drawable.ic_action_person_purple, LIST_ITEM_USER));


        String members = getString(R.string.group_consists_of) + " " + personsNumber + " ";
        if (personsNumber == 1){
            members += getString(R.string.member).toLowerCase();
        }else{
            members += getString(R.string.members).toLowerCase();
        }

        items.add(new MyOtionsItem(members, R.drawable.ic_action_people_purple, LIST_ITEM_GROUP));

        String projects = String.valueOf(projectsNumber) + " ";
        if (projectsNumber == 1){
            projects += getString(R.string.project);
        }else{
            projects += getString(R.string.projects);
        }
        projects += " " + getString(R.string.in_progress).toLowerCase();

        items.add(new MyOtionsItem(projects, R.drawable.ic_action_project_purple, LIST_ITEM_PROJECT));

        String sprints = String.valueOf(sprintsNumber) + " ";
        if (sprintsNumber == 1){
            sprints += getString(R.string.sprint);
        }else{
            sprints += getString(R.string.sprints);
        }
        sprints += " " + getString(R.string.in_progress).toLowerCase();

        items.add(new MyOtionsItem(sprints, R.drawable.ic_action_event_purple, LIST_ITEM_SPRINT));
    }

}