package com.elegion.tracktor.ui.results.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.di.ViewModelModule;
import com.elegion.tracktor.ui.results.ResultsDetailsFragment;
import com.elegion.tracktor.ui.results.ResultsViewModel;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentDialogFragment extends DialogFragment {


    private static final String COMMENT = "COMMENT";
    private static final String TRACK_ID = "TRACK_ID";
    private static final String CONTENT = "CONTENT";
    private EditText mComment;

    @Inject
    ResultsViewModel mResultsViewModel;
    private Track mTrack;


    public CommentDialogFragment() {
        // Required empty public constructor
    }

    public static CommentDialogFragment newInstance(String comment) {
        Bundle args = new Bundle();
        args.putString(COMMENT, comment);
        args.putString(CONTENT, "COMMENT");
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CommentDialogFragment newInstance(long trackId) {
        Bundle args = new Bundle();
        args.putLong(TRACK_ID, trackId);
        args.putString(CONTENT, "ID");
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Scope scope = Toothpick.openScope(ResultsDetailsFragment.class).installModules(new ViewModelModule(this));
        Toothpick.inject(this, scope);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.dfr_comment_dialog, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.setTitle(R.string.add_comment)
                .setPositiveButton(R.string.save, (dialog, id) -> onSaveComment())
                .setNegativeButton(R.string.cancel, (dialog, id) -> onCancel())
                .create();
        mComment = view.findViewById(R.id.etComment);
        switch (getArguments().getString(CONTENT)) {
            case "COMMENT":
                mComment.setText(getArguments().getString(COMMENT));
                break;
            case "ID":
                mTrack = mResultsViewModel.getTrack(getArguments().getLong(TRACK_ID));
                mComment.setText(mTrack.getComment());
                break;
            default:
                break;
        }
        return alertDialog;
    }

    void onSaveComment() {
        switch (getArguments().getString(CONTENT)) {
            case "COMMENT":
                mResultsViewModel.saveComment(mComment.getText().toString());
                break;
            case "ID":
                mTrack.setComment(mComment.getText().toString().trim());
                mResultsViewModel.updateTrack(mTrack);
                break;
            default:
                break;
        }
        dismiss();
    }

    void onCancel() {
        this.dismiss();
    }

}
