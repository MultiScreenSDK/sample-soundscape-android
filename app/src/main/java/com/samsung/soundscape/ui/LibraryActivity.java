package com.samsung.soundscape.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.samsung.soundscape.R;
import com.samsung.soundscape.util.ConnectivityManager;

public class LibraryActivity extends AppCompatActivity implements ConnectivityManager.ServiceChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //Start to monitor multiscreen status.
        ConnectivityManager.getInstance().addServiceChangedListener(this);
    }

    protected void onDestroy() {
        super.onDestroy();

        //Remove from listener list
        ConnectivityManager.getInstance().removeServiceChangedListener(this);
    }

    @Override
    public void onServiceChanged() {
    }

    @Override
    public void onConnectionChanged() {
        //When the device is disconnected, go back to connect screen.
        if (!ConnectivityManager.getInstance().isTVConnected()) {
            finish();
        }
    }
}
