package com.example.jingyuan.footprints;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingyuan on 11/30/17.
 */

public class MyJournalViewAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    public List<Journal> journals;

    public MyJournalViewAdapter(Activity activity, List<Journal> journals) {
        this.activity = activity;
        this.journals = journals;
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
            mHolder.title = convertView.findViewById(R.id.list_title);
            mHolder.content = convertView.findViewById(R.id.list_content);
            mHolder.tag = convertView.findViewById(R.id.list_tag);
            mHolder.month = convertView.findViewById(R.id.list_mon);
            mHolder.date = convertView.findViewById(R.id.list_date);
            mHolder.location = convertView.findViewById(R.id.list_location);
            convertView.setTag(mHolder);
        }
        else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        // Set text
        mHolder.title.setText(journals.get(i).getTitle());
        mHolder.content.setText(journals.get(i).getContent());
        String tags = journals.get(i).getTags().toString();
        mHolder.tag.setText(tags.substring(1,tags.length() - 1));
        mHolder.month.setText(journals.get(i).getMonth());
        mHolder.date.setText(journals.get(i).getDate());
        mHolder.location.setText(journals.get(i).getLocation());

        return convertView;
    }

    final class ViewHolder {
        TextView title;
        TextView content;
        TextView tag;
        TextView month;
        TextView date;
        TextView location;
    }
}
