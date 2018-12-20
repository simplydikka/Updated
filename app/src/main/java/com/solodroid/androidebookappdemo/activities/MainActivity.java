package com.solodroid.androidebookappdemo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.solodroid.androidebookappdemo.Config;
import com.solodroid.androidebookappdemo.R;
import com.solodroid.androidebookappdemo.firebase.Analytics;
import com.solodroid.androidebookappdemo.fragments.FragmentAbout;
import com.solodroid.androidebookappdemo.fragments.FragmentBooks;
import com.solodroid.androidebookappdemo.fragments.FragmentFavorite;
import com.solodroid.androidebookappdemo.fragments.FragmentPdf;
import com.solodroid.androidebookappdemo.utilities.GDPR;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    private long exitTime = 0;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //Firebase LogEvent
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getResources().getString(R.string.analytics_item_id_1));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getResources().getString(R.string.analytics_item_name_1));
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity");

        //Logs an app event.
        Analytics.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //Sets whether analytics collection is enabled for this app on this device.
        Analytics.getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 5 seconds
        Analytics.getFirebaseAnalytics().setMinimumSessionDuration(5000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes). Let’s make it 10.
        Analytics.getFirebaseAnalytics().setSessionTimeoutDuration(1000000);

        loadBannerAd();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.main_drawer);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.frame_container, new FragmentBooks()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                //setTitle(menuItem.getTitle());

                if (menuItem.getItemId() == R.id.menu_book) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, new FragmentBooks()).commit();
                }

                if (menuItem.getItemId() == R.id.menu_pdf) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, new FragmentPdf()).commit();
                }

                if (menuItem.getItemId() == R.id.menu_bookmark) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, new FragmentFavorite()).commit();
                }

                if (menuItem.getItemId() == R.id.drawer_rate) {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                }

                if (menuItem.getItemId() == R.id.drawer_more) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_apps))));
                }

                if (menuItem.getItemId() == R.id.menu_about) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, new FragmentAbout()).commit();
                }

                return false;
            }

        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void loadBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            MobileAds.initialize(MainActivity.this, getResources().getString(R.string.admob_app_id));
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(MainActivity.this)).build();
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

}
