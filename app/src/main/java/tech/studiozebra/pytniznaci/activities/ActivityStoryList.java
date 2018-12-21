package tech.studiozebra.pytniznaci.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import tech.studiozebra.pytniznaci.Config;
import tech.studiozebra.pytniznaci.R;
import tech.studiozebra.pytniznaci.adapters.RecyclerAdapterStory;
import tech.studiozebra.pytniznaci.firebase.Analytics;
import tech.studiozebra.pytniznaci.models.ItemStoryList;
import tech.studiozebra.pytniznaci.utilities.ClickListener;
import tech.studiozebra.pytniznaci.utilities.GDPR;
import tech.studiozebra.pytniznaci.utilities.JsonConstant;
import tech.studiozebra.pytniznaci.utilities.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityStoryList extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ItemStoryList> arrayItemStoryList;
    RecyclerAdapterStory adapter;
    ArrayList<String> book_list, book_list_cat_name;
    ArrayList<String> book_id, book_cat_id, book_cat_image, book_cat_name, book_title, book_image, book_desc, book_subtitle;
    String[] str_book_list, str_book_list_cat_name;
    String[] str_book_cid, str_book_cat_id, str_book_cat_image, str_book_cat_name, str_book_title, str_book_image, str_book_desc, str_book_subtitle;
    private ItemStoryList itemStoryList;
    JsonUtils util;
    int text_length = 0;
    SwipeRefreshLayout swipeRefreshLayout = null;
    private AdView adView;
    private InterstitialAd interstitialAd;
    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(JsonConstant.CATEGORY_TITLE);
        }

        //Firebase LogEvent
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getResources().getString(R.string.analytics_item_id_2));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getResources().getString(R.string.analytics_item_name_2));
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");

        //Logs an app event.
        Analytics.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
        Analytics.getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 5 seconds
        Analytics.getFirebaseAnalytics().setMinimumSessionDuration(5000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes). Let’s make it 10.
        Analytics.getFirebaseAnalytics().setSessionTimeoutDuration(1000000);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);

        initializeAd();
        loadBannerAd();
        loadInterstitialAd();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayItemStoryList = new ArrayList<ItemStoryList>();
        book_list = new ArrayList<String>();
        book_list_cat_name = new ArrayList<String>();
        book_id = new ArrayList<String>();
        book_cat_id = new ArrayList<String>();
        book_cat_image = new ArrayList<String>();
        book_cat_name = new ArrayList<String>();
        book_title = new ArrayList<String>();
        book_image = new ArrayList<String>();
        book_desc = new ArrayList<String>();
        book_subtitle = new ArrayList<String>();

        str_book_list = new String[book_list.size()];
        str_book_list_cat_name = new String[book_list_cat_name.size()];
        str_book_cid = new String[book_id.size()];
        str_book_cat_id = new String[book_cat_id.size()];
        str_book_cat_image = new String[book_cat_image.size()];
        str_book_cat_name = new String[book_cat_name.size()];
        str_book_title = new String[book_title.size()];
        str_book_image = new String[book_image.size()];
        str_book_desc = new String[book_desc.size()];
        str_book_subtitle = new String[book_subtitle.size()];

        util = new JsonUtils(getApplicationContext());


        if (JsonUtils.isNetworkAvailable(ActivityStoryList.this)) {
            new MyTask().execute(Config.SERVER_URL + "/api.php?cat_id=" + JsonConstant.CATEGORY_IDD);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                book_list.clear();
                book_list_cat_name.clear();
                book_id.clear();
                book_cat_id.clear();
                book_cat_image.clear();
                book_cat_name.clear();
                book_title.clear();
                book_image.clear();
                book_desc.clear();
                book_subtitle.clear();
                clearData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        new RefreshTask().execute(Config.SERVER_URL + "/api.php?cat_id=" + JsonConstant.CATEGORY_IDD);
                    }
                }, 1500);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Config.ENABLE_VIEW_PAGER) {

                            itemStoryList = arrayItemStoryList.get(position);
                            int pos = Integer.parseInt(itemStoryList.getCatId());

                            Intent intent = new Intent(getApplicationContext(), ActivityDetailViewPager.class);
                            intent.putExtra("POSITION", pos);
                            intent.putExtra("CATEGORY_ITEM_CID", str_book_cid);
                            intent.putExtra("CATEGORY_ITEM_NAME", str_book_cat_name);
                            intent.putExtra("CATEGORY_ITEM_IMAGE", str_book_cat_image);
                            intent.putExtra("CATEGORY_ITEM_CAT_ID", str_book_cat_id);
                            intent.putExtra("CATEGORY_ITEM_NEWSIMAGE", str_book_image);
                            intent.putExtra("CATEGORY_ITEM_NEWSHEADING", str_book_title);
                            intent.putExtra("CATEGORY_ITEM_NEWSDESCRI", str_book_desc);
                            intent.putExtra("CATEGORY_ITEM_NEWSDATE", str_book_subtitle);

                            startActivity(intent);

                            showInterstitialAd();

                        } else {

                            itemStoryList = arrayItemStoryList.get(position);
                            int pos = Integer.parseInt(itemStoryList.getCatId());

                            Intent intent = new Intent(getApplicationContext(), ActivityDetailStory.class);
                            intent.putExtra("POSITION", pos);
                            JsonConstant.NEWS_ITEMID = itemStoryList.getCatId();

                            startActivity(intent);

                            showInterstitialAd();

                        }
                    }
                }, 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }


    public void clearData() {
        int size = this.arrayItemStoryList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayItemStoryList.remove(0);
            }
            adapter.notifyItemRangeRemoved(0, size);
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

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
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConstant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemStoryList objItem = new ItemStoryList();

                        objItem.setCId(objJson.getString(JsonConstant.CATEGORY_ITEM_CID));
                        objItem.setCategoryName(objJson.getString(JsonConstant.CATEGORY_ITEM_NAME));
                        objItem.setCategoryImage(objJson.getString(JsonConstant.CATEGORY_ITEM_IMAGE));
                        objItem.setCatId(objJson.getString(JsonConstant.CATEGORY_ITEM_CAT_ID));
                        objItem.setStoryImage(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSIMAGE));
                        objItem.setStoryTitle(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSHEADING));
                        objItem.setStoryDescription(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSDESCRI));
                        objItem.setStorySubTitle(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSDATE));

                        arrayItemStoryList.add(objItem);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < arrayItemStoryList.size(); j++) {

                    itemStoryList = arrayItemStoryList.get(j);

                    book_cat_id.add(itemStoryList.getCatId());
                    str_book_cat_id = book_cat_id.toArray(str_book_cat_id);

                    book_cat_name.add(itemStoryList.getCategoryName());
                    str_book_cat_name = book_cat_name.toArray(str_book_cat_name);

                    book_id.add(String.valueOf(itemStoryList.getCId()));
                    str_book_cid = book_id.toArray(str_book_cid);

                    book_image.add(String.valueOf(itemStoryList.getStoryImage()));
                    str_book_image = book_image.toArray(str_book_image);


                    book_title.add(String.valueOf(itemStoryList.getStoryTitle()));
                    str_book_title = book_title.toArray(str_book_title);

                    book_desc.add(String.valueOf(itemStoryList.getStoryDescription()));
                    str_book_desc = book_desc.toArray(str_book_desc);

                    book_subtitle.add(String.valueOf(itemStoryList.getStorySubTitle()));
                    str_book_subtitle = book_subtitle.toArray(str_book_subtitle);

                }

                setAdapterToRecyclerView();
            }

        }
    }

    private class RefreshTask extends AsyncTask<String, Void, String> {


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
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConstant.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemStoryList objItem = new ItemStoryList();

                        objItem.setCId(objJson.getString(JsonConstant.CATEGORY_ITEM_CID));
                        objItem.setCategoryName(objJson.getString(JsonConstant.CATEGORY_ITEM_NAME));
                        objItem.setCategoryImage(objJson.getString(JsonConstant.CATEGORY_ITEM_IMAGE));
                        objItem.setCatId(objJson.getString(JsonConstant.CATEGORY_ITEM_CAT_ID));
                        objItem.setStoryImage(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSIMAGE));
                        objItem.setStoryTitle(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSHEADING));
                        objItem.setStoryDescription(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSDESCRI));
                        objItem.setStorySubTitle(objJson.getString(JsonConstant.CATEGORY_ITEM_NEWSDATE));

                        arrayItemStoryList.add(objItem);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < arrayItemStoryList.size(); j++) {

                    itemStoryList = arrayItemStoryList.get(j);

                    book_cat_id.add(itemStoryList.getCatId());
                    str_book_cat_id = book_cat_id.toArray(str_book_cat_id);

                    book_cat_name.add(itemStoryList.getCategoryName());
                    str_book_cat_name = book_cat_name.toArray(str_book_cat_name);

                    book_id.add(String.valueOf(itemStoryList.getCId()));
                    str_book_cid = book_id.toArray(str_book_cid);

                    book_image.add(String.valueOf(itemStoryList.getStoryImage()));
                    str_book_image = book_image.toArray(str_book_image);


                    book_title.add(String.valueOf(itemStoryList.getStoryTitle()));
                    str_book_title = book_title.toArray(str_book_title);

                    book_desc.add(String.valueOf(itemStoryList.getStoryDescription()));
                    str_book_desc = book_desc.toArray(str_book_desc);

                    book_subtitle.add(String.valueOf(itemStoryList.getStorySubTitle()));
                    str_book_subtitle = book_subtitle.toArray(str_book_subtitle);

                }

                setAdapterToRecyclerView();
            }

        }
    }

    public void setAdapterToRecyclerView() {
        adapter = new RecyclerAdapterStory(this, arrayItemStoryList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);


        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.search));

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

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

                text_length = newText.length();
                arrayItemStoryList.clear();

                for (int i = 0; i < str_book_title.length; i++) {
                    if (text_length <= str_book_title[i].length()) {
                        if (str_book_title[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemStoryList objItem = new ItemStoryList();

                            objItem.setCategoryName(str_book_cat_name[i]);
                            objItem.setCatId(str_book_cat_id[i]);
                            objItem.setCId(str_book_cid[i]);
                            objItem.setStorySubTitle(str_book_subtitle[i]);
                            objItem.setStoryDescription(str_book_desc[i]);
                            objItem.setStoryTitle(str_book_title[i]);
                            objItem.setStoryImage(str_book_image[i]);

                            arrayItemStoryList.add(objItem);

                        }
                    }
                }

                setAdapterToRecyclerView();
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
        super.onDestroy();
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

    public void initializeAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS || Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            MobileAds.initialize(ActivityStoryList.this, getResources().getString(R.string.admob_app_id));
        }
    }

    public void loadBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityStoryList.this)).build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_id));
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityStoryList.this)).build();
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
