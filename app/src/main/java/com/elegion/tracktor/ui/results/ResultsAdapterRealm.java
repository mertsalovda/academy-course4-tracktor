package com.elegion.tracktor.ui.results;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ResultsAdapterRealm extends RealmRecyclerViewAdapter<Track, ResultHolder> {


    public ResultsAdapterRealm(@Nullable OrderedRealmCollection<Track> data, boolean autoUpdate, boolean updateOnModification) {
        super(data, autoUpdate, updateOnModification);
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.li_track, parent, false);
        return new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        holder.bind(getData().get(position));
    }

    @Override
    public void updateData(@Nullable OrderedRealmCollection<Track> data) {
        super.updateData(data);
    }
}
