package gr.eap.dxt.tools;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import gr.eap.dxt.R;

/**
 * Created by GEO on 19/2/2017.
 */

public class MySpinnerDialog {

    public interface MyListener {
        void onItemSelected(int position);
    }

    private Dialog dialog;
    private Context context;
    private String title;
    private ArrayList<String> values;
    private MyListener myListener;

    public MySpinnerDialog(Context context, String title, ArrayList<String> values, MyListener myListener) {
        dialog = new Dialog(context, R.style.my_dialog_no_title);
        this.context = context;
        this.title = title;
        this.values = new ArrayList<>();
        if (values != null && !values.isEmpty()){
            this.values.addAll(values);
        }
        this.myListener = myListener;

        setMyContentView();
    }

    private void setMyContentView(){
        if (dialog == null) return;

        dialog.setContentView(R.layout.dialog_my_spinner_dialog);

        final ListView listView = (ListView) dialog.findViewById(R.id.my_listview);
        if (listView != null){
            SpinnerSimpleAdapter adapter = new SpinnerSimpleAdapter(context, R.layout.spinner_simple_item_small_font, values);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (values == null){
                        AppShared.writeErrorToLogString(getClass().toString(), "values == null");
                        return;
                    }
                    int pos = i - listView.getHeaderViewsCount();
                    if (pos < 0){
                        AppShared.writeErrorToLogString(getClass().toString(), "pos < 0");
                        return;
                    }
                    if (pos >= values.size()){
                        AppShared.writeErrorToLogString(getClass().toString(), "pos >= values.size()");
                        return;
                    }

                    if (dialog != null) dialog.dismiss();
                    if (myListener != null) myListener.onItemSelected(pos);
                }
            });
        }

        TextView titleTextView = (TextView) dialog.findViewById(R.id.my_title_view);
        if (titleTextView != null){
            titleTextView.setText(title != null ? title : "");
        }

        ImageButton backButton = (ImageButton) dialog.findViewById(R.id.back);
        if (backButton != null){
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null) dialog.dismiss();
                }
            });
        }
    }

    public void show(){
        if (dialog != null) dialog.show();
    }

}
