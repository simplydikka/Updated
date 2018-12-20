package com.app.androidebookapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.androidebookapp.R;
import com.app.androidebookapp.models.ItemStory;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;

public class AdapterStory extends RecyclerView.Adapter {

    private ArrayList<ItemStory> list;
    private Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ItemStory obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, subtitle;
        public MaterialRippleLayout lyt_parent;

        private MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.sub_title);
            lyt_parent = (MaterialRippleLayout) view.findViewById(R.id.lyt_parent);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {

        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    public AdapterStory(Context context, ArrayList<ItemStory> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_story_list, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_loading, parent, false);
            return new ProgressViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {

            ((MyViewHolder) holder).title.setText(list.get(position).getStory_title());
            ((MyViewHolder) holder).subtitle.setText(list.get(position).getStory_subtitle());

            ((MyViewHolder) holder).lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, list.get(position), position);
                    }
                }
            });

        } else {
            if (getItemCount() == 1) {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return position == list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
    }
}