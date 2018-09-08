package gr.eap.dxt.persons;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 12/2/2017.
 */

public class DialogPersonSelect extends Dialog{

    public interface Listener {
        void onPersonSelected(Person person);
    }
    private final Listener mListener;

    private final Context context;
    private ArrayList<Person> persons;
    private final String personRole;

    private ListView listView;

    public DialogPersonSelect(Context context, String personRole, Listener mListener) {
        super(context, R.style.my_dialog_no_title);
        this.context = context;
        this.personRole = personRole;
        this.mListener = mListener;
        persons = new ArrayList<>();
        setContext();
    }

    private void setContext(){
        setContentView(R.layout.dialog_person_select);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = (int) (metrics.heightPixels*0.8);
        int width = (int) (metrics.widthPixels*0.8);

        if ( getWindow() != null){
            getWindow().setLayout(width,height);
        }

        listView = (ListView) findViewById(R.id.my_list_view);
        if (listView != null){
            ListPersonAdapter adapter = new ListPersonAdapter(context, persons);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

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

                    dismiss();
                    if (mListener != null){
                        mListener.onPersonSelected(persons.get(position));
                    }
                }
            });
        }

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        if (backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    @Override
    public void show() {
        super.show();

        new FirebasePersonGetAll(context, personRole, new FirebasePersonGetAll.Listener() {
            @Override
            public void onResponse(ArrayList<Person> _persons, String errorMsg) {

                if (errorMsg != null && !errorMsg.isEmpty()){
                    MyAlertDialog.alertError(context, null, errorMsg);
                    return;
                }
                if (_persons == null){
                    MyAlertDialog.alertError(context, null, "persons == null");
                    return;
                }

                persons = new ArrayList<>();
                Person noPerson = new Person();
                noPerson.setName(context.getString(R.string.none));
                noPerson.setPersonId(AppShared.NO_ID);
                persons.add(noPerson);

                persons.addAll(_persons);

                if (listView != null) {
                    ListPersonAdapter adapter = new ListPersonAdapter(context, persons);
                    listView.setAdapter(adapter);
                }
            }
        }).execute();
    }
}
