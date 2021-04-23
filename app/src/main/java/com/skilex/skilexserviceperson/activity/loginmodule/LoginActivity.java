package com.skilex.skilexserviceperson.activity.loginmodule;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.credentials.HintRequest;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.languagesupport.LocaleManager;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.CommonUtils;
import com.skilex.skilexserviceperson.utils.FirstTimePreference;
import com.skilex.skilexserviceperson.utils.PermissionUtil;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;
import com.skilex.skilexserviceperson.utils.SkilExValidator;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class LoginActivity extends BaseActivity implements DialogClickListener, IServiceListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    Context context;
    private EditText edtMobileNo;
    private Button btnLogin;
    String res = "";
    ImageView lang;

    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSION_All = 111;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private static final int CREDENTIAL_PICKER_REQUEST = 1;  // Set to an unused request code

    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtMobileNo = findViewById(R.id.edtMobileNumber);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        lang = findViewById(R.id.langues);
        lang.setOnClickListener(this);

        FirstTimePreference prefFirstTime = new FirstTimePreference(getApplicationContext());

        if (prefFirstTime.runTheFirstTime("FirstTimePermit")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestAllPermissions();
            }
        }

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
            if (!isIgnoringBatteryOptimizations) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST);

                /*intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);*/
            }
        }

//        if (LocaleManager.getLanguagePref(this).isEmpty()) {
//            showLangAlert();
//        }
    }


    /*// Construct a request for phone numbers and show the picker
    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }*/


    private void requestAllPermissions() {

        boolean requestPermission = PermissionUtil.requestAllPermissions(this);

        if (requestPermission) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

            ActivityCompat
                    .requestPermissions(this, PERMISSIONS_ALL,
                            REQUEST_PERMISSION_All);
        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, REQUEST_PERMISSION_All);
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
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == btnLogin) {
                if (validateFields()) {
                    res = "mob_verify";
                    String number = edtMobileNo.getText().toString();
                    PreferenceStorage.saveMobileNo(this, number);
                    String GCMKey = PreferenceStorage.getGCM(getApplicationContext());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.PHONE_NUMBER, number);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.MOBILE_VERIFICATION;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            } else if (v == lang) {
                showLangAlert();
            } else {
                AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
            }
        }
    }

    private boolean validateFields() {
        if (!SkilExValidator.checkMobileNumLength(this.edtMobileNo.getText().toString().trim())) {
            edtMobileNo.setError(getString(R.string.error_number));
            requestFocus(edtMobileNo);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.edtMobileNo.getText().toString().trim())) {
            edtMobileNo.setError(getString(R.string.empty_entry));
            requestFocus(edtMobileNo);
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
                String saveUserMasterId = response.getString("user_master_id");
                PreferenceStorage.saveUserMasterId(this, saveUserMasterId);
                PreferenceStorage.saveMobileNo(this, edtMobileNo.getText().toString());
                PreferenceStorage.saveLoginType(getApplicationContext(), "Login");

                Intent i = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                startActivity(i);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void showLangAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Language");
        alertDialogBuilder.setMessage("Choose your prefered language");
        alertDialogBuilder.setPositiveButton("English", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                LocaleManager.setNewLocale(getApplicationContext(), LocaleManager.LANGUAGE_KEY_ENGLISH);
                recreate();
            }
        });
        alertDialogBuilder.setNegativeButton("தமிழ்", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocaleManager.setNewLocale(getApplicationContext(), LocaleManager.LANGUAGE_KEY_TAMIL);
                recreate();
            }
        });
        alertDialogBuilder.show();
    }
}
