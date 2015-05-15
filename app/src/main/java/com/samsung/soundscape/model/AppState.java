package com.samsung.soundscape.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by bliu on 5/13/2015.
 */
public class AppState extends Base {
    private CurrentStatus status;
    private String nowPlaying;
    private ArrayList<Track> playlist = new ArrayList<>();

    public CurrentStatus getStatus() {
        return status;
    }

    public void setStatus(CurrentStatus status) {
        this.status = status;
    }

    public String getNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(String nowPlaying) {
        this.nowPlaying = nowPlaying;
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
