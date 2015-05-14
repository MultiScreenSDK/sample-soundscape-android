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

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.multiscreen.util.RunUtil;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.ServiceAdapter;
import com.samsung.soundscape.events.ConnectionChangedEvent;
import com.samsung.soundscape.events.ServiceChangedEvent;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

import de.greenrobot.event.EventBus;

public class ConnectActivity extends AppCompatActivity implements ConnectivityManager.ServiceChangedListener {
    Button actionButton;
    TextView discoveryMessage;
    TextView wifiMessage;

    /** The connectivity manager instance. */
    private ConnectivityManager mConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connectContent).setVisibility(View.VISIBLE);
            }
        }, getResources().getInteger(R.integer.splash_timeout));

        actionButton = (Button) findViewById(R.id.connect_button);
        actionButton.setOnClickListener(actionButtonOnClickListener);

        discoveryMessage = (TextView) findViewById(R.id.discover_message);
        wifiMessage = (TextView) findViewById(R.id.wifi_message);

        //Get connectivity manager.
        mConnectivityManager = ConnectivityManager.getInstance();
        //Create service adapter to store services
        mConnectivityManager.setServiceAdapter(new ServiceAdapter(this, R.layout.service_list_item));

        //Register to receive events.
        EventBus.getDefault().register(this);
    }

    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        //Clean up everything before exiting.
        cleanup();
    }

    public void onStart() {
        super.onStart();

        //Start the service discovery if it is not started before.
        if (!mConnectivityManager.isDiscovering()) {
            //Make sure the connected device will be added into list.
            //mConnectivityManager.getServiceAdapter().setAddConnectedService(true);



            //start discovery.
            mConnectivityManager.startDiscovery();
        }
    }

    public void onStop() {
        super.onStop();

        //Stop discovery when the app goes to background.
        mConnectivityManager.stopDiscovery();
    }

    private OnClickListener actionButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            ServiceAdapter adapter = mConnectivityManager.getServiceAdapter();
            int count = adapter.getCount();

            if (count == 0) {
                //Show information
            } else if (count == 1) {
                //Connect to the device directly.
               mConnectivityManager.setService(adapter.getItem(0));
            } else {
                //show select device dialog.
                showServiceListDialog();
            }

        }
    };


    // This method will be called when a MessageEvent is posted
    public void onEvent(ConnectionChangedEvent event){
        if (mConnectivityManager.isTVConnected()) {

            //When TV is connected, go to playlist screen.
            startActivity(new Intent(this, PlaylistActivity.class));
        }
    }

    // This method will be called when a SomeOtherEvent is posted
    public void onEvent(ServiceChangedEvent event){
        int count = mConnectivityManager.getServiceAdapter().getCount();

        if (count == 0) {
            updateUI(getString(R.string.connect_status_nodevice),
                    String.format(getString(R.string.connect_status_on),
                            Util.getWifiName()),
                    getString(R.string.connect_status_information)
            );
        } else if (count == 1) {
            updateUI(String.format(getString(R.string.connect_status_discovered), Util.getWifiName()),
                    null,
                    getString(R.string.connect)
            );
        } else {
            updateUI(String.format(getString(R.string.connect_status_found_devices), count),
                    String.format(getString(R.string.connect_status_on),
                            Util.getWifiName()),
                    getString(R.string.select_device)
            );
        }
    }

    @Override
    public void onServiceChanged() {

    }

    @Override
    public void onConnectionChanged() {

    }

    public void updateUI(final String firstMsg, final String secondMsg, final String buttonCaption) {
        RunUtil.runOnUI(new Runnable() {
            @Override
            public void run() {

                discoveryMessage.setVisibility(firstMsg != null ? View.VISIBLE : View.INVISIBLE);
                wifiMessage.setVisibility(secondMsg != null ? View.VISIBLE : View.INVISIBLE);

                if (firstMsg != null) {
                    discoveryMessage.setText(firstMsg);
                }

                if (secondMsg != null) {
                    wifiMessage.setText(secondMsg);
                }

                if (buttonCaption != null) {
                    actionButton.setText(buttonCaption);
                }
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

        // Create and show the dialog, only shows the connect to panel.
        DialogFragment newFragment = ServiceListFragment.newInstance(0);
        newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_NoTitleBar);
        newFragment.show(ft, "dialog");
    }

    /**
     * The clean up method, it should only be called when application exits.
     */
    private void cleanup() {
        //Clean up multiscreen service.
        mConnectivityManager.clearService();
        mConnectivityManager = null;
    }

    public void displayConnectingMessage(String tvName) {
        String message = String.format(getString(R.string.connect_to_message), Util.getFriendlyTvName(tvName));
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(40, 20, 40, 20);
        textView.setBackgroundColor(0x80000000);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);


        Toast toast = Toast.makeText(this,message, Toast.LENGTH_LONG);
        toast.setView(textView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
