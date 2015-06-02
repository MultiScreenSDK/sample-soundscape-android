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

package com.samsung.soundscape.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemAdapterMangerImpl;
import com.daimajia.swipe.util.Attributes;
import com.samsung.soundscape.R;
import com.samsung.soundscape.model.Track;
import com.samsung.soundscape.ui.PlaylistActivity;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A swipeable adapter for playlist tracks that supports swiping to reveal a trash icon
 * to remove tracks.
 */
public class SwipeableTracksAdapter extends ArraySwipeAdapter<Track> {
    private SwipeItemAdapterMangerImpl mItemManager = new SwipeItemAdapterMangerImpl(this);
    private int layoutResourceId;
    private LayoutInflater inflater;

    private static int colorBlack;
    private static int colorItem;

    public SwipeableTracksAdapter(Context context, int resource) {
        super(context, resource);
        this.layoutResourceId = resource;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        colorBlack = getContext().getResources().getColor(R.color.black);
        colorItem = getContext().getResources().getColor(R.color.playlist_list_item);
    }

    public boolean contains(Track track) {
        return (getPosition(track) >= 0);
    }

    /**
     * Replace the existing service with new service.
     *
     * @param track the new track.
     */
    public void replace(Track track) {

        //Get the service position.
        int position = getPosition(track);

        //Check if position is valid.
        if (position >= 0) {

            //Remove the existing service.
            remove(track);

            //Insert the new service at the same position.
            insert(track, position);
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    static class ViewHolder {
        public TextView songTitle;
        public TextView songArtist;
        public ImageView albumArt;
        public View userColor;
        public ImageView nowPlayingIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean convertViewIsNull = (convertView == null);
        View row = convertView;

        Track track = (Track) getItem(position);

        ImageView trash = null;

        if (convertViewIsNull) {
            row = inflater.inflate(layoutResourceId, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.songTitle = (TextView) row.findViewById(R.id.songTitle);
            holder.songArtist = (TextView) row.findViewById(R.id.songArtist);
            holder.albumArt = (ImageView) row.findViewById(R.id.albumArt);
            holder.userColor = row.findViewById(R.id.userColor);
            holder.nowPlayingIcon = (ImageView) row.findViewById(R.id.nowPlayingIcon);
            row.setTag(holder);
            this.mItemManager.initialize(row, position);
        } else {
            this.mItemManager.updateConvertView(row, position);
        }
        SwipeLayout sl = (SwipeLayout)row;
        sl.setLeftSwipeEnabled(false);
        sl.setTopSwipeEnabled(false);
        sl.setBottomSwipeEnabled(false);

        trash = (ImageView) row.findViewById(R.id.trash);
        trash.setTag(track);

        if (convertViewIsNull) {
            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Track track = (Track) v.getTag();

                    closeAllItems();
                    Activity activity = (Activity) getContext();

                    if (activity instanceof PlaylistActivity) {
                        remove(track);

                        //Tell other clients and TV app to remove it as well.
                        ConnectivityManager.getInstance().removeTrack(track.getId());
                    }
                }
            });
        }

        final ViewHolder holder = (ViewHolder) row.getTag();

        //Set the track title.
        holder.songTitle.setText(track.getTitle());

        //Set the first item to black.
        holder.songTitle.setTextColor((position == 0)?colorBlack:colorItem);

        //Update the artist.
        holder.songArtist.setText(track.getArtist());

        //Load the thumbnail.
        String albumArtThumbnail = track.getAlbumArtThumbnail();
        if (albumArtThumbnail != null) {
            Picasso.with(getContext()).load(Util.getUriFromUrl(albumArtThumbnail)).fit().
                    error(R.drawable.album_placeholder).into(holder.albumArt);
        }

        //Update user color
        if (holder.userColor != null) {
            holder.userColor.setBackgroundColor(track.getColorInt());
        }

        //Show now playing icon on top of thumbnail for the first song.
        if (holder.nowPlayingIcon != null) {
            holder.nowPlayingIcon.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        }

        return row;
    }

    @Override
    public void openItem(int position) {
        this.mItemManager.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        this.mItemManager.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        this.mItemManager.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        this.mItemManager.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return this.mItemManager.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return this.mItemManager.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        this.mItemManager.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return this.mItemManager.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return this.mItemManager.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        this.mItemManager.setMode(mode);
    }
}
