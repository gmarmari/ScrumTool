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
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.Keyboard;
import gr.eap.dxt.tools.MyAlertDialog;

/**
 * Created by GEO on 12/2/2017.
 */

public class BacklogNewDialogActivity extends Activity implements BacklogEditFragment.FragmentInteractionListener {


    private BacklogEditFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity_backlog);

        if (savedInstanceState == null) {
            fragment = BacklogEditFragment.newInstance(null, null, null,  BacklogEditFragment.BacklogEditFrom.NEW);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }

        TextView myTitleTextView = (TextView) findViewById(R.id.my_title_view);
        if (myTitleTextView != null){
            myTitleTextView.setText(R.string.new_backlog);
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

