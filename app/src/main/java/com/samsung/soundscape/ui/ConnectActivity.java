package com.samsung.soundscape.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsung.soundscape.R;
import com.samsung.soundscape.util.ConnectivityManager;

public class ConnectActivity extends AppCompatActivity implements ConnectivityManager.ServiceChangedListener{
    ConnectivityManager mConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);

        mConnectivityManager = ConnectivityManager.getInstance(this);
        mConnectivityManager.addServiceChangedListener(this);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void onStart() {
        super.onStart();

        //Start the service discovery if it is not started before.
        if (!mConnectivityManager.isDiscovering()) {
            mConnectivityManager.startDiscovery();
        }
    }

    public void onStop() {
        super.onStop();

        //Stop discovery when the app goes to background.
        if (mConnectivityManager.isDiscovering()) {
            mConnectivityManager.stopDiscovery();
        }
    }

    @Override
    public void onServiceChanged() {
    }

    void showDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = MyDialogFragment.newInstance(0);
        newFragment.show(ft, "dialog");
    }


    public static class MyDialogFragment extends DialogFragment {
        int mNum;

        /**
         * Create a new instance of MyDialogFragment, providing "num"
         * as an argument.
         */
        static MyDialogFragment newInstance(int num) {
            MyDialogFragment f = new MyDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments().getInt("num");

            // Pick a style based on the num.
            int style = DialogFragment.STYLE_NORMAL, theme = 0;
            switch ((mNum-1)%6) {
                case 1: style = DialogFragment.STYLE_NO_TITLE; break;
                case 2: style = DialogFragment.STYLE_NO_FRAME; break;
                case 3: style = DialogFragment.STYLE_NO_INPUT; break;
                case 4: style = DialogFragment.STYLE_NORMAL; break;
                case 5: style = DialogFragment.STYLE_NORMAL; break;
                case 6: style = DialogFragment.STYLE_NO_TITLE; break;
                case 7: style = DialogFragment.STYLE_NO_FRAME; break;
                case 8: style = DialogFragment.STYLE_NORMAL; break;
            }
            switch ((mNum-1)%6) {
                case 4: theme = android.R.style.Theme_Holo; break;
                case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
                case 6: theme = android.R.style.Theme_Holo_Light; break;
                case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
                case 8: theme = android.R.style.Theme_Holo_Light; break;
            }
            setStyle(style, theme);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.fragment_dialog, container, false);
//            View tv = v.findViewById(R.id.text);
//            ((TextView)tv).setText("Dialog #" + mNum + ": using style "
//                    + getNameForNum(mNum));
//
//            // Watch for button clicks.
//            Button button = (Button)v.findViewById(R.id.show);
//            button.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    // When button is clicked, call up to owning activity.
//                    ((FragmentDialog)getActivity()).showDialog();
//                }
//            });
//
//            return v;
            return null;
        }
    }
}
