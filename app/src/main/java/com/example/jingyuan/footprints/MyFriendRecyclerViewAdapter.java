package com.example.jingyuan.footprints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jingyuan on 12/10/17.
 */

public class MyFriendRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendRecyclerViewAdapter.ViewHolder> {
    private List<User> friends;
    // view types
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView username;
        public ImageView profile;
        private final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyFriendRecyclerViewAdapter(List<User> myDataset) {
        friends = myDataset;
    }

    private MyFriendRecyclerViewAdapter.OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyFriendRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        Context context = parent.getContext();
        View v = null;

        // View type: item
        if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(context).inflate(R.layout.friend_list, parent, false);
        } else if (viewType == TYPE_HEADER) {
            // Header
            v = LayoutInflater.from(context).inflate(R.layout.friend_list_header, parent, false);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view,(int)view.getTag());
                }
            }
        });

        // set the view's size, margins, paddings and layout parameters
        MyFriendRecyclerViewAdapter.ViewHolder vh = new ViewHolder(v);
        vh.username = v.findViewById(R.id.friends_username);
        vh.profile = v.findViewById(R.id.friends_profile);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyFriendRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.username.setText(friends.get(position).getUsername());
//        if (friends.get(position).getProfile() != null)
//            holder.profile.setImageBitmap(friends.get(position).getProfile());
        if(friends.get(position).getProfileByteArray().length > 0) {
            Bitmap myProfile = BitmapFactory.decodeByteArray(friends.get(position).getProfileByteArray(), 0, friends.get(position).getProfileByteArray().length);
            holder.profile.setImageBitmap(myProfile);
        }
        else {
            holder.profile.setImageResource(R.drawable.ic_person_black_24dp);
        }
        holder.itemView.setTag(position);
    }


    public void setOnItemClickListener(MyFriendRecyclerViewAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return friends.size();
    }

    // Get item view type according to the position
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    // Check if given position is a header
    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
