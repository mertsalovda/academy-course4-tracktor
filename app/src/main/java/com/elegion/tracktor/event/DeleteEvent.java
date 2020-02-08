package com.elegion.tracktor.event;

public class DeleteEvent {
    private long mTrackId;

    public DeleteEvent(long trackId) {
        mTrackId = trackId;
    }

    public long getTrackId() {
        return mTrackId;
    }
}
