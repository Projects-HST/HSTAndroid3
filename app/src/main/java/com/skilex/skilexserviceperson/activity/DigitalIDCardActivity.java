package com.skilex.skilexserviceperson.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.loginmodule.OTPVerificationActivity;
import com.skilex.skilexserviceperson.customview.CircleImageView;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class DigitalIDCardActivity extends BaseActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private CircleImageView profilePic;
    private TextView txtExpertName;
    private TextView txtExpertExpertise;
    private TextView txtExpertJoinedDate;
    private TextView txtExpertPhoneNo;
    private TextView txtExpertDOB;
    private TextView txtExpertID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_digital_id);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        profilePic = (CircleImageView) findViewById(R.id.user_profile_img);
        String url = PreferenceStorage.getProfilePicture(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).into(profilePic);
        } else {
            profilePic.setImageResource(R.drawable.ic_profile);
        }

        txtExpertName = findViewById(R.id.expert_name);
        txtExpertExpertise = findViewById(R.id.expert_major);
        txtExpertJoinedDate = findViewById(R.id.expert_join_date);
        txtExpertPhoneNo = findViewById(R.id.expert_phone);
        txtExpertDOB = findViewById(R.id.expert_dob);
        txtExpertID = findViewById(R.id.expert_job_id_no);

        loadSmartCardInfo();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadSmartCardInfo() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.DIGITAL_ID_CARD_API;
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
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE_EN);
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

                JSONObject smartCardData = response.getJSONObject("result");

                String expertExpertiseIn = ""; //userData.getString("id");


                JSONObject getExpServiceData = response.getJSONObject("service_data");
//                if (validateSignInResponse(getExpServiceData)) {
                JSONArray expertExpertiseData = getExpServiceData.getJSONArray("service_list");

                String arrayOfExpertise[] = new String[expertExpertiseData.length()];

                if (expertExpertiseData != null && expertExpertiseData.length() > 0) {
                    for (int i = 0; i < expertExpertiseData.length(); i++) {

                        JSONObject jsonobj = expertExpertiseData.getJSONObject(i);
                        String localVariable = "";
                        localVariable = jsonobj.getString("main_cat_name");

                        arrayOfExpertise[i] = localVariable;
                    }
                }
//                }

                Log.d(TAG, "userData dictionary" + smartCardData.toString());
                //User Data variables

                String expertID = smartCardData.getString("id");
                String expertName = smartCardData.getString("full_name");
                String expertJoinedDate = smartCardData.getString("joining_date");
                String expertPhoneNo = smartCardData.getString("phone_no");
//                String expertExpertiseIn = ""; //userData.getString("id");
                expertExpertiseIn = toCSV(arrayOfExpertise);

                txtExpertName.setText(expertName);
                txtExpertExpertise.setText(expertExpertiseIn); //need
                txtExpertJoinedDate.setText(expertJoinedDate);
                txtExpertPhoneNo.setText(expertPhoneNo);
                txtExpertDOB.setText(expertName); //need
                txtExpertID.setText("ID - SKILEX" + expertID);


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    public static String toCSV(String[] array) {
        String result = "";

        if (array.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (String s : array) {
                sb.append(s).append(",");
            }

            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }


    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onClick(View v) {

    }
}
