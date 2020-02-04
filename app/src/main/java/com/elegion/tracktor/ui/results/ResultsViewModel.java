package com.elegion.tracktor.ui.results;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.util.CaloriesUtil;
import com.elegion.tracktor.util.MathUtils;
import com.elegion.tracktor.util.SpeedUtil;

import java.util.List;

import javax.inject.Inject;

import toothpick.Toothpick;

/**
 * @author Azret Magometov
 */
public class ResultsViewModel extends ViewModel {

    @Inject
    IRepository<Track> mRepository;
    @Inject
    SharedPreferences mPreferences;

    private MutableLiveData<List<Track>> mTracks = new MutableLiveData<>();
    private MutableLiveData<Track> mTrack = new MediatorLiveData<>();
    private float result;

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

    public MutableLiveData<Track> getTrack() {
        return mTrack;
    }

    public boolean deleteItem(long trackId) {
        return mRepository.deleteItem(trackId);
    }

    public void updateTrack(long trackId) {
        Track track = mRepository.getItem(trackId);
        track.setAverageSpeed(getAverageSpeed(track));
        track.setCalories(getCalories(track));
        mTrack.postValue(track);
    }

    private float getAverageSpeed(Track track) {
        float result = SpeedUtil.speedInKmS(track.getDistance(), track.getDuration());
        result = MathUtils.round(result, 2);
        return result;
    }

    private float getCalories(Track track) {
        float weight = Float.valueOf(mPreferences.getString("weight", "70"));
        int height = Integer.valueOf(mPreferences.getString("height", "170"));
        String[] activeTypes = App.getApp().getResources().getStringArray(R.array.action_type);
        result = CaloriesUtil.execute(
                track.getActionType(),
                track.getAverageSpeed(),
                track.getDuration(),
                weight,
                height,
                activeTypes);
        return MathUtils.round(result, 2);
    }

    public void updateActionTypeForTrack(int position){
        Track track = mTrack.getValue();
        track.setActionType(position);
        track.setCalories(getCalories(track));
        mTrack.postValue(track);
        mRepository.updateItem(track);
    }

    public void saveComment(String comment) {
        Track track = mTrack.getValue();
        track.setComment(comment);
        mRepository.updateItem(track);
        mTrack.postValue(track);
    }
}
