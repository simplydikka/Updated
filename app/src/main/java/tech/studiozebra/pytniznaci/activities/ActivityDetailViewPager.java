package tech.studiozebra.pytniznaci.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import es.dmoral.toasty.Toasty;
import tech.studiozebra.pytniznaci.Config;
import tech.studiozebra.pytniznaci.R;
import tech.studiozebra.pytniznaci.cache.ImageLoader;
import tech.studiozebra.pytniznaci.firebase.Analytics;
import tech.studiozebra.pytniznaci.utilities.DatabaseHandler;
import tech.studiozebra.pytniznaci.utilities.GDPR;
import tech.studiozebra.pytniznaci.utilities.JsonConstant;
import tech.studiozebra.pytniznaci.models.ItemFavorite;

import java.util.List;

import tech.studiozebra.pytniznaci.utilities.DatabaseHandler;

public class ActivityDetailViewPager extends AppCompatActivity {

    int position;
    String[] book_cid, book_cat_id, book_cat_image, book_cat_name, book_title, book_image, book_desc, book_subtitle;
    ViewPager viewpager;
    public ImageLoader imageLoader;
    int total_image;
    public DatabaseHandler databaseHandler;
    private Menu menu;
    String str_book_cid, str_book_cat_id, str_book_cat_name, str_book_title, str_book_image, str_book_desc, str_book_subtitle;
    private AdView adView;
    private InterstitialAd interstitialAd;
    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail_view_pager);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        initializeAd();
        loadBannerAd();
        loadInterstitialAd();

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

        databaseHandler = new DatabaseHandler(this);
        Intent i = getIntent();

        position = i.getIntExtra("POSITION", 0);
        book_cid = i.getStringArrayExtra("CATEGORY_ITEM_CID");
        book_cat_name = i.getStringArrayExtra("CATEGORY_ITEM_NAME");
        book_cat_image = i.getStringArrayExtra("CATEGORY_ITEM_IMAGE");
        book_cat_id = i.getStringArrayExtra("CATEGORY_ITEM_CAT_ID");
        book_image = i.getStringArrayExtra("CATEGORY_ITEM_NEWSIMAGE");
        book_title = i.getStringArrayExtra("CATEGORY_ITEM_NEWSHEADING");
        book_desc = i.getStringArrayExtra("CATEGORY_ITEM_NEWSDESCRI");
        book_subtitle = i.getStringArrayExtra("CATEGORY_ITEM_NEWSDATE");


        //total_image=str_book_list.length-1;
        viewpager = (ViewPager) findViewById(R.id.news_slider);
        //viewpager.setPageTransformer(true, new DepthPageTransformer());

        imageLoader = new ImageLoader(getApplicationContext());

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewpager.setAdapter(adapter);

        boolean found = false;
        int j1 = 0;
        for (int i1 = 0; i1 < book_cat_id.length; i1++) {
            if (book_cat_id[i1].contains(String.valueOf(position))) {
                found = true;
                j1 = i1;
                break;
            }
        }
        if (found) {
            viewpager.setCurrentItem(j1);
        }

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub

                position = viewpager.getCurrentItem();
                str_book_cat_id = book_cat_id[position];

                List<ItemFavorite> pojolist = databaseHandler.getFavRow(str_book_cat_id);
                if (pojolist.size() == 0) {
                    menu.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_outline));
                } else {
                    if (pojolist.get(0).getCatId().equals(str_book_cat_id)) {
                        menu.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white));
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int position) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewpager, menu);
        this.menu = menu;
        FirstFav();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_back:
                position = viewpager.getCurrentItem();
                position--;
                if (position < 0) {
                    position = 0;
                }
                viewpager.setCurrentItem(position);
                return true;

            case R.id.menu_next:

                position = viewpager.getCurrentItem();
                position++;
                if (position == total_image) {
                    position = total_image;
                }
                viewpager.setCurrentItem(position);
                return true;

            case R.id.menu_fav:

                position = viewpager.getCurrentItem();
                str_book_cat_id = book_cat_id[position];

                List<ItemFavorite> pojolist = databaseHandler.getFavRow(str_book_cat_id);
                if (pojolist.size() == 0) {
                    AddtoFav(position);//if size is zero i.e means that record not in database show add to favorite
                } else {
                    if (pojolist.get(0).getCatId().equals(str_book_cat_id)) {
                        RemoveFav(position);
                    }
                }
                return true;

            case R.id.menu_share:

                position = viewpager.getCurrentItem();
                str_book_title = book_title[position];
                str_book_desc = book_desc[position];
                String formattedString = android.text.Html.fromHtml(str_book_desc).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str_book_title + "\n" + formattedString + "\n" + " I Would like to share this with you. Here You Can Download This Application from PlayStore " + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void AddtoFav(int position) {
        str_book_cat_id = book_cat_id[position];
        str_book_cid = book_cid[position];
        str_book_cat_name = book_cat_name[position];
        str_book_title = book_title[position];
        str_book_image = book_image[position];
        str_book_desc = book_desc[position];
        str_book_subtitle = book_subtitle[position];

        databaseHandler.AddtoFavorite(new ItemFavorite(str_book_cat_id, str_book_cid, str_book_cat_name, str_book_title, str_book_image, str_book_desc, str_book_subtitle));

        //custom toast added to bookmarks
        Toast toast= Toasty.success(getApplicationContext(),
                R.string.bookmark_added, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,  0, 300);
        toast.show();

        menu.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white));

        showInterstitialAd();
    }

    //remove from favorite
    public void RemoveFav(int position) {
        str_book_cat_id = book_cat_id[position];
        databaseHandler.RemoveFav(new ItemFavorite(str_book_cat_id));

        //custom toast removed from bookmarks
        Toast toast= Toasty.error(getApplicationContext(),
                R.string.bookmark_removed, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();


        menu.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_outline));

    }

    public void FirstFav() {
        int first = viewpager.getCurrentItem();
        String Image_id = book_cat_id[first];

        List<ItemFavorite> pojolist = databaseHandler.getFavRow(Image_id);
        if (pojolist.size() == 0) {
            menu.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_outline));

        } else {
            if (pojolist.get(0).getCatId().equals(Image_id)) {
                menu.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_bookmark_white));

            }

        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public ImagePagerAdapter() {
            // TODO Auto-generated constructor stub

            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return book_cat_id.length;

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            if (Config.ENABLE_RTL_MODE) {

                View imageLayout = inflater.inflate(R.layout.lsv_item_view_pager, container, false);
                assert imageLayout != null;

                ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image_news);
                TextView txt_title = (TextView) imageLayout.findViewById(R.id.text_newstitle);
                TextView txt_subtitle = (TextView) imageLayout.findViewById(R.id.text_newsdate);
                WebView webView_desc = (WebView) imageLayout.findViewById(R.id.webView_newsdes);

                imageLoader.DisplayImage(Config.SERVER_URL + "/upload/" + book_image[position], imageView);

                txt_title.setText(book_title[position]);
                txt_subtitle.setText(book_subtitle[position]);
                webView_desc.setBackgroundColor(Color.parseColor("#FFFFFF"));
                webView_desc.setFocusableInTouchMode(false);
                webView_desc.setFocusable(false);
                webView_desc.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // For final release of your app, comment the toast notification
                        return true;
                    }
                });
                webView_desc.setLongClickable(false);

                webView_desc.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;
                    }
                });

                WebSettings webSettings = webView_desc.getSettings();
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
                String htmlText = book_desc[position];

                String text = "<html dir='rtl'><head>"
                        + "<style type=\"text/css\">body{color: #525252;}"
                        + "</style></head>"
                        + "<body>"
                        + htmlText
                        + "</body></html>";

                webView_desc.loadData(text, mimeType, encoding);

                container.addView(imageLayout, 0);

                return imageLayout;

            } else {
                View imageLayout = inflater.inflate(R.layout.lsv_item_view_pager, container, false);
                assert imageLayout != null;

                ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image_news);
                TextView txt_title = (TextView) imageLayout.findViewById(R.id.text_newstitle);
                TextView txt_subtitle = (TextView) imageLayout.findViewById(R.id.text_newsdate);
                WebView webView_desc = (WebView) imageLayout.findViewById(R.id.webView_newsdes);

                imageLoader.DisplayImage(Config.SERVER_URL + "/upload/" + book_image[position], imageView);

                txt_title.setText(book_title[position]);
                txt_subtitle.setText(book_subtitle[position]);
                webView_desc.setBackgroundColor(Color.parseColor("#FFFFFF"));
                webView_desc.setFocusableInTouchMode(false);
                webView_desc.setFocusable(false);
                webView_desc.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // For final release of your app, comment the toast notification
                        return true;
                    }
                });
                webView_desc.setLongClickable(false);

                webView_desc.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;
                    }
                });

                WebSettings webSettings = webView_desc.getSettings();
                Resources res = getResources();
                int fontSize = res.getInteger(R.integer.font_size);
                webSettings.setDefaultFontSize(fontSize);
                webSettings.setDefaultTextEncodingName("UTF-8");
                webSettings.setJavaScriptEnabled(true);
                //webSettings.setSupportZoom(true);
                //webSettings.setBuiltInZoomControls(true);
                //webSettings.setDisplayZoomControls(true);

                String mimeType = "text/html; charset=UTF-8";
                String encoding = "utf-8";
                String htmlText = book_desc[position];

                String text = "<html><head>"
                        + "<style type=\"text/css\">body{color: #525252;}"
                        + "</style></head>"
                        + "<body>"
                        + htmlText
                        + "</body></html>";

                webView_desc.loadData(text, mimeType, encoding);

                container.addView(imageLayout, 0);

                return imageLayout;
            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
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

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public void initializeAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS || Config.ENABLE_ADMOB_INTERSTITIAL_ADS) {
            MobileAds.initialize(ActivityDetailViewPager.this, getResources().getString(R.string.admob_app_id));
        }
    }

    public void loadBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityDetailViewPager.this)).build();
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
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityDetailViewPager.this)).build();
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
