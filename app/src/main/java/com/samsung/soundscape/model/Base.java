package com.samsung.soundscape.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samsung.soundscape.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bliu on 5/14/2015.
 */
public class Base {
    public String toJsonString() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        return gson.toJson(this);
    }

    public JSONObject getJson() {
        JSONObject obj = null;
        try {
            obj = new JSONObject(toJsonString());
        } catch (JSONException e) {
            Util.e("Error during parse string to JSONObject:" + e.toString());
        }

        return obj;
    }
}
