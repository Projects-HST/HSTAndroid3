package com.skilex.skilexserviceperson.activity.fragmentmodule.assigned;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.fragmentmodule.cancelled.CancelRequestedServiceActivity;
import com.skilex.skilexserviceperson.bean.support.AssignedService;
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

import static android.util.Log.d;

public class AssignedServiceDetailActivity extends BaseActivity implements IServiceListener, DialogClickListener,
        View.OnClickListener {

    private static final String TAG = AssignedServiceDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    AssignedService assignedService;
    private TextView catName, subCatName, custName, serviceDate, serviceTimeSlot, orderID, custNumber, custAddress,
            estimatedCost, serviceLocation, serviceNumber, serviceExpert;
    private TextView cusName, cusNumber, serviceTime, estimateAmount;
    Button btnCancel, btnInitiate;
    String res = "";
    String expertId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_service_detail);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        assignedService = (AssignedService) getIntent().getSerializableExtra("serviceObj");

        setUpUI();
        loadServiceDetails();
    }

    void setUpUI() {

        catName = findViewById(R.id.category_name);
        catName.setText(assignedService.getServiceCategoryMainName());
        subCatName = findViewById(R.id.sub_category_name);
        subCatName.setText(assignedService.getServiceSubCategoryName());
        serviceDate = findViewById(R.id.service_date);
        serviceDate.setText(assignedService.getServiceOrderDate());
        serviceTimeSlot = findViewById(R.id.service_time_slot);
        serviceTimeSlot.setText(assignedService.getServiceOrderFromTime());
        serviceExpert = findViewById(R.id.service_expert);
        serviceExpert.setText(assignedService.getServiceAssociateName());
        serviceNumber = findViewById(R.id.txt_service_number);
        serviceNumber.setText("Service Number: " + assignedService.getServiceOrderId());
        cusName = findViewById(R.id.txt_customer_name);
        cusNumber = findViewById(R.id.txt_customer_number);
        serviceTime = findViewById(R.id.txt_service_time);
        estimateAmount = findViewById(R.id.txt_estimated_cost);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnInitiate = findViewById(R.id.btnInitiate);
        btnInitiate.setOnClickListener(this);

    }

    void loadServiceDetails() {
        res = "serviceDetail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, assignedService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_ASSIGNED_SERVICE_DETAILS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnCancel) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AssignedServiceDetailActivity.this);
                alertDialogBuilder.setTitle(R.string.cancel);
                alertDialogBuilder.setMessage(R.string.cancel_service_noadvance_alert1);
                alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        cancelOrder();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else if (v == btnInitiate) {
                initiateService();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }

    private void cancelOrder() {
        Intent intent = new Intent(this, CancelRequestedServiceActivity.class);
        intent.putExtra("serviceOrderId", assignedService.getServiceOrderId());
        startActivity(intent);
        finish();
    }

    private void initiateService() {
        res = "initiateService";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, assignedService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_INITIATE_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                if (res.equalsIgnoreCase("serviceDetail")) {
                    JSONArray getData = response.getJSONArray("detail_services_order");
                    Gson gson = new Gson();
                    JSONObject getServiceData = getData.getJSONObject(0);

                    cusName.setText(getServiceData.getString("contact_person_name"));
                    cusNumber.setText(getServiceData.getString("contact_person_number"));
                    serviceTime.setText(getServiceData.getString("from_time"));
                    estimateAmount.setText(getServiceData.getString("service_rate_card"));
                } else if (res.equalsIgnoreCase("initiateService")) {
                    finish();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }
}
