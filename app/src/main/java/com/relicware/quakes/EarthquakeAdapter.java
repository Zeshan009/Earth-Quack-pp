package com.relicware.quakes;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Muhammad on 09-Mar-18.
 */


import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> {
    private ArrayList<Earthquake> mDataset;
    private ArrayList<Integer> mExpandedPositions;
    private ListItemClickListener listItemClickListener;
    private LayoutManager mLayoutManager;

    private interface ViewType {
        Integer COLLAPSED = 0;
        Integer EXPANDED = 1;
    }

    public class EarthquakeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView time, date, magnitude, location, distance, details;
        public CardView magnitudeCard;
        public EarthquakeViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.time);
            date = view.findViewById(R.id.date);

            details = view.findViewById(R.id.details);

            magnitude = view.findViewById(R.id.magnitude);
            magnitudeCard = view.findViewById(R.id.magnitude_card);

            location = view.findViewById(R.id.location);
            distance = view.findViewById(R.id.distance);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if(mExpandedPositions.contains(position)) mExpandedPositions.remove((Integer)position);
            else mExpandedPositions.add(position);

            notifyItemChanged(position);

            if(mLayoutManager != null) ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(position, 0);

            listItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ListItemClickListener listener) {
        this.listItemClickListener = listener;
    }

    public EarthquakeAdapter() {
        mDataset = new ArrayList<>();
        mExpandedPositions = new ArrayList<>();
    }

    public EarthquakeAdapter(ArrayList<Earthquake> dataset) {
        mDataset = dataset;
        mExpandedPositions = new ArrayList<>();
    }

    @Override
    public EarthquakeAdapter.EarthquakeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EarthquakeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.earthquake, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (mExpandedPositions.contains(position)) return ViewType.EXPANDED;
        else return ViewType.COLLAPSED;
    }

    @Override
    public void onBindViewHolder(EarthquakeViewHolder holder, int position) {
        Earthquake earthquake = mDataset.get(position);

        holder.magnitude.setText(earthquake.getMagnitude());
        holder.magnitudeCard.setCardBackgroundColor(earthquake.getColorCode());

        if (earthquake.getDistance() != null)
            holder.distance.setText(earthquake.getDistance());
        else holder.distance.setVisibility(View.GONE);
        holder.location.setText(earthquake.getLocation());

        holder.time.setText(earthquake.getTime());
        holder.date.setText(earthquake.getDate());

        if(getItemViewType(position) == ViewType.EXPANDED) holder.details.setVisibility(View.VISIBLE);
        else holder.details.setVisibility(View.GONE);
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    public LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public void addItem(Earthquake dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public Earthquake getItem(int position) {
        return mDataset.get(position);
    }

    public void clearItems() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public void replaceDataset(ArrayList<Earthquake> earthquakes) {
        mDataset = earthquakes;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Earthquake> earthquakes) {
        mDataset.addAll(earthquakes);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface ListItemClickListener {
        void onItemClick(int position, View v);
    }
}
