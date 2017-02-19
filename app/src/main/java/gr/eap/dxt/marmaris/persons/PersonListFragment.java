package gr.eap.dxt.marmaris.persons;

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

import gr.eap.dxt.marmaris.R;
import gr.eap.dxt.marmaris.tools.AppShared;
import gr.eap.dxt.marmaris.tools.FirebaseParse;
import gr.eap.dxt.marmaris.tools.MyAlertDialog;

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

    private ListView listView;
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
        View rootView = inflater.inflate(R.layout.fragment_person_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.my_list_view);
        if (listView != null){
            adapter = new ListPersonAdapter(getActivity(), persons);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    listView.setItemChecked(i, true);

                    int position = i - listView.getHeaderViewsCount();

                    if (persons == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "persons == null");
                        return;
                    }

                    personSelected(position);
                }
            });
        }


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
                    person.setPersonRole(FirebaseParse.getString(dataSnapshot.child(Person.PERSON_ROLE)));

                    persons.add(person);
                    if (listView != null) {
                        adapter = new ListPersonAdapter(getActivity(), persons);
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
                    Person person = getPersonWithId(dataSnapshot.getKey());
                    if (person == null) return;

                    person.setEmail(FirebaseParse.getString(dataSnapshot.child(Person.EMAIL)));
                    person.setName(FirebaseParse.getString(dataSnapshot.child(Person.NAME)));
                    person.setPersonRole(FirebaseParse.getString(dataSnapshot.child(Person.PERSON_ROLE)));

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
                    Person person = getPersonWithId(dataSnapshot.getKey());
                    if (person == null) return;

                    persons.remove(person);
                    if (listView != null) {
                        adapter = new ListPersonAdapter(getActivity(), persons);
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
        if (persons == null){
            getAllPersons();
        }
    }

    private void getAllPersons(){
        new FirebasePersonGetAll(getActivity(), new FirebasePersonGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Person> _persons) {
                if (_persons == null) return;

                persons = _persons;
                if (listView != null) {
                    adapter = new ListPersonAdapter(getActivity(), persons);
                    listView.setAdapter(adapter);
                }
            }
        }).execute();
    }


    private Person getPersonWithId(String personId){
        if (personId == null || personId.isEmpty()) return null;
        if (persons == null || persons.isEmpty()) return null;

        for (Person person : persons) {
            if (person != null){
                if (person.getPersonId() != null){
                    if (person.getPersonId().equals(personId)) return person;
                }
            }
        }

        return null;
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
