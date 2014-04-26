package com.qvdev.apps.everywhereis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by QVDev on 4/27/14.
 */
public class EventListAdapter extends ArrayAdapter<EventItem> {

    private List<EventItem> items;
    private LayoutInflater inflater;

    public enum RowType {
        // Here we have two items types, you can have as many as you like though
        CALL_ITEM,
    }

    public EventListAdapter(Context context, LayoutInflater inflater, List<EventItem> items) {
        super(context, 0, items);
        this.items = items;
        this.inflater = inflater;
    }

    @Override
    public int getViewTypeCount() {
        // Get the number of items in the enum
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        // Use getViewType from the Item interface
        return items.get(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Use getView from the Item interface
        return items.get(position).getView(inflater, convertView);
    }
}
