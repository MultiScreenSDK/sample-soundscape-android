package com.samsung.soundscape.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.samsung.soundscape.R;

public class InfoFragment extends DialogFragment {
//    int mColor;

    /**
     * Create a new instance of MyDialogFragment, providing dialog type
     * as an argument.
     */
    static InfoFragment newInstance() {
        InfoFragment f = new InfoFragment();

        // Supply type input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mColor = getArguments().getInt("color");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.information, null);


//        if (view != null) {
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
//                    ConnectivityManager.getInstance().setService(service);
//                    InfoFragment.this.getDialog().dismiss();
//                }
//            });
//
//            LinearLayout llConnectTo = (LinearLayout)view.findViewById(R.id.selectedServiceLayout);
//            TextView connectedToText = (TextView)view.findViewById(R.id.connectedToText);
//
//            if (ConnectivityManager.getInstance().isTVConnected()) {
//                //Display connected device and disconnect button.
//                llConnectTo.setVisibility(View.VISIBLE);
//                connectedToText.setText(getString(R.string.connected_to));
//
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
//                        //Make sure stop discovery
//                        ConnectivityManager.getInstance().stopDiscovery();
//                        //Disconnect from application.
//                        ConnectivityManager.getInstance().disconnect();
//                    }
//                });
//            } else {
//                //Hide connected device and disconnect button.
//                llConnectTo.setVisibility(View.GONE);
//                connectedToText.setText(getString(R.string.connect_to));
//            }
//        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder.setView(view);
        return builder.show();
    }
}
