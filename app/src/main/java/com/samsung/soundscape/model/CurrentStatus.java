package com.samsung.soundscape.model;

/**
 * Created by bliu on 5/14/2015.
 */
public class CurrentStatus {
    public static final String STATE_PLAYING = "playing";
    public static final String STATE_PAUSED = "paused";

    private String id;
    private long time;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
