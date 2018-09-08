package gr.eap.dxt.board;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import gr.eap.dxt.R;
import gr.eap.dxt.backlog.Backlog;
import gr.eap.dxt.backlog.BacklogType;
import gr.eap.dxt.tools.AppShared;

/**
 * Created by GEO on 7/4/2017.
 */

public class BoardBacklogLayout extends LinearLayout {

    public interface Listener {
        void onBacklogSelected(Backlog backlog);
    }
    private Listener myListener;

    private Context context;
    private View view;

    private Backlog backlog;
    private int marginTop;
    private int height;

    public BoardBacklogLayout(Context context){
        super(context);
    }

    public BoardBacklogLayout(Context context, Backlog backlog, int marginTop, int height, Listener myListener) {
        super(context);
        this.context = context;
        this.backlog = backlog;
        this.marginTop = marginTop > 0 ? marginTop : 0;
        this.height = height > 0 ? height : 0;
        this.myListener = myListener;
    }

    public boolean setContent(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.linear_layout_board_backlog, this, true);

        if (view == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "view == null");
            return false;
        }
        if (backlog == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "sprint == null");
            return false;
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.backlog_name);
        if (nameTextView != null){
            String name = "";

            String type = BacklogType.getBacklogType(context, backlog.getType());
            if (type != null && !type.isEmpty()) {
                name += type;
            }
            if (backlog.getName() != null && !backlog.getName().isEmpty()){
                if (!name.isEmpty()) name += ": ";
                name += backlog.getName();
            }

            nameTextView.setText(name);
        }

        TextView detailsTextView = (TextView) view.findViewById(R.id.backlog_details);
        if (detailsTextView != null) {
            String details = "";
            if (backlog.getDuration() != null && backlog.getDuration() > 0){
                details += backlog.getDuration();
                if (backlog.getDuration() == 1){
                    details += " " + context.getString(R.string.day).toLowerCase();
                }else{
                    details += " " + context.getString(R.string.days).toLowerCase();
                }
            }

            if (backlog.getDescription() != null && !backlog.getDescription().isEmpty()){
                if (!details.isEmpty()) details += "\n";
                details += backlog.getDescription();
            }

            detailsTextView.setText(details);
        }

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (myListener != null) myListener.onBacklogSelected(backlog);

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


