package gr.eap.dxt.backlog;

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

public class ListBacklogGroupedStatusAdapter extends BaseExpandableListAdapter{

    private Context context;
    private ArrayList<ListBacklogGroupedStatusParent> parents;
    public void setParents(ArrayList<ListBacklogGroupedStatusParent> parents){
        this.parents = parents != null ? parents : new ArrayList<ListBacklogGroupedStatusParent>();
    }

    public ListBacklogGroupedStatusAdapter(Context context, ArrayList<ListBacklogGroupedStatusParent> parents){
        this.context = context;
        this.parents = parents != null ? parents : new ArrayList<ListBacklogGroupedStatusParent>();
    }

    @Override
    public int getGroupCount() {
        if (parents == null){
            return 0;
        }
        return parents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (parents == null) return 0;
        if (groupPosition < 0 || groupPosition >= parents.size()) return 0;
        ListBacklogGroupedStatusParent parent = parents.get(groupPosition);
        if (parent.getChildren() == null) return 0;
        return parent.getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (parents == null) return null;
        if (groupPosition < 0 || groupPosition >= parents.size()) return null;
        return parents.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (parents == null) return null;
        if (groupPosition < 0 || groupPosition >= parents.size()) return null;
        ListBacklogGroupedStatusParent parent = parents.get(groupPosition);
        if (parent.getChildren() == null) return null;
        if (childPosition < 0 || childPosition >= parent.getChildren().size()) return null;
        return parent.getChildren().get(childPosition);
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
            convertView = infalInflater.inflate(R.layout.list_backlog_grouped_status_parent, parent, false);
        }

        if (parents == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "parents == null");
            return convertView;
        }
        if (groupPosition < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "groupPosition < 0");
            return convertView;
        }
        if (groupPosition >= parents.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "groupPosition >= parents.size()");
            return convertView;
        }

        ListBacklogGroupedStatusParent mParent = parents.get(groupPosition);
        if (mParent == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "mParent == null");
            return convertView;
        }

        TextView textView = (TextView) convertView.findViewById(R.id.backlog_type);
        if (textView != null){
            String text = "";
            String type = BacklogStatus.getBacklogStatus(context, mParent.getStatus());
            if (type != null && !type.isEmpty()){
                text += type;
            }
            if (mParent.getChildren() != null){
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
            convertView = infalInflater.inflate(R.layout.list_backlog_item, parent, false);
        }

        if (parents == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "parents == null");
            return convertView;
        }
        if (groupPosition < 0){
            AppShared.writeErrorToLogString(getClass().toString(), "groupPosition < 0");
            return convertView;
        }
        if (groupPosition >= parents.size()){
            AppShared.writeErrorToLogString(getClass().toString(), "groupPosition >= parents.size()");
            return convertView;
        }

        ListBacklogGroupedStatusParent mParent = parents.get(groupPosition);
        if (mParent == null) {
            AppShared.writeErrorToLogString(getClass().toString(), "mParent == null");
            return convertView;
        }
        ArrayList<Backlog> mChildren = mParent.getChildren();
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

        Backlog backlog = mChildren.get(childPosition);
        return ListBacklogAdapter.getBackLogViewForList(context, backlog, convertView, parent);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
