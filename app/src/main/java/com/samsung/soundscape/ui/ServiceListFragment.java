/**
 * ****************************************************************************
 * Copyright (c) 2015 Samsung Electronics
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * *****************************************************************************
 */

package com.samsung.soundscape.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.samsung.multiscreen.Service;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.ServiceAdapter;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

public class ServiceListFragment extends DialogFragment {
    int mColor;

    /**
     * Create a new instance of MyDialogFragment, providing dialog type
     * as an argument.
     */
    static ServiceListFragment newInstance(int color) {
        ServiceListFragment f = new ServiceListFragment();

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_connect_service, null);


        if (view != null) {
            ListView listView = (ListView)view.findViewById(R.id.deviceListView);
            listView.setAdapter(ConnectivityManager.getInstance().getServiceAdapter());
            listView.setOnItemClickListener(new ListView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ServiceAdapter adapter = ConnectivityManager.getInstance().getServiceAdapter();
                    Service service = adapter.getItem(position);

                    Activity activity = getActivity();
                    if (activity instanceof ConnectActivity) {
                        ConnectActivity ca = (ConnectActivity) getActivity();
                        ca.displayConnectingMessage(service.getName());
                    } else if (activity instanceof PlaylistActivity) {
                        PlaylistActivity pa = (PlaylistActivity) activity;
                        pa.isSwitchingService = true;
                        ConnectivityManager.getInstance().disconnect();
                    }

                    ConnectivityManager.getInstance().setService(service);
                    ServiceListFragment.this.getDialog().dismiss();
                }
            });

            LinearLayout llConnectTo = (LinearLayout)view.findViewById(R.id.selectedServiceLayout);
            TextView connectedToText = (TextView)view.findViewById(R.id.connectedToText);
            ImageView connectedToIcon = (ImageView)view.findViewById(R.id.connectedToIcon);

            if (ConnectivityManager.getInstance().isTVConnected()) {
                //Display connected device and disconnect button.
                llConnectTo.setVisibility(View.VISIBLE);
                connectedToText.setText(getString(R.string.connected_to));
                connectedToIcon.setImageResource(R.drawable.ic_connected_white);

                ConnectivityManager.getInstance().removeConnectedServiceFromList();


                ImageView selectedServiceIcon = (ImageView)view.findViewById(R.id.selectedServiceIcon);
                if (ConnectivityManager.getInstance().getConnectedServiceType() == ConnectivityManager.ServiceType.Speaker) {
                    //The speaker is connected
                    selectedServiceIcon.setImageResource(R.drawable.ic_speaker_gray);
                } else if (ConnectivityManager.getInstance().getConnectedServiceType() == ConnectivityManager.ServiceType.TV) {
                    //The TV or TV simulator is connected.
                    selectedServiceIcon.setImageResource(R.drawable.ic_tv_white);
                }
                TextView selectedServiceText = (TextView)view.findViewById(R.id.selectedServiceText);
                selectedServiceText.setText(Util.getFriendlyTvName(ConnectivityManager.getInstance().getService().getName()));

                Button btnDisconnect = (Button) view.findViewById(R.id.disconnectButton);
                btnDisconnect.setTextColor(mColor);

                btnDisconnect.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ConnectivityManager.getInstance().addConnectedServerToList();
                        getActivity().finish();
                    }
                });
            } else {
                //Hide connected device and disconnect button.
                llConnectTo.setVisibility(View.GONE);
                connectedToText.setText(getString(R.string.connect_to));
                connectedToIcon.setImageResource(R.drawable.ic_discovered_white);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder.setView(view);

        // Allow dismiss by clicking outside the dialog
        AlertDialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }
}
