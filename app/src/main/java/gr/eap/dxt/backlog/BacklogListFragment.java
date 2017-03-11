package gr.eap.dxt.backlog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.login.FirebaseGetUser;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.persons.PersonRole;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseParse;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogListFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onShowBacklog(Backlog backlog);
        void onAddNewBacklog();
    }
    private FragmentInteractionListener mListener;

    public BacklogListFragment() { }
    public static BacklogListFragment newInstance() {
        return new BacklogListFragment();
    }

    private ListView listView;
    private ListBacklogAdapter adapter;
    private ArrayList<Backlog> backlogs;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_backlog_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView != null){
            adapter = new ListBacklogAdapter(getActivity(), backlogs);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    listView.setItemChecked(i, true);

                    int position = i - listView.getHeaderViewsCount();

                    if (backlogs == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "backlogs == null");
                        return;
                    }

                    backlogSelected(position);
                }
            });
        }

        ImageButton addButton = (ImageButton) rootView.findViewById(R.id.my_button_add);
        if (addButton != null){
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    handleBacklogAdd();

                }
            });
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Backlog.FIREBASE_LIST);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null) return;
                try {
                    Backlog backlog = new Backlog();
                    backlog.setBacklogId(dataSnapshot.getKey());

                    backlog.setName(FirebaseParse.getString(dataSnapshot.child(Backlog.NAME)));
                    backlog.setDescription(FirebaseParse.getString(dataSnapshot.child(Backlog.DESCRIPTION)));
                    backlog.setPriority(FirebaseParse.getString(dataSnapshot.child(Backlog.PRIORITY)));
                    backlog.setStatus(FirebaseParse.getString(dataSnapshot.child(Backlog.STATUS)));
                    backlog.setDuration(FirebaseParse.getLong(dataSnapshot.child(Backlog.DURATION)));
                    backlog.setPersonId(FirebaseParse.getString(dataSnapshot.child(Backlog.PERSON_ID)));
                    backlog.setSprintId(FirebaseParse.getString(dataSnapshot.child(Backlog.SPRINT_ID)));
                    backlog.setType(FirebaseParse.getString(dataSnapshot.child(Backlog.TYPE)));

                    backlogs.add(backlog);
                    if (listView != null) {
                        adapter = new ListBacklogAdapter(getActivity(), backlogs);
                        listView.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null) return;
                try {
                    Backlog backlog = getBacklogWithId(dataSnapshot.getKey());
                    if (backlog == null) return;

                    backlog.setName(FirebaseParse.getString(dataSnapshot.child(Backlog.NAME)));
                    backlog.setDescription(FirebaseParse.getString(dataSnapshot.child(Backlog.DESCRIPTION)));
                    backlog.setPriority(FirebaseParse.getString(dataSnapshot.child(Backlog.PRIORITY)));
                    backlog.setStatus(FirebaseParse.getString(dataSnapshot.child(Backlog.STATUS)));
                    backlog.setDuration(FirebaseParse.getLong(dataSnapshot.child(Backlog.DURATION)));
                    backlog.setPersonId(FirebaseParse.getString(dataSnapshot.child(Backlog.PERSON_ID)));
                    backlog.setSprintId(FirebaseParse.getString(dataSnapshot.child(Backlog.SPRINT_ID)));
                    backlog.setType(FirebaseParse.getString(dataSnapshot.child(Backlog.TYPE)));

                    if (adapter != null) adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;
                try {
                    Backlog backlog = getBacklogWithId(dataSnapshot.getKey());
                    if (backlog == null) return;

                    backlogs.remove(backlog);
                    if (listView != null) {
                        adapter = new ListBacklogAdapter(getActivity(), backlogs);
                        listView.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (backlogs == null){
            getAllBacklogs();
        }
    }

    private void getAllBacklogs(){
        new FirebaseBacklogGetAll(getActivity(), new FirebaseBacklogGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Backlog> _backlogs, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }

                if (_backlogs == null) {
                    MyAlertDialog.alertError(getActivity(), null, "backlogs == null");
                    return;
                }

                backlogs = _backlogs;
                if (listView != null) {
                    adapter = new ListBacklogAdapter(getActivity(), backlogs);
                    listView.setAdapter(adapter);
                }

            }
        }).execute();
    }

    private Backlog getBacklogWithId(String backlogId){
        if (backlogId == null || backlogId.isEmpty()) return null;
        if (backlogs == null || backlogs.isEmpty()) return null;

        for (Backlog backlog : backlogs) {
            if (backlog != null){
                if (backlog.getBacklogId() != null){
                    if (backlog.getBacklogId().equals(backlogId)) return backlog;
                }
            }
        }

        return null;
    }

    private void backlogSelected(final int position){
        if (backlogs == null){
            AppShared.writeErrorToLogString(getClass().toString(), "backlogs == null");
            return;
        }
        if (position < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
            return;
        }
        if (position >= backlogs.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "position >= backlogs.size()");
            return;
        }

        final Backlog backlog = backlogs.get(position);

        if (backlog == null){
            AppShared.writeErrorToLogString(getClass().toString(), "backlog == null");
            MyAlertDialog.alertError(getActivity(), null, "backlog == null");
            return;
        }
        if (backlog.getBacklogId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "backlog.getBacklogId() == null");
            MyAlertDialog.alertError(getActivity(), null, "backlog.getBacklogId() == null");
            return;
        }
        if (backlog.getBacklogId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "backlog.getBacklogId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "backlog.getBacklogId().isEmpty()");
            return;
        }

        if (mListener != null) mListener.onShowBacklog(backlog);
    }

    private void handleBacklogAdd(){
        Person person = AppShared.getLogginUser();
        if (person != null){
            handleBacklogAdd(person);
        }else{
            new FirebaseGetUser(getActivity(), new FirebaseGetUser.Listener() {
                @Override
                public void onResponse(Person person, String errorMsg) {

                    if (errorMsg != null && !errorMsg.isEmpty()){
                        MyAlertDialog.alertError(getActivity(), null, errorMsg);
                        return;
                    }
                    if (person == null){
                        MyAlertDialog.alertError(getActivity(), null, "person == null");
                        return;
                    }

                    AppShared.setLogginUser(person);
                    handleBacklogAdd(person);
                }
            }).execute();
        }
    }
    private void handleBacklogAdd(Person person){
        if (person == null) {
            MyAlertDialog.alertError(getActivity(), null, "person == null");
            return;
        }

        String personRole = person.getRole() != null ? person.getRole() : "";
        if (!personRole.equals(PersonRole.PRODUCT_OWNER)) {
            MyAlertDialog.alertError(getActivity(), getString(R.string.not_allowed), getString(R.string.backlog_new_not_allowed_message));
            return;
        }

        if (mListener != null){
            mListener.onAddNewBacklog();
        }
    }
}
