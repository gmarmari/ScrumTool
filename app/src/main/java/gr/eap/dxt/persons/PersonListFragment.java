package gr.eap.dxt.persons;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.FirebaseParse;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 4/2/2017.
 */

public class PersonListFragment extends Fragment {

    public interface FragmentInteractionListener {
        void onShowPerson(Person person);
    }
    private FragmentInteractionListener mListener;

    public PersonListFragment() { }
    public static PersonListFragment newInstance() {
        return new PersonListFragment();
    }

    private View rootView;
    private ListPersonAdapter adapter;
    private ArrayList<Person> persons;

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
        rootView = inflater.inflate(R.layout.fragment_person_list, container, false);

        setlist();
        setFirebaseListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (persons == null){
            getAllPersons();
        }else{
            setActivatedPosition(AppShared.personListSelPos, true);
        }
    }

    private void setActivatedPosition(final int position, boolean scroll) {
        if (persons == null) return;
        if (position < 0 || position >= persons.size())  return;
        AppShared.personListSelPos = position;
        if (rootView == null) return;
        final ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        listView.setItemChecked(position, true);
        if (!scroll) return;
        listView.post(new Runnable() {
            public void run() {
                listView.smoothScrollToPosition(position);
            }
        });
    }


    private void setlist(){
        if (rootView == null) return;
        final  ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;
        adapter = new ListPersonAdapter(getActivity(), persons);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setActivatedPosition(i, false);

                int position = i - listView.getHeaderViewsCount();

                if (persons == null) {
                    AppShared.writeErrorToLogString(getClass().toString(), "persons == null");
                    return;
                }

                personSelected(position);
            }
        });
    }

    private void setFirebaseListeners(){
        if (rootView == null) return;
        final  ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView == null) return;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Person.FIREBASE_LIST);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null) return;
                try {
                    Person person = new Person();
                    person.setPersonId(dataSnapshot.getKey());

                    person.setEmail(FirebaseParse.getString(dataSnapshot.child(Person.EMAIL)));
                    person.setName(FirebaseParse.getString(dataSnapshot.child(Person.NAME)));
                    person.setRole(FirebaseParse.getString(dataSnapshot.child(Person.ROLE)));

                    persons.add(person);
                    adapter = new ListPersonAdapter(getActivity(), persons);
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    AppShared.writeErrorToLogString(getClass().toString(), e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot == null) return;
                try {
                    Person person = Person.getPersonWithId(dataSnapshot.getKey(), persons);
                    if (person == null) return;

                    person.setEmail(FirebaseParse.getString(dataSnapshot.child(Person.EMAIL)));
                    person.setName(FirebaseParse.getString(dataSnapshot.child(Person.NAME)));
                    person.setRole(FirebaseParse.getString(dataSnapshot.child(Person.ROLE)));
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
                    Person person = Person.getPersonWithId(dataSnapshot.getKey(), persons);
                    if (person == null) return;

                    persons.remove(person);
                    adapter = new ListPersonAdapter(getActivity(), persons);
                    listView.setAdapter(adapter);

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
    }

    private void getAllPersons(){
        new FirebasePersonGetAll(getActivity(), null, new FirebasePersonGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Person> _persons, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(getActivity(), null, errorMsg);
                    return;
                }
                if (_persons == null){
                    MyAlertDialog.alertError(getActivity(), null, "persons == null");
                    return;
                }

                persons = _persons;
                if (rootView == null) return;
                final  ListView listView = (ListView) rootView.findViewById(R.id.my_list_view);
                if (listView == null) return;
                adapter = new ListPersonAdapter(getActivity(), persons);
                listView.setAdapter(adapter);
                setActivatedPosition(AppShared.personListSelPos, true);

            }
        }).execute();
    }


    private void personSelected(final int position){
        if (persons == null){
            AppShared.writeErrorToLogString(getClass().toString(), "persons == null");
            return;
        }
        if (position < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "position < 0");
            return;
        }
        if (position >= persons.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "position >= persons.size()");
            return;
        }

        final Person person = persons.get(position);

        if (person == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person == null");
            MyAlertDialog.alertError(getActivity(), null, "person == null");
            return;
        }
        if (person.getPersonId() == null){
            AppShared.writeErrorToLogString(getClass().toString(), "person.getPersonId() == null");
            MyAlertDialog.alertError(getActivity(), null, "person.getPersonId() == null");
            return;
        }
        if (person.getPersonId().isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), "person.getPersonId().isEmpty()");
            MyAlertDialog.alertError(getActivity(), null, "person.getPersonId().isEmpty()");
            return;
        }

        if (mListener != null) mListener.onShowPerson(person);
    }
}
