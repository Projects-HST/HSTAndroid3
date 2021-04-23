package com.skilex.skilexserviceperson.activity.fragmentmodule.ongoing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.LandingPageActivity;
import com.skilex.skilexserviceperson.bean.database.SQLiteHelper;
import com.skilex.skilexserviceperson.bean.support.OngoingService;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.servicehelpers.GoogleLocationService;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.CommonUtils;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;
import com.skilex.skilexserviceperson.utils.SkilExValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ServiceProcessActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = OngoingServiceDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    OngoingService ongoingService;

    private TextView txtServiceCategory, txtSubCategory, txtCustomerName, txtServiceDate, txtServiceTime, txtServiceProvider;
    private EditText edtOTP;
    private TextView txtRequestOTP, tvCountDown;
    private Button btnStartService;
    String res = "";

    SQLiteHelper database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_process);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");
        database = new SQLiteHelper(getApplicationContext());

        init();
        loadServiceDetail();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void init() {
        txtServiceCategory = findViewById(R.id.txt_service_cat);
        txtSubCategory = findViewById(R.id.txt_service_sub_cat);
        txtCustomerName = findViewById(R.id.txt_customer_name);
        txtServiceDate = findViewById(R.id.txt_service_date);
        txtServiceTime = findViewById(R.id.txt_service_time);
        txtServiceProvider = findViewById(R.id.txt_service_provider);
        edtOTP = findViewById(R.id.edt_otp);
        txtRequestOTP = findViewById(R.id.txt_request_otp);
        txtRequestOTP.setOnClickListener(this);
        tvCountDown = findViewById(R.id.contentresend);
        btnStartService = findViewById(R.id.btn_start);
        btnStartService.setOnClickListener(this);
    }

    private void loadServiceDetail() {
        res = "serviceDetail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_SERVICE_PROCESS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void requestOTP() {
        tvCountDown.setVisibility(View.VISIBLE);
        edtOTP.setEnabled(true);
        res = "opt";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserMasterId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_REQUEST_OTP;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    void countDownTimers() {
        new CountDownTimer(30 * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtRequestOTP.setVisibility(View.GONE);
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvCountDown.setText("Resend in " + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds) + " seconds");
            }

            public void onFinish() {
                tvCountDown.setText("Try again...");
                tvCountDown.setVisibility(View.GONE);
                txtRequestOTP.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void serviceStart() {
        res = "start";
        JSONObject jsonObject = new JSONObject();
        String id = "", otp = "";
        id = PreferenceStorage.getUserMasterId(this);
        otp = edtOTP.getText().toString();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());
            jsonObject.put(SkilExConstants.SERVICE_OTP, otp);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_START_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {

        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == txtRequestOTP) {
                requestOTP();
            } else if (v == btnStartService) {
                if (validateFields()) {
                    serviceStart();
                }
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }

    }

    private boolean validateFields() {
        if (!SkilExValidator.checkNullString(this.edtOTP.getText().toString().trim())) {
            edtOTP.setError(getString(R.string.otp_invalid));
            requestFocus(edtOTP);
            return false;
        } else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
        return true;
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

                    txtServiceCategory.setText(getServiceData.getString("main_cat_name"));
                    txtSubCategory.setText(getServiceData.getString("sub_cat_name"));
                    txtCustomerName.setText(getServiceData.getString("contact_person_name"));
                    txtServiceDate.setText(getServiceData.getString("order_date"));
                    txtServiceTime.setText(getServiceData.getString("from_time"));
                    txtServiceProvider.setText(getServiceData.getString("service_provider"));
                } else if (res.equalsIgnoreCase("opt")) {
                    countDownTimers();
                    Toast.makeText(getApplicationContext(), "OTP has been sent to your customer number", Toast.LENGTH_LONG).show();

                } else if (res.equalsIgnoreCase("start")) {

                    database.deleteAllCurrentBestLocation();
                    database.deleteAllPreviousBestLocation();
                    database.deleteAllStoredLocationData();


//        deleteTableRecords.deleteAllRecords();
                    stopService(new Intent(ServiceProcessActivity.this, GoogleLocationService.class));
//        stopService(new Intent(MainActivity.this, GPSTracker.class));


//                    am.cancel(pi);


                    Toast.makeText(getApplicationContext(), "Service has been started!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LandingPageActivity.class);
                    startActivity(i);
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
