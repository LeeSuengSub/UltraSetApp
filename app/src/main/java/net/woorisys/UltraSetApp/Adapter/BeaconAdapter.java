package net.woorisys.UltraSetApp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.woorisys.UltraSetApp.R;
import net.woorisys.UltraSetApp.ble.BeaconDomain;

import java.util.ArrayList;

public class BeaconAdapter extends BaseAdapter {
    private ArrayList<BeaconDomain> beaconArrayList;
    private LayoutInflater inflater;

    public BeaconAdapter(Context context, ArrayList<BeaconDomain> beacon) {
        this.beaconArrayList = beacon;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return beaconArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return beaconArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.scan_list_item, parent, false);
        }

        TextView macAddress = convertView.findViewById(R.id.macAddress);
        TextView serialNumber = convertView.findViewById(R.id.serialNumber);

        macAddress.setText("MacAddress : " + beaconArrayList.get(position).getMacAddress());
        serialNumber.setText("serialNumber : " + beaconArrayList.get(position).getSerialNumber());

        return convertView;
    }
}
