package net.woorisys.UltraSetApp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.woorisys.UltraSetApp.R;
import net.woorisys.UltraSetApp.ble.BeaconDomain;

import java.util.ArrayList;

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconViewHolder> {

    public ArrayList<BeaconDomain> beaconArrayList = new ArrayList<BeaconDomain>();

    public BeaconAdapter(ArrayList<BeaconDomain> beacon) {
        this.beaconArrayList = beacon;
    }

    @Override
    public int getItemCount(){
        return beaconArrayList.size();
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_list_item, parent, false);
        return new BeaconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        holder.macAddress.setText("MacAddress : " + beaconArrayList.get(position).getMacAddress());
        holder.serialNumber.setText("serialNumber : "+ beaconArrayList.get(position).getSerialNumber());
    }

    public class BeaconViewHolder extends RecyclerView.ViewHolder {
        TextView macAddress;
        TextView serialNumber;

        public BeaconViewHolder(View itemView) {
            super(itemView);
            macAddress = (TextView) itemView.findViewById(R.id.macAddress);
            serialNumber = (TextView) itemView.findViewById(R.id.serialNumber);
        }
    }
}