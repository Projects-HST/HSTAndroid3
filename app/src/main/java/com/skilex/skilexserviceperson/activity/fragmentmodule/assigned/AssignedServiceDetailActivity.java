package com.skilex.skilexserviceperson.activity.fragmentmodule.assigned;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.fragmentmodule.cancelled.CancelRequestedServiceActivity;
import com.skilex.skilexserviceperson.bean.support.AssignedService;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    AlarmManager am;
    PendingIntent pi;

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

        if (!checkPhoneModel()) {
            if (PreferenceStorage.getLocationCheck(this)) {
                PreferenceStorage.saveLocationCheck(this, false);
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AssignedServiceDetailActivity.this);
                alertDialogBuilder.setTitle("Auto Start");
                alertDialogBuilder.setMessage("Enable auto start for the app to function properly");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        addAutoStartup();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();

            }
        }

    }

    private boolean checkPhoneModel() {

        String manufacturer = android.os.Build.MANUFACTURER;

        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("Letv".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("Honor".equalsIgnoreCase(manufacturer)) {
            return false;
        } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
            return false;
        } else {
            return true;
        }
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            } else if ("oneplus".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListAct‌​ivity"));
            }
//            intent.setComponent(new ComponentName("com.samsung.android.lool",
//                    "com.samsung.android.sm.ui.battery.BatteryActivity"));
//                    new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT);
//                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                    intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
//                    intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
//                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"));
//                    intent.setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"));
//                    intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
//                    intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"));
//                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
//                    intent.setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(
//                            Uri.parse("mobilemanager://function/entry/AutoStart"));

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
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
                PreferenceStorage.saveServiceOrderId(getApplicationContext(), assignedService.getServiceOrderId());


                try {
                    String alarm = Context.ALARM_SERVICE;
                    am = (AlarmManager) getSystemService(alarm);

                    Intent intent = new Intent("REFRESH_THIS");
                    pi = PendingIntent.getBroadcast(this, 123456789, intent, 0);

                    int type = AlarmManager.RTC_WAKEUP;
                    long interval = 50 * 50;

                    am.setInexactRepeating(type, System.currentTimeMillis(), interval, pi);
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                startService(new Intent(AssignedServiceDetailActivity.this, GoogleLocationService.class));

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
                    Toast.makeText(this, "Service Initiated!", Toast.LENGTH_SHORT).show();
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
