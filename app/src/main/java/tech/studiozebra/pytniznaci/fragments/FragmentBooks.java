package tech.studiozebra.pytniznaci.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import tech.studiozebra.pytniznaci.Config;
import tech.studiozebra.pytniznaci.R;
import tech.studiozebra.pytniznaci.activities.ActivityStoryList;
import tech.studiozebra.pytniznaci.adapters.RecyclerAdapterBook;
import tech.studiozebra.pytniznaci.models.ItemBooks;
import tech.studiozebra.pytniznaci.utilities.ClickListener;
import tech.studiozebra.pytniznaci.utilities.GDPR;
import tech.studiozebra.pytniznaci.utilities.JsonConstant;
import tech.studiozebra.pytniznaci.utilities.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tech.studiozebra.pytniznaci.activities.ActivityStoryList;
import tech.studiozebra.pytniznaci.utilities.ClickListener;
import tech.studiozebra.pytniznaci.utilities.JsonUtils;

public class FragmentBooks extends Fragment {

    RecyclerView recyclerView;
    List<ItemBooks> arrayItemBooks;
    RecyclerAdapterBook adapter;
    private ItemBooks object;
    ArrayList<String> cat_id, cat_name, cat_author, cat_image;
    String[] str_cat_id, str_cat_name, str_cat_author, str_cat_image;
    int textlength = 0;
    SwipeRefreshLayout swipeRefreshLayout = null;
    //ProgressBar progressBar;
    private InterstitialAd interstitialAd;
    int counter = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(tech.studiozebra.pytniznaci.R.layout.fragment_book, container, false);

        loadInterstitialAd();

        //progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(tech.studiozebra.pytniznaci.R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(tech.studiozebra.pytniznaci.R.color.orange, tech.studiozebra.pytniznaci.R.color.green, tech.studiozebra.pytniznaci.R.color.blue, tech.studiozebra.pytniznaci.R.color.red);

        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) v.findViewById(tech.studiozebra.pytniznaci.R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayItemBooks = new ArrayList<ItemBooks>();
        setHasOptionsMenu(true);

        cat_id = new ArrayList<String>();
        cat_image = new ArrayList<String>();
        cat_name = new ArrayList<String>();
        cat_author = new ArrayList<String>();

        str_cat_id = new String[cat_id.size()];
        str_cat_name = new String[cat_name.size()];
        str_cat_author = new String[cat_author.size()];
        str_cat_image = new String[cat_image.size()];

        // Using to refresh webpage when user swipes the screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                cat_id.clear();
                cat_image.clear();
                cat_name.clear();
                cat_author.clear();
                clearData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        new RefreshTask().execute(Config.SERVER_URL + "/api.php");
                    }
                }, 1500);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        object = arrayItemBooks.get(position);
                        int Catid = object.getBookId();
                        JsonConstant.CATEGORY_IDD = object.getBookId();
                        Log.e("cat_id", "" + Catid);
                        JsonConstant.CATEGORY_TITLE = object.getBookName();

                        Intent intent = new Intent(getActivity(), ActivityStoryList.class);
                        startActivity(intent);

                        showInterstitialAd();

                    }
                }, 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new MyTask().execute(Config.SERVER_URL + "/api.php");
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public void clearData() {
        int size = this.arrayItemBooks.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayItemBooks.remove(0);
            }
            adapter.notifyItemRangeRemoved(0, size);
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            swipeRefreshLayout.setRefreshing(false);

            if (null == result || result.length() == 0) {
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConstant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemBooks objItem = new ItemBooks();
                        objItem.setBookName(objJson.getString(JsonConstant.CATEGORY_NAME));
                        objItem.setBookAuthorName(objJson.getString(JsonConstant.CATEGORY_AUTHOR));
                        objItem.setBookId(objJson.getInt(JsonConstant.CATEGORY_CID));
                        objItem.setCategoryImageurl(objJson.getString(JsonConstant.CATEGORY_IMAGE));
                        arrayItemBooks.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < arrayItemBooks.size(); j++) {
                    object = arrayItemBooks.get(j);

                    cat_id.add(String.valueOf(object.getBookId()));
                    str_cat_id = cat_id.toArray(str_cat_id);

                    cat_name.add(object.getBookName());
                    str_cat_name = cat_name.toArray(str_cat_name);

                    cat_author.add(object.getBookAuthorName());
                    str_cat_author = cat_author.toArray(str_cat_author);

                    cat_image.add(object.getCategoryImageurl());
                    str_cat_image = cat_image.toArray(str_cat_image);

                }
                setAdapterToListview();
            }

        }
    }

    private class RefreshTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            if (null == result || result.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(tech.studiozebra.pytniznaci.R.string.failed_connect_network), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConstant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemBooks objItem = new ItemBooks();
                        objItem.setBookName(objJson.getString(JsonConstant.CATEGORY_NAME));
                        objItem.setBookAuthorName(objJson.getString(JsonConstant.CATEGORY_AUTHOR));
                        objItem.setBookId(objJson.getInt(JsonConstant.CATEGORY_CID));
                        objItem.setCategoryImageurl(objJson.getString(JsonConstant.CATEGORY_IMAGE));
                        arrayItemBooks.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < arrayItemBooks.size(); j++) {
                    object = arrayItemBooks.get(j);

                    cat_id.add(String.valueOf(object.getBookId()));
                    str_cat_id = cat_id.toArray(str_cat_id);

                    cat_name.add(object.getBookName());
                    str_cat_name = cat_name.toArray(str_cat_name);

                    cat_author.add(object.getBookAuthorName());
                    str_cat_author = cat_author.toArray(str_cat_author);

                    cat_image.add(object.getCategoryImageurl());
                    str_cat_image = cat_image.toArray(str_cat_image);

                }
                setAdapterToListview();
            }

        }
    }

    public void setAdapterToListview() {
        adapter = new RecyclerAdapterBook(getActivity(), arrayItemBooks);
        recyclerView.setAdapter(adapter);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(tech.studiozebra.pytniznaci.R.menu.menu_search, menu);

        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(tech.studiozebra.pytniznaci.R.id.search));

        final MenuItem searchMenuItem = menu.findItem(tech.studiozebra.pytniznaci.R.id.search);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                textlength = newText.length();
                arrayItemBooks.clear();

                for (int i = 0; i < str_cat_name.length; i++) {
                    if (textlength <= str_cat_name[i].length()) {

                        if (str_cat_name[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemBooks objItem = new ItemBooks();
                            objItem.setBookId(Integer.parseInt(str_cat_id[i]));
                            objItem.setBookName(str_cat_name[i]);
                            objItem.setBookAuthorName(str_cat_author[i]);
                            objItem.setCategoryImageurl(str_cat_image[i]);

                            arrayItemBooks.add(objItem);
                        }
                    }
                }

                setAdapterToListview();

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            MobileAds.initialize(getActivity(), getResources().getString(tech.studiozebra.pytniznaci.R.string.admob_app_id));
            interstitialAd = new InterstitialAd(getActivity());
            interstitialAd.setAdUnitId(getResources().getString(tech.studiozebra.pytniznaci.R.string.admob_interstitial_id));
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
