package com.elegion.tracktor.ui.results;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.event.ClickOnHolderEvent;
import com.elegion.tracktor.event.DeleteEvent;
import com.elegion.tracktor.event.EditCommentEvent;
import com.elegion.tracktor.event.ShareEvent;
import com.elegion.tracktor.util.DistanceConverter;
import com.elegion.tracktor.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Azret  Magometov
 */
public class ResultHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_date)
    TextView mDateText;
    @BindView(R.id.tv_distance)
    TextView mDistanceText;
    @BindView(R.id.tv_time)
    TextView mTimeText;
    @BindView(R.id.btn_extension)
    ImageButton mExceptionBtn;

    @BindView(R.id.view_extension)
    View mExtensionView;
    @BindView(R.id.tv_speed)
    TextView mSpeedText;
    @BindView(R.id.tv_calories)
    TextView mCaloriesText;
    @BindView(R.id.tv_action_type)
    TextView mActionTypeText;
    @BindView(R.id.tv_comment)
    TextView mCommentText;
    @BindView(R.id.btn_edit_comment)
    ImageButton mEditCommentBtn;
    @BindView(R.id.btn_share)
    ImageButton mShareBtn;
    @BindView(R.id.btn_delete_track)
    ImageButton mDeleteBtn;

    private long mTrackId;
    private View mView;

    public ResultHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        mView = view;
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mDistanceText.getText() + "'";
    }

    public void bind(Track track, int unit) {
        String[] actionTypes = App.getApp().getResources().getStringArray(R.array.action_type);
        mTrackId = track.getId();
        mDateText.setText(DateFormat.getDateFormat(App.getApp()).format(track.getDate()));
        mTimeText.setText("Время затрачено: " + StringUtil.getTimeText(track.getDuration()));
        mDistanceText.setText("Пройдено: " + DistanceConverter.formatDistance(track.getDistance(), unit));

        mSpeedText.setText("Средняя скорость: " + track.getAverageSpeed() + " км/ч");
        mCaloriesText.setText("Потрачено каллорий: " + track.getCalories() + " ккал");
        mActionTypeText.setText("Вид активности: " + actionTypes[track.getActionType()]);
        mCommentText.setText("Комментарий: " + track.getComment());
    }

    @OnClick
    void onClick() {
        EventBus.getDefault().post(new ClickOnHolderEvent(mTrackId));
    }

    @OnClick(R.id.btn_extension)
    void onClickExtension(ImageButton button) {
        switch (mExtensionView.getVisibility()) {
            case View.VISIBLE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    button.setImageDrawable(App.getApp().getDrawable(R.drawable.ic_arrow_drop_down_50dp));
                }
                mExtensionView.setVisibility(View.GONE);
                break;
            case View.GONE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    button.setImageDrawable(App.getApp().getDrawable(R.drawable.ic_arrow_drop_up_50dp));
                }
                mExtensionView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }

    @OnClick(R.id.btn_edit_comment)
    void onClickEditComment() {
        EventBus.getDefault().post(new EditCommentEvent(mTrackId));
    }

    @OnClick(R.id.btn_share)
    void onClickShare() {
        EventBus.getDefault().post(new ShareEvent(mTrackId));
    }

    @OnClick(R.id.btn_delete_track)
    void onClickDelete() {
        EventBus.getDefault().post(new DeleteEvent(mTrackId));
    }
}
