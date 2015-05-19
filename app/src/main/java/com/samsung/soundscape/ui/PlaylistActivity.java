package com.samsung.soundscape.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rey.material.widget.FloatingActionButton;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.util.RunUtil;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.TracksAdapter;
import com.samsung.soundscape.events.AddTrackEvent;
import com.samsung.soundscape.events.AppStateEvent;
import com.samsung.soundscape.events.ConnectionChangedEvent;
import com.samsung.soundscape.events.TrackPlaybackEvent;
import com.samsung.soundscape.events.TrackStatusEvent;
import com.samsung.soundscape.model.CurrentStatus;
import com.samsung.soundscape.model.Track;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

import java.util.Arrays;
import java.util.UUID;

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

    //The library layout
    private ViewGroup libraryLayoutWrapper;
    private ViewGroup libraryLayout;

    //user colors
    private String[] colors;
    private int userColor;

    //The flag shows that it is switch service.
    //Do not close this activity while switching service.
    public boolean isSwitchingService = false;

    //The adapter to display tracks from library.
    TracksAdapter libraryAdapter;
    //The adapter to display playlist.
    TracksAdapter playlistAdapter;

    ListView playlistListView;
    TextView songTitle, songArtist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //Add toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Load user colors resource.
        colors = getResources().getStringArray(R.array.UserColors);


        initializeLibraryView();
        initializePlaylistView();

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

    private void initializeLibraryView() {

        libraryLayoutWrapper = (ViewGroup) findViewById(R.id.libraryLayoutWrapper);
        libraryLayoutWrapper.setVisibility(View.GONE);
        libraryLayout = (ViewGroup) findViewById(R.id.libraryLayout);

        ListView libraryListView = (ListView) findViewById(R.id.libraryListView);
        libraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = libraryAdapter.getItem(position);
                //format to string #AARRGGBB
                track.setColor(String.format("#%08X", (0xFFFFFFFF & userColor)));
                //Give a unique id for the song to be added.
                track.setId(UUID.randomUUID().toString());

                ConnectivityManager.getInstance().addTrack(track);
                showAddTrackToastMessage();
            }
        });

        //Create library adapter.
        libraryAdapter = new TracksAdapter(PlaylistActivity.this, R.layout.library_list_item);
        libraryListView.setAdapter(libraryAdapter);
    }


    private void initializePlaylistView() {
        playlistAdapter = new TracksAdapter(this, R.layout.playlist_list_item);
        playlistListView = (ListView) findViewById(R.id.playlistListView);
        playlistListView.setAdapter(playlistAdapter);

        //show the speaker or TV icon depends on service type.
        connectedToIcon = (ImageView) findViewById(R.id.connectedToIcon);

        connectedToText = (TextView) findViewById(R.id.connectedToText);
        connectedToHeader = (LinearLayout) findViewById(R.id.connectedToHeader);

        songTitle = (TextView) findViewById(R.id.songTitle);
        songArtist = (TextView) findViewById(R.id.songArtist);

        playControl = (ImageView) findViewById(R.id.playControl);
        playControlDrawable = (StateListDrawable) playControl.getDrawable();
        playControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying()) {
                    ConnectivityManager.getInstance().pause();
                    setPlayState(true);
                } else {
                    ConnectivityManager.getInstance().play();
                    setPlayState(false);
                }
            }
        });

        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateLibrary(v);
            }
        });
    }

    private void showAddTrackToastMessage() {
        View toastLayout = getLayoutInflater().inflate(R.layout.add_track, (ViewGroup) findViewById(R.id.toastLayout));

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();

    }

    private void animateLibrary(final View v) {
        int animResource = R.anim.rotate_clockwise;
        int drawResource = R.drawable.ic_action_cancel;
        int level = 5000;
        if (!clockwise) {
            animResource = R.anim.rotate_counterclockwise;
            drawResource = R.drawable.ic_add_white;
            level = 0;
        }
        final boolean show = clockwise;
        clockwise = !clockwise;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            applyAnimation(v, animResource);
//            Drawable drawable = ((FloatingActionButton)v).getIcon();
//            drawable.setState(new int[]{android.R.attr.state_enabled, android.R.attr.state_checked});
            ((FloatingActionButton)v).getIcon().setLevel(level);
        } else {
            // TODO: Figure out a workaround for the pre-Lollipop animation issue where
            // the final animation state is always reset, regardless of setting fillAfter.
//            addButton.setIcon(getResources().getDrawable(drawResource), false);
            ((FloatingActionButton)v).getIcon().setLevel(level);
        }
        if (show) {
            showLibraryDialog();
        } else {
            hideLibraryDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!clockwise) {
            animateLibrary(addButton);
        } else {
            super.onBackPressed();
        }
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
    public void onEvent(ConnectionChangedEvent event) {
        if (event.errorMessage == null) {
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
    }

    /**
     * Triggered when app state is changed.
     *
     * @param event
     */
    public void onEvent(AppStateEvent event) {
        playlistAdapter.clear();
        playlistAdapter.addAll(event.state.getPlaylist());
        playlistAdapter.notifyDataSetChanged();

        CurrentStatus state = event.state.getStatus();
        //If it is playing something, update the status.
        if (state != null) {
            updatePlaybackView(state.getId(), state.getTime());
        }

    }

    /**
     * Triggered when a new track is added.
     *
     * @param event
     */
    public void onEvent(AddTrackEvent event) {
        addTrack(event.track);
    }

    /**
     * Triggered when playback state is changed.
     *
     * @param event the playback event.
     */
    public void onEvent(TrackPlaybackEvent event) {

        if (event.isStart()) {
            //Update the songs information in control panel.
            updatePlaybackView(event.id, 0);
        } else {
            //Track is end. Remove the track from playlist.
            removeTrackById(event.id);
        }
    }

    public void onEvent(TrackStatusEvent event) {
        CurrentStatus status = event.status;
        if (status != null) {

            //Only update the play status when the state is changed.
            if (status.getState() != null && status.isPlaying() != isPlaying()) {
                setPlayState(!status.isPlaying());
            }

            updatePlaybackView(status.getId(), status.getTime());
        }
    }

    public void addTrack(Track track) {
        playlistAdapter.add(track);
        playlistAdapter.notifyDataSetChanged();
    }

    private boolean isPlaying() {
        //When pause button is showed, it is playing status, vice verse.
        return !Arrays.equals(playControlDrawable.getState(), new int[]{android.R.attr.state_enabled});
    }

    private void setPlayState(boolean statePlaying) {
        Util.d("setPlayState, playing state = " + statePlaying);

        if (statePlaying) {
            //show play button.
            playControlDrawable.setState(new int[]{android.R.attr.state_enabled});
        } else {
            //show paused button.
            playControlDrawable.setState(new int[]{android.R.attr.state_enabled, android.R.attr.state_checked});
        }

        playControl.setImageDrawable(playControlDrawable.getCurrent());
    }

    private Runnable loadLibrary = new Runnable() {
        @Override
        public void run() {
            String data = null;
            try {
                data = Util.readUrl(getString(R.string.playlist_url));
            } catch (Exception e) {
                Util.e("Error when loading library:" + e.toString());
            }

            //Parse data into objects.
            if (data != null) {
                Gson gson = new Gson();
                //Parse string to track array, then add into library list.
                addTracksIntoLibrary(gson.fromJson(data, Track[].class));
            }
        }
    };

    private void addTracksIntoLibrary(final Track[] tracks) {
        RunUtil.runOnUI(new Runnable() {
            @Override
            public void run() {
                libraryAdapter.clear();
                libraryAdapter.addAll(tracks);
                libraryAdapter.notifyDataSetChanged();
            }
        });
    }


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


    private void showLibraryDialog() {
        libraryLayoutWrapper.setVisibility(View.VISIBLE);
        com.samsung.soundscape.util.AnimationUtils.expand(this, libraryLayout, null);
    }

    private void hideLibraryDialog() {
        com.samsung.soundscape.util.AnimationUtils.collapse(this, libraryLayout, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                libraryLayout.clearAnimation();
                libraryLayoutWrapper.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
        if (service != null) {
            connectedToText.setText(Util.getFriendlyTvName(service.getName()));
        }

        //Given the initial state of the play/pause button.
        setPlayState(false);

        //Update the playback panel.
        updatePlaybackView(null, 0);
    }

    private void updatePlaybackView(String id, float time) {
        if (id != null) {
            playControl.setVisibility(View.VISIBLE);
            songTitle.setVisibility(View.VISIBLE);
            songArtist.setVisibility(View.VISIBLE);

            Track track = getTrackById(id);
            if (track != null) {
                songTitle.setText(track.getTitle());
                songArtist.setText(track.getArtist());
            }

        } else {
            playControl.setVisibility(View.INVISIBLE);
            songTitle.setVisibility(View.INVISIBLE);
            songArtist.setVisibility(View.INVISIBLE);
        }
    }

    private Track getTrackById(String id) {
        if (id != null && playlistAdapter != null) {
            for (int i = 0; i < playlistAdapter.getCount(); i++) {
                Track track = playlistAdapter.getItem(i);
                if (track.getId().equals(id)) {
                    return track;
                }
            }
        }

        return null;
    }

    private void removeTrackById(String id) {
        if (id != null && playlistAdapter != null) {
            for (int i = 0; i < playlistAdapter.getCount(); i++) {
                Track track = playlistAdapter.getItem(i);
                if (track.getId().equals(id)) {

                    playlistAdapter.remove(track);
                    break;
                } else {
                    //Remove the tracks before the song with given id.
                    playlistAdapter.remove(track);
                }
            }

            //If there is no track in the playlist, hide the play control panel.
            if (playlistAdapter.getCount() == 0) {
                updatePlaybackView(null, 0);
            }
        }
    }

    /**
     * Select user color. If there is only one color, use the first color in the color list.
     */
    private void selectColor() {
        int index = 0;

        //The host and user are two clients.
        if (ConnectivityManager.getInstance().getClientCount() > 2) {
            index = (int) (colors.length * Math.random());

            //Run again it is the first color. It should already be taken by other users.
            if (index == 0) {
                index = (int) (colors.length * Math.random());
            }
        }

        //Get user color randomly.
        String color = colors[index];

        //parse color string to color value.
        userColor = Color.parseColor(color);
    }
}
