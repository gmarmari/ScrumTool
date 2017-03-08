package gr.eap.dxt.projects;

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
 * Created by GEO on 11/2/2017.
 */

public class ProjectDialogActivity extends Activity implements ProjectShowFragment.FragmentInteractionListener{

    /**
     * Static content for input
     */
    private static Project project;

    public static void setStaticContent(Project _project){
        clearStaticContent();
        // Create a copy from this project, so if cancel the changes are only on the copy, not on the original
        project = Project.getCopy(_project);
    }

    private static void clearStaticContent(){
        project = null;
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
        setContentView(R.layout.dialog_activity_project);

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
            goToProjectShowFragment();
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

    private void goToProjectShowFragment(){
        setDimensions();
        ProjectShowFragment fragment = ProjectShowFragment.newInstance(project);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        if (myTitleTextView != null){
            String name = project != null ? project.getName() : null;
            myTitleTextView.setText(name != null ? name : getString(R.string.project));
        }
        if (backButton != null){
            backButton.setImageResource(R.drawable.ic_action_back);
        }
        if (saveButton != null){
            saveButton.setVisibility(View.INVISIBLE);
        }
    }

    private void goToProjectEditFragment(){
        setDimensions();
        final ProjectEditFragment fragment = ProjectEditFragment.newInstance(project);
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

    @Override
    public void onProjectEdit() {
        goToProjectEditFragment();
    }
}
