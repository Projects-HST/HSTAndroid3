package com.skilex.skilexserviceperson.activity.loginmodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.LandingPageActivity;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExValidator;
import com.skilex.skilexserviceperson.utils.SmsVerification;

import java.util.ArrayList;

public class SplashScreenActivity extends BaseActivity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_display);

        String GCMKey = PreferenceStorage.getGCM(getApplicationContext());
        if (GCMKey.equalsIgnoreCase("")) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            PreferenceStorage.saveGCM(getApplicationContext(), refreshedToken);
        }

        ArrayList<String> appCodes = new ArrayList<>();
        SmsVerification hash = new SmsVerification(getBaseContext());
        appCodes = hash.getAppSignatures();
        String yourhash = appCodes.get(0);
        Log.d("Hash Key: ", yourhash);
        System.out.println("Hash Key: " + yourhash);

//        Toast.makeText(SplashScreenActivity.this, "Hash key...  " + yourhash, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PreferenceStorage.getUserMasterId(getApplicationContext()) != null && SkilExValidator.checkNullString(PreferenceStorage.getUserMasterId(getApplicationContext()))) {
                    if (PreferenceStorage.getActiveStatus(getApplicationContext()).equalsIgnoreCase("Live")) {

                        Intent i = new Intent(SplashScreenActivity.this, LandingPageActivity.class);
//                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    } else {
                        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
//                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                } else {

                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}

