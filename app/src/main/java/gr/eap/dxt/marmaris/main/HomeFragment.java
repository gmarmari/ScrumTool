package gr.eap.dxt.marmaris.main;

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

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.persons.FirebasePersonGetNumber;
import gr.eap.dxt.marmaris.persons.Person;
import gr.eap.dxt.marmaris.tools.AppShared;
import gr.eap.dxt.marmaris.tools.ListMyOptionsItemAdapter;
import gr.eap.dxt.marmaris.tools.MyOtionsItem;


public class HomeFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onShowPerson(Person person);
        void openFragmentPerson();
    }
    private FragmentInteractionListener mListener;

    private static final String LIST_ITEM_USER = "gr.eap.dxt.marmaris.main.HomeFragment.LIST_ITEM_USER";
    private static final String LIST_ITEM_GROUP = "gr.eap.dxt.marmaris.main.HomeFragment.LIST_ITEM_GROUP";
    private static final String LIST_ITEM_PROJECT = "gr.eap.dxt.marmaris.main.HomeFragment.LIST_ITEM_PROJECT";

    public HomeFragment() { }

    private Person person;
    private long personsNumber;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private ListView listView;
    private ListMyOptionsItemAdapter adapter;
    private ArrayList<MyOtionsItem> items;

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
        items = new ArrayList<>();
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
                                if (mListener != null) mListener.onShowPerson(person);
                            }
                            break;
                        case LIST_ITEM_GROUP:
                            if (mListener != null) {
                                mListener.openFragmentPerson();
                            }
                            break;
                        case LIST_ITEM_PROJECT:

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
            new FirebasePersonGetNumber(getActivity(), new FirebasePersonGetNumber.Listener() {
                @Override
                public void onResponse(long number) {
                    personsNumber = number >= 0 ? number : 0;
                    createListItems();
                    if (listView != null){
                        adapter.notifyDataSetChanged();
                    }
                }
            }).execute();
        }
    }

    private void createListItems(){
        items.clear();

        if (person == null) return;

        String user = "";
        if (person.getName() != null && !person.getName().isEmpty()) {
            user += getString(R.string.welcome) + " " + person.getName() + "!";
        } else if (person.getEmail() != null && !person.getEmail().isEmpty()) {
            user += getString(R.string.welcome) + " " + person.getEmail() + "!";
        }
        items.add(new MyOtionsItem(user, R.drawable.ic_action_person_purple, LIST_ITEM_USER));


        String members = getString(R.string.group_consists_of) + " " + personsNumber + " " + getString(R.string.members);
        items.add(new MyOtionsItem(members, R.drawable.ic_action_people_purple, LIST_ITEM_GROUP));

        //: TODO replace 0 with the number of projects
        String projects = getString(R.string.currently_you_work_on) + " " + 0 + " " + getString(R.string.projects).toLowerCase();
        items.add(new MyOtionsItem(projects, R.drawable.ic_action_project_purple, LIST_ITEM_PROJECT));
    }


}
