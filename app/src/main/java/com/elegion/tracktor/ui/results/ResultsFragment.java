package com.elegion.tracktor.ui.results;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.di.ViewModelModule;
import com.elegion.tracktor.event.DeleteEvent;
import com.elegion.tracktor.event.EditCommentEvent;
import com.elegion.tracktor.event.ShareEvent;
import com.elegion.tracktor.ui.results.dialog.CommentDialogFragment;
import com.elegion.tracktor.util.ScreenshotMaker;
import com.elegion.tracktor.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import toothpick.Scope;
import toothpick.Toothpick;

public class ResultsFragment extends Fragment {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.stubView)
    View mStubView;

    @Inject
    ResultsViewModel mResultsViewModel;
    private ResultsAdapterRealm mResultsAdapterRealm;

    private static int askDesk = 0;
    private static int dateDurationDistance = 0;

    public ResultsFragment() {
    }

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toothpick.inject(this, App.getAppScope());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scope scope = Toothpick.openScope(ResultsFragment.class).installModules(new ViewModelModule(this));
        Toothpick.inject(this, scope);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        OrderedRealmCollection<Track> trackList = mResultsViewModel.getTracksRealm().getValue();
        // !!!!!!!!!!
        // Раскоментировать, чтобы заполнить тестовыми данными\
//        if (trackList.isEmpty()) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_map);
//            mResultsViewModel.setTestDataToRepository(3, ScreenshotMaker.toBase64(bitmap));
//        }
        // !!!!!!!!!!
        mResultsAdapterRealm = new ResultsAdapterRealm(trackList, true, true);
        mResultsViewModel.getTracksRealm().observe(this, tracks -> {
            if (tracks.isEmpty()) {
                mStubView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mStubView.setVisibility(View.GONE);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mResultsAdapterRealm);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort_results, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_order:
                if (askDesk == 0) {
                    askDesk++;
                    item.setTitle(R.string.order_desk);
                } else {
                    askDesk--;
                    item.setTitle(R.string.order_ask);
                }
                sort(askDesk, dateDurationDistance);
                break;
            case R.id.sort_params:
                if (dateDurationDistance == 0) {
                    dateDurationDistance++;
                    item.setTitle(R.string.sort_duration);
                } else if (dateDurationDistance == 1) {
                    dateDurationDistance++;
                    item.setTitle(R.string.sort_distance);
                } else {
                    dateDurationDistance = 0;
                    item.setTitle(R.string.sort_date);
                }
                sort(askDesk, dateDurationDistance);
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void sort(int order, int params) {
        RealmResults<Track> sort = mResultsViewModel.sort(order, params);
        mResultsAdapterRealm.updateData(sort);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void editCommentTrack(EditCommentEvent event) {
        DialogFragment dialog = CommentDialogFragment.newInstance(event.getTrackId());
        dialog.show(getChildFragmentManager(), "id");
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void shareTrackResults(ShareEvent event) {
        Track track = mResultsViewModel.getTrack(event.getTrack());
        String[] actions = getResources().getStringArray(R.array.action_type);
        Bitmap image = ScreenshotMaker.fromBase64(track.getImageBase64());
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), image, "Мой маршрут", null);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, StringUtil.getTextForShare(track, actions));
        startActivity(Intent.createChooser(intent, "Результаты маршрута"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void deleteTreckById(DeleteEvent event) {
        mResultsViewModel.deleteItem(event.getTrackId());
        mResultsViewModel.updateTracks();
    }

}
