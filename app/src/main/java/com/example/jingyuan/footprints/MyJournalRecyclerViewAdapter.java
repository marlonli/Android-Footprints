package com.example.jingyuan.footprints;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jingyuan on 12/6/17.
 */

public class MyJournalRecyclerViewAdapter extends RecyclerView.Adapter<MyJournalRecyclerViewAdapter.ViewHolder> {
    private List<Journal> journals;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView content;
        public TextView tag;
        public TextView month;
        public TextView date;
        public TextView location;
        private final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyJournalRecyclerViewAdapter(List<Journal> myDataset) {
        journals = myDataset;
    }

    private OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyJournalRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journal_list, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view,(int)view.getTag());
                }
            }
        });

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        vh.title = v.findViewById(R.id.list_title);
        vh.content = v.findViewById(R.id.list_content);
        vh.tag = v.findViewById(R.id.list_tag);
        vh.month = v.findViewById(R.id.list_mon);
        vh.date = v.findViewById(R.id.list_date);
        vh.location = v.findViewById(R.id.list_location);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(journals.get(position).getTitle());
        holder.content.setText(journals.get(position).getContent());
        String tags = journals.get(position).getTags().toString();
        holder.tag.setText(tags.substring(1,tags.length() - 1));
        // Date format: Sat Dec 02 19:19:45 EST 2017
        holder.month.setText(journals.get(position).getDateTimeString().toString().split(" ")[1]);
        holder.date.setText(journals.get(position).getDateTimeString().toString().split(" ")[2]);
        // TODO: lat, lng to address
        holder.location.setText(journals.get(position).getLat() + journals.get(position).getLng());
        holder.itemView.setTag(position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return journals.size();
    }
}
