package com.example.viet.chatapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viet on 23/08/2017.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;
    public static final int TYPE_IMAGE = 2;

    private ArrayList<Chat> mArrChat;
    private UserManager mUserManager = UserManager.getsInstance();
    private Context mContext;

    public ChatRecyclerViewAdapter(ArrayList<Chat> mArrChat) {
        this.mArrChat = mArrChat;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        if (viewType == TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right_recycler_view, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left_recycler_view, parent, false);
            return new ChatViewHolder(view);
        }
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_image_recycler_view, parent, false);
            return new ImageViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mArrChat.get(position).getType().equals("image")) {
            return TYPE_IMAGE;
        }
        if (mArrChat.get(position).getDisplayName().equals(mUserManager.getmDisplayName())) {
            return TYPE_LEFT;
        } else {
            return TYPE_RIGHT;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = mArrChat.get(position);
        if (holder instanceof ChatViewHolder) {
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            chatViewHolder.tvContent.setText(chat.getContent());
            chatViewHolder.tvDisplayName.setText(chat.getDisplayName());
//            if (mOnItemBinded != null) {
//                mOnItemBinded.onBinded(getItemCount());
//            }
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            Glide.with(mContext).load(chat.getContent()).centerCrop().crossFade(200).into(imageViewHolder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return mArrChat.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvContent)
        TextView tvContent;

        @BindView(R.id.tvDisplayName)
        TextView tvDisplayName;

        public ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        ImageView ivImage;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArrayList<Chat> getmArrChat() {
        return mArrChat;
    }

    public void setmOnItemBinded(OnItemBinded mOnItemBinded) {
        this.mOnItemBinded = mOnItemBinded;
    }

    private OnItemBinded mOnItemBinded;

    public interface OnItemBinded {
        void onBinded(int size);
    }
}
