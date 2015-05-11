/*******************************************************************************
 * Copyright (c) 2015 Samsung Electronics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

package com.samsung.soundscape;

import android.app.Application;
import android.graphics.BitmapFactory;

import com.samsung.soundscape.util.ConnectivityManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class App extends Application {
    /** The application instance. */
    private static App instance;

    private ExecutorService createThumbnailsService = Executors.newFixedThreadPool(10);

    /** Used to calculate how many activity is at foreground. */
    public int activityCounter = 0;

    /** The default options used to load bitmap file. */
    private BitmapFactory.Options optionsLoadingThumbnail;

    /** The connectivity manager instance. */
    private ConnectivityManager mConnectivityManager;


    /**
     * Static method to return App instance.
     *
     * @return App instance.
     */
    public static App getInstance() {
        return instance;
    }

    public App() {
        instance = this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();

        //Get connectivity manager.
        mConnectivityManager = ConnectivityManager.getInstance();
    }

    /**
     * Get the connectivity manager.
     * @return
     */
    public ConnectivityManager getConnectivityManager() {
        return mConnectivityManager;
    }



    /**
     * The clean up method, it should only be called when application exits.
     */
    public void cleanup() {
        //Clean up multiscreen service.
        mConnectivityManager.clearService();
        mConnectivityManager = null;
    }


}
