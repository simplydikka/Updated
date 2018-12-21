package tech.studiozebra.pytniznaci.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import tech.studiozebra.pytniznaci.R;
import tech.studiozebra.pytniznaci.adapters.AdapterAbout;

public class FragmentAbout extends Fragment {

    ListView list;
    String[] titleId;
    String[] subtitleId;

    Integer[] imageId = {
            tech.studiozebra.pytniznaci.R.drawable.ic_other_appname,
            tech.studiozebra.pytniznaci.R.drawable.ic_other_build,
            tech.studiozebra.pytniznaci.R.drawable.ic_other_email,
            tech.studiozebra.pytniznaci.R.drawable.ic_other_copyright,
            tech.studiozebra.pytniznaci.R.drawable.ic_other_rate,
            tech.studiozebra.pytniznaci.R.drawable.ic_other_more

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(tech.studiozebra.pytniznaci.R.layout.activity_about, container, false);

        titleId = getResources().getStringArray(tech.studiozebra.pytniznaci.R.array.title);
        subtitleId = getResources().getStringArray(tech.studiozebra.pytniznaci.R.array.subtitle);

        AdapterAbout adapter = new AdapterAbout(getActivity(), titleId, subtitleId, imageId);
        list = (ListView) v.findViewById(tech.studiozebra.pytniznaci.R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    final String appName = getActivity().getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                }
                if (position == 5) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(tech.studiozebra.pytniznaci.R.string.more_apps))));
                }
            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}