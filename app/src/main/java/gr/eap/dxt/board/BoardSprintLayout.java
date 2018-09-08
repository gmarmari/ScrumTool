package gr.eap.dxt.board;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import gr.eap.dxt.R;
import gr.eap.dxt.sprints.Sprint;
import gr.eap.dxt.sprints.SprintStatus;
import gr.eap.dxt.tools.AppShared;
import gr.eap.dxt.tools.MyColor;

/**
 * Created by GEO on 7/4/2017.
 */

public class BoardSprintLayout extends LinearLayout {

    public interface Listener {
        void onSprintSelected(Sprint sprint);
    }
    private Listener myListener;

    private Context context;
    private View view;

    private Sprint sprint;
    private int marginTop;
    private int height;

    public BoardSprintLayout(Context context){
        super(context);
    }

    public BoardSprintLayout(Context context, Sprint sprint, int marginTop, int height, Listener myListener) {
        super(context);
        this.context = context;
        this.sprint = sprint;
        this.marginTop = marginTop > 0 ? marginTop : 0;
        this.height = height > 0 ? height : 0;
        this.myListener = myListener;
    }

    public boolean setContent(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.linear_layout_board_sprint, this, true);

        if (view == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "view == null");
            return false;
        }
        if (sprint == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "sprint == null");
            return false;
        }

        TextView statusTextView = (TextView) view.findViewById(R.id.status);
        if (statusTextView != null){
            String status = SprintStatus.getSprintStatus(context, sprint.getStatus());
            Integer color = SprintStatus.getSprintStatusColor(context, sprint.getStatus());
            if (color == null){
                color = MyColor.getColorAccordingToAndroidVersion(context, R.color.color_eap_logo_dark_blue);
            }
            statusTextView.setText(status != null ? status : "");
            statusTextView.setTextColor(color);
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.sprint_name);
        if (nameTextView != null){
            String details = "";
            if (sprint.getName() != null && !sprint.getName().isEmpty()){
                details += sprint.getName();
            }

            nameTextView.setText(details);
        }

        TextView dateTextView = (TextView) view.findViewById(R.id.sprint_date);
        if (dateTextView != null) {
            String date = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.my_date_format_sprint), Locale.getDefault());
            if (sprint.getStartDate() != null){
                date += dateFormat.format(sprint.getStartDate());
            }
            if (sprint.getEndDate() != null){
                date += " - ";
                date += dateFormat.format(sprint.getEndDate());
            }
            if (sprint.getDuration() != null && sprint.getDuration() > 0){
                date += "\n" + sprint.getDuration() + " ";
                if (sprint.getDuration() == 1){
                    date += context.getString(R.string.day).toLowerCase();
                }else{
                    date += context.getString(R.string.days).toLowerCase();
                }
            }
            dateTextView.setText(date);
        }

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (myListener != null) myListener.onSprintSelected(sprint);

            }
        });

        setMyLayoutParams();

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setMyLayoutParams();
    }

    private void setMyLayoutParams(){
        try {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.height = height;
            params.setMargins(0, marginTop, 0, 0);
            view.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
            AppShared.writeErrorToLogString(getClass().toString(), e.toString());
        }

    }
}

