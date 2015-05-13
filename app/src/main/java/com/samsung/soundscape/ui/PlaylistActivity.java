package com.samsung.soundscape.ui;

import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.rey.material.widget.FloatingActionButton;
import com.samsung.soundscape.App;
import com.samsung.soundscape.R;
import com.samsung.soundscape.util.ConnectivityManager;

import java.util.Arrays;

public class PlaylistActivity extends AppCompatActivity implements ConnectivityManager.ServiceChangedListener {

    private ImageView playControl;
    private StateListDrawable playControlDrawable;
    private boolean clockwise = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        playControl = (ImageView)findViewById(R.id.playControl);
        playControlDrawable = (StateListDrawable)playControl.getDrawable();

        // TODO: Replace this example play control
        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(this.getClass().getName(), "Play Control");
                if (Arrays.equals(playControlDrawable.getState(), new int[]{android.R.attr.state_enabled})) {
                    playControlDrawable.setState(new int[]{android.R.attr.state_enabled, android.R.attr.state_checked});
                } else {
                    playControlDrawable.setState(new int[]{android.R.attr.state_enabled});
                }
                playControl.setImageDrawable(playControlDrawable.getCurrent());
            }
        });

        final FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resource = R.anim.rotate_clockwise;
                if (!clockwise) {
                    resource = R.anim.rotate_counterclockwise;
                }
                clockwise = !clockwise;
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), resource);
                v.startAnimation(rotation);
            }
        });

        //Start to monitor multiscreen status.
        App.getInstance().getConnectivityManager().addServiceChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
//        if (drawerLayout != null && isDrawerOpen()) {
//            inflater.inflate(R.menu.global, menu);
//            showGlobalContextActionBar();
//        }
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
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
