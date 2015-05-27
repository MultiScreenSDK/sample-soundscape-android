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

import com.google.gson.Gson;

import java.util.ArrayList;

public class AppState extends Base {
    //The current status.
    private CurrentStatus currentStatus;

    //The tracks list.
    private ArrayList<Track> playlist = new ArrayList<>();

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }


    /**
     * Set the current status.
     * @param currentStatus the new status.
     */
    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }


    /**
     * Get the play list.
     * @return
     */
    public ArrayList<Track> getPlaylist() {
        return playlist;
    }

    /**
     * Set the play list.
     * @param playlist the new playlist to be used.
     */
    public void setPlaylist(ArrayList<Track> playlist) {
        this.playlist = playlist;
    }

    /**
     * Parse json string into AppState instance.
     * @param data the json string.
     * @return AppState instance.
     */
    public static AppState parse(String data) {
        if (data == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(data, AppState.class);
    }
}
