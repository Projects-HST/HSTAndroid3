package com.skilex.skilexserviceperson.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AssignedServiceList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("list_services_order")
    @Expose
    private ArrayList<AssignedService> serviceArrayList = new ArrayList<>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The serviceArrayList
     */
    public ArrayList<AssignedService> getServiceArrayList() {
        return serviceArrayList;
    }

    /**
     * @param serviceArrayList The serviceArrayList
     */
    public void setServiceArrayList(ArrayList<AssignedService> serviceArrayList) {
        this.serviceArrayList = serviceArrayList;
    }
}
