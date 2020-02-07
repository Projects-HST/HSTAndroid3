package com.skilex.skilexserviceperson.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.fragment.HomeFragment;
import com.skilex.skilexserviceperson.fragment.ProfileFragment;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.CommonUtils;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class LandingPageActivity extends BaseActivity implements IServiceListener, DialogClickListener {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    int checkPointSearch = 0;
    boolean doubleBackToExitPressedOnce = false;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceStorage.saveActiveStatus(getApplicationContext(), "Live");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        callGetSubCategoryService();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_home:
                                changeFragment(0);
//                                fabView.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_profile:
                                changeFragment(1);
//                                fabView.setVisibility(View.VISIBLE);
                                break;
                        }
                        return true;
                    }
                });

        changeFragment(0);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }

    private void changeFragment(int position) {

        Fragment newFragment = null;

        if (position == 1) {
            checkPointSearch = 1;
            newFragment = new ProfileFragment();
        } else if (position == 0) {
            checkPointSearch = 0;
            newFragment = new HomeFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentContainer, newFragment)
                .commit();
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    public void callGetSubCategoryService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadCart();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void loadCart() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        try {
            jsonObject.put(SkilExConstants.KEY_APP_VERSION, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CHECK_VERSION;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        try {
            String status = response.getString("status");
            if (!status.equalsIgnoreCase("success")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(LandingPageActivity.this);
                alertDialogBuilder.setTitle("Update");
                alertDialogBuilder.setMessage("A new version of SkilEx is available!");
                alertDialogBuilder.setPositiveButton("Get it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            finish();
                        }
                        catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {

    }
}
