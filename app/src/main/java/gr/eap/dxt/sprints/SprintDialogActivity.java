package gr.eap.dxt.sprints;

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
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogEditFragment;
import gr.eap.dxt.backlog.BacklogShowFragment;
import gr.eap.dxt.persons.Person;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.Keyboard;
import gr.eap.dxt.tools.MyAlertDialog;
import gr.eap.dxt.tools.MyRequestCodes;

/**
 * Created by GEO on 15/2/2017.
 */

public class SprintDialogActivity extends Activity
        implements SprintShowFragment.FragmentInteractionListener,
        SprintEditFragment.FragmentInteractionListener,
        BacklogShowFragment.FragmentInteractionListener,
        BacklogEditFragment.FragmentInteractionListener{

    /**
     * Static content for input
     */
    private static Sprint sprint;

    public static void setStaticContent(Sprint _sprint){
        clearStaticContent();
        // Create a copy from this sprint, so if cancel the changes are only on the copy, not on the original
        sprint = Sprint.getCopy(_sprint);
    }

    private static void clearStaticContent(){
        sprint = null;
    }
    /**
     * End of static content
     */

    private TextView myTitleTextView;
    private ImageButton backButton;
    private ImageButton saveButton;

    private boolean isSprintShowFragmentOn;
    private boolean isSprintEditFragmentOn;
    private boolean isBacklogShowFragmentOn;
    private boolean isBacklogEditFragmentOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity_sprint);

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
            goToSprintShowFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearStaticContent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MyRequestCodes.BACKLOG_EDIT_REQUEST){
            if (resultCode == RESULT_OK){
                // Backlog editted or added
                if (data != null){
                    if (data.getBooleanExtra(BacklogEditFragment.RELOAD, false)){
                        goToSprintShowFragment();
                    }
                }
            }
        }
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

    private void goToSprintShowFragment(){
        setDimensions();
        SprintShowFragment fragment = SprintShowFragment.newInstance(sprint);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        isSprintShowFragmentOn = true;
        isSprintEditFragmentOn = false;
        isBacklogShowFragmentOn = false;
        isBacklogEditFragmentOn = false;

        if (myTitleTextView != null){
            String name = sprint != null ? sprint.getName() : null;
            myTitleTextView.setText(name != null ? name : getString(R.string.sprint));
        }
        if (backButton != null){
            backButton.setImageResource(R.drawable.ic_action_back);
        }
        if (saveButton != null){
            saveButton.setVisibility(View.INVISIBLE);
        }
    }

    private void goToSprintEditFragment(Sprint mSprint){
        setDimensions();
        final SprintEditFragment fragment = SprintEditFragment.newInstance(mSprint);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        isSprintShowFragmentOn = false;
        isSprintEditFragmentOn = true;
        isBacklogShowFragmentOn = false;
        isBacklogEditFragmentOn = false;

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

    private void goToBacklogShowFragment(Backlog backlog){
        setDimensions();
        BacklogShowFragment fragment = BacklogShowFragment.newInstance(backlog, false);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        isSprintShowFragmentOn = false;
        isSprintEditFragmentOn = false;
        isBacklogShowFragmentOn = true;
        isBacklogEditFragmentOn = false;

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

    private void goToBacklogEditFragment(Backlog backlog, Person person, Sprint sprint, boolean isNew){
        setDimensions();

        final BacklogEditFragment fragment = BacklogEditFragment.newInstance(backlog, person, sprint,  BacklogEditFragment.BacklogEditFrom.SPRINT);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        isSprintShowFragmentOn = false;
        isSprintEditFragmentOn = false;
        isBacklogEditFragmentOn = true;
        isBacklogShowFragmentOn = false;

        if (myTitleTextView != null){
            if (isNew) {
                myTitleTextView.setText(R.string.new_backlog);
            }else{
                myTitleTextView.setText(R.string.edit);
            }
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

    private void handleBack(){
        Keyboard.close(this);
        if (isSprintShowFragmentOn) {
            finish();
            return;
        }
        if (isSprintEditFragmentOn) {
            finish();
            return;
        }
        if (isBacklogShowFragmentOn) {
            goToSprintShowFragment();
            return;
        }
        if (isBacklogEditFragmentOn) {
            goToSprintShowFragment();
        }
    }

    /** From {@link SprintShowFragment.FragmentInteractionListener} */
    @Override
    public void onSprintEdit(Sprint mSprint) {
        goToSprintEditFragment(mSprint);
    }

    /** From {@link SprintEditFragment.FragmentInteractionListener} */
    @Override
    public void onSprintChangesSaved(String errorMsg) {
        if (errorMsg != null && !errorMsg.isEmpty()) {
            AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
            MyAlertDialog.alertError(this, null, errorMsg);
        } else {
            Intent data = new Intent();
            data.putExtra(SprintEditFragment.RELOAD, true);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }

    /** From {@link SprintShowFragment.FragmentInteractionListener} */
    @Override
    public void onAddSprintBacklog(Sprint sprint) {
        if (sprint == null) return;
        if (sprint.getSprintId() == null || sprint.getSprintId().isEmpty()) return;

        Backlog backlog = new Backlog();
        backlog.setSprintId(sprint.getSprintId());

        goToBacklogEditFragment(backlog, null, sprint, true);
    }

    /** From {@link SprintShowFragment.FragmentInteractionListener} */
    @Override
    public void onOpenBacklog(Backlog backlog) {
        goToBacklogShowFragment(backlog);
    }

    /** From {@link  BacklogShowFragment.FragmentInteractionListener}*/
    @Override
    public void onBacklogEdit(Backlog mBacklog, Person person, Sprint sprint) {
        goToBacklogEditFragment(mBacklog, person, sprint, false);
    }

    /** from {@link BacklogEditFragment.FragmentInteractionListener} */
    @Override
    public void onBacklogChangesSaved(String errorMsg) {
        if (errorMsg != null && !errorMsg.isEmpty()){
            AppShared.writeErrorToLogString(getClass().toString(), errorMsg);
            MyAlertDialog.alertError(this, null, errorMsg);
        }else{
            goToSprintShowFragment();
        }
    }

}
