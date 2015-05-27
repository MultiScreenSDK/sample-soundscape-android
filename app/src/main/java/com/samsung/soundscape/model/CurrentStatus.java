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

public class CurrentStatus extends Base {
    /**
     * The playback state - playing.
     */
    public static final String STATE_PLAYING = "playing";

    /**
     * The playback state - paused.
     */
    public static final String STATE_PAUSED = "paused";

    //Track id
    private String id;

    //The playback position
    private float time;

    //The current playback state either STATE_PLAYING or STATE_PAUSED
    private String state;

    public CurrentStatus(String id, float time, String state) {
        this.id = id;
        this.time = time;
        this.state = state;
    }

    /**
     * Check if the track is playing.
     * @return true if tracking is playing, otherwise false.
     */
    public boolean isPlaying() {
        return (state != null && state.equals(STATE_PLAYING));
    }


    /**
     * Parse the given json string into CurrentStatus instance.
     * @param data the json string.
     * @return the CurrentStatus instance.
     */
    public static CurrentStatus parse(String data) {
        if (data == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(data, CurrentStatus.class);
    }

    /**
     * Get the track id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set the track id.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the playback position.
     * @return
     */
    public float getTime() {
        return time;
    }

    /**
     * Set the playback position.
     * @param time
     */
    public void setTime(float time) {
        this.time = time;
    }

    /**
     * Get the playback state.
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     * Set the playback state.
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }
}
