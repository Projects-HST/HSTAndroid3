package com.skilex.skilexserviceperson.activity.fragmentmodule.ongoing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.adapter.CartServiceDeleteListAdapter;
import com.skilex.skilexserviceperson.adapter.SwipeToDeleteCallback;
import com.skilex.skilexserviceperson.bean.support.AdditionalService;
import com.skilex.skilexserviceperson.bean.support.OngoingService;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.interfaces.DialogClickListener;
import com.skilex.skilexserviceperson.servicehelpers.ServiceHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.CommonUtils;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.util.Log.d;

public class AdditionalServicesAcitivity extends AppCompatActivity implements IServiceListener, DialogClickListener, CartServiceDeleteListAdapter.OnItemClickListener {

    private static final String TAG = AdditionalServicesAcitivity.class.getName();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ArrayList<AdditionalService> serviceArrayList = new ArrayList<>();
    CartServiceDeleteListAdapter serviceListAdapter;
    //    ListView loadMoreListView;
    private RecyclerView mRecyclerView;
    OngoingService ongoingService;

    TextView advanceAmount, totalCost;
    String res = "";
    Button confrm;
    ListView loadMoreListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_services_list);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

//        loadMoreListView = findViewById(R.id.listSumService);
        mRecyclerView = findViewById(R.id.listSumService);
//        advanceAmount = (TextView) findViewById(R.id.additional_cost);
//        totalCost = (TextView) findViewById(R.id.total_cost);
//        confrm = (Button) findViewById(R.id.confirm);
//        confrm.setOnClickListener(this);


//        ser = getIntent().getStringExtra("serviceObj");
        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");

//        loadMoreListView = findViewById(R.id.listSumService);
        callGetSubCategoryService();
    }

    private void showExit() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AdditionalServicesAcitivity.this);
        alertDialogBuilder.setTitle("Additional Services");
        alertDialogBuilder.setMessage("All Services Removed");
        alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
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
                if (res.equalsIgnoreCase("clear")) {
//                    PreferenceStorage.saveServiceCount(this, "");
//                    PreferenceStorage.saveRate(this, "");
//                    PreferenceStorage.savePurchaseStatus(this, false);
//                    Intent i = new Intent(this, SubCategoryActivity.class);
//                    i.putExtra("cat", category);
//                    startActivity(i);
//                    finish();
                } else {
                    JSONArray getData = response.getJSONArray("service_list");
//                    loadMembersList(getData.length());
                    Gson gson = new Gson();
//                    CartServiceList serviceList = gson.fromJson(response.toString(), CartServiceList.class);
//                    if (serviceList.getserviceArrayList() != null && serviceList.getserviceArrayList().size() > 0) {
//                        totalCount = serviceList.getCount();
//                        this.categoryArrayList.addAll(subCategoryList.getCategoryArrayList());
//                        isLoadingForFirstTime = false;
//                        updateListAdapter(serviceList.getserviceArrayList());
//                    } else {
//                        if (serviceArrayList != null) {
//                            serviceArrayList.clear();
//                            updateListAdapter(serviceList.getserviceArrayList());
//                        }
//                    }
                    Type listType = new TypeToken<ArrayList<AdditionalService>>() {
                    }.getType();
                    serviceArrayList = (ArrayList<AdditionalService>) gson.fromJson(getData.toString(), listType);
                    serviceListAdapter = new CartServiceDeleteListAdapter(this, serviceArrayList, AdditionalServicesAcitivity.this);
//                    mRecyclerView.setAdapter(serviceListAdapter);
                    setUpRecyclerView();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

//    protected void updateListAdapter(ArrayList<CartService> serviceArrayList) {
//        this.serviceArrayList.clear();
//        this.serviceArrayList.addAll(serviceArrayList);
//        if (serviceListAdapter == null) {
//            serviceListAdapter = new GeneralServiceListAdapter(this, this.serviceArrayList);
//            loadMoreListView.setAdapter(serviceListAdapter);
//            advanceAmount.setText("" + serviceArrayList.get(0).getAdvance_amount());
//            ArrayList<Integer> a = new ArrayList<>();
//            for (int i = 0; i < serviceArrayList.size(); i++) {
////                a.add(Integer.parseInt(serviceArrayList.get(i).getRate_card()));
//            }
//            int sum = 0;
//            for (Integer d : a) {
//                sum += d;
//            }
//            totalCost.setText("" + sum);
//        } else {
//            serviceListAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.service_person_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_service_person:
                Intent intent = new Intent(getApplicationContext(), AddAdditionalServices.class);
                intent.putExtra("serviceObj", ongoingService);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }


    @Override
    public void onError(String error) {

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
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, ongoingService.getServiceOrderId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_LIST_ADDITIONAL_SERVICES;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void setUpRecyclerView() {
        mRecyclerView.setAdapter(serviceListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(serviceListAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
//        if (serviceArrayList.get(0).getAdvance_amount().isEmpty()) {
//            advanceAmount.setText("₹ 0");
//        } else {
//            advanceAmount.setText("₹ " + serviceArrayList.get(0).getAdvance_amount());
//        }
//        ArrayList<Double> a = new ArrayList<>();
//        for (int i = 0; i < serviceArrayList.size(); i++) {
//            a.add(Double.parseDouble(serviceArrayList.get(i).getRate_card()));
//        }
//        int sum = 0;
//        for (Double d : a) {
//            sum += d;
//        }
//        totalCost.setText("₹ " + sum);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
