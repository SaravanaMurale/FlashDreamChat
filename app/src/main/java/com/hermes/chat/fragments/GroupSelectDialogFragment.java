package com.hermes.chat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hermes.chat.R;
import com.hermes.chat.adapters.GroupsAdapter;
import com.hermes.chat.interfaces.UserGroupSelectionDismissListener;
import com.hermes.chat.models.Group;
import com.hermes.chat.utils.Helper;
import com.hermes.chat.utils.SharedPreferenceHelper;
import com.hermes.chat.views.MyRecyclerView;

import java.util.ArrayList;

/**
 * Created by a_man on 31-12-2017.
 */

public class GroupSelectDialogFragment extends BaseFullDialogFragment {
    private TextView heading;
    private EditText query;
    private MyRecyclerView recyclerView;
    private ArrayList<Group> myGroups;

    public GroupSelectDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_select, container);
        heading = view.findViewById(R.id.heading);
        heading.setText("Groups");
        query = view.findViewById(R.id.searchQuery);
        query.setHint("Send to:");

        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setEmptyView(view.findViewById(R.id.emptyView));
        recyclerView.setEmptyImageView(((ImageView) view.findViewById(R.id.emptyImage)));
        TextView emptyTextView = view.findViewById(R.id.emptyText);
        emptyTextView.setText(getString(R.string.no_groups));
        recyclerView.setEmptyTextView(emptyTextView);

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.createGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(getContext());
                sharedPreferenceHelper.setBooleanPreference(Helper.GROUP_CREATE, true);
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new GroupsAdapter(getActivity(), myGroups));
    }

    public static GroupSelectDialogFragment newInstance(Context context, ArrayList<Group> myGroups) {
        GroupSelectDialogFragment dialogFragment = new GroupSelectDialogFragment();
        dialogFragment.myGroups = myGroups;
        if (context instanceof UserGroupSelectionDismissListener) {
            dialogFragment.dismissListener = (UserGroupSelectionDismissListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement UserGroupSelectionDismissListener");
        }
        return dialogFragment;
    }

}
