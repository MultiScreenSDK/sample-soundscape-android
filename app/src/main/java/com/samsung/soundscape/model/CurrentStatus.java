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
    private String state;

    public CurrentStatus(String id, float time, String state) {
        this.id = id;
        this.time = time;
        this.state = state;
    }

    public boolean isPlaying() {
        return (state != null && state.equals(STATE_PLAYING));
    }

    public boolean isPaused() {
        return (state != null && state.equals(STATE_PLAYING));
    }

    public static boolean isPlaying(String state) {
        return (state != null && state.equals(STATE_PLAYING));
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
