package com.example.jingyuan.footprints;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jingyuan on 11/30/17.
 */

public class MyJournalViewAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    public List<Journal> journals;

    public MyJournalViewAdapter() {
        super();
    }

    @Override
    public int getCount() {
        return journals.size();
    }

    @Override
    public Object getItem(int i) {
        return journals.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        // Use view holder to set tag for each view
        ViewHolder mHolder = null;

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.journal_list, null);

            // Get TextView
            mHolder = new ViewHolder();
            convertView.setTag(mHolder);
        }
        else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        // Set TextView


        return convertView;
    }

    final class ViewHolder {
    }
}
