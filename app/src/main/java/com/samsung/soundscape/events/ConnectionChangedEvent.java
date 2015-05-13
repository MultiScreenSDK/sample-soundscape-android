package com.samsung.soundscape.events;

/**
 * Created by bliu on 5/13/2015.
 */
public class ConnectionChangedEvent {
    public final String errorMessage;

    public ConnectionChangedEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
