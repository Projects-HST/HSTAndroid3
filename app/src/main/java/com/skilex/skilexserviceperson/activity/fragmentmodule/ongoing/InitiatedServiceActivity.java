package com.skilex.skilexserviceperson.activity.fragmentmodule.ongoing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.bean.support.OngoingService;
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

public class InitiatedServiceActivity extends BaseActivity implements OnMapReadyCallback, IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = InitiatedServiceActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    OngoingService ongoingService;
    private TextView cusName, cusPhone, cusAddress;
    private ImageView imgCall;
    private Button btnNext;
    String res = "";
    private MapView mapView;
    private GoogleMap mMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    LatLng livLoc;
    Marker currentLocationMarker;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiated_service_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");

        cusName = findViewById(R.id.txt_customer_name);
        cusPhone = findViewById(R.id.txt_customer_number);
        cusAddress = findViewById(R.id.txt_address);

        imgCall = findViewById(R.id.img_call_button);
        imgCall.setOnClickListener(this);

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

//        livLoc = getIntent().getParcelableExtra("dist");


        mapView = findViewById(R.id.mapview);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        loadServiceDetails();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void loadServiceDetails() {
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
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_INITIATED_SERVICE_DETAIL;
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
                    cusPhone.setText(getServiceData.getString("contact_person_number"));
                    cusAddress.setText(getServiceData.getString("service_address"));

                    String string = getServiceData.getString("service_latlon");
                    String[] parts = string.split(",");
                    String part1 = parts[0]; // 004
                    String part2 = parts[1]; // 034556
                    Double lat = Double.valueOf(part1);
                    Double longi = Double.valueOf(part2);
                    LatLng latLng = new LatLng(lat, longi);
                    showMarker(latLng);

                } else if (res.equalsIgnoreCase("initiateService")) {
                    finish();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void showMarker(@NonNull LatLng latLng) {
//        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        int height = 60;
//        int width = 30;
//        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.commando);
//        Bitmap b = bitmapdraw.getBitmap();
//        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        if (currentLocationMarker == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            mapView.onResume();
        }
//        else {
//            MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());
//        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == imgCall) {

            } else if (v == btnNext) {
                Intent i = new Intent(getApplicationContext(), ServiceProcessActivity.class);
                i.putExtra("serviceObj", ongoingService);
                startActivity(i);
                finish();
            }

        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection available");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
    }
}
