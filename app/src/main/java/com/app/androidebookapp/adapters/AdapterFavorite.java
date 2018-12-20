package com.app.androidebookapp.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.androidebookapp.Config;
import com.app.androidebookapp.R;
import com.app.androidebookapp.models.ItemBook;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private List<ItemBook> items = new ArrayList<>();
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemClickListener mOnItemOverflowClickListener;
    private ItemBook pos;
    private CharSequence charSequence = null;

    public interface OnItemClickListener {
        void onItemClick(View view, ItemBook obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setOnItemOverflowClickListener(final OnItemClickListener mItemOverflowClickListener) {
        this.mOnItemOverflowClickListener = mItemOverflowClickListener;
    }

    public AdapterFavorite(Context context, RecyclerView view, List<ItemBook> items) {
        this.items = items;
        this.context = context;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        private ImageView book_image;
        private ImageView img_overflow;
        private TextView book_name, author;
        private MaterialRippleLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);

            book_image = (ImageView) v.findViewById(R.id.image);
            img_overflow = (ImageView) v.findViewById(R.id.img_overflow);
            book_name = (TextView) v.findViewById(R.id.book);
            author = (TextView) v.findViewById(R.id.author);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_book, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof OriginalViewHolder) {

            final ItemBook p = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.book_name.setText(p.book_name);
            vItem.author.setText(p.author);

            Picasso
                    .with(context)
                    .load(Config.ADMIN_PANEL_URL + "/upload/category/" + p.book_image.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_loading)
                    .resize(200, 300)
                    .centerCrop()
                    .into(vItem.book_image);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, p, position);
                    }
                }
            });

            vItem.img_overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemOverflowClickListener != null) {
                        mOnItemOverflowClickListener.onItemClick(view, p, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }


    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / 100;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}