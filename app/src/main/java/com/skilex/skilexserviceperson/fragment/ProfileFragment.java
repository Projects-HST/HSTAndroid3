package com.skilex.skilexserviceperson.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skilex.skilexserviceperson.activity.ProfileActivity;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.activity.AboutUsActivity;
import com.skilex.skilexserviceperson.activity.loginmodule.SplashScreenActivity;
import com.skilex.skilexserviceperson.customview.CircleImageView;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.languagesupport.LocaleManager;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = ProfileFragment.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private View rootView;
    private CircleImageView profileImage;
    private LinearLayout profile, about, share, logout;
    TextView userName, number, mail;

    public static ProfileFragment newInstance(int position) {
        ProfileFragment frag = new ProfileFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        getActivity().setTitle("Profile");

        serviceHelper = new ServiceHelper(rootView.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(rootView.getContext());

        profileImage = rootView.findViewById(R.id.user_profile_img);

        profile = rootView.findViewById(R.id.layout_profile);
        profile.setOnClickListener(this);
        about = rootView.findViewById(R.id.layout_about);
        about.setOnClickListener(this);
        share = rootView.findViewById(R.id.layout_share);
        share.setOnClickListener(this);

        logout = rootView.findViewById(R.id.layout_logout);
        logout.setOnClickListener(this);

        userName = rootView.findViewById(R.id.user_name);
        number = rootView.findViewById(R.id.user_phone_number);
        mail = rootView.findViewById(R.id.user_mail);

        getUserInfo();

        return rootView;
    }

    private void getUserInfo() {
        String id = "";
        if (!PreferenceStorage.getUserMasterId(rootView.getContext()).isEmpty()) {
            id = PreferenceStorage.getUserMasterId(rootView.getContext());
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.PROFILE_INFO;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.change_language, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_lang:
                showLangAlert();
                break;
        }
        return true;
    }

    private void showLangAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Language");
        alertDialogBuilder.setMessage("Choose your prefered language");
        alertDialogBuilder.setPositiveButton("English", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                LocaleManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_KEY_ENGLISH);
                getActivity().recreate();
            }
        });
        alertDialogBuilder.setNegativeButton("தமிழ்", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocaleManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_KEY_TAMIL);
                getActivity().recreate();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void onClick(View v) {
        if (v == profile) {

            Intent homeIntent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(homeIntent);

        }
        if (v == about) {
            Intent homeIntent = new Intent(getActivity(), AboutUsActivity.class);
            startActivity(homeIntent);
        }
        if (v == share) {
            Intent i = new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
            i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey! Get SkilEx app. https://bit.ly/2JgIyom");
            startActivity(Intent.createChooser(i, "Share via"));

        }
        if (v == logout) {
            doLogout();
        }
    }


    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    public void doLogout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().clear().apply();
//        TwitterUtil.getInstance().resetTwitterRequestToken();
        Intent homeIntent = new Intent(getActivity(), SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        getActivity().finish();
    }


    private boolean validateResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(rootView.getContext(), msg);

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
                JSONObject data = response.getJSONObject("user_details");
                String url = data.getString("profile_pic");
                if (!url.isEmpty()) {
                    Picasso.get().load(url).into(profileImage);
                }
                userName.setText(data.getString("full_name"));
                number.setText(data.getString("phone_no"));
                mail.setText(data.getString("email"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(rootView.getContext(), error);
    }
}
