package com.samsung.soundscape.events;

import com.samsung.soundscape.util.ConnectivityManager;

/**
 * Created by bliu on 5/14/2015.
 */
public class TrackPlaybackEvent {
    public String id;
    public String event;

    public TrackPlaybackEvent(String id, String event) {
        this.id = id;
        this.event = event;
    }

    public boolean isStart() {
        return (event != null && event.equals(ConnectivityManager.EVENT_TRACK_START));
    }
}
