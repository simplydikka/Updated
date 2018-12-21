package tech.studiozebra.pytniznaci.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tech.studiozebra.pytniznaci.Config;
import tech.studiozebra.pytniznaci.R;
import tech.studiozebra.pytniznaci.models.ItemBooks;
import com.squareup.picasso.Picasso;

import java.util.List;

import tech.studiozebra.pytniznaci.models.ItemBooks;

public class RecyclerAdapterBook extends RecyclerView.Adapter<RecyclerAdapterBook.ViewHolder> {

    private Context context;
    private List<ItemBooks> arrayItemBooks;
    private ItemBooks itemBooks;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView book;
        public TextView author;

        public ViewHolder(View view) {
            super(view);

            book = (TextView) view.findViewById(tech.studiozebra.pytniznaci.R.id.book);
            author = (TextView) view.findViewById(tech.studiozebra.pytniznaci.R.id.author);
            image = (ImageView) view.findViewById(tech.studiozebra.pytniznaci.R.id.image);

        }

    }

    public RecyclerAdapterBook(Context context, List<ItemBooks> arrayItemBooks) {
        this.context = context;
        this.arrayItemBooks = arrayItemBooks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(tech.studiozebra.pytniznaci.R.layout.lsv_item_books, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemBooks = arrayItemBooks.get(position);

        holder.book.setText(itemBooks.getBookName());
        holder.author.setText(itemBooks.getBookAuthorName());

        Picasso.with(context).load(Config.SERVER_URL + "/upload/category/" +
                itemBooks.getCategoryImageurl()).placeholder(tech.studiozebra.pytniznaci.R.drawable.ic_loading).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return arrayItemBooks.size();
    }

}
