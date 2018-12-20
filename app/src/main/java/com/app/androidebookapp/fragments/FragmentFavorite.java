package com.app.androidebookapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.app.androidebookapp.Config;
import com.app.androidebookapp.R;
import com.app.androidebookapp.activities.ActivityPdfView;
import com.app.androidebookapp.activities.ActivityStory;
import com.app.androidebookapp.activities.MainActivity;
import com.app.androidebookapp.adapters.AdapterFavorite;
import com.app.androidebookapp.models.ItemBook;
import com.app.androidebookapp.utilities.AppBarLayoutBehavior;
import com.app.androidebookapp.utilities.Constant;
import com.app.androidebookapp.utilities.DbHandler;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    private MainActivity mainActivity;
    private Toolbar toolbar;
    private List<ItemBook> data = new ArrayList<ItemBook>();
    AdapterFavorite adapterReadLater;
    DbHandler databaseHandler;
    RecyclerView recyclerView;
    View lyt_no_read_later;
    private DbHandler dbHandler;
    private CharSequence charSequence = null;

    public FragmentFavorite() {
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_later, null);

        AppBarLayout appBarLayout = view.findViewById(R.id.tab_appbar_layout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        toolbar = view.findViewById(R.id.toolbar);
        setupToolbar();

        lyt_no_read_later = view.findViewById(R.id.lyt_no_read_later);
        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), Config.SPAN_COUNT);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        databaseHandler = new DbHandler(getActivity());
        data = databaseHandler.getAllData();
        adapterReadLater = new AdapterFavorite(getActivity(), recyclerView, data);
        recyclerView.setAdapter(adapterReadLater);

        if (data.size() == 0) {
            lyt_no_read_later.setVisibility(View.VISIBLE);
        } else {
            lyt_no_read_later.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {

        data = databaseHandler.getAllData();
        adapterReadLater = new AdapterFavorite(getActivity(), recyclerView, data);
        recyclerView.setAdapter(adapterReadLater);
        onItemClickListener();
        onItemOverflowClickListener();

        if (data.size() == 0) {
            lyt_no_read_later.setVisibility(View.VISIBLE);
        } else {
            lyt_no_read_later.setVisibility(View.INVISIBLE);
        }

        super.onResume();
    }

    public void onItemClickListener() {
        adapterReadLater.setOnItemClickListener(new AdapterFavorite.OnItemClickListener() {
            @Override
            public void onItemClick(View v, ItemBook obj, int position) {

                if (obj.getType().equals("pdf_upload")) {
                    Intent intent = new Intent(getActivity(), ActivityPdfView.class);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("TITLE", obj.getBook_name());
                    intent.putExtra("URL", Config.ADMIN_PANEL_URL + "/upload/pdf/" + obj.getPdf_name());
                    startActivity(intent);
                } else if (obj.getType().equals("pdf_url")) {
                    Intent intent = new Intent(getActivity(), ActivityPdfView.class);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("TITLE", obj.getBook_name());
                    intent.putExtra("URL", obj.getPdf_name());
                    startActivity(intent);
                } else {
                    Constant.CATEGORY_ID = obj.getBook_id();
                    Constant.CATEGORY_TITLE = obj.getBook_name();
                    Intent intent = new Intent(getActivity(), ActivityStory.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void onItemOverflowClickListener() {
        adapterReadLater.setOnItemOverflowClickListener(new AdapterFavorite.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final ItemBook obj, int position) {

                PopupMenu popup = new PopupMenu(getActivity(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_context_favorite:
                                if (charSequence.equals(getString(R.string.option_set_favorite))) {
                                    dbHandler.AddtoFavorite(new ItemBook(
                                            obj.getBook_id(),
                                            obj.getBook_name(),
                                            obj.getBook_image(),
                                            obj.getAuthor(),
                                            obj.getType(),
                                            obj.getPdf_name(),
                                            obj.getCount()
                                    ));
                                    //Toast.makeText(getActivity(), getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), obj.getBook_name() + " " + getString(R.string.favorite_added), Snackbar.LENGTH_SHORT).show();

                                } else if (charSequence.equals(getString(R.string.option_unset_favorite))) {
                                    dbHandler.RemoveFav(new ItemBook(obj.getBook_id()));
                                    //Toast.makeText(getActivity(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), obj.getBook_name() + " " + getString(R.string.favorite_removed), Snackbar.LENGTH_SHORT).show();
                                    refreshFragment();
                                }
                                return true;

                            case R.id.menu_context_share:

                                String share_title = android.text.Html.fromHtml(obj.getBook_name()).toString();
                                String share_content = android.text.Html.fromHtml(getResources().getString(R.string.share_content)).toString();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, share_title + "\n\n" + share_content + "\n\n" + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                return true;

                            default:
                        }
                        return false;
                    }
                });
                popup.show();

                dbHandler = new DbHandler(getActivity());
                List<ItemBook> data = dbHandler.getFavRow(obj.getBook_id());
                if (data.size() == 0) {
                    popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.option_set_favorite);
                    charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
                } else {
                    if (data.get(0).getBook_id().equals(obj.getBook_id())) {
                        popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.option_unset_favorite);
                        charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
                    }
                }

            }
        });
    }

    public void refreshFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setupNavigationDrawer(toolbar);
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        mainActivity.setSupportActionBar(toolbar);
    }

}
