package com.samsung.soundscape.events;

import com.samsung.soundscape.model.Track;

/**
 * Created by bliu on 5/14/2015.
 */
public class AddTrackEvent {
    public Track track;

    public AddTrackEvent(Track track) {
        this.track = track;
    }
}
