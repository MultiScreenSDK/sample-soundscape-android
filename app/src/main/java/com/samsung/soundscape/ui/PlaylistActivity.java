package com.samsung.soundscape.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.samsung.soundscape.App;
import com.samsung.soundscape.R;
import com.samsung.soundscape.util.ConnectivityManager;

public class PlaylistActivity extends AppCompatActivity implements ConnectivityManager.ServiceChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //Start to monitor multiscreen status.
        App.getInstance().getConnectivityManager().addServiceChangedListener(this);
    }


    protected void onDestroy() {
        super.onDestroy();

        //Remove from listener list
        App.getInstance().getConnectivityManager().removeServiceChangedListener(this);
        App.getInstance().getConnectivityManager().disconnect();
    }

    @Override
    public void onServiceChanged() {}

    @Override
    public void onConnectionChanged() {
        //When the device is disconnected, go back to connect screen.
        if (!App.getInstance().getConnectivityManager().isTVConnected()) {
            finish();
        }
    }
}
