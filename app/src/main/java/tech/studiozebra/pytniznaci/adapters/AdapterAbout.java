package tech.studiozebra.pytniznaci.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import tech.studiozebra.pytniznaci.R;

public class AdapterAbout extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] titleId;
    private final String[] subtitleId;
    private final Integer[] imageId;

    public AdapterAbout(Activity context, String[] titleId, String[] subtitleId, Integer[] imageId) {
        super(context, tech.studiozebra.pytniznaci.R.layout.lsv_item_about, titleId);
        this.context = context;
        this.titleId = titleId;
        this.subtitleId = subtitleId;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(tech.studiozebra.pytniznaci.R.layout.lsv_item_about, null, true);

        TextView title = (TextView) rowView.findViewById(tech.studiozebra.pytniznaci.R.id.title);
        TextView subtitle = (TextView) rowView.findViewById(tech.studiozebra.pytniznaci.R.id.subtitle);
        ImageView imageView = (ImageView) rowView.findViewById(tech.studiozebra.pytniznaci.R.id.image);

        title.setText(titleId[position]);
        subtitle.setText(subtitleId[position]);
        imageView.setImageResource(imageId[position]);

        return rowView;
    }
}