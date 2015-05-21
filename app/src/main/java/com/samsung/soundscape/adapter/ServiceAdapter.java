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

package com.samsung.soundscape.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.multiscreen.Service;
import com.samsung.soundscape.R;
import com.samsung.soundscape.util.ConnectivityManager;
import com.samsung.soundscape.util.Util;

public class ServiceAdapter extends ArrayAdapter<Service> {

    private int layoutResourceId;
    private static LayoutInflater inflater = null;
    private Context context;

    public ServiceAdapter(Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
        this.layoutResourceId = resourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean contains(Service service) {
        return (getPosition(service) >= 0);
    }

    /**
     * Replace the existing service with new service.
     * @param service the new service.
     */
    public void replace(Service service) {

        //Get the service position.
        int position = getPosition(service);

        //Check if position is valid.
        if (position >= 0) {

            //Remove the existing service.
            remove(service);

            //Insert the new service at the same position.
            insert(service, position);
        }
    }


    static class ViewHolder {
        public TextView deviceName;
        public ImageView serviceIcon;
        public int position;
    }

	@Override
	public long getItemId(int position) {
		return position;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.deviceName = (TextView) row.findViewById(R.id.serviceText);
            holder.serviceIcon = (ImageView)row.findViewById(R.id.serviceIcon);
            row.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) row.getTag();

        final Service service = getItem(position);
        holder.position = position;
        holder.deviceName.setText(Util.getFriendlyTvName(service.getName()));

        if (ConnectivityManager.getInstance().getServiceType(service) == ConnectivityManager.ServiceType.Speaker) {
            holder.serviceIcon.setImageResource(R.drawable.ic_speaker_gray);
        } else {
            holder.serviceIcon.setImageResource(R.drawable.ic_tv_gray);
        }

        return row;
    }

}
