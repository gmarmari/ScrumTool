package gr.eap.dxt.backlog;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import gr.eap.dxt.R;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.Keyboard;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogDialogActivity extends Activity implements
        BacklogShowFragment.FragmentInteractionListener,
        BacklogEditFragment.FragmentInteractionListener{

    /**
     * Static content for input
     */
    private static Backlog backlog;

    public static void setStaticContent(Backlog _backlog){
        clearStaticContent();
        // Create a copy from this backlog, so if cancel the changes are only on the copy, not on the original person
        backlog = Backlog.getCopy(_backlog);
    }

    private static void clearStaticContent(){
        backlog = null;
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
        setContentView(R.layout.dialog_activity_backlog);

        myTitleTextView = (TextView) findViewById(R.id.my_title_view);

        backButton = (ImageButton) findViewById(R.id.back);
        if (backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Keyboard.close(BacklogDialogActivity.this);
                    finish();
                }
            });
        }

        saveButton = (ImageButton) findViewById(R.id.save);
        setDimensions();

        if (savedInstanceState == null) {
            goToBacklogShowFragment();
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


    private void goToBacklogShowFragment(){
        setDimensions();
        BacklogShowFragment fragment = BacklogShowFragment.newInstance(backlog, true);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        if (myTitleTextView != null){
            String name = backlog != null ? backlog.getName() : null;
            myTitleTextView.setText(name != null ? name : getString(R.string.backlog));
        }
        if (backButton != null){
            backButton.setImageResource(R.drawable.ic_action_back);
        }
        if (saveButton != null){
            saveButton.setVisibility(View.INVISIBLE);
        }
    }

    private void goToBacklogEditFragment(Backlog mBacklog,  Person person, Sprint sprint){
        setDimensions();
        final BacklogEditFragment fragment = BacklogEditFragment.newInstance(mBacklog, person, sprint, BacklogEditFragment.BacklogEditFrom.EDIT);
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

    /** from {@link BacklogShowFragment.FragmentInteractionListener} */
    @Override
    public void onBacklogEdit(Backlog mBacklog, Person person, Sprint sprint) {
        goToBacklogEditFragment(mBacklog, person, sprint);
    }

    /** from {@link BacklogEditFragment.FragmentInteractionListener} */
    @Override
    public void onBacklogChangesSaved(String errorMsg) {
        if (errorMsg != null && !errorMsg.isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
            MyAlertDialog.alertError(this, null, errorMsg);
        }else{
            Intent data = new Intent();
            data.putExtra(BacklogEditFragment.RELOAD, true);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}

