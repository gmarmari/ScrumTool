package gr.eap.dxt.sprints;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import gr.eap.dxt.R;
import gr.eap.dxt.projects.Project;
import gr.eap.dxt.tools.Keyboard;

/**
 * Created by GEO on 15/2/2017.
 */

public class SprintNewDialogActivity  extends Activity {

    /* Static content for input */
    private static Project project;
    private static Date startDate;
    private static long durationDays;
    private static int oldNumber;
    public static void setStaticContent(Project _project, Date _startDate, long _durationDays, int _oldNumber){
        project = _project;
        startDate = _startDate;
        durationDays = _durationDays;
        oldNumber = _oldNumber;
    }
    private static void clearStaticContent(){
        project = null;
        startDate = null;
        durationDays = 0;
        oldNumber = 0;
    }
    /* End of static content */

    private SprintNewFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity_sprint_new);

        if (savedInstanceState == null) {
            fragment = SprintNewFragment.newInstance(project, startDate, durationDays, oldNumber);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }

        TextView myTitleTextView = (TextView) findViewById(R.id.my_title_view);
        if (myTitleTextView != null){
            if (startDate == null){
                myTitleTextView.setText(R.string.sprints_create);
            }else{
                myTitleTextView.setText(R.string.sprints_add);
            }

        }

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        if (backButton != null){
            backButton.setImageResource(R.drawable.ic_action_not_ok);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Keyboard.close(SprintNewDialogActivity.this);
                    finish();

                }
            });
        }

        ImageButton saveButton = (ImageButton) findViewById(R.id.save);
        if (saveButton != null){
            saveButton.setVisibility(View.VISIBLE);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fragment != null) fragment.saveChanges();
                }
            });
        }

        setDimensions();
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

}

