package com.samsung.soundscape.events;

import com.samsung.soundscape.model.CurrentStatus;

/**
 * Created by bliu on 5/14/2015.
 */
public class TrackStatusEvent {
    public CurrentStatus status;

    public TrackStatusEvent(CurrentStatus status) {
        this.status = status;
    }
}
