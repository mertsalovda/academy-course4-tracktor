package com.elegion.tracktor.event;

public class EditCommentEvent {
    private long trackId;
    public EditCommentEvent(long trackId) {
        this.trackId = trackId;
    }

    public long getTrackId() {
        return trackId;
    }
}
