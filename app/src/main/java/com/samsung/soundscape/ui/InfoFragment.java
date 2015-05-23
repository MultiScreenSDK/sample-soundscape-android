package com.samsung.soundscape.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.samsung.soundscape.R;
import com.samsung.soundscape.util.Util;

import java.io.IOException;
import java.io.InputStream;

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

        WebView wv = (WebView) view.findViewById(R.id.infoText);
        String data = "";
        AssetManager assetManager = getActivity().getResources().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("info.html");
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            data = new String(b).replace("%%SSID%%", Util.getWifiName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }

        wv.loadData(data, "text/html", "utf-8");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder.setView(view);

        // Allow dismiss by clicking outside the dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        // Set window size
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = Math.round((float) Util.getDisplayHeight(getActivity()) * 75 / 100);
        dialog.getWindow().setAttributes(lp);

        return dialog;
    }
}
