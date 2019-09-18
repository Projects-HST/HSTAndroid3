package com.skilex.skilexserviceperson.activity.fragmentmodule.ongoing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.fragmentmodule.assigned.AssignedServiceDetailActivity;
import com.skilex.skilexserviceperson.activity.fragmentmodule.assigned.AssignedServicesActivity;
import com.skilex.skilexserviceperson.adapter.AssignedServiceListAdapter;
import com.skilex.skilexserviceperson.adapter.OngoingServiceListAdapter;
import com.skilex.skilexserviceperson.bean.support.AssignedService;
import com.skilex.skilexserviceperson.bean.support.AssignedServiceList;
import com.skilex.skilexserviceperson.bean.support.OngoingService;
import com.skilex.skilexserviceperson.bean.support.OngoingServiceList;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.CommonUtils;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class OnGoingServicesActivity extends BaseActivity implements IServiceListener, DialogClickListener,
        AdapterView.OnItemClickListener {

    private static final String TAG = OnGoingServicesActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<OngoingService> ongoingServiceArrayList = new ArrayList<>();
    private ListView loadMoreListView;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    OngoingServiceListAdapter ongoingServiceListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_service);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        loadMoreListView = findViewById(R.id.ongoing_service_list);
        loadMoreListView.setOnItemClickListener(this);

        callAssignService();
    }

    public void callAssignService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadReqService();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private void loadReqService() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_ONGOING_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        OngoingService service = null;
        if ((ongoingServiceListAdapter != null) && (ongoingServiceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = ongoingServiceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = ongoingServiceArrayList.get(actualindex);
        } else {
            service = ongoingServiceArrayList.get(position);
        }

        String checkServiceStatus = service.getServiceOrderStatus();

        if (checkServiceStatus.equalsIgnoreCase("Initiated")) {
            Intent intent = new Intent(this, InitiatedServiceActivity.class);
//            Intent intent = new Intent(this, LocationNavigationActivity.class);
            intent.putExtra("serviceObj", service);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, OngoingServiceDetailActivity.class);
            intent.putExtra("serviceObj", service);
            startActivity(intent);
        }
        /*Intent intent = new Intent(this, OngoingServiceDetailActivity.class);
        intent.putExtra("serviceObj", service);
        startActivity(intent);*/
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONArray getData = response.getJSONArray("list_services_order");
//                    loadMembersList(getData.length());
                Gson gson = new Gson();
                OngoingServiceList ongoingServiceList = gson.fromJson(response.toString(), OngoingServiceList.class);
                if (ongoingServiceList.getServiceArrayList() != null && ongoingServiceList.getServiceArrayList().size() > 0) {
                    totalCount = ongoingServiceList.getCount();
//                    this.ongoingServiceArrayList.addAll(ongoingServiceList.getserviceArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(ongoingServiceList.getServiceArrayList());
                } else {
                    if (ongoingServiceArrayList != null) {
                        ongoingServiceArrayList.clear();
                        updateListAdapter(ongoingServiceList.getServiceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<OngoingService> ongoingServiceArrayLists) {
        ongoingServiceArrayList.clear();
        ongoingServiceArrayList.addAll(ongoingServiceArrayLists);
        if (ongoingServiceListAdapter == null) {
            ongoingServiceListAdapter = new OngoingServiceListAdapter(this, ongoingServiceArrayList);
            loadMoreListView.setAdapter(ongoingServiceListAdapter);
        } else {
            ongoingServiceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
