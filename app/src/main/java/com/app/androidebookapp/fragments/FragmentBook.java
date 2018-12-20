package com.app.androidebookapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.androidebookapp.Config;
import com.app.androidebookapp.R;
import com.app.androidebookapp.activities.ActivityPdfView;
import com.app.androidebookapp.activities.ActivityStory;
import com.app.androidebookapp.activities.MainActivity;
import com.app.androidebookapp.adapters.AdapterBook;
import com.app.androidebookapp.models.ItemBook;
import com.app.androidebookapp.utilities.AppBarLayoutBehavior;
import com.app.androidebookapp.utilities.Constant;
import com.app.androidebookapp.utilities.DbHandler;
import com.app.androidebookapp.utilities.EndlessRecyclerViewScrollListener;
import com.app.androidebookapp.utilities.GDPR;
import com.app.androidebookapp.utilities.ItemOffsetDecoration;
import com.app.androidebookapp.utilities.Utils;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentBook extends Fragment {

    private MainActivity mainActivity;
    private Toolbar toolbar;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    AdapterBook adapterBook;
    ArrayList<ItemBook> itemBooks;
    ItemBook itemBook;
    TextView txt_no;
    Boolean isOver = false;
    SwipeRefreshLayout swipeRefreshLayout = null;
    EndlessRecyclerViewScrollListener scrollListener;
    private DbHandler dbHandler;
    private CharSequence charSequence = null;
    private InterstitialAd interstitialAd;
    int counter = 1;

    public FragmentBook() {
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        AppBarLayout appBarLayout = view.findViewById(R.id.tab_appbar_layout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);

        loadInterstitialAd();

        toolbar = view.findViewById(R.id.toolbar);
        setupToolbar();

        txt_no = view.findViewById(R.id.lyt_no_item);

        itemBooks = new ArrayList<ItemBook>();

        gridLayoutManager = new GridLayoutManager(getActivity(), Config.SPAN_COUNT);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapterBook.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.book_space);
        recyclerView.addItemDecoration(itemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                itemBooks.add(null);
                adapterBook.notifyItemInserted(itemBooks.size() - 1);
                itemBooks.remove(itemBooks.size() - 1);
                adapterBook.notifyItemRemoved(itemBooks.size());
                if (!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api/api.php?get_book");
                        }
                    }, Constant.DELAY_LOAD_MORE);
                } else {
                    adapterBook.hideHeader();
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                resetAdapter();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshContent();
                    }
                }, Constant.DELAY_LOAD_MORE);
            }
        });

        if (Utils.isNetworkAvailable(getActivity())) {
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api/api.php?get_book");
        } else {
            Toast.makeText(getActivity(), "failed to connect network!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public void resetAdapter() {
        recyclerView.getRecycledViewPool().clear();
        itemBooks.clear();
        adapterBook.notifyDataSetChanged();
        //scrollListener.resetState();
    }

    public void refreshContent() {
        if (Utils.isNetworkAvailable(getActivity())) {
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api/api.php?get_book");
        } else {
            Toast.makeText(getActivity(), "please enable your network to refresh content", Toast.LENGTH_SHORT).show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (itemBooks.size() == 0) {
                swipeRefreshLayout.setRefreshing(true);
            }
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return Utils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (itemBooks.size() == 0) {
                swipeRefreshLayout.setRefreshing(false);
            }
            recyclerView.setVisibility(View.VISIBLE);


            if (null == result || result.length() == 0) {
                adapterBook.hideHeader();
                Toast.makeText(getActivity(), "Something Went Wrong! Please check your internet and reload", Toast.LENGTH_SHORT).show();
            }

            try {
                JSONArray jsonArray = new JSONObject(result).getJSONArray(Constant.JSON_ARRAY_NAME);

                if (jsonArray.length() <= itemBooks.size() + Config.LOAD_MORE) {
                    isOver = true;
                }

                int a = itemBooks.size();

                for (int i = a; i < a + Config.LOAD_MORE; i++) {
                    JSONObject objJson = jsonArray.getJSONObject(i);
                    String book_id = objJson.getString(Constant.BOOK_ID);
                    String book_name = objJson.getString(Constant.BOOK_NAME);
                    String book_image = objJson.getString(Constant.BOOK_IMAGE);
                    String author = objJson.getString(Constant.AUTHOR);
                    String type = objJson.getString(Constant.TYPE);
                    String pdf_name = objJson.getString(Constant.PDF_NAME);
                    String count = objJson.getString(Constant.COUNT);
                    ItemBook itemCategory = new ItemBook(book_id, book_name, book_image, author, type, pdf_name, count);
                    itemBooks.add(itemCategory);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                isOver = true;
            }
            setAdapterToRecyclerView();
            onItemClickListener();
            onItemOverflowClickListener();

        }
    }

    public void setAdapterToRecyclerView() {
        if (itemBooks.size() < Config.LOAD_MORE + 1) {
            adapterBook = new AdapterBook(getActivity(), itemBooks);
            recyclerView.setAdapter(adapterBook);
        }
        adapterBook.notifyDataSetChanged();
        setEmptyText();
    }

    private void setEmptyText() {
        if (adapterBook.getItemCount() == 0) {
            txt_no.setVisibility(View.VISIBLE);
        } else {
            txt_no.setVisibility(View.INVISIBLE);
        }
    }

    public void onItemClickListener() {
        adapterBook.setOnItemClickListener(new AdapterBook.OnItemClickListener() {
            @Override
            public void onItemClick(View v, ItemBook obj, int position) {
                itemBook = itemBooks.get(position);

                if (itemBook.getType().equals("pdf_upload")) {
                    Intent intent = new Intent(getActivity(), ActivityPdfView.class);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("TITLE", itemBook.getBook_name());
                    intent.putExtra("URL", Config.ADMIN_PANEL_URL + "/upload/pdf/" + itemBook.getPdf_name());
                    startActivity(intent);
                } else if (itemBook.getType().equals("pdf_url")) {
                    Intent intent = new Intent(getActivity(), ActivityPdfView.class);
                    intent.putExtra("POSITION", position);
                    intent.putExtra("TITLE", itemBook.getBook_name());
                    intent.putExtra("URL", itemBook.getPdf_name());
                    startActivity(intent);
                } else {
                    Constant.CATEGORY_ID = itemBook.getBook_id();
                    Constant.CATEGORY_TITLE = itemBook.getBook_name();
                    Intent intent = new Intent(getActivity(), ActivityStory.class);
                    startActivity(intent);
                }
                showInterstitialAd();
            }
        });
    }

    public void onItemOverflowClickListener() {
        adapterBook.OnItemOverflowClickListener(new AdapterBook.OnItemOverflowClickListener() {
            @Override
            public void onItemClick(View v, ItemBook obj, int position) {
                itemBook = itemBooks.get(position);

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
                                            itemBook.getBook_id(),
                                            itemBook.getBook_name(),
                                            itemBook.getBook_image(),
                                            itemBook.getAuthor(),
                                            itemBook.getType(),
                                            itemBook.getPdf_name(),
                                            itemBook.getCount()
                                    ));
                                    //Toast.makeText(getActivity(), getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), itemBook.getBook_name() + " " + getString(R.string.favorite_added), Snackbar.LENGTH_SHORT).show();

                                } else if (charSequence.equals(getString(R.string.option_unset_favorite))) {
                                    dbHandler.RemoveFav(new ItemBook(itemBook.getBook_id()));
                                    //Toast.makeText(getActivity(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), itemBook.getBook_name() + " " + getString(R.string.favorite_removed), Snackbar.LENGTH_SHORT).show();

                                }
                                return true;

                            case R.id.menu_context_share:

                                String share_title = android.text.Html.fromHtml(itemBook.getBook_name()).toString();
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
                List<ItemBook> data = dbHandler.getFavRow(itemBook.getBook_id());
                if (data.size() == 0) {
                    popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.option_set_favorite);
                    charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
                } else {
                    if (data.get(0).getBook_id().equals(itemBook.getBook_id())) {
                        popup.getMenu().findItem(R.id.menu_context_favorite).setTitle(R.string.option_unset_favorite);
                        charSequence = popup.getMenu().findItem(R.id.menu_context_favorite).getTitle();
                    }
                }
            }
        });
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

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            MobileAds.initialize(getActivity(), getResources().getString(R.string.admob_app_id));
            interstitialAd = new InterstitialAd(getActivity());
            interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_unit_id));
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(getActivity())).build();
            interstitialAd.loadAd(adRequest);

            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                if (counter == Config.ADMOB_INTERSTITIAL_ADS_INTERVAL) {
                    interstitialAd.show();
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }
    }

}