package com.samsung.soundscape.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class SwipeableTracksAdapter extends ArraySwipeAdapter<Track> {
    private SwipeItemAdapterMangerImpl mItemManger = new SwipeItemAdapterMangerImpl(this);
    private int layoutResourceId;
    private LayoutInflater inflater;

    public SwipeableTracksAdapter(Context context, int resource) {
        super(context, resource);
        this.layoutResourceId = resource;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            this.mItemManger.initialize(row, position);
        } else {
            this.mItemManger.updateConvertView(row, position);
        }

        trash = (ImageView) row.findViewById(R.id.trash);
        trash.setTag(track);

        if (convertViewIsNull) {
            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Track track = (Track) v.getTag();
                    int position = getPosition(track);

                    Log.d(this.getClass().getName(), "Remove track: " + track.toJsonString());
                    Log.d(this.getClass().getName(), "position " + position + ": " + track.getTitle());
                    Log.d(this.getClass().getName(), "num tracks: " + SwipeableTracksAdapter.this.getCount());
                    closeAllItems();
                    Activity activity = (Activity) getContext();

                    if (activity instanceof PlaylistActivity) {
                        if (position == 0) {
                            ConnectivityManager.getInstance().next();
                        } else {
                            ((PlaylistActivity)activity).removeTrack(track);

                            //Tell other clients and TV app to remove it as well.
                            ConnectivityManager.getInstance().removeTrack(track.getId());
                        }
                    }
                }
            });
        }

        final ViewHolder holder = (ViewHolder) row.getTag();

        holder.songTitle.setText(track.getTitle());
        holder.songArtist.setText(track.getArtist());

        String albumArtThumbnail = track.getAlbumArtThumbnail();
        if (albumArtThumbnail != null) {
            Uri uri = Uri.parse(albumArtThumbnail);

            Uri.Builder builder = uri.buildUpon();
            builder.scheme(uri.getScheme())
                    .authority(uri.getAuthority())
                    .path(uri.getPath())
                    .query(uri.getQuery())
                    .fragment(uri.getFragment());

            Picasso.with(getContext()).load(builder.toString()).fit().into(holder.albumArt);
        }

        if (holder.userColor != null) {
            holder.userColor.setBackgroundColor(track.getColorInt());
        }

        if (holder.nowPlayingIcon != null) {
            holder.nowPlayingIcon.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        }

        return row;
    }

    @Override
    public void openItem(int position) {
        this.mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        this.mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        this.mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        this.mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return this.mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return this.mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        this.mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return this.mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return this.mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        this.mItemManger.setMode(mode);
    }
}
