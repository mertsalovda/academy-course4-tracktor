package com.elegion.tracktor.event;

public class ClickOnHolderEvent {

    private long trackId;

    public ClickOnHolderEvent(long trackId) {
        this.trackId = trackId;
    }

    public long getTrackId() {
        return trackId;
    }
}
