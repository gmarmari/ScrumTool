package gr.eap.dxt.persons;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.Keyboard;

/**
 * Created by GEO on 5/2/2017.
 */

public class PersonDialogActivity extends Activity implements PersonShowFragment.FragmentInteractionListener{

    public static final String RELOAD = "gr.eap.dxt.persons.PersonDialogActivity.RELOAD";

    /**
     * Static content for input
     */
    private static Person person;

    public static void setStaticContent(Person _person){
        clearStaticContent();
        // Create a copy from this person, so if cancel the changes are only on the copy, not on the original person
        person = Person.getCopy(_person);
    }

    private static void clearStaticContent(){
        person = null;
    }
    /**
     * End of static content
     */


    private TextView myTitleTextView;
    private ImageButton backButton;
    private ImageButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity_person);

        myTitleTextView = (TextView) findViewById(R.id.my_title_view);

        backButton = (ImageButton) findViewById(R.id.back);
        if (backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleBack();
                }
            });
        }

        saveButton = (ImageButton) findViewById(R.id.save);
        setDimensions();

        if (savedInstanceState == null) {
            goToPersonShowFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearStaticContent();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDimensions();
    }

    private void setDimensions(){
        if(getResources().getBoolean(R.bool.large_screen)){
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int height = (int) (metrics.heightPixels*0.8);
            int width = (int) (metrics.widthPixels*0.8);
            getWindow().setLayout(width,height);
            return;
        }
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void handleBack(){
        Keyboard.close(this);
        finish();
    }

    private void goToPersonShowFragment(){
        setDimensions();
        PersonShowFragment fragment = PersonShowFragment.newInstance(person);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        if (myTitleTextView != null){
            String name = person != null ? person.getName() : null;
            myTitleTextView.setText(name != null ? name : "");
        }
        if (backButton != null){
            backButton.setImageResource(R.drawable.ic_action_back);
        }
        if (saveButton != null){
            saveButton.setVisibility(View.INVISIBLE);
        }
    }

    private void goToPersonEditFragment(){
        setDimensions();
        final PersonEditFragment fragment = PersonEditFragment.newInstance(person);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        if (myTitleTextView != null){
            myTitleTextView.setText(R.string.edit);
        }
        if (backButton != null){
            backButton.setImageResource(R.drawable.ic_action_not_ok);
        }
        if (saveButton != null){
            saveButton.setVisibility(View.VISIBLE);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fragment != null) fragment.saveChanges();
                }
            });
        }
    }

    /** From PersonShowFragment.FragmentInteractionListener */
    @Override
    public void onPersonEdit() {
        goToPersonEditFragment();
    }

}