package tech.studiozebra.pytniznaci.firebase;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;


public class Analytics extends Application {

    private static FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate() {
        super.onCreate();
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
}