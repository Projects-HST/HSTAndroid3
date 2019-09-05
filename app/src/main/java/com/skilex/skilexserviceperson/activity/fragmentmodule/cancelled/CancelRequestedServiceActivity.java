package com.skilex.skilexserviceperson.activity.fragmentmodule.cancelled;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.LandingPageActivity;
import com.skilex.skilexserviceperson.bean.support.StoreCancelReasons;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class CancelRequestedServiceActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = CancelRequestedServiceActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    EditText reason, comment;
    Button submit;
//    RequestedServiceArray requestedServiceArray;

    String res = "";

    ArrayAdapter<StoreCancelReasons> storeCancelReasonsArrayAdapter = null;
    ArrayList<StoreCancelReasons> storeCancelReasons;
    String cancelMasterId = "";
    String serviceId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_service);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        initVal();
    }

    private void initVal() {
//        requestedServiceArray = (RequestedServiceArray) getIntent().getSerializableExtra("serviceObj");

        Intent intent = getIntent();
        serviceId = intent.getExtras().getString("serviceOrderId");

        reason = (EditText) findViewById(R.id.edt_user_reason);
        reason.setFocusable(false);
        reason.setOnClickListener(this);
        comment = (EditText) findViewById(R.id.edt_user_comment);
        submit = (Button) findViewById(R.id.submit_reason);
        submit.setOnClickListener(this);
        loadReason();
    }

    private void loadReason() {
        res = "reason";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.KEY_USER_TYPE, "4");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_CANCEL_REASON;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void cancelOrder() {
        res = "cancel";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(this));
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceId);
            jsonObject.put(SkilExConstants.CANCEL_ID, cancelMasterId);
            jsonObject.put(SkilExConstants.CANCEL_COMMENTS, comment.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_CANCEL_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == reason) {
            showCancelReasonList();
        }
        if (v == submit) {
            cancelOrder();
        }
    }

    private void showCancelReasonList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.header);
//        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
//            header.setText("காரணத்தைத் தேர்ந்தெடுக்கவும்");
//        } else {
        header.setText("Select Reason");
//        }
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(storeCancelReasonsArrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreCancelReasons cty = storeCancelReasons.get(which);
                        reason.setText(cty.getCancelReason());
                        cancelMasterId = cty.getCancelMasterId();
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateResponse(JSONObject response) {
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
        if (validateResponse(response)) {
            try {
                if (res.equalsIgnoreCase("reason")) {
                    JSONArray getData = response.getJSONArray("list_reasons");
                    int getLength = getData.length();
                    String cancelMasterId = "";
                    String cancelReason = "";
                    storeCancelReasons = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        cancelMasterId = getData.getJSONObject(i).getString("id");
                        cancelReason = getData.getJSONObject(i).getString("cancel_reason");
                        storeCancelReasons.add(new StoreCancelReasons(cancelMasterId, cancelReason));
                    }

                    storeCancelReasonsArrayAdapter = new ArrayAdapter<StoreCancelReasons>(getApplicationContext(), R.layout.cancel_reason_layout, R.id.txt_cancel_reason, storeCancelReasons) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.cancel_reason_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.txt_cancel_reason);
                            gendername.setText(storeCancelReasons.get(position).getCancelReason());

                            // ... Fill in other views ...
                            return view;
                        }
                    };


                } else if (res.equalsIgnoreCase("cancel")) {
                    Intent intent = new Intent(this, LandingPageActivity.class);
                    startActivity(intent);
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
