/**
 * ****************************************************************************
 * Copyright (c) 2015 Samsung Electronics
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * *****************************************************************************
 */

package com.samsung.soundscape.model;


import android.graphics.Color;

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


    public int getColorInt() {
        int intColor = 0;

        if (color != null) {
            try {
                intColor = Color.parseColor(color);
            } catch (IllegalArgumentException iae) {
                Util.e("getColorInt exception: " + iae.toString());
            }
        }

        return intColor;
    }

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
        } catch (Exception e) {
            Util.e("Error parsing string to class: " + e.toString());
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        // Return true if the objects are identical.
        if (this == object) {
            return true;
        }

        if (object == null || !(object instanceof Track)) {
            return false;
        }

        Track track = (Track) object;

        if (this.id.equals(track.getId()) && this.title.equals(track.getTitle()) &&
                this.artist.equals(track.getArtist())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        final int PRIME = 31;
        result = PRIME * result + id.hashCode();
        return result;
    }
}
