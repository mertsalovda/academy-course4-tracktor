package com.elegion.tracktor.ui.results;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.App;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.util.StringUtil;

import java.util.List;

import javax.inject.Inject;

import toothpick.Toothpick;

/**
 * @author Azret Magometov
 */
public class ResultsViewModel extends ViewModel {

    @Inject
    IRepository<Track> mRepository;

    private MutableLiveData<List<Track>> mTracks = new MutableLiveData<>();
    private MutableLiveData<String> mDistance = new MediatorLiveData<>();
    private MutableLiveData<String> mTime = new MediatorLiveData<>();
    private MutableLiveData<String> mImageBase64 = new MediatorLiveData<>();

    public ResultsViewModel() {
        Toothpick.inject(this, App.getAppScope());
    }

    public void loadTracks() {
        if (mTracks.getValue() == null || mTracks.getValue().isEmpty()) {
            mTracks.postValue(mRepository.getAll());
        }
    }

    public MutableLiveData<List<Track>> getTracks() {
        return mTracks;
    }

    public MutableLiveData<String> getDistance() {
        return mDistance;
    }

    public MutableLiveData<String> getTime() {
        return mTime;
    }

    public MutableLiveData<String> getImageBase64() {
        return mImageBase64;
    }

    public boolean deleteItem(long trackId) {
        return mRepository.deleteItem(trackId);
    }

    public void updateTrack(long trackId) {

        Track track = mRepository.getItem(trackId);

        mDistance.postValue(StringUtil.getDistanceText(track.getDistance()));
        mTime.postValue(StringUtil.getTimeText(track.getDuration()));
        mImageBase64.postValue(track.getImageBase64());
    }
}
