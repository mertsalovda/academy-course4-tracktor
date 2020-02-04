package com.elegion.tracktor.ui.results.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.elegion.tracktor.App;
import com.elegion.tracktor.R;
import com.elegion.tracktor.di.ViewModelModule;
import com.elegion.tracktor.ui.results.ResultsDetailsFragment;
import com.elegion.tracktor.ui.results.ResultsViewModel;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends DialogFragment {


    private static final String COMMENT = "COMMENT";
    EditText mComment;

    @Inject
    ResultsViewModel mResultsViewModel;


    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment newInstance(String comment) {
        Bundle args = new Bundle();
        args.putString(COMMENT, comment);
        CommentFragment fragment = new CommentFragment();
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
        mComment.setText(getArguments().getString(COMMENT));
        return alertDialog;
    }

    void onSaveComment() {
        mResultsViewModel.saveComment(mComment.getText().toString());
        dismiss();
    }

    void onCancel() {
        this.dismiss();
    }

}
