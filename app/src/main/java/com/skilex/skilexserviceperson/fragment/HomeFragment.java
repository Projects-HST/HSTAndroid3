package com.skilex.skilexserviceperson.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.fragmentmodule.assigned.AssignedServicesActivity;
import com.skilex.skilexserviceperson.activity.fragmentmodule.completed.CompletedServicesActivity;
import com.skilex.skilexserviceperson.activity.fragmentmodule.ongoing.OnGoingServicesActivity;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;

import org.json.JSONObject;

public class HomeFragment extends Fragment implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = HomeFragment.class.getName();
    Context context;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Handler mHandler = new Handler();
    private View rootView;

    private LinearLayout layoutAssigned, layoutOnGoing, layoutCompleted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Dashboard");
        init();
        return rootView;
    }

    void init() {
        layoutAssigned = rootView.findViewById(R.id.llAssigned);
        layoutAssigned.setOnClickListener(this);
        layoutOnGoing = rootView.findViewById(R.id.llOnGoing);
        layoutOnGoing.setOnClickListener(this);
        layoutCompleted = rootView.findViewById(R.id.ll_service_history);
        layoutCompleted.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == layoutAssigned) {
            Intent intent = new Intent(getActivity(), AssignedServicesActivity.class);
            startActivity(intent);

        } else if (v == layoutOnGoing) {
            Intent intent = new Intent(getActivity(), OnGoingServicesActivity.class);
            startActivity(intent);

        } else if (v == layoutCompleted) {
            Intent intent = new Intent(getActivity(), CompletedServicesActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(String error) {

    }
}
