package com.solodroid.androidebookappdemo.fragments;

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

import com.solodroid.androidebookappdemo.R;
import com.solodroid.androidebookappdemo.adapters.AdapterAbout;

public class FragmentAbout extends Fragment {

    ListView list;
    String[] titleId;
    String[] subtitleId;

    Integer[] imageId = {
            R.drawable.ic_other_appname,
            R.drawable.ic_other_build,
            R.drawable.ic_other_email,
            R.drawable.ic_other_copyright,
            R.drawable.ic_other_rate,
            R.drawable.ic_other_more

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_about, container, false);

        titleId = getResources().getStringArray(R.array.title);
        subtitleId = getResources().getStringArray(R.array.subtitle);

        AdapterAbout adapter = new AdapterAbout(getActivity(), titleId, subtitleId, imageId);
        list = (ListView) v.findViewById(R.id.list);
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
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_apps))));
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