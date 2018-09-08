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
 * Created by GEO on 12/2/2017.
 */

public class ProjectNewDialogActivity extends Activity {

    private ProjectEditFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity_project);

        if (savedInstanceState == null) {
            fragment = ProjectEditFragment.newInstance(null);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }

        TextView myTitleTextView = (TextView) findViewById(R.id.my_title_view);
        if (myTitleTextView != null){
            myTitleTextView.setText(R.string.new_project);
        }

        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        if (backButton != null){
            backButton.setImageResource(R.drawable.ic_action_not_ok);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeKeyboardAndFinish();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDimensions();
    }

    private void closeKeyboardAndFinish(){
        Keyboard.close(this);
        finish();
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
