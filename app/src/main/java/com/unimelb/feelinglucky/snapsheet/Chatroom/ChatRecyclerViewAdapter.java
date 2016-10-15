package com.unimelb.feelinglucky.snapsheet.Chatroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimelb.feelinglucky.snapsheet.Chat.widget.FriendInfoAdapter;
import com.unimelb.feelinglucky.snapsheet.R;
import com.unimelb.feelinglucky.snapsheet.View.SlideableItem;

import java.util.List;

/**
 * Created by asahui on 10/10/2016.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mChatMessages;

    public ChatRecyclerViewAdapter(Context context, List<String> mChatMessages) {
        this.mContext = context;
        this.mChatMessages = mChatMessages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("messageAdapter", mChatMessages.get(position));
        holder.mItem.setMessage(mChatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public MessageSlideableItem mItem;

        public ViewHolder(View v) {
            super(v);
            mItem = (MessageSlideableItem) v;
        }
    }
}