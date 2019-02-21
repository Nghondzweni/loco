package com.project.loco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {

    private final LayoutInflater mInflater;
    private List<LocoLocation> locoLocations; // Cached copy of words

    LocationsAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new LocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        if (locoLocations != null) {
            LocoLocation current = locoLocations.get(position);
            holder.wordItemView.setText(current.getLocationName());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No LocoLocation");
        }
    }

    void setWords(List<LocoLocation> locoLocations){
        this.locoLocations = locoLocations;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (locoLocations != null)
            return locoLocations.size();
        else return 0;
    }


    class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private LocationViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
        }
    }
}
