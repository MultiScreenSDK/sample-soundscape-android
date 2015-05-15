package com.samsung.soundscape.model;

import com.google.gson.Gson;

/**
 * Created by bliu on 5/14/2015.
 */
public class CurrentStatus extends Base {
    public static final String STATE_PLAYING = "playing";
    public static final String STATE_PAUSED = "paused";

    private String id;
    private float time;
    private String status;

    public CurrentStatus(String id, long time, String status) {
        this.setId(id);
        this.setTime(time);
        this.setStatus(status);
    }

    public boolean isPlaying() {
        return (getStatus() != null && getStatus().equals(STATE_PLAYING));
    }

    public boolean isPaused() {
        return (getStatus() != null && getStatus().equals(STATE_PLAYING));
    }

    public static boolean isPlaying(String status) {
        return (status != null && status.equals(STATE_PLAYING));
    }

    public static CurrentStatus parse(String data) {
        if (data == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(data, CurrentStatus.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
