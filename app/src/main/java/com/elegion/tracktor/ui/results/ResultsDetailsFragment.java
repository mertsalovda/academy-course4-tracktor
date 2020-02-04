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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.di.ViewModelModule;
import com.elegion.tracktor.ui.results.dialog.CommentFragment;
import com.elegion.tracktor.util.ScreenshotMaker;
import com.elegion.tracktor.util.StringUtil;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.provider.SharedPreferencesProvider;

import static com.elegion.tracktor.ui.results.ResultsActivity.RESULT_ID;

/**
 * @author Azret Magometov
 */
public class ResultsDetailsFragment extends Fragment {

    @BindView(R.id.tvDate)
    TextView mDateText;
    @BindView(R.id.tvTime)
    TextView mTimeText;
    @BindView(R.id.tvDistance)
    TextView mDistanceText;
    @BindView(R.id.tvAverageSpeed)
    TextView mAverageSpeedText;
    @BindView(R.id.tvCalories)
    TextView mCaloriesText;
    @BindView((R.id.spActionType))
    Spinner mActionType;
    @BindView(R.id.btnAddComment)
    ImageButton mAddComment;
    @BindView((R.id.tvComment))
    TextView mComment;
    @BindView(R.id.ivScreenshot)
    ImageView mScreenshotImage;

    private Bitmap mImage;
    @Inject
    ResultsViewModel mResultsViewModel;
    private long mTrackId;

    public static ResultsDetailsFragment newInstance(long trackId) {
        Bundle bundle = new Bundle();
        bundle.putLong(RESULT_ID, trackId);
        ResultsDetailsFragment fragment = new ResultsDetailsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Scope scope = Toothpick.openScope(ResultsDetailsFragment.class).installModules(new ViewModelModule(this));
        Toothpick.inject(this, scope);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mTrackId = getArguments().getLong(RESULT_ID, 0);

        //temporary
        mResultsViewModel.updateTrack(mTrackId);
        java.text.DateFormat format = DateFormat.getDateFormat(App.getApp());

        mResultsViewModel.getTrack().observe(this, track -> {
            mDateText.setText(format.format(track.getDate()));
            mTimeText.setText(StringUtil.getTimeText(track.getDuration()));
            mDistanceText.setText(track.getDistance().toString() + " м");
            mAverageSpeedText.setText(track.getAverageSpeed() + " км/ч");
            mCaloriesText.setText(track.getCalories() + " ккал");
            mActionType.setSelection(track.getActionType());
            mComment.setText(getComment(track));

            mImage = ScreenshotMaker.fromBase64(track.getImageBase64());
            mScreenshotImage.setImageBitmap(mImage);
        });
        mActionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mResultsViewModel.updateActionTypeForTrack(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getComment(Track track) {
        String result = track.getComment();
        if (TextUtils.isEmpty(result)){
            result = "Введите комментарий";
        }
        return result.trim();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fr_result_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionShare) {
            String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), mImage, "Мой маршрут", null);
            Uri uri = Uri.parse(path);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT,
                    "Время: " + mTimeText.getText()
                            + "\nРасстояние: " + mDistanceText.getText()
                            + "\nСредняя скорость: " + mAverageSpeedText.getText()
                            + "\nПотрачено калорий: " + mCaloriesText.getText()
                            + "\nВид активности: " + mActionType.getSelectedItem()
                            + "\nКомментарий: " + mComment.getText());
            startActivity(Intent.createChooser(intent, "Результаты маршрута"));
            return true;
        } else if (item.getItemId() == R.id.actionDelete) {
            if (mResultsViewModel.deleteItem(mTrackId)) {
                getActivity().onBackPressed();
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnAddComment)
    void addComment() {
        DialogFragment dialog = CommentFragment.newInstance(mComment.getText().toString());
        dialog.show(getChildFragmentManager(), "comment");
    }
}
