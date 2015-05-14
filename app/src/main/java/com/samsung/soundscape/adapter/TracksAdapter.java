package com.samsung.soundscape.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class TracksAdapter extends RecyclerView.Adapter
                <TracksAdapter.ListItemViewHolder> {

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        public ListItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
