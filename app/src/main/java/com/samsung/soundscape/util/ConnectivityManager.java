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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;

import com.samsung.multiscreen.Channel;
import com.samsung.multiscreen.Client;
import com.samsung.multiscreen.Message;
import com.samsung.multiscreen.Result;
import com.samsung.multiscreen.Search;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.util.JSONUtil;
import com.samsung.multiscreen.util.RunUtil;
import com.samsung.soundscape.App;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.ServiceAdapter;
import com.samsung.soundscape.events.AddTrackEvent;
import com.samsung.soundscape.events.AppStateEvent;
import com.samsung.soundscape.events.AssignColorEvent;
import com.samsung.soundscape.events.ConnectionChangedEvent;
import com.samsung.soundscape.events.RemoveTrackEvent;
import com.samsung.soundscape.events.ServiceChangedEvent;
import com.samsung.soundscape.events.TrackPlaybackEvent;
import com.samsung.soundscape.events.TrackStatusEvent;
import com.samsung.soundscape.model.AppState;
import com.samsung.soundscape.model.CurrentStatus;
import com.samsung.soundscape.model.Track;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;


/**
 * Provides the Samsung MultiScreen functions.
 */
public class ConnectivityManager {
    public enum ServiceType {
        //Other unknown type
        Other,
        //Samsung smart TV.
        TV,
        //Samsung smart speaker.
        Speaker
    }

    public static final String EVENT_ADD_TRACK = "addTrack";
    public static final String EVENT_REMOVE_TRACK = "removeTrack";
    public static final String EVENT_TRACK_STATUS = "trackStatus";
    public static final String EVENT_APP_STATE_REQUEST = "appStateRequest";
    public static final String EVENT_APP_STATE = "appState";
    public static final String EVENT_TRACK_START = "trackStart";
    public static final String EVENT_TRACK_END = "trackEnd";
    public static final String EVENT_PLAY = "play";
    public static final String EVENT_PAUSE = "pause";
    public static final String EVENT_NEXT = "next";
    public static final String EVENT_ASSIGN_COLOR = "assignColor";
    public static final String EVENT_VOL_DOWN = "volDown";
    public static final String EVENT_VOL_UP = "volUp";

    /**
     * An singleton instance of this class
     */
    private static ConnectivityManager instance = null;

    /**
     * A lock used to synchronize creation of this object and access to the service map.
     */
    protected static final Object lock = new Object();

    /**
     * The Search object which is going to run discovery service.
     */
    private Search search = null;

    /**
     * Multiscreen TV service
     */
    private com.samsung.multiscreen.Service service;

    /**
     * Multiscreen TV application
     */
    private com.samsung.multiscreen.Application mMultiscreenApp;

    /**
     * The adapter used to maintain TV service list.
     */
    private ServiceAdapter adapter;

    /**
     * The flag shows that application is going to exit.
     */
    private boolean isExisting = false;


    /**
     * Returns the instance.
     *
     * @return
     */
    public static ConnectivityManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConnectivityManager();
                }
            }
        }
        return instance;
    }

    private ConnectivityManager() {
        //Register Wifi state listener.
        registerWiFiStateListener();
    }

    /**
     * Adapter for the service list dialog.
     */
    public ServiceAdapter getServiceAdapter() {
        return adapter;
    }

    public void setServiceAdapter(ServiceAdapter adapter) {
        this.adapter = adapter;
    }


    /**
     * Check if discovery process is running.
     *
     * @return true discovery is running otherwise false.
     */
    public boolean isDiscovering() {
        return (search != null && search.isSearching());
    }

    /**
     * start TV discovery.
     */
    public void startDiscovery() {
        Util.d("startDiscovery");

        //Create the search object if it is null.
        if (search == null) {
            search = Service.search(App.getInstance());
            search.setOnServiceFoundListener(mOnServiceFoundListener);
            search.setOnServiceLostListener(mOnServiceLostListener);
        }

        //When WiFi is connected and search process is not running.
        if (Util.isWiFiConnected() && !search.isSearching()) {

            if (!search.isStarting()) {
                //Clear the TV list.
                if (adapter != null) {
                    adapter.clear();

                    //Notify UI to update cast icon.
                    EventBus.getDefault().post(new ServiceChangedEvent());
                }

                //Start discovery.
                search.start();
            }
        }
    }

    private Search.OnServiceFoundListener mOnServiceFoundListener = new Search.OnServiceFoundListener() {
        @Override
        public void onFound(Service service) {
            Util.d("Service onFound: " + service);

            //TV is found, update the TV list.
            updateTVList(service);
        }
    };
    private Search.OnServiceLostListener mOnServiceLostListener = new Search.OnServiceLostListener() {
        @Override
        public void onLost(Service service) {
            //TV is removed. We will remove the TV from TV list.
            removeTV(service);
        }
    };


    /**
     * Stop TV discovery.
     */
    public void stopDiscovery() {
        Util.d("stopDiscovery");

        //Stop discovery if search object it not null and it is search.
        if (search != null && search.isSearching()) {
            if (!search.isStopping()) {
                try {
                    search.stop();
                } catch (Exception e) { //ignore any error during stop search.
                }
            }
        }
    }


    /**
     * Remove TV from the TV list.
     *
     * @param service
     */
    private void removeTV(Service service) {
        if (service == null) {
            return;
        }

        //Remove the service if it exists.
        if (adapter.contains(service)) {
            adapter.remove(service);

            //Notify service is changed.
            notifyDataChange();
        }
    }


    /**
     * Add TV into TV list.
     *
     * @param service the TV service to be added.
     */
    private void updateTVList(Service service) {
        if (service == null || adapter == null) {
            return;
        }

        Util.d("updateTVList: " + service.toString());

        //Add if does not exist or replace it.
        if (!adapter.contains(service)) {
            //Do not add it to the list when the service is connected already.
            if (!service.equals(this.service) || !isTVConnected()) {
                adapter.add(service);
            }
        } else {
            //Replace the service with new service.
            adapter.replace(service);
        }

        //Notify listeners that service has changed. Please update UI accordingly.
        notifyDataChange();
    }

    public void addConnectedServerToList() {
        if (service == null) {
            return;
        }

        //Add if does not exist or replace it.
        if (!getServiceAdapter().contains(service)) {
                adapter.add(service);
        } else {
            //Replace the service with new service.
            adapter.replace(service);
        }

        //Notify listeners that service has changed. Please update UI accordingly.
        notifyDataChange();

    }

    public void removeConnectedServiceFromList() {
        if (service != null && adapter != null) {
            adapter.remove(service);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Notify TV list adapter that data set is changed.
     */
    void notifyDataChange() {
        //Run it at UI thread.
        RunUtil.runOnUI(new Runnable() {
            @Override
            public void run() {

                //notify data set changes when adapter is not null.
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //Notify UI to update cast icon.
        EventBus.getDefault().post(new ServiceChangedEvent());
    }


    /**
     * Set the TV service.
     *
     * @param service the new TV service to be used.
     */
    public void setService(final Service service) {
        if (this.service != null) {

            //Launch the TV mMultiscreenApp directly if we already got the TV service.
            if (this.service.equals(service)) {

                //Launch the TV app it is not connected.
                //Do nothing if it is already connected.
                if (!mMultiscreenApp.isConnected()) {
                    launchApplication();
                }

            } else {

                //If different TV is selected, disconnect the previous application.
                if (mMultiscreenApp != null && mMultiscreenApp.isConnected()) {
                    mMultiscreenApp.disconnect(new Result<Client>() {
                        @Override
                        public void onSuccess(Client client) {
                            //disconnect onSuccess, update service.
                            updateServiceAndConnect(service);
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            //disconnect failed.
                            Util.e("disconnect onError: " + error.getMessage());

                            //Update service.
                            updateServiceAndConnect(service);
                        }
                    });
                } else {
                    updateServiceAndConnect(service);
                }
            }
        } else {
            //connect to a new TV.
            updateServiceAndConnect(service);
        }
    }

    public Service getService() {
        return service;
    }

    /**
     * Update the current service and start to launch TV application.
     *
     * @param service the new TV service.
     */
    private void updateServiceAndConnect(Service service) {
        this.service = service;

        //Start to launch TV application.
        if (this.service != null) {
            launchApplication();
        }
    }

    /**
     * Clean up the service.
     */
    public void clearService() {
        //Shows that application is existing.
        isExisting = true;

        //Unregister the WiFi state listener.
        try {
            App.getInstance().unregisterReceiver(mWifiStateChangedReceiver);
        } catch (Exception e) {//ignore if there is error.
        }

        //Disconnect TV if it is connected.
        if (isTVConnected()) {
            mMultiscreenApp.disconnect();
        }

        service = null;
    }

    /**
     * Check if TV is connected already.
     *
     * @return true if TV is connected otherwise false.
     */
    public boolean isTVConnected() {
        return service != null && mMultiscreenApp != null && mMultiscreenApp.isConnected();
    }


    /**
     * Return the service type of the connected service.
     * @return
     */
    public ServiceType getConnectedServiceType() {
        return getServiceType(this.service);
    }

    /**
     * Return the service type of given service.
     * @param service
     * @return
     */
    public ServiceType getServiceType(Service service) {
        if (service == null) {
            return ServiceType.Other;
        }

        String type = service.getType();
        if (type.endsWith(" speaker") || type.endsWith("Speaker")) {
            return ServiceType.Speaker;
        }

        return ServiceType.TV;
    }

    /**
     * Makes connection to the TV and start the application on the TV
     * if the current service is available.
     */
    public void launchApplication() {
        if (service == null) {
            return;
        }

        //Parse Application Url.
        Uri url = Uri.parse(App.getInstance().getString(R.string.app_url));

        // Get an instance of Application.
        mMultiscreenApp = service.createApplication(url, App.getInstance().getString(R.string.channel_id));

        //Set the connection timeout to 20 seconds.
        //When the TV is unavailable after 20 seconds, onDisconnect event is called.
        mMultiscreenApp.setConnectionTimeout(20000);


        //Listen for the disconnect event.
        mMultiscreenApp.setOnDisconnectListener(new Channel.OnDisconnectListener() {
            @Override
            public void onDisconnect(Client client) {
                Util.d("OnDisconnectListener");
                if (client != null) {

                    //Notify service change listeners.
                    EventBus.getDefault().post(new ConnectionChangedEvent(null));

                    //restart to discovery if service is disconnected and
                    // this application is not closing.
                    if (!isExisting) startDiscovery();
                }
            }
        });

        // Listen for the connect event
        mMultiscreenApp.setOnConnectListener(new Channel.OnConnectListener() {
            @Override
            public void onConnect(Client client) {
                Util.d("Application is connected: " + client.toString());

                //stop discovery to save battery when a service is selected.
                stopDiscovery();

                //Update Service list.
                //Remove the connected service from switch to list.
                //adapter.remove(getService());
                //adapter.notifyDataSetChanged();

                //Notify to update UI.
                EventBus.getDefault().post(new ConnectionChangedEvent(null));
            }
        });

        // Listen for the errors.
        mMultiscreenApp.setOnErrorListener(new Channel.OnErrorListener() {
            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Util.e("setOnErrorListener: " + error.toString());
                EventBus.getDefault().post(new ConnectionChangedEvent(error.toString()));

                if (!isExisting) startDiscovery();
            }
        });

        mMultiscreenApp.addOnMessageListener(EVENT_APP_STATE, onAppStateListener);
        mMultiscreenApp.addOnMessageListener(EVENT_TRACK_STATUS, onTrackStatusListener);
        mMultiscreenApp.addOnMessageListener(EVENT_ADD_TRACK, onAddTrackListener);
        mMultiscreenApp.addOnMessageListener(EVENT_TRACK_START, onTrackStartListener);
        mMultiscreenApp.addOnMessageListener(EVENT_TRACK_END, onTrackEndListener);
        mMultiscreenApp.addOnMessageListener(EVENT_ASSIGN_COLOR, onAssignColorListener);
        mMultiscreenApp.addOnMessageListener(EVENT_REMOVE_TRACK, onRemoveTrackColorListener);

        //Connect and launch the TV application.
        mMultiscreenApp.connect(new Result<Client>() {

            @Override
            public void onSuccess(Client client) {
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                Util.e("connect onError: " + error.toString());

                //failed to launch TV application. Notify TV service changes.
                EventBus.getDefault().post(new ConnectionChangedEvent(error.toString()));
            }
        });
    }

    /**
     * Disconnect the multiscreen web application.
     */
    public void disconnect() {
        if (service != null && mMultiscreenApp != null && mMultiscreenApp.isConnected()) {
            mMultiscreenApp.removeOnMessageListeners();
            mMultiscreenApp.disconnect(false, null);
            service = null;
        }
    }

    /**
     * Set the discovery stop listener called when discovery is stopped.
     *
     * @param listener the listener.
     */
    public void setDiscoveryOnStopListener(Search.OnStopListener listener) {
        if (search != null) {
            search.setOnStopListener(listener);
        }
    }

    /**
     * Stop the discovery if it is searching and start a new discovery.
     */
    public void restartDiscovery() {
        if (search == null) {
            return;
        }

        if (search.isSearching()) {
            //Set the listener. Called when search is completely stopped.
            search.setOnStopListener(new Search.OnStopListener() {
                @Override
                public void onStop() {

                    //Clear the onStopListener.
                    search.setOnStopListener(null);

                    //Start a new discovery.
                    startDiscovery();
                }
            });

            //Start to stop discovery.
            stopDiscovery();
        } else {
            //There is no search process, start a new discovery.
            startDiscovery();
        }
    }

    /**
     * Get the clients amount.
     * @return
     */
    public int getClientCount() {
        int count = 0;

        if (isTVConnected()) {
            count = mMultiscreenApp.getClients().size();
        }

        return count;
    }

    /**
     * Send the app state reqeust.
     */
    public void requestAppState() {
        sendToTV(EVENT_APP_STATE_REQUEST, null, Message.TARGET_HOST);
    }

    /**
     * broadcast the add track event.
     * @param track
     */
    public void addTrack(Track track) {
        try {
            JSONObject obj = new JSONObject(track.toJsonString());
            sendToTV(EVENT_ADD_TRACK, obj, Message.TARGET_BROADCAST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Broadcast the remove track event.
     * @param trackId
     */
    public void removeTrack(String trackId) {
        sendToTV(EVENT_REMOVE_TRACK, trackId, Message.TARGET_BROADCAST);
    }

    /**
     * Pause the playing track.
     */
    public void pause() {
        sendToTV(EVENT_PAUSE, null, Message.TARGET_BROADCAST);
    }

    /**
     * Play the track.
     */
    public void play() {
        sendToTV(EVENT_PLAY, null, Message.TARGET_BROADCAST);
    }

    /**
     * Skip the song at the top of the playlist.
     */
    public void next() {
        sendToTV(EVENT_NEXT, null, Message.TARGET_BROADCAST);
    }

    /**
     * Receive the response data of app state request.
     */
    private Channel.OnMessageListener onAppStateListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            Util.d("onAppStateListener: " + message.toString());

            if (message != null && message.getData() != null) {
                String jsonString = JSONUtil.toJSONString((HashMap)message.getData());
                EventBus.getDefault().post(new AppStateEvent(AppState.parse(jsonString)));
            }
        }
    };

    /**
     * Receive the update of track status.
     */
    private Channel.OnMessageListener onTrackStatusListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            //Util.d("onTrackStatusListener: " + message.toString());
            if (message != null && message.getData() != null) {

                if (message.getData() instanceof HashMap) {
                    String jsonString = JSONUtil.toJSONString((HashMap)message.getData());
                    EventBus.getDefault().post(new TrackStatusEvent(CurrentStatus.parse(jsonString)));
                }
            }
        }
    };

    /**
     * Receive the track start event.
     */
    private Channel.OnMessageListener onTrackStartListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            Util.d("onTrackStartListener: " + message.toString());

            if (message != null && message.getData() != null) {
                EventBus.getDefault().post(new TrackPlaybackEvent(message.getData().toString(),
                        message.getEvent()));
            }
        }
    };

    /**
     * Receive the track end event.
     */
    private Channel.OnMessageListener onTrackEndListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            Util.d("onTrackEndListener: " + message.toString());

            if (message != null && message.getData() != null) {
                EventBus.getDefault().post(new TrackPlaybackEvent(message.getData().toString(),
                        message.getEvent()));
            }
        }
    };


    /**
     * Receive the add track event.
     */
    private Channel.OnMessageListener onAddTrackListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            Util.d("onAddTrackListener: " + message.toString());

            if (message != null && message.getData() != null) {
                if (message.getData() instanceof HashMap) {
                    String jsonString = JSONUtil.toJSONString((HashMap)message.getData());
                    EventBus.getDefault().post(new AddTrackEvent(Track.parse(jsonString)));
                }
            }
        }
    };

    /**
     * Receive the add track event.
     */
    private Channel.OnMessageListener onAssignColorListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            Util.d("onAssignColorListener: " + message.toString());

            if (message != null && message.getData() != null) {
                EventBus.getDefault().post(new AssignColorEvent(message.getData().toString()));
            }
        }
    };

    /**
     * Receive the add track event.
     */
    private Channel.OnMessageListener onRemoveTrackColorListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            Util.d("onRemoveTrackColorListener: " + message.toString());

            if (message != null && message.getData() != null) {
                EventBus.getDefault().post(new RemoveTrackEvent(message.getData().toString()));
            }
        }
    };


    /**
     * Sent the data to TV.
     *
     * @param event  the channel event.
     * @param data   the object to sent to TV.
     * @param target the target to receive message.
     */
    private void sendToTV(String event, Object data, String target) {
        if (mMultiscreenApp != null && mMultiscreenApp.isConnected()) {
            Util.d("Send to TV: event=" + event + "  data=" + data);
            mMultiscreenApp.publish(event, data, target);
        }
    }


    /**
     * Register network change listeners.
     */
    private void registerWiFiStateListener() {
        IntentFilter mWiFiStateFilter = new IntentFilter();
        mWiFiStateFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mWiFiStateFilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        App.getInstance().registerReceiver(mWifiStateChangedReceiver, mWiFiStateFilter);
    }


    /**
     * Broadcast receiver for network changes.
     */
    BroadcastReceiver mWifiStateChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                switch (extraWifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                    case WifiManager.WIFI_STATE_DISABLING:
                        //WiFi is not available.
                        stopDiscovery();
                        getServiceAdapter().clear();
                        notifyDataChange();
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        //Use ConnectivityManager.CONNECTIVITY_ACTION instead.
                        break;
                }
            } else if (android.net.ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //WiFi is connected
                startDiscovery();
            }
        }
    };
}
