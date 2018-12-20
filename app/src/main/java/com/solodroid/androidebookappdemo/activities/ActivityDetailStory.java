package com.solodroid.androidebookappdemo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.solodroid.androidebookappdemo.Config;
import com.solodroid.androidebookappdemo.R;
import com.solodroid.androidebookappdemo.firebase.Analytics;
import com.solodroid.androidebookappdemo.models.ItemStoryList;
import com.solodroid.androidebookappdemo.utilities.DatabaseHandler;
import com.solodroid.androidebookappdemo.utilities.JsonConstant;
import com.solodroid.androidebookappdemo.utilities.JsonUtils;
import com.solodroid.androidebookappdemo.models.ItemFavorite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetailStory extends AppCompatActivity {

    int position;
    String str_cid, str_cat_id, str_cat_image, str_cat_name, str_title, str_image, str_desc, str_date;
    TextView book_title, book_subtitle;
    WebView news_desc;
    ImageView imageView;
    DatabaseHandler databaseHandler;
    List<ItemStoryList> listItem;
    ItemStoryList object;
    final Context context = this;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_story_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(JsonConstant.CATEGORY_TITLE);
        }

        //Firebase LogEvent
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getResources().getString(R.string.analytics_item_id_3));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getResources().getString(R.string.analytics_item_name_3));
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");

        //Logs an app event.
        Analytics.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
        Analytics.getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 5 seconds
        Analytics.getFirebaseAnalytics().setMinimumSessionDuration(5000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes). Let’s make it 10.
        Analytics.getFirebaseAnalytics().setSessionTimeoutDuration(1000000);

        //show admob banner ad
        adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
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

        linearLayout = (LinearLayout) findViewById(R.id.content);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.image);

        book_title = (TextView) findViewById(R.id.title);
        book_subtitle = (TextView) findViewById(R.id.subtitle);
        news_desc = (WebView) findViewById(R.id.desc);

        databaseHandler = new DatabaseHandler(ActivityDetailStory.this);

        listItem = new ArrayList<ItemStoryList>();
        //imageLoader = new ImageLoader(ActivityDetailStory.this);

        if (JsonUtils.isNetworkAvailable(ActivityDetailStory.this)) {
            new MyTask().execute(Config.SERVER_URL + "/api.php?nid=" + JsonConstant.NEWS_ITEMID);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);

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

                        listItem.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setAdapterToListview();
            }

        }
    }

    public void setAdapterToListview() {

        if (Config.ENABLE_RTL_MODE) {

            object = listItem.get(0);
            str_cid = object.getCId();
            str_cat_name = object.getCategoryName();
            str_cat_image = object.getCategoryImage();
            str_cat_id = object.getCatId();
            str_title = object.getStoryTitle();
            str_desc = object.getStoryDescription();
            str_image = object.getStoryImage();
            str_date = object.getStorySubTitle();

            book_title.setText(str_title);
            book_subtitle.setText(str_date);

            news_desc.setBackgroundColor(Color.parseColor("#FFFFFF"));
            news_desc.setFocusableInTouchMode(false);
            news_desc.setFocusable(false);
            news_desc.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // For final release of your app, comment the toast notification
                    return true;
                }
            });
            news_desc.setLongClickable(false);

            news_desc.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });

            WebSettings webSettings = news_desc.getSettings();
            Resources res = getResources();
            int fontSize = res.getInteger(R.integer.font_size);
            webSettings.setDefaultFontSize(fontSize);
            webSettings.setDefaultTextEncodingName("UTF-8");
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(true);

            String mimeType = "text/html; charset=UTF-8";
            String encoding = "utf-8";
            String htmlText = str_desc;

            String text = "<html dir='rtl'><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/Roboto-Light.ttf\")}body {font-family: MyFont;font-size: medium; color: #525252;}</style></head><body>"
                    + htmlText + "</body></html>";

            news_desc.loadData(text, mimeType, encoding);

            List<ItemFavorite> pojolist = databaseHandler.getFavRow(str_cat_id);
            if (pojolist.size() == 0) {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_outline));
            } else {
                if (pojolist.get(0).getCatId().equals(str_cat_id)) {
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white));
                }
            }

        } else {

            object = listItem.get(0);
            str_cid = object.getCId();
            str_cat_name = object.getCategoryName();
            str_cat_image = object.getCategoryImage();
            str_cat_id = object.getCatId();
            str_title = object.getStoryTitle();
            str_desc = object.getStoryDescription();
            str_image = object.getStoryImage();
            str_date = object.getStorySubTitle();

            book_title.setText(str_title);
            book_subtitle.setText(str_date);

            news_desc.setBackgroundColor(Color.parseColor("#FFFFFF"));
            news_desc.setFocusableInTouchMode(false);
            news_desc.setFocusable(false);
            news_desc.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // For final release of your app, comment the toast notification
                    return true;
                }
            });
            news_desc.setLongClickable(false);

            news_desc.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });

            WebSettings webSettings = news_desc.getSettings();
            Resources res = getResources();
            int fontSize = res.getInteger(R.integer.font_size);
            webSettings.setDefaultFontSize(fontSize);
            webSettings.setDefaultTextEncodingName("UTF-8");
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(true);

            String mimeType = "text/html; charset=UTF-8";
            String encoding = "utf-8";
            String htmlText = str_desc;

            String text = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/Roboto-Light.ttf\")}body {font-family: MyFont;font-size: medium; color: #525252;}</style></head><body>"
                    + htmlText + "</body></html>";

            news_desc.loadData(text, mimeType, encoding);

            List<ItemFavorite> pojolist = databaseHandler.getFavRow(str_cat_id);
            if (pojolist.size() == 0) {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_outline));
            } else {
                if (pojolist.get(0).getCatId().equals(str_cat_id)) {
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white));
                }
            }

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_story, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_share:
                String formattedString = android.text.Html.fromHtml(str_desc).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str_title + "\n" + formattedString + "\n" + " I Would like to share this with you. Here You Can Download This Application from PlayStore " + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.menu_favorite:
                List<ItemFavorite> pojolist = databaseHandler.getFavRow(str_cat_id);
                if (pojolist.size() == 0) {

                    databaseHandler.AddtoFavorite(new ItemFavorite(str_cat_id, str_cid, str_cat_name, str_title, str_image, str_desc, str_date));
                    Toast.makeText(getApplicationContext(), "Added to Favorite", Toast.LENGTH_SHORT).show();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white));

                    interstitialAd = new InterstitialAd(ActivityDetailStory.this);
                    interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
                    AdRequest adRequest = new AdRequest.Builder().build();
                    interstitialAd.loadAd(adRequest);
                    interstitialAd.setAdListener(new AdListener() {
                        public void onAdLoaded() {
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            }
                        }
                    });

                } else {
                    if (pojolist.get(0).getCatId().equals(str_cat_id)) {

                        databaseHandler.RemoveFav(new ItemFavorite(str_cat_id));
                        Toast.makeText(getApplicationContext(), "Removed from Favorite", Toast.LENGTH_SHORT).show();
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_outline));
                    }
                }
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

}
