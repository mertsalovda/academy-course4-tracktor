package com.elegion.tracktor;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.elegion.tracktor.di.RepositoryModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import toothpick.Scope;
import toothpick.Toothpick;

public class App extends Application {

    private static Scope sAppScope;
    private static App sApp;
    private static SharedPreferences sSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        sApp = this;
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sAppScope = Toothpick.openScope(App.class);
        sAppScope.installModules(new RepositoryModule(this));
    }

    public static Scope getAppScope() {
        return sAppScope;
    }

    public static App getApp() {
        return sApp;
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }
}
