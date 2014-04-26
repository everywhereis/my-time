package com.qvdev.apps.everywhereis;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by QVDev on 4/27/14.
 */
public interface EventItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}
