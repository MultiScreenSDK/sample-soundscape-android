package com.samsung.soundscape.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsung.multiscreen.Application;
import com.samsung.multiscreen.Channel;
import com.samsung.multiscreen.Message;
import com.samsung.soundscape.R;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaylistActivityFragment extends Fragment {

    public PlaylistActivityFragment() {
    }

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application application = ConnectivityManager.getInstance().getMultiscreenApp();
        if (application != null) {
            application.addOnMessageListener(
                    ConnectivityManager.EVENT_ADD_TRACK, onAddTrackListener);
            application.addOnMessageListener(
                    ConnectivityManager.EVENT_APP_STATE, onAppStateListener);

            application.publish(ConnectivityManager.EVENT_APP_STATE_REQUEST, null, Message.TARGET_HOST);
        }
    }

    public void onDestroy () {
        Application application = ConnectivityManager.getInstance().getMultiscreenApp();
        if (application != null) {
            application.removeOnMessageListener(ConnectivityManager.EVENT_ADD_TRACK, onAddTrackListener);
        }

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }


    private Channel.OnMessageListener onAddTrackListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {

        }
    };

    private Channel.OnMessageListener onAppStateListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {
            Util.d("onAppStateListener:" + message.toString());
        }
    };
}
