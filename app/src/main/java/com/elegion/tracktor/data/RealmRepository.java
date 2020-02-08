package com.elegion.tracktor.data;

import com.elegion.tracktor.App;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.util.MathUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;

/**
 * @author Azret Magometov
 */
public class RealmRepository implements IRepository<Track> {

    private Realm mRealm;

    private static AtomicLong sPrimaryId;
    @Inject
    public RealmRepository(App context) {
        Realm.init(context);
        mRealm = Realm.getDefaultInstance();
        Number max = mRealm.where(Track.class).max("id");
        sPrimaryId = max == null ? new AtomicLong(0) : new AtomicLong(max.longValue());
    }

    @Override
    public Track getItem(long id) {
        Track track = getRealmAssociatedTrack(id);
        return track != null ? mRealm.copyFromRealm(track) : null;
    }

    private Track getRealmAssociatedTrack(long id) {
        return mRealm.where(Track.class).equalTo("id", id).findFirst();
    }

    @Override
    public List<Track> getAll() {
        return mRealm.where(Track.class).findAll();
    }

    @Override
    public long insertItem(Track track) {
        track.setId(sPrimaryId.incrementAndGet());
        mRealm.beginTransaction();
        mRealm.copyToRealm(track);
        mRealm.commitTransaction();
        return sPrimaryId.longValue();
    }

    @Override
    public boolean deleteItem(final long id) {

        boolean isDeleteSuccessful;
        mRealm.beginTransaction();

        Track track = getRealmAssociatedTrack(id);

        if (track != null) {
            track.deleteFromRealm();
            isDeleteSuccessful = true;
        } else {
            isDeleteSuccessful = false;
        }

        mRealm.commitTransaction();

        return isDeleteSuccessful;
    }

    @Override
    public void updateItem(final Track track) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(track);
        mRealm.commitTransaction();
    }

    @Override
    public OrderedRealmCollection<Track> getItemsList() {
        return mRealm.where(Track.class).findAll();
    }

    @Override
    public void clearRepository() {
        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.commitTransaction();
    }

    public long createAndInsertTrackFrom(long duration, double distanse, String base64image) {
        Track track = new Track();

        track.setDistance(MathUtils.round(distanse, 2));
        track.setDuration(duration);
        track.setImageBase64(base64image);
        track.setDate(new Date());

        return insertItem(track);

    }

    public void insertAll(List<Track> tracks){
        mRealm.beginTransaction();
        mRealm.deleteAll();
        for (Track track : tracks){
            track.setId(sPrimaryId.incrementAndGet());
            mRealm.copyToRealm(track);
        }
        mRealm.commitTransaction();
    }
}
