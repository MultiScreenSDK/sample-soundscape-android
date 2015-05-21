package com.samsung.soundscape.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by bliu on 5/13/2015.
 */
public class AppState extends Base {
    private CurrentStatus currentStatus;
    private ArrayList<Track> playlist = new ArrayList<>();

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }


    public ArrayList<Track> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ArrayList<Track> playlist) {
        this.playlist = playlist;
    }

    public static AppState parse(String data) {
        if (data == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(data, AppState.class);
    }
}
