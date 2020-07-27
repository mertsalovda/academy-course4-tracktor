package com.elegion.tracktor.ui.map;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;

import com.elegion.tracktor.App;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.event.AddPositionToRouteEvent;
import com.elegion.tracktor.event.StopBtnClickedEvent;
import com.elegion.tracktor.event.UpdateRouteEvent;
import com.elegion.tracktor.event.UpdateTimerEvent;
import com.elegion.tracktor.util.DistanceConverter;
import com.elegion.tracktor.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import toothpick.Toothpick;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> startEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> stopEnabled = new MutableLiveData<>();

    private MutableLiveData<String> mTimeText = new MutableLiveData<>();
    private MutableLiveData<String> mDistanceText = new MutableLiveData<>();

    private long mDurationRaw;
    private double mDistanceRaw;

    @Inject
    RealmRepository mRealmRepository;
    @Inject
    SharedPreferences mPreferences;

    @Inject
    public MainViewModel() {
        Toothpick.inject(this, App.getAppScope());
        EventBus.getDefault().register(this);
        startEnabled.setValue(true);
        stopEnabled.setValue(false);
    }

    public void switchButtons() {
        startEnabled.setValue(!startEnabled.getValue());
        stopEnabled.setValue(!stopEnabled.getValue());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void switchButtons(StopBtnClickedEvent event) {
        startEnabled.postValue(!startEnabled.getValue());
        stopEnabled.postValue(!stopEnabled.getValue());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTimer(UpdateTimerEvent event) {
        int unit = Integer.parseInt(mPreferences.getString("unit", DistanceConverter.METER + ""));
        mTimeText.postValue(StringUtil.getTimeText(event.getSeconds()));
        mDistanceText.postValue(DistanceConverter.formatDistance(event.getDistance(), unit));
        mDurationRaw = event.getSeconds();
        mDistanceRaw = event.getDistance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateRoute(UpdateRouteEvent event) {
        int unit = Integer.parseInt(mPreferences.getString("unit", DistanceConverter.METER + ""));
        mDistanceText.postValue(DistanceConverter.formatDistance(event.getDistance(), unit));
        mDistanceRaw = event.getDistance();

        startEnabled.postValue(false);
        stopEnabled.postValue(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPositionToRoute(AddPositionToRouteEvent event) {
        int unit = Integer.parseInt(mPreferences.getString("unit", DistanceConverter.METER + ""));
        mDistanceText.postValue(DistanceConverter.formatDistance(event.getDistance(), unit));
    }

    public MutableLiveData<String> getTimeText() {
        return mTimeText;
    }

    public MutableLiveData<Boolean> getStartEnabled() {
        return startEnabled;
    }

    public MutableLiveData<Boolean> getStopEnabled() {
        return stopEnabled;
    }

    public MutableLiveData<String> getDistanceText() {
        return mDistanceText;
    }

    @Override
    protected void onCleared() {
        EventBus.getDefault().unregister(this);
        super.onCleared();
    }

    public void clear() {
        mTimeText.setValue("");
        mDistanceText.setValue("");
    }

    public void update() {
        int unit = Integer.parseInt(mPreferences.getString("unit", DistanceConverter.METER + ""));
        mDistanceText.postValue(DistanceConverter.formatDistance(mDistanceRaw, unit));
    }

    public long saveResults(String base54image) {

        return mRealmRepository.createAndInsertTrackFrom(mDurationRaw, mDistanceRaw, base54image);
    }
}