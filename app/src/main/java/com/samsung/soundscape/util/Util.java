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


package com.samsung.soundscape.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.samsung.soundscape.App;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Util {
    //The debug tag.
    private static final String TAG = "SamsungSoundScape";

    //The flag to output debug message or not.
    public static final boolean DEBUG = true;


    /**
     * Generate the Log.d log message.
     * @param message The message to be output.
     */
    public static void d(String message) {
        if (DEBUG) Log.d(TAG, message);
    }

    /**
     * Generate the Log.e log message.
     * @param message The message to be output.
     */
    public static void e(String message) {
        if (DEBUG) Log.e(TAG, message);
    }


    /**
     * Get the connected WiFi network name.
     * @return network name or null is WiFi is not connected.
     */
    public static String getWifiName() {
        String ssid = null;
        if (isWiFiConnected()) {
            WifiManager wifiManager = (WifiManager) App.getInstance().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ssid = wifiInfo.getSSID();
        }

        return ssid;
    }


    /**
     * Return whether or not WiFi is connected.
     *
     * @return true if WiFi is connected, otherwise false.
     */
    public static boolean isWiFiConnected() {
        //Get connectivity manager.
        ConnectivityManager connManager = (ConnectivityManager) App.getInstance().
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //get network info object.
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }


    /**
     * Read content from Url.
     * @param urlString the url string.
     * @return the content string.
     * @throws Exception
     */
    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * Stripe out the leading characters and returns a friendly name.
     * @param name The TV name.
     * @return the friendly name.
     */
    public static String getFriendlyTvName(String name) {
        if (name == null) {
            return null;
        }

        if (name.startsWith("[TV]")) {
            return name.substring(4).trim();
        }

        return name;
    }

    public static Uri getUriFromUrl(String thisUrl) throws MalformedURLException {
        URL url = new URL(thisUrl);
        Uri.Builder builder =  new Uri.Builder()
                .scheme(url.getProtocol())
                .authority(url.getAuthority())
                .appendPath(url.getPath());
        return builder.build();
    }
}
