package com.qvdev.apps.everywhereis;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by QVDev on 4/27/14.
 */
public class CallEventItem implements EventItem {

    private static class ViewHolder {
        TextView title;
        TextView time;
    }

    private ViewHolder viewholder;

    private final String str1;
    private final String str2;

    public CallEventItem(String text1, String text2) {
        this.str1 = text1;
        this.str2 = text2;
    }

    @Override
    public int getViewType() {
        return EventListAdapter.RowType.CALL_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.call_list_item, null);

            viewholder = new ViewHolder();
            viewholder.title = (TextView) convertView.findViewById(R.id.call_title);
            viewholder.time = (TextView) convertView.findViewById(R.id.call_time);

            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.title.setText(str1);
        viewholder.time.setText(str2);

        return convertView;
    }
}
