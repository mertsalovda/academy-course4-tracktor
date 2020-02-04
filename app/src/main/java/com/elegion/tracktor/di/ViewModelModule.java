package com.elegion.tracktor.di;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.lifecycle.ViewModelStoreOwner;

import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.results.ResultsViewModel;
import com.elegion.tracktor.util.CustomViewModelFactory;


import toothpick.config.Module;

public class ViewModelModule extends Module {

    private CustomViewModelFactory mFactory = new CustomViewModelFactory();

    public ViewModelModule(ViewModelStoreOwner fragment) {
        bind(MainViewModel.class).toInstance(provideMainViewModel(fragment));
        bind(ResultsViewModel.class).toInstance(provideResultViewModel(fragment));
    }

    private MainViewModel provideMainViewModel(ViewModelStoreOwner fragment) {
        return ViewModelProviders.of((android.support.v4.app.Fragment) fragment, mFactory).get(MainViewModel.class);
    }

    private ResultsViewModel provideResultViewModel(ViewModelStoreOwner fragment) {
        return ViewModelProviders.of((android.support.v4.app.Fragment) fragment, mFactory).get(ResultsViewModel.class);
    }
}
