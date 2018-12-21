package tech.studiozebra.pytniznaci.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tech.studiozebra.pytniznaci.Config;
import tech.studiozebra.pytniznaci.R;
import tech.studiozebra.pytniznaci.models.ItemStoryList;
import com.squareup.picasso.Picasso;

import java.util.List;

import tech.studiozebra.pytniznaci.models.ItemStoryList;

public class RecyclerAdapterStory extends RecyclerView.Adapter<RecyclerAdapterStory.ViewHolder> {

    private Context context;
    private List<ItemStoryList> arrayItemStoryList;
    private ItemStoryList itemStoryList;
    private Context iamgecontext;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;
        public TextView sub_title;
        public LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(tech.studiozebra.pytniznaci.R.id.title);
            sub_title = (TextView) view.findViewById(tech.studiozebra.pytniznaci.R.id.sub_title);
            image = (ImageView) view.findViewById(tech.studiozebra.pytniznaci.R.id.image);
            linearLayout = (LinearLayout) view.findViewById(tech.studiozebra.pytniznaci.R.id.linearLayout);
        }

    }

    public RecyclerAdapterStory(Context context, List<ItemStoryList> arrayItemStoryList) {
        this.context = context;
        this.arrayItemStoryList = arrayItemStoryList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(tech.studiozebra.pytniznaci.R.layout.lsv_item_story_list, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemStoryList = arrayItemStoryList.get(position);

        holder.title.setText(itemStoryList.getStoryTitle());
        holder.sub_title.setText(itemStoryList.getStorySubTitle()) ;

        Picasso.with(holder.image.getContext()).load(Config.SERVER_URL + "/upload/thumbs/" +
                itemStoryList.getStoryImage()).placeholder(tech.studiozebra.pytniznaci.R.drawable.ic_loading).into(holder.image);


    }


    @Override
    public int getItemCount() {
        return arrayItemStoryList.size();
    }

}
