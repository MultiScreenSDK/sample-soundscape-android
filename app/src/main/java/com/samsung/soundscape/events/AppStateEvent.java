package com.samsung.soundscape.events;

import com.samsung.soundscape.model.AppState;

/**
 * Created by bliu on 5/13/2015.
 */
public class AppStateEvent {
    public AppState state;

    public AppStateEvent(AppState state) {
        this.state = state;
    }
}
