package com.samsung.soundscape.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rey.material.widget.FloatingActionButton;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.util.RunUtil;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.TracksAdapter;
import com.samsung.soundscape.events.ConnectionChangedEvent;
import com.samsung.soundscape.model.Track;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

import java.util.Arrays;

import de.greenrobot.event.EventBus;

public class PlaylistActivity extends AppCompatActivity {

    private ImageView playControl;
    private StateListDrawable playControlDrawable;
    private boolean clockwise = true;

    //The S icon at the top right screen.
    private ImageView connectedToIcon;
    //The add buton at the botoom and right corner.
    private FloatingActionButton addButton;

    //The text to display the connected service name.
    private TextView connectedToText;

    //The connected to section container.
    private LinearLayout connectedToHeader;
    Toolbar toolbar;

    //user colors
    private String[] colors;
    private int userColor;

    //The flag shows that it is switch service.
    //Do not close this activity while switching service.
    public boolean isSwitchingService = false;

    //The adapter to display tracks from library.
    TracksAdapter tracksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        connectedToIcon = (ImageView)findViewById(R.id.connectedToIcon);
        connectedToIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.d("Connect to icon is clicked.");

            }
        });

        connectedToText = (TextView)findViewById(R.id.connectedToText);
        connectedToHeader = (LinearLayout)findViewById(R.id.connectedToHeader);

        //Load user colors resource.
        colors = getResources().getStringArray(R.array.UserColors);

        //Add toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        playControl = (ImageView)findViewById(R.id.playControl);
        playControlDrawable = (StateListDrawable)playControl.getDrawable();

        // TODO: Replace this example play control
        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: replace with Util.d API.
                Log.d(this.getClass().getName(), "Play Control");
                if (Arrays.equals(playControlDrawable.getState(), new int[]{android.R.attr.state_enabled})) {
                    playControlDrawable.setState(new int[]{android.R.attr.state_enabled, android.R.attr.state_checked});
                } else {
                    playControlDrawable.setState(new int[]{android.R.attr.state_enabled});
                }
                playControl.setImageDrawable(playControlDrawable.getCurrent());
            }
        });

        addButton = (FloatingActionButton)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resource = R.anim.rotate_clockwise;
                if (!clockwise) {
                    resource = R.anim.rotate_counterclockwise;


                } else {
                    showLibraryDialog();
                }
                clockwise = !clockwise;
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), resource);
                v.startAnimation(rotation);
            }
        });

        //Register to receive events.
        EventBus.getDefault().register(this);

        //select user color.
        selectColor();

        //Request multiscreen app state.
        ConnectivityManager.getInstance().requestAppState();

        //Load library in background.
        RunUtil.runInBackground(loadLibrary);

        //Update UI with color and service information.
        updateUI();
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

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect:
                //When the S icon is clicked, opens the service list dialog.
                showServiceListDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onDestroy() {
        super.onDestroy();

        //Remove event monitor.
        EventBus.getDefault().unregister(this);

        //Disconnect from multiscreen app.
        ConnectivityManager.getInstance().disconnect();
    }


    // This method will be called when a MessageEvent is posted
    public void onEvent(ConnectionChangedEvent event){
        if (!ConnectivityManager.getInstance().isTVConnected()) {

            //Ignore the disconnect event when it is switching service.
            if (!isSwitchingService) {
                finish();
            }
        } else {
            //Reset the flag when it is connected;
            isSwitchingService = false;

            //Happens when switch service.
            selectColor();
            updateUI();
        }
    }


    private Runnable loadLibrary = new Runnable() {
        @Override
        public void run() {
            String data = null;
            try {
                data = Util.readUrl(getString(R.string.playlist_url));
            }catch (Exception e) {
                Util.e("Error when loading library:" + e.toString());
            }

            //Parse data into objects.
            if (data != null) {
                Track[] tracks;
                Gson gson = new Gson();
                //Parse string to track array.
                tracks = gson.fromJson(data, Track[].class);

                //Create tracks adapter.
                if (tracksAdapter == null) {
                    tracksAdapter = new TracksAdapter(PlaylistActivity.this, R.layout.library_list_item);
                }

                tracksAdapter.clear();
                tracksAdapter.addAll(tracks);
                tracksAdapter.notifyDataSetChanged();
            }
        }
    };

    void showServiceListDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);


        //Get Connectivity manager instance.
        ConnectivityManager cm = ConnectivityManager.getInstance();

        //start discovery
        cm.restartDiscovery();

        // Create and show the dialog, only shows the connect to panel.
        DialogFragment newFragment = ServiceListFragment.newInstance(userColor);
        newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_NoTitleBar);
        newFragment.show(ft, "dialog");
    }


    void showLibraryDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("libraryDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog, only shows the connect to panel.
        DialogFragment newFragment = LibraryFragment.newInstance(userColor);
        newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_NoTitleBar);
        newFragment.show(ft, "libraryDialog");
    }

    private void updateUI() {
        if (ConnectivityManager.getInstance().getConnectedServiceType() == ConnectivityManager.ServiceType.Speaker) {
            //The speaker is connected
            connectedToIcon.setImageResource(R.drawable.ic_speaker_white);
        } else if (ConnectivityManager.getInstance().getConnectedServiceType() == ConnectivityManager.ServiceType.TV) {
            //The TV or TV simulator is connected.
            connectedToIcon.setImageResource(R.drawable.ic_tv_white);
        }


        toolbar.setBackgroundColor(userColor);
        connectedToHeader.setBackgroundColor(userColor);
        addButton.setBackgroundColor(userColor);
        Service service = ConnectivityManager.getInstance().getService();
        connectedToText.setText(Util.getFriendlyTvName(service.getName()));
    }

    private void selectColor() {
        //Get user color randomly.
        String color = colors[(int)(colors.length*Math.random())];
        userColor = Color.parseColor(color);
    }
}
