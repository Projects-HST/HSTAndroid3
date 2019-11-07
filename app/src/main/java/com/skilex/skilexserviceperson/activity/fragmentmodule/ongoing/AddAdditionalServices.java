package com.skilex.skilexserviceperson.activity.fragmentmodule.ongoing;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.adapter.MainServiceListAdapter;
import com.skilex.skilexserviceperson.bean.support.SubCategory;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddAdditionalServices extends BaseActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = AddAdditionalServices.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    ArrayList<SubCategory> categoryArrayList;
    MainServiceListAdapter categoryListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_additional_services);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
