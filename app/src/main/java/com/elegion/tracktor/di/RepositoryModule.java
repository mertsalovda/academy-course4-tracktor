package com.elegion.tracktor.di;

import com.elegion.tracktor.App;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;

import toothpick.config.Module;

public class RepositoryModule extends Module {

    private final App mApp;

    public RepositoryModule(App app) {
        this.mApp = app;
        bind(App.class).toInstance(provideApp());
        bind(IRepository.class).toInstance(provideRepository());
    }

    private App provideApp() {
        return mApp;
    }

    private IRepository<com.elegion.tracktor.data.model.Track> provideRepository(){
        return new RealmRepository(mApp);
    }
}
