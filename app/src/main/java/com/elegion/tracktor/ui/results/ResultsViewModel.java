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
import com.elegion.tracktor.util.DistanceConverter;
import com.elegion.tracktor.util.MathUtils;
import com.elegion.tracktor.util.SpeedUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import io.realm.Sort;
import toothpick.Toothpick;

/**
 * @author Azret Magometov
 */
public class ResultsViewModel extends ViewModel {

    @Inject
    IRepository<Track> mRepository;
    @Inject
    SharedPreferences mPreferences;

    private MutableLiveData<Track> mTrack = new MediatorLiveData<>();
    private MutableLiveData<OrderedRealmCollection<Track>> mTracksRealm = new MediatorLiveData<>();

    private MutableLiveData<String> mDistance = new MediatorLiveData<>();

    private float result;

    public ResultsViewModel() {
        Toothpick.inject(this, App.getAppScope());
    }

    public void loadTracks() {
        if (mTracksRealm.getValue() == null || mTracksRealm.getValue().isEmpty()) {
            updateTracks();
        }
    }

    public MutableLiveData<String> getDistance() {
        return mDistance;
    }

    private void updateDistance(Double distance){
        int unit = Integer.parseInt(mPreferences.getString("unit", DistanceConverter.METER+""));
        mDistance.postValue(DistanceConverter.formatDistance(distance, unit));
    }

    public MutableLiveData<Track> getTrack() {
        return mTrack;
    }

    public MutableLiveData<OrderedRealmCollection<Track>> getTracksRealm() {
        mTracksRealm.setValue(mRepository.getItemsList());
        return mTracksRealm;
    }

    public boolean deleteItem(long trackId) {
        return mRepository.deleteItem(trackId);
    }

    public void updateTrack(long trackId) {
        Track track = mRepository.getItem(trackId);
        track.setAverageSpeed(getAverageSpeed(track));
        track.setCalories(getCalories(track));
        mTrack.postValue(track);
        updateDistance(track.getDistance());
    }

    public void updateTrack(Track track) {
        mRepository.updateItem(track);
    }

    public void updateTracks() {
        mTracksRealm.postValue(mRepository.getItemsList());
    }

    private float getAverageSpeed(Track track) {
        float result = SpeedUtil.speedInKmS(track.getDistance(), track.getDuration());
        result = MathUtils.round(result, 2);
        return result;
    }

    private float getCalories(Track track) {
        float weight = Float.parseFloat(mPreferences.getString("weight", "70"));
        int height = Integer.parseInt(mPreferences.getString("height", "170"));
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

    public void updateActionTypeForTrack(int position) {
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

    public Track getTrack(long id) {
        return mRepository.getItem(id);
    }

    public void setTestDataToRepository(int count, String imageBase64) {
        List<Track> testData = getTestData(count, imageBase64);
        mRepository.insertAll(testData);
    }

    private List<Track> getTestData(int count, String imageBase64) {
        List<Track> result = new ArrayList<>();
        String[] actions = App.getApp().getResources().getStringArray(R.array.action_type);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            Track track = new Track();
            track.setDuration(random.nextInt(100_000));
            track.setDistance(random.nextInt(100_000) + 0.0);
            track.setActionType(random.nextInt(4));
            track.setComment("Тут комментарий " + random.nextInt());
            track.setAverageSpeed(SpeedUtil.speedInKmS(track.getDistance(), track.getDuration()));
            track.setImageBase64(imageBase64);
            track.setCalories(CaloriesUtil.execute(track.getActionType(),
                    track.getAverageSpeed(), track.getDuration(),
                    62, 184, actions));
            track.setDate(new Date(random.nextLong()));
            result.add(track);
        }
        return result;

    }

    public RealmResults<Track> sort(int order, int params) {
        RealmResults<Track> results = null;
        if (order == 0) { // ask
            switch (params) {
                case 0: // date
                    results = mTracksRealm.getValue().sort("date", Sort.ASCENDING);
                    break;
                case 1: // duration
                    results = mTracksRealm.getValue().sort("duration", Sort.ASCENDING);
                    break;
                case 2: // distance
                    results = mTracksRealm.getValue().sort("distance", Sort.ASCENDING);
                    break;
                default:
                    break;
            }
        }
        if (order == 1) { // desk
            switch (params) {
                case 0: // date
                    results = mTracksRealm.getValue().sort("date", Sort.DESCENDING);
                    break;
                case 1: // duration
                    results = mTracksRealm.getValue().sort("duration", Sort.DESCENDING);
                    break;
                case 2: // distance
                    results = mTracksRealm.getValue().sort("distance", Sort.DESCENDING);
                    break;
                default:
                    break;
            }
        }
        return results;
    }
}
