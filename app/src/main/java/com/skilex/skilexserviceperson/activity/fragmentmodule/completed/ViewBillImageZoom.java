package com.skilex.skilexserviceperson.activity.fragmentmodule.completed;

import android.os.Bundle;

import com.skilex.skilexserviceperson.R;
import com.skilex.skilexserviceperson.languagesupport.BaseActivity;
import com.skilex.skilexserviceperson.utils.SkilExValidator;
import com.squareup.picasso.Picasso;

public class ViewBillImageZoom extends BaseActivity {

//    private TouchImageView billImg;
//    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill_zoom);
//        billImg = findViewById(R.id.bill);
//        url = (String) getIntent().getSerializableExtra("eventObj");
//        if (SkilExValidator.checkNullString(url)) {
//            Picasso.get().load(url).into(billImg);
//        } else {
////            holder.mImageView.setImageResource(R.drawable.ic_user_profile_image);
//        }
    }


}
