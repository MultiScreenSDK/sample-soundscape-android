package com.samsung.soundscape.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;

import com.samsung.multiscreen.Channel;
import com.samsung.multiscreen.Client;
import com.samsung.multiscreen.Result;
import com.samsung.multiscreen.Search;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.util.RunUtil;
import com.samsung.soundscape.App;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.ServiceAdapter;
import com.samsung.soundscape.events.ConnectionChangedEvent;
import com.samsung.soundscape.events.ServiceChangedEvent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


/**
 * Provides the Samsung MultiScreen functions.
 */
public class ConnectivityManager {
    public static final String EVENT_ADD_TRACK = "addTrack";
    public static final String EVENT_REMOVE_TRACK = "removeTrack";
    public static final String EVENT_APP_STATE_REQUEST = "appStateRequest";
    public static final String EVENT_APP_STATE = "appState";
    public static final String EVENT_TRACK_START = "trackStart";
    public static final String EVENT_PLAY = "play";
    public static final String EVENT_PAUSE = "pause";
    public static final String EVENT_NEXT = "next";

    /** An singleton instance of this class */
    private static ConnectivityManager instance = null;

    /** A lock used to synchronize creation of this object and access to the service map. */
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
     * The service change listener, it will invoked when service is changed like add, remove, etc.
     */
    public interface ServiceChangedListener {
        //Triggered when service is added or removed.
        public void onServiceChanged();
        //Triggered when the connection is changed (either disconnected or connected).
        public void onConnectionChanged();
    }

    /**
     * The array list of ServiceChangedListener.
     */
    private ArrayList<ServiceChangedListener> listeners = new ArrayList<ServiceChangedListener>();


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
     * Current selected service.
     */
    public Service getService() {
        return service;
    }


    /**
     * Get TV application.
     */
    public com.samsung.multiscreen.Application getMultiscreenApp() {
        return mMultiscreenApp;
    }

    /**
     * Add service change listener to the list.
     *
     * @param listener the listener to be added.
     */
    public void addServiceChangedListener(ServiceChangedListener listener) {

        //Add the listener if it does not exist.
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove the service change listener from list.
     *
     * @param listener the listener to be removed.
     */
    public void removeServiceChangedListener(ServiceChangedListener listener) {
        //Remove the listener.
        listeners.remove(listener);
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
        if (service == null) {
            return;
        }

        //Add if does not exist or replace it.
        if (!getServiceAdapter().contains(service)) {

//            //We do not add it to the list when the service is connected already.
//            if (!service.equals(getService()) || !isTVConnected()) {
//                adapter.add(service);
//            }
            adapter.add(service);
        } else {
            //Replace the service with new service.
            adapter.replace(service);
        }

        //Notify listeners that service has changed. Please update UI accordingly.
        notifyDataChange();
    }

//    public void addConnectedServerToList() {
//        if (service == null) {
//            return;
//        }
//
//        //Add if does not exist or replace it.
//        if (!getServiceAdapter().contains(service)) {
//                adapter.add(service);
//        } else {
//            //Replace the service with new service.
//            adapter.replace(service);
//        }
//
//        //Notify listeners that service has changed. Please update UI accordingly.
//        notifyDataChange();
//
//    }

    public void removeConnectedServiceFromList() {
        if (service != null && adapter != null) {
            adapter.remove(service);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Notify all the service change listeners that service change happens.
     */
    private void notifyListeners(boolean serviceChanged) {
        for (ServiceChangedListener listener : listeners) {
            if (listener != null) {
                if (serviceChanged) {
                    listener.onServiceChanged();
                } else {
                    listener.onConnectionChanged();
                }
            }
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
        notifyListeners(true);

        EventBus.getDefault().post(new ServiceChangedEvent());
    }


    /**
     * Set the TV service.
     *
     * @param service the new TV service to be used.
     */
    public void setService(final Service service) {
        if (this.getService() != null) {

            //Launch the TV mMultiscreenApp directly if we already got the TV service.
            if (getService().equals(service)) {

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

        //Clear all the listeners.
        listeners.clear();

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
                if (client != null) {

                    //Notify service change listeners.
                    notifyListeners(false);
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
                //Log.d(Constants.APP_TAG, "Application is connected: " + client.toString());

                //stop discovery to save battery when a service is selected.
                stopDiscovery();

                //Update Service list.
                //Remove the connected service from switch to list.
                //adapter.remove(getService());
                //adapter.notifyDataSetChanged();

                //Notify UI listeners.
                notifyListeners(false);
                EventBus.getDefault().post(new ConnectionChangedEvent(null));
            }
        });

        // Listen for the errors.
        mMultiscreenApp.setOnErrorListener(new Channel.OnErrorListener() {
            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                notifyListeners(false);
                EventBus.getDefault().post(new ConnectionChangedEvent(error.toString()));

                if (!isExisting) startDiscovery();
            }
        });

        //Connect and launch the TV application.
        mMultiscreenApp.connect(new Result<Client>() {

            @Override
            public void onSuccess(Client client) {
            }

            @Override
            public void onError(com.samsung.multiscreen.Error error) {
                //failed to launch TV application. Notify TV service changes.
                notifyListeners(false);

                EventBus.getDefault().post(new ConnectionChangedEvent(error.toString()));
            }
        });
    }

    public void disconnect() {
        if (service != null && mMultiscreenApp != null && mMultiscreenApp.isConnected()) {
            mMultiscreenApp.disconnect();
        }
    }


    /**
     * Sent the byte array to TV.
     *
     * @param event   the channel event.
     * @param data    the object to sent to TV.
     * @param payload payload data in format of byte array.
     */
    public void sendToTV(String event, Object data, byte[] payload) {
        if (mMultiscreenApp != null && mMultiscreenApp.isConnected()) {
            mMultiscreenApp.publish(event, data, payload);
        }
    }

    /**
     * Sent the data to TV.
     *
     * @param event the channel event.
     * @param data  the object to sent to TV.
     */
    public void sendToTV(String event, Object data) {
        if (mMultiscreenApp != null && mMultiscreenApp.isConnected()) {
            mMultiscreenApp.publish(event, data);
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
