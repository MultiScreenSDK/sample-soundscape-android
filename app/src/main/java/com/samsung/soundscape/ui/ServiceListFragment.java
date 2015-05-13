package com.samsung.soundscape.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.samsung.multiscreen.Service;
import com.samsung.soundscape.R;
import com.samsung.soundscape.adapter.ServiceAdapter;
import com.samsung.soundscape.util.ConnectivityManager;

public class ServiceListFragment extends DialogFragment {
    public static final int TYPE_SELECT_SERVICE = 0;
    public static final int TYPE_FULL = 1;
    int mType;

    /**
     * Create a new instance of MyDialogFragment, providing dialog type
     * as an argument.
     */
    static ServiceListFragment newInstance(int type) {
        ServiceListFragment f = new ServiceListFragment();

        // Supply type input as an argument.
        Bundle args = new Bundle();
        args.putInt("type", type);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt("type");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_connect_service, null);


        if (view != null) {
            ListView listView = (ListView)view.findViewById(R.id.deviceListView);
            listView.setAdapter(ConnectivityManager.getInstance().getServiceAdapter());
            listView.setOnItemClickListener(new ListView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ServiceAdapter adapter = ConnectivityManager.getInstance().getServiceAdapter();
                    Service service = adapter.getItem(position);
                    ConnectivityManager.getInstance().setService(service);

                    if (getActivity() instanceof  ConnectActivity) {
                        ConnectActivity activity = (ConnectActivity)getActivity();
                        activity.displayConnectingMessage(service.getName());
                    }
                    ServiceListFragment.this.getDialog().dismiss();
                }
            });
            LinearLayout llConnectTo = (LinearLayout)view.findViewById(R.id.selectedServiceLayout);

            if (mType == TYPE_FULL) {
                //Display connected device and disconnect button.
                llConnectTo.setVisibility(View.VISIBLE);

                Button btnDisconnect = (Button) view.findViewById(R.id.disconnectButton);

                //TODO: set the text color accoding to user's color.
                //btnDisconnect.setTextColor();

                btnDisconnect.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Disconnect from application.
                        ConnectivityManager.getInstance().getMultiscreenApp().disconnect();
                    }
                });
            } else if (mType == TYPE_SELECT_SERVICE) {
                //Hide connected device and disconnect button.
                llConnectTo.setVisibility(View.GONE);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder.setView(view);
        return builder.show();
    }
}
