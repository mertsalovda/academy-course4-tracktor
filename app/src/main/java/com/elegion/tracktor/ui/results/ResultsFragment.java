package com.elegion.tracktor.ui.results;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.di.ViewModelModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import toothpick.Scope;
import toothpick.Toothpick;

public class ResultsFragment extends Fragment {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private OnItemClickListener mListener;
    @Inject
    ResultsViewModel mResultsViewModel;
    private ResultsAdapter mResultsAdapter;

    public ResultsFragment() {
    }

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toothpick.inject(this, App.getAppScope());
        if (context instanceof OnItemClickListener) {
            mListener = (OnItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scope scope = Toothpick.openScope(ResultsFragment.class).installModules(new ViewModelModule(this));
        Toothpick.inject(this, scope);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mResultsAdapter = new ResultsAdapter(mListener);
        mResultsViewModel.getTracks().observe(this, tracks -> mResultsAdapter.submitList(tracks));
        mResultsViewModel.loadTracks();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mResultsAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnItemClickListener {
        void onClick(long trackId);
    }
}
