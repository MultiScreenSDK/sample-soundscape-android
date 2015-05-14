package com.samsung.soundscape.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.samsung.soundscape.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class LibraryFragment extends DialogFragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private ListView libraryListView;


    //The user color.
    int mColor;

    /**
     * Create a new instance of LibraryFragment.
     */
    static LibraryFragment newInstance(int color) {
        LibraryFragment f = new LibraryFragment();

        // Supply type input as an argument.
        Bundle args = new Bundle();
        args.putInt("color", color);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColor = getArguments().getInt("color");
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_library, container, false);
//        libraryListView = (ListView)view.findViewById(R.id.libraryListView);
//
//        return view;
//    }


    public void onDestroy () {
        super.onDestroy();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_library, null);

        if (view != null) {
//            ListView listView = (ListView)view.findViewById(R.id.deviceListView);
//            listView.setAdapter(ConnectivityManager.getInstance().getServiceAdapter());
//            listView.setOnItemClickListener(new ListView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    ServiceAdapter adapter = ConnectivityManager.getInstance().getServiceAdapter();
//                    Service service = adapter.getItem(position);
//
//                    Activity activity = getActivity();
//                    if (activity instanceof ConnectActivity) {
//                        ConnectActivity ca = (ConnectActivity) getActivity();
//                        ca.displayConnectingMessage(service.getName());
//                    } else if (activity instanceof PlaylistActivity) {
//                        PlaylistActivity pa = (PlaylistActivity) activity;
//                        pa.isSwitchingService = true;
//                        ConnectivityManager.getInstance().disconnect();
//                    }
//
//
//                }
//            });
//
//            LinearLayout llConnectTo = (LinearLayout)view.findViewById(R.id.selectedServiceLayout);
//
//            if (ConnectivityManager.getInstance().isTVConnected()) {
//                //Display connected device and disconnect button.
//                llConnectTo.setVisibility(View.VISIBLE);
//
//                ImageView selectedServiceIcon = (ImageView)view.findViewById(R.id.selectedServiceIcon);
//                if (ConnectivityManager.getInstance().getConnectedServiceType() == ConnectivityManager.ServiceType.Speaker) {
//                    //The speaker is connected
//                    selectedServiceIcon.setImageResource(R.drawable.ic_speaker_gray);
//                } else if (ConnectivityManager.getInstance().getConnectedServiceType() == ConnectivityManager.ServiceType.TV) {
//                    //The TV or TV simulator is connected.
//                    selectedServiceIcon.setImageResource(R.drawable.ic_tv_gray);
//                }
//                TextView selectedServiceText = (TextView)view.findViewById(R.id.selectedServiceText);
//                selectedServiceText.setText(Util.getFriendlyTvName(ConnectivityManager.getInstance().getService().getName()));
//
//                Button btnDisconnect = (Button) view.findViewById(R.id.disconnectButton);
//                btnDisconnect.setTextColor(mColor);
//
//                btnDisconnect.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        //Disconnect from application.
//                        ConnectivityManager.getInstance().disconnect();
//                    }
//                });
//            } else {
//                //Hide connected device and disconnect button.
//                llConnectTo.setVisibility(View.GONE);
//            }

            ListView libraryListView = (ListView)view.findViewById(R.id.libraryListView);
            Activity activity = getActivity();
            if (activity instanceof PlaylistActivity) {
                PlaylistActivity pa = (PlaylistActivity)activity;
                libraryListView.setAdapter(pa.tracksAdapter);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder.setView(view);
        return builder.show();
    }
}
