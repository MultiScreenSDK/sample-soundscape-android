package com.samsung.soundscape.model;


import com.google.gson.Gson;
import com.samsung.soundscape.util.Util;

public class Track extends Base {
    private String id;
    private String artist;
    private String album;
    private String title;
    private String file;
    private String albumArt;
    private String albumArtThumbnail;
    private String color;
    private int duration;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAlbumArtThumbnail() {
        return albumArtThumbnail;
    }

    public void setAlbumArtThumbnail(String albumArtThumbnail) {
        this.albumArtThumbnail = albumArtThumbnail;
    }


//    public int getColorInt() {
//        int intColor = 0;
//
//        if (color != null) {
//            try {
//                intColor = Color.parseColor(color);
//            } catch (IllegalArgumentException iae) {
//                Util.e("getColorInt exception: " + iae.toString());
//            }
//        }
//
//        return intColor;
//    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public static Track parse(String data) {
        if (data == null) {
            return null;
        }

        Track result = null;
        Gson gson = new Gson();

        try {
            result = gson.fromJson(data, Track.class);
        }catch (Exception e) {
            Util.e("Error parsing string to class: " + e.toString());
        }
        return result;
    }
}
