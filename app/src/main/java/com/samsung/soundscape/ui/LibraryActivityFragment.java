package com.samsung.soundscape.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsung.multiscreen.Application;
import com.samsung.multiscreen.Channel;
import com.samsung.multiscreen.Message;
import com.samsung.soundscape.App;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.SongsAdapter;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

/**
 * A placeholder fragment containing a simple view.
 */
public class LibraryActivityFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    public LibraryActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application application = App.getInstance().getConnectivityManager().getMultiscreenApp();
        if (application != null) {
            application.addOnMessageListener(
                    ConnectivityManager.EVENT_ADD_SONG, onAddSongListener);
        }
    }

    public void onDestroy () {
        Application application = App.getInstance().getConnectivityManager().getMultiscreenApp();
        if (application != null) {
            application.removeOnMessageListener(ConnectivityManager.EVENT_ADD_SONG, onAddSongListener);
        }

        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        //adapter
        mAdapter = new SongsAdapter();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (!Util.supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
            //mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }
        //mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));

    }


    @Override
    public void onDestroyView() {
        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mAdapter != null) {
            //WrapperAdapterUtils.releaseAll(mWrappedAdapter);

            mAdapter = null;
        }

        mLayoutManager = null;

        super.onDestroyView();
    }

    private Channel.OnMessageListener onAddSongListener = new Channel.OnMessageListener() {
        @Override
        public void onMessage(Message message) {

        }
    };
}