package com.elegion.tracktor.event;

public class ShareEvent {
    private long mTrack;

    public ShareEvent(long track) {
        mTrack = track;
    }

    public long getTrack() {
        return mTrack;
    }
}
