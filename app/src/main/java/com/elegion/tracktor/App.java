package com.elegion.tracktor;

import android.app.Application;

import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.di.RepositoryModule;

import io.realm.Realm;
import toothpick.Scope;
import toothpick.Toothpick;

public class App extends Application {

    private static Scope sAppScope;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppScope = Toothpick.openScope(App.class);
        sAppScope.installModules(new RepositoryModule(this));

        Realm.init(this);
    }

    public static Scope getAppScope() {
        return sAppScope;
    }
}
