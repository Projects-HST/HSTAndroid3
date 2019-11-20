package com.skilex.skilexserviceperson.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServicesList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("services")
    @Expose
    private ArrayList<Services> serviceArrayList = new ArrayList<>();

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
    public ArrayList<Services> getserviceArrayList() {
        return serviceArrayList;
    }

    /**
     * @param serviceArrayList The serviceArrayList
     */
    public void setserviceArrayList(ArrayList<Services> serviceArrayList) {
        this.serviceArrayList = serviceArrayList;
    }
}