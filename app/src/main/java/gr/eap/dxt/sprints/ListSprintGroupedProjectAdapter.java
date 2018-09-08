package gr.eap.dxt.sprints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gr.eap.dxt.R;
import gr.eap.dxt.tools.AppShared;

/**
 * Created by GEO on 19/2/2017.
 */

public class ListSprintGroupedProjectAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ListSprintGroupedProjectParent mParent;
    public void setParent(ListSprintGroupedProjectParent parent){
        this.mParent = parent;
    }

    public ListSprintGroupedProjectAdapter(Context context, ListSprintGroupedProjectParent parent){
        this.context = context;
        this.mParent = parent;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mParent == null) return 0;
        if (mParent.getChildren() == null) return 0;
        return mParent.getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mParent;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (mParent == null) return null;
        if (mParent.getChildren() == null) return null;
        if (childPosition < 0 || childPosition >= mParent.getChildren().size()) return null;
        return mParent.getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_sprint_grouped_project_parent, parent, false);
        }

        if (mParent == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "mParent == null");
            return convertView;
        }

        TextView textView = (TextView) convertView.findViewById(R.id.sprints);
        if (textView != null){
            String text = context.getString(R.string.sprints);
            if (mParent.getChildren() != null && mParent.getChildren().size() >= 0){
                if (!text.isEmpty()) text += " ";
                text += "(" + mParent.getChildren().size() + ")";
            }
            textView.setText(text);
        }

        ImageView toogleView = (ImageView) convertView.findViewById(R.id.toogle);
        if (toogleView != null){
            if (mParent.isOn()){
                toogleView.setImageResource(R.drawable.ic_action_less_purple);
            }else{
                toogleView.setImageResource(R.drawable.ic_action_more_purple);
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_sprint_item, parent, false);
        }

        if (mParent == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "mParent == null");
            return convertView;
        }
        ArrayList<Sprint> mChildren = mParent.getChildren();
        if (mChildren == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "mChildren == null");
            return convertView;
        }
        if (childPosition < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "childPosition < 0");
            return convertView;
        }
        if (childPosition >= mChildren.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "childPosition >= mChildren.size()");
            return convertView;
        }

        Sprint sprint = mChildren.get(childPosition);
        return ListSprintAdapter.getSprintViewForList(context, sprint, convertView, parent);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
