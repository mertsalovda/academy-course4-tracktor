package com.elegion.tracktor.di;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.elegion.tracktor.App;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.data.model.Track;

import toothpick.config.Module;

public class RepositoryModule extends Module {

    private final App mApp;

    public RepositoryModule(App app) {
        this.mApp = app;
        bind(App.class).toInstance(provideApp());
        bind(IRepository.class).toInstance(provideRepository());
        bind(SharedPreferences.class).toInstance(providePreferences());
    }

    private App provideApp() {
        return mApp;
    }

    private IRepository<Track> provideRepository(){
        return new RealmRepository(mApp);
    }

    private SharedPreferences providePreferences(){
        return PreferenceManager.getDefaultSharedPreferences(mApp);
    }
}
