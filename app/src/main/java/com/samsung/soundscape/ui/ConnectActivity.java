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

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.util.RunUtil;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.ServiceAdapter;
import com.samsung.soundscape.events.ConnectionChangedEvent;
import com.samsung.soundscape.events.ServiceChangedEvent;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

import de.greenrobot.event.EventBus;

public class ConnectActivity extends AppCompatActivity {
    Button actionButton;
    TextView discoveryMessage;
    TextView wifiMessage;

    /**
     * The connectivity manager instance.
     */
    private ConnectivityManager mConnectivityManager;

    //Show connecting message
    AlertDialog alertDialog;

    //Handler
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);

        //Display the message panel after certain seconds.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connectContent).setVisibility(View.VISIBLE);
            }
        }, getResources().getInteger(R.integer.splash_timeout));


        //In case there is no devices found in your network, we display message after certain seconds.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, getResources().getInteger(R.integer.discoverying_timeout));

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

        //Unregister the events.
        EventBus.getDefault().unregister(this);

        //Stop discovery before exit.
        mConnectivityManager.stopDiscovery();

        //Clean up everything before exiting.
        cleanup();
    }

    public void onStart() {
        super.onStart();

        Util.d("onStart,  isDiscovering=" + mConnectivityManager.isDiscovering());

        //Start the service discovery if it is not started before.
        if (Util.isWiFiConnected()) {
            //start discovery.
            mConnectivityManager.restartDiscovery();
        } else {
            //If it is already discovering. Fetch the result directly.
            updateUI();
        }
    }

    public void onResume() {
        super.onResume();

        //Update the UI.
        updateUI();
    }

    public void onStop() {
        super.onStop();

        Util.d("onStop,  isDiscovering=" + mConnectivityManager.isDiscovering());

        //Stop discovery when the app goes to background.
        mConnectivityManager.stopDiscovery();
    }

    private OnClickListener actionButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            //We will handle the different action according to button title.
            String buttonTitle = ((Button) v).getText().toString();
            if (buttonTitle.equals(getString(R.string.connect_status_information))) {

                //Show information
                showDialog(InfoFragment.newInstance());
            } else if (buttonTitle.equals(getString(R.string.connect))) {
                ServiceAdapter adapter = mConnectivityManager.getServiceAdapter();
                if (adapter != null && adapter.getCount()>0) {

                    //Get the service.
                    Service service = adapter.getItem(0);

                    //Show connecting message.
                    displayConnectingMessage(Util.getFriendlyTvName(service.getName()));

                    //Connect to the device directly.
                    mConnectivityManager.setService(service);
                }
            } else if (buttonTitle.equals(getString(R.string.turn_on_wifi))) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            } else {

                //show select device dialog.
                showDialog(ServiceListFragment.newInstance(0));
            }
        }
    };


    // This method will be called when a MessageEvent is posted
    public void onEvent(ConnectionChangedEvent event) {
        if (mConnectivityManager.isTVConnected()) {

            //Cancel the toast before launch playlist
            cancelToast();

            //When TV is connected, go to playlist screen.
            startActivity(new Intent(this, PlaylistActivity.class));
        } else if (event.errorMessage != null) {
            //Error happens.
            Util.e(event.errorMessage);

            //Show the error message to user.
            displayErrorMessage(event.errorMessage);
        }
    }

    // This method will be called when a service is changed.
    public void onEvent(ServiceChangedEvent event) {
        updateUI();
    }


    /**
     * Update the UI according to the service count and network condition.
     */
    private void updateUI() {
        String buttonTitle = null;
        String discoveryStatus = null;
        String networkInfo = null;

        //Do nothing if connectivity manager is null.
        if (mConnectivityManager == null) {
            return;
        }

        //Check if the WIFI is connected.
        if (Util.isWiFiConnected()) {

            //Get the service adapter.
            ServiceAdapter serviceAdapter = mConnectivityManager.getServiceAdapter();

            //Make sure service adapter is not null.
            if (serviceAdapter == null) {
                return;
            }


            //Get the stored service.
            int count = serviceAdapter.getCount();

            //GEt the Wifi network name.
            String wifiName = Util.getWifiName();

            //When we can get the WiFi network name, we will show the WiFi name.
            if (wifiName != null) {
                networkInfo = String.format(getString(R.string.connect_status_on), wifiName);
            }

            if (count == 0) {//When there is no device found.
                buttonTitle = getString(R.string.connect_status_information);
                discoveryStatus = getString(R.string.connect_status_nodevice);
            } else if (count == 1) {//When there is only one device.

                //Get the device name.
                Service service = mConnectivityManager.getServiceAdapter().getItem(0);
                String tvName = Util.getFriendlyTvName(service.getName());

                discoveryStatus = String.format(getString(R.string.connect_status_discovered), tvName);
                buttonTitle = getString(R.string.connect);
            } else {
                discoveryStatus = String.format(getString(R.string.connect_status_found_devices), count);
                buttonTitle = getString(R.string.select_device);
            }
        } else {
            discoveryStatus = getString(R.string.no_wifi);
            buttonTitle = getString(R.string.turn_on_wifi);
        }


        //Update the UI views.
        updateViews(discoveryStatus, networkInfo, buttonTitle);
    }

    private void updateViews(final String firstMsg, final String secondMsg, final String buttonCaption) {
        RunUtil.runOnUI(new Runnable() {
            @Override
            public void run() {

                discoveryMessage.setVisibility(firstMsg != null ? View.VISIBLE : View.INVISIBLE);
                wifiMessage.setVisibility(secondMsg != null ? View.VISIBLE : View.INVISIBLE);
                actionButton.setVisibility(buttonCaption != null ? View.VISIBLE : View.INVISIBLE);

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

    void showDialog(DialogFragment dialog) {

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
        dialog.show(ft, "dialog");
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
        //final String message = String.format(getString(R.string.connect_to_message), Util.getFriendlyTvName(tvName));
        final String message = Util.getFriendlyTvName(tvName) + "...";
        handler.post(new Runnable() {
            @Override
            public void run() {
                View toastLayout = getLayoutInflater().inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastLayout));
                TextView connectingTo = (TextView) toastLayout.findViewById(R.id.connectingTo);
                connectingTo.setVisibility(View.VISIBLE);
                TextView serviceText = (TextView) toastLayout.findViewById(R.id.serviceText);
                serviceText.setText(message);

                if (alertDialog != null && alertDialog.isShowing()) {
                    cancelToast();
                }
                alertDialog = new AlertDialog.Builder(ConnectActivity.this, R.style.CustomTheme_Dialog).create();
                alertDialog.setView(toastLayout);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }


    /**
     * Display an error message.
     * @param errorMsg
     */
    public void displayErrorMessage(final String errorMsg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                View toastLayout = getLayoutInflater().inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastLayout));
                TextView connectingTo = (TextView) toastLayout.findViewById(R.id.connectingTo);
                connectingTo.setVisibility(View.GONE);
                TextView serviceText = (TextView) toastLayout.findViewById(R.id.serviceText);
                serviceText.setText(errorMsg);

                if (alertDialog != null && alertDialog.isShowing()) {
                    cancelToast();
                }
                alertDialog = new AlertDialog.Builder(ConnectActivity.this, R.style.CustomTheme_Dialog).create();
                alertDialog.setView(toastLayout);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }


    /**
     * Dismiss the connecting message dialog.
     */
    public void cancelToast() {
        if (alertDialog != null) {
            alertDialog.cancel();
        }

        alertDialog = null;
    }

}
