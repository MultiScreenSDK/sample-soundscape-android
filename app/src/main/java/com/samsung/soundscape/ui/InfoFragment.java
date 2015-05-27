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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;

import com.samsung.soundscape.R;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

import java.io.IOException;
import java.io.InputStream;

public class InfoFragment extends DialogFragment {
    /**
     * Create a new instance of InfoFragment, providing dialog type
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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.information, null);

        Button retry = (Button)view.findViewById(R.id.retry_button);
        retry.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ConnectivityManager.getInstance().restartDiscovery();
                getDialog().dismiss();
            }
        });


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
