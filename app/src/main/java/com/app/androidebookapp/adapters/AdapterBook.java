package com.app.androidebookapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.androidebookapp.Config;
import com.app.androidebookapp.R;
import com.app.androidebookapp.models.ItemBook;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBook extends RecyclerView.Adapter {

    private ArrayList<ItemBook> list;
    private Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private OnItemClickListener mOnItemClickListener;
    private OnItemOverflowClickListener mOnItemOverflowClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ItemBook obj, int position);
    }

    public interface OnItemOverflowClickListener {
        void onItemClick(View view, ItemBook obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void OnItemOverflowClickListener(final OnItemOverflowClickListener mOnItemOverflowClickListener) {
        this.mOnItemOverflowClickListener = mOnItemOverflowClickListener;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView book_image;
        private ImageView img_overflow;
        private TextView book_name, author;
        private MaterialRippleLayout lyt_parent;

        private MyViewHolder(View view) {
            super(view);
            book_image = (ImageView) view.findViewById(R.id.image);
            img_overflow = (ImageView) view.findViewById(R.id.img_overflow);
            book_name = (TextView) view.findViewById(R.id.book);
            author = (TextView) view.findViewById(R.id.author);
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

    public AdapterBook(Context context, ArrayList<ItemBook> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_book, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_loading, parent, false);
            return new ProgressViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).book_name.setText(list.get(position).getBook_name());
            ((MyViewHolder) holder).author.setText(list.get(position).getAuthor());
            Picasso.with(context)
                    .load(Config.ADMIN_PANEL_URL + "/upload/category/" + list.get(position).getBook_image().replace(" ", "%20"))
                    .placeholder(R.drawable.ic_loading)
                    .resize(200, 300)
                    .centerCrop()
                    .into(((MyViewHolder) holder).book_image);

            ((MyViewHolder) holder).lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, list.get(position), position);
                    }
                }
            });

            ((MyViewHolder) holder).img_overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemOverflowClickListener != null) {
                        mOnItemOverflowClickListener.onItemClick(view, list.get(position), position);
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

    public void setLoaded() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
        for (int i = 0; i < getItemCount(); i++) {
            if (list.get(i) == null) {
                list.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public boolean isHeader(int position) {
        return position == list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
    }

}