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

package com.samsung.soundscape.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;
import com.rey.material.widget.FloatingActionButton;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.util.RunUtil;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.SwipeableTracksAdapter;
import com.samsung.soundscape.adapter.TracksAdapter;
import com.samsung.soundscape.events.AddTrackEvent;
import com.samsung.soundscape.events.AppStateEvent;
import com.samsung.soundscape.events.AssignColorEvent;
import com.samsung.soundscape.events.ConnectionChangedEvent;
import com.samsung.soundscape.events.RemoveTrackEvent;
import com.samsung.soundscape.events.TrackPlaybackEvent;
import com.samsung.soundscape.events.TrackStatusEvent;
import com.samsung.soundscape.interceptor.AppCompatActivityMenuKeyInterceptor;
import com.samsung.soundscape.model.CurrentStatus;
import com.samsung.soundscape.model.Track;
import com.samsung.soundscape.util.AnimationUtils;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

import java.util.Arrays;
import java.util.UUID;

import de.greenrobot.event.EventBus;

public class PlaylistActivity extends AppCompatActivity {
    //The add button at the bottom and right corner.
    private FloatingActionButton addButton;

    //The direction of the add button.
    private boolean clockwise = true;

    //The color of current user.
    private int userColor;

    //The flag shows that it is switch service.
    //Do not close this activity while switching service.
    public boolean isSwitchingService = false;


    //-------------------------The header area ------------------------------
    //The connected to section container.
    private LinearLayout connectedToHeader;
    Toolbar toolbar;

    //The S icon at the top right screen.
    private ImageView connectedToIcon;

    //The text to display the connected service name.
    private TextView connectedToText;

    //-------------------------The library view ------------------------------
    //The library layout
    private ViewGroup libraryLayoutWrapper;
    private ViewGroup libraryLayout;

    //The adapter to display tracks from library.
    TracksAdapter libraryAdapter;

    //-------------------------The playlist view ------------------------------
    //The adapter to display playlist.
    SwipeableTracksAdapter playlistAdapter;

    //The list view to display playlist.
    ListView playlistListView;


    //-------------------------The playback view ------------------------------
    //playback view.
    LinearLayout nowPlaying;

    //The track title and artist in the playback view.
    TextView songTitle, songArtist;

    //The play/pause button
    private ImageView playControl;
    private StateListDrawable playControlDrawable;

    //The media time panel.
    private View mediaTime;

    //The next button in the playback view.
    private ImageView nextControl;

    //The seek bar in playback control.
    SeekBar seekBar;

    //The media playback position.
    private TextView postionTextView;

    //The video duration.
    private TextView durationTextView;

    //The flag shows it is seeking to a new position. The video status event will be ignored during seeking.
    boolean isSeeking = false;

    //When is seeking, we will not update the seek bar until this value is reached.
    int expectedSeekbarValue;

    private Handler handler = new Handler();

    private Toast toastShowAddTrack;
    private  View toastLayout;
    private TextView addTrackText;


    //=========================Activity methods===================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //Initialize the interceptor
        AppCompatActivityMenuKeyInterceptor.intercept(this);

        //Add toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //select user color.
        selectColor(null);

        //Initialize library view
        initializeLibraryView();

        //initialize playback view.
        initializePlaybackView();

        //initialize playlist view
        initializeOtherViews();

        //Register to receive events.
        EventBus.getDefault().register(this);

        //Request user color.
        ConnectivityManager.getInstance().requestAssignColor();

        //Request multiscreen app state.
        ConnectivityManager.getInstance().requestAppState();

        //Load library in background.
        RunUtil.runInBackground(loadLibrary);

        //Update UI with color and service information.
        updateUI();


        //Given the initial state of the play/pause button.
        setPlayState(false);

        //Hide the playback panel.
        updatePlaybackView(null, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (!clockwise) {

            //If library list is shown, close the dialog instead of exit activity.
            animateLibrary(addButton);
        } else {

            //Exit activity.
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

        Util.d("PlaylistActivity.onDestroy");

        // Reset add button and close library in case it is open
        clockwise = false;
        animateLibrary(addButton);

        //Remove event monitor.
        EventBus.getDefault().unregister(this);

        ConnectivityManager.getInstance().restartDiscovery();

        //Disconnect from multiscreen app.
        ConnectivityManager.getInstance().disconnect();
    }

    //=====================Events received from ConnectivityManager============================

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

                //Request user color from server.
                ConnectivityManager.getInstance().requestAssignColor();

                //Request multiscreen app state.
                ConnectivityManager.getInstance().requestAppState();

                //Update UI such as user color.
                updateUI();
            }
        } else {
            //Error happens, go back to connect screen.
            finish();
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

        CurrentStatus state = event.state.getCurrentStatus();

        //If it is playing something, update the status.
        if (state != null) {
            setPlayState(!state.isPlaying());
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
     * Triggered when a track is removed.
     *
     * @param event
     */
    public void onEvent(RemoveTrackEvent event) {
        Util.d("RemoveTrackEvent: " + event.id);

        Track track = getTrackById(event.id);

        //remove it from playlist
        removeTrack(track);
    }

    /**
     * Triggered when playback state is changed.
     *
     * @param event the playback event.
     */
    public void onEvent(TrackPlaybackEvent event) {
        Util.d("TrackPlaybackEvent: " + event.event);

        if (event.isStart()) {
            //Update the songs information in control panel when there is track in the playlist.
            if (playlistAdapter.getCount()>0) {
                updatePlaybackView(event.id, 0);
            }
        } else {
            //Track is end. Remove the track from playlist.
           removeTrack(event.id);
        }
    }

    /**
     * Triggered when track status is update.
     *
     * @param event
     */
    public void onEvent(TrackStatusEvent event) {
        Util.d("TrackStatusEvent: " + event.status.toJsonString());

        CurrentStatus status = event.status;
        if (status != null) {

            //Only update the play status when the state is changed.
            if (status.getState() != null && status.isPlaying() != isPlaying()) {
                setPlayState(!status.isPlaying());
            }

            if (isSeeking) {
                if ((int) status.getTime() == expectedSeekbarValue) {
                    isSeeking = false;
                } else {
                    Util.d("MainActivity  in the seeking mode, ignore the status update.");
                    return;
                }
            }

            //Only update view when there is track in the playlist.
            if (playlistAdapter.getCount()>0) {
                updatePlaybackView(status.getId(), status.getTime());
            }
        }
    }

    /**
     * Triggered when user color is assigned.
     *
     * @param event
     */
    public void onEvent(AssignColorEvent event) {
        selectColor(event.color);
        updateUI();
    }

    //====================private methods============================

    private void initializeLibraryView() {

        libraryLayoutWrapper = (ViewGroup) findViewById(R.id.libraryLayoutWrapper);
        libraryLayoutWrapper.setVisibility(View.GONE);
        libraryLayoutWrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Prevent touch events from bubbling to other views
                return true;
            }
        });
        libraryLayout = (ViewGroup) findViewById(R.id.libraryLayout);
        libraryLayout.setVisibility(View.GONE);

        ListView libraryListView = (ListView) findViewById(R.id.libraryListView);
        libraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track track = libraryAdapter.getItem(position);

                Track copyTrack = (Track)libraryAdapter.getItem(position).clone();
                copyTrack.setColor(String.format("#%06X", (0xFFFFFF & userColor)));
                copyTrack.setId(UUID.randomUUID().toString());

                //Add track to the playlist.
                addTrack(copyTrack);

                //Broadcast the addtrack event.
                ConnectivityManager.getInstance().addTrack(copyTrack);

                //Show track is added message.
                showAddTrackToastMessage(copyTrack.getTitle());
            }
        });

        //Create library adapter.
        libraryAdapter = new TracksAdapter(PlaylistActivity.this, R.layout.library_list_item);
        libraryListView.setAdapter(libraryAdapter);
    }

    private void initializeOtherViews() {
        //Playlist view and adapter
        playlistAdapter = new SwipeableTracksAdapter(this, R.layout.playlist_list_item);
        playlistListView = (ListView) findViewById(R.id.playlistListView);
        playlistListView.setAdapter(playlistAdapter);
        playlistAdapter.setMode(Attributes.Mode.Single);

        // Reveal the trash can to remove tracks onItemClick
//        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ((SwipeLayout) (playlistListView.getChildAt(position - playlistListView.getFirstVisiblePosition()))).open(true);
//            }
//        });

        // Hide any revealed trash can when the playlist is scrolled
        playlistListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                playlistAdapter.closeAllItems();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        //show the speaker or TV icon depends on service type.
        connectedToIcon = (ImageView) findViewById(R.id.connectedToIcon);

        connectedToText = (TextView) findViewById(R.id.connectedToText);
        connectedToHeader = (LinearLayout) findViewById(R.id.connectedToHeader);

        //Add button
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Try to download the content again if something wrong before.
                if (libraryAdapter.getCount() == 0) {
                    //Load library in background.
                    RunUtil.runInBackground(loadLibrary);
                }

                //Display the animation.
                animateLibrary(v);
            }
        });
    }

    /**
     * Initialize the playback view located at the bottom of the screen.
     */
    private void initializePlaybackView() {
        //Playback layout.
        nowPlaying = (LinearLayout) findViewById(R.id.nowPlaying);

        songTitle = (TextView) findViewById(R.id.songTitle);
        songArtist = (TextView) findViewById(R.id.songArtist);

        //Play and pause button
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

        //Next button
        nextControl = (ImageView) findViewById(R.id.nextControl);
        nextControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistAdapter.getCount()>0) {
                    removeTrack((Track)playlistAdapter.getItem(0));
                    ConnectivityManager.getInstance().next();
                }
            }
        });

        mediaTime = findViewById(R.id.mediaTime);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                // Seek bar is changed.
                isSeeking = true;
                expectedSeekbarValue = seekBar.getProgress();
                postionTextView.setText(Util.formatTimeString(expectedSeekbarValue * 1000));
                ConnectivityManager.getInstance().seek(expectedSeekbarValue);
            }
        });

        //Media position view.
        postionTextView = (TextView) findViewById(R.id.positionTextView);

        //Track duration view.
        durationTextView = (TextView) findViewById(R.id.durationTextView);
    }

    /**
     * Show the track is added toast message.
     */
    private void showAddTrackToastMessage(String title) {

        //Load customized layout.
        if (toastLayout == null || addTrackText==null) {
            toastLayout = getLayoutInflater().inflate(R.layout.add_track, (ViewGroup) findViewById(R.id.toastLayout));
            addTrackText = (TextView)toastLayout.findViewById(R.id.addTrackText);
        }

        if (toastShowAddTrack != null) {
            toastShowAddTrack.cancel();
        }
        toastShowAddTrack = null;


        //Get the add button position in the window.
        int[] locationInWindow = new int[2];
        addButton.getLocationInWindow(locationInWindow);

        addTrackText.setText("\"" + title + "\"");
        toastShowAddTrack = new Toast(getApplicationContext());
        toastShowAddTrack.setDuration(Toast.LENGTH_SHORT);
        toastShowAddTrack.setView(toastLayout);

        // Move the message above add button with offset 30
        toastShowAddTrack.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, Util.getDisplayHeight(this) - locationInWindow[1] + 30);
        toastShowAddTrack.show();
    }

    /**
     * Animate showing and hiding the music library view.
     *
     * @param v the view to animate
     */
    private void animateLibrary(final View v) {
        int level = 5000;
        if (!clockwise) {
            level = 0;
        }
        final boolean show = clockwise;
        clockwise = !clockwise;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((FloatingActionButton) v).getIcon().setLevel(level);
        } else {
            // TODO: Figure out a workaround for the pre-Lollipop animation issue where
            // the final animation state is always reset, regardless of setting fillAfter.
            ((FloatingActionButton) v).getIcon().setLevel(level);
        }
        if (show) {
            showLibraryDialog();
        } else {
            hideLibraryDialog();
        }
    }

    /**
     * Add track into playlist and update UI.
     *
     * @param track
     */
    private void addTrack(Track track) {
        playlistAdapter.add(track);
    }

    /**
     * Whether or not it is playing track.
     *
     * @return true playing tracking otherwise paused.
     */
    private boolean isPlaying() {
        //When pause button is showed, it is playing status, vice verse.
        return !Arrays.equals(playControlDrawable.getState(), new int[]{android.R.attr.state_enabled});
    }

    /**
     * Update the play/pause button
     *
     * @param statePlaying
     */
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

    /**
     * The background thread to load playlist from server.
     */
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
                Track[] tracks = gson.fromJson(data, Track[].class);
                if (tracks != null) {
                    addTracksIntoLibrary(tracks);
                }
            }
        }
    };

    /**
     * Add tracks into library.
     * @param tracks
     */
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


    /**
     * Show service list dialog.
     */
    private void showServiceListDialog() {

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
        newFragment.show(ft, "dialog");
    }


    /**
     * Display library list dialog.
     */
    private void showLibraryDialog() {
        libraryLayoutWrapper.setVisibility(View.INVISIBLE);
        AnimationUtils.applyViewAnimation(this, libraryLayoutWrapper, R.anim.fade_in, null);
        libraryLayout.setVisibility(View.INVISIBLE);
        AnimationUtils.expand(this, libraryLayout, null);
    }

    /**
     * Hide the library list dialog.
     */
    private void hideLibraryDialog() {
        AnimationUtils.collapse(this, libraryLayout, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                libraryLayout.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AnimationUtils.applyViewAnimation(this, libraryLayoutWrapper, R.anim.fade_out, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                libraryLayoutWrapper.clearAnimation();
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


        //Update the toolbar background color.
        toolbar.setBackgroundColor(userColor);

        //Update the connected to header background color.
        connectedToHeader.setBackgroundColor(userColor);

        //Set the plus button color.
        addButton.setBackgroundColor(userColor);

        //Display the service name.
        Service service = ConnectivityManager.getInstance().getService();
        if (service != null) {
            connectedToText.setText(Util.getFriendlyTvName(service.getName()));
        }

    }

    /**
     * Update the playback view according to track id and time.
     *
     * @param id   the track id which is currently playing.
     * @param time the playback position. You may update the progress bar according to this value.
     */
    private void updatePlaybackView(String id, float time) {
        int marginBottom = (int) getResources().getDimension(R.dimen.add_button_bottom_margin);

        if (id != null) {

            //When some track is playing, show the playback view.
            nowPlaying.setVisibility(View.VISIBLE);

            //Show seek bar.
            seekBar.setVisibility(View.VISIBLE);

            //Show the media time panel.
            mediaTime.setVisibility(View.VISIBLE);

            //Get the track information.
            Track track = getTrackById(id);

            //If the playing track is in the playlist, update the track information.
            if (track != null) {
                songTitle.setText(track.getTitle());
                songArtist.setText(track.getArtist());

                seekBar.setMax(track.getDuration());
                seekBar.setProgress((int)time);
                postionTextView.setText(Util.formatTimeString((int)(time * 1000)));
                durationTextView.setText(Util.formatTimeString(track.getDuration() * 1000));
            }

            //Move the add button
            setAddButtonBottomMargin(marginBottom*3);

        } else {

            //Nothing is playing now. Hide the playback view.
            nowPlaying.setVisibility(View.GONE);

            //Hide seek bar.
            seekBar.setVisibility(View.GONE);

            //Hide the media time panel.
            mediaTime.setVisibility(View.GONE);

            //Move the add button
            setAddButtonBottomMargin(marginBottom);
        }
    }

    private void setAddButtonBottomMargin(int margin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)addButton.getLayoutParams();
        params.setMargins(0, 0,
                (int) getResources().getDimension(R.dimen.add_button_right_margin),
                margin);
        addButton.setLayoutParams(params);
    }

    /**
     * Find the track by id in the playlist.
     *
     * @param id the track id.
     * @return the track.
     */
    private Track getTrackById(String id) {
        if (id != null && playlistAdapter != null) {
            for (int i = 0; i < playlistAdapter.getCount(); i++) {
                Track track = (Track) playlistAdapter.getItem(i);
                if (track.getId().equals(id)) {
                    return track;
                }
            }
        }

        return null;
    }

    /**
     * Remove the track by given track id.
     *
     * @param id the track id to be removed.
     */
    private void removeTrack(String id) {
        if (id != null && playlistAdapter != null) {
            removeTrack(getTrackById(id));
        }
    }

    /**
     * Remove the given track.
     * @param track the track to remove.
     */
    private void removeTrack(Track track) {
        if (track == null || playlistAdapter == null) {
            return;
        }

        playlistAdapter.remove(track);

        //If there is no track in the playlist, hide the play control panel.
        if (playlistAdapter.getCount() == 0) {
            updatePlaybackView(null, 0);
        }
    }


    /**
     * Select user color.
     */
    private void selectColor(String newColor) {
        String color = newColor;

        if (color != null) {
            //parse color string to color value.
            userColor = Color.parseColor(color);
        } else {
            //Use default color.
            userColor = getResources().getColor(R.color.user_color_1);
        }
    }
}
