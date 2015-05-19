package com.samsung.soundscape.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.soundscape.R;
import com.samsung.soundscape.model.Track;
import com.squareup.picasso.Picasso;

public class TracksAdapter extends ArrayAdapter<Track> {

    private int layoutResourceId;
    private static LayoutInflater inflater = null;
    private Context context;

    public TracksAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
        this.layoutResourceId = resourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


    static class ViewHolder {
        public TextView songTitle;
        public TextView songArtist;
        public ImageView albumArt;
        public View userColor;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.songTitle = (TextView) row.findViewById(R.id.songTitle);
            holder.songArtist = (TextView) row.findViewById(R.id.songArtist);
            holder.albumArt = (ImageView) row.findViewById(R.id.albumArt);
            holder.userColor = row.findViewById(R.id.userColor);
            row.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) row.getTag();

        final Track track = getItem(position);
        holder.songTitle.setText(track.getTitle());
        holder.songArtist.setText(track.getArtist());

        String albumArtThumbnail = track.getAlbumArtThumbnail();
        if (albumArtThumbnail != null) {
            Picasso.with(context).load(albumArtThumbnail.replace(" ", "%20")).fit().into(holder.albumArt);
        }

        if (holder.userColor != null) {
            holder.userColor.setBackgroundColor(track.getColorInt());
        }

        return row;
    }

}
