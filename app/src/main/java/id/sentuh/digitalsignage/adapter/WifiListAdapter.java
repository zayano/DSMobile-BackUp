package id.sentuh.digitalsignage.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import id.sentuh.digitalsignage.R;
import id.sentuh.digitalsignage.models.WifiList;

/**
 * Created by sony on 3/23/2018.
 */

public class WifiListAdapter extends ArrayAdapter {
    List<WifiList> items;
    Context mContext;
    int Layout;
    public WifiListAdapter(@NonNull Context context, int resource, @NonNull List<WifiList> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.mContext = context;
        this.Layout = resource;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public WifiList getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(Layout,parent,false);
        TextView name = convertView.findViewById(R.id.wifi_name);
        WifiList item = items.get(position);
        if(item!=null){
            name.setText(item.ssid_name);
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(Layout,parent,false);
        TextView name = convertView.findViewById(R.id.wifi_name);
        WifiList item = items.get(position);
        if(item!=null){
            name.setText(item.ssid_name);
        }
        return convertView;
    }
}
