package com.skilex.skilexserviceperson.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssignedService implements Serializable {

    @SerializedName("id")
    @Expose
    private String service_order_id;

    @SerializedName("service_location")
    @Expose
    private String service_location;

    @SerializedName("order_date")
    @Expose
    private String service_order_date;

    @SerializedName("status")
    @Expose
    private String service_order_status;

    @SerializedName("main_cat_name")
    @Expose
    private String service_main_category_name;

    @SerializedName("main_cat_ta_name")
    @Expose
    private String service_main_category_name_ta;

    @SerializedName("sub_cat_name")
    @Expose
    private String service_subcategory_name;

    @SerializedName("sub_cat_ta_name")
    @Expose
    private String service_subcategory_name_ta;

    @SerializedName("service_name")
    @Expose
    private String service_name;

    @SerializedName("service_ta_name")
    @Expose
    private String service_name_ta;

    @SerializedName("from_time")
    @Expose
    private String service_order_from_time;

    @SerializedName("to_time")
    @Expose
    private String service_order_to_time;

    @SerializedName("service_provider")
    @Expose
    private String service_associate_name;

    /**
     * @return The service_order_id
     */
    public String getServiceOrderId() {
        return service_order_id;
    }

    /**
     * @param service_order_id The service_order_id
     */
    public void setServiceOrderId(String service_order_id) {
        this.service_order_id = service_order_id;
    }

    /**
     * @return The service_location
     */
    public String getServiceLocation() {
        return service_location;
    }

    /**
     * @param service_location The service_location
     */
    public void setServiceLocation(String service_location) {
        this.service_location = service_location;
    }

    /**
     * @return The service_order_date
     */
    public String getServiceOrderDate() {
        return service_order_date;
    }

    /**
     * @param service_order_date The service_order_date
     */
    public void setServiceOrderDate(String service_order_date) {
        this.service_order_date = service_order_date;
    }

    /**
     * @return The service_order_status
     */
    public String getServiceOrderStatus() {
        return service_order_status;
    }

    /**
     * @param service_order_status The service_order_status
     */
    public void setServiceOrderStatus(String service_order_status) {
        this.service_order_status = service_order_status;
    }

    /**
     * @return The service_main_category_name
     */
    public String getServiceCategoryMainName() {
        return service_main_category_name;
    }

    /**
     * @param service_main_category_name The service_main_category_name
     */
    public void setServiceCategoryMainName(String service_main_category_name) {
        this.service_main_category_name = service_main_category_name;
    }

    /**
     * @return The service_main_category_name_ta
     */
    public String getServiceCategoryMainNameTA() {
        return service_main_category_name_ta;
    }

    /**
     * @param service_main_category_name_ta The service_main_category_name_ta
     */
    public void setServiceCategoryMainNameTA(String service_main_category_name_ta) {
        this.service_main_category_name_ta = service_main_category_name_ta;
    }


    /**
     * @return The service_subcategory_name
     */
    public String getServiceSubCategoryName() {
        return service_subcategory_name;
    }

    /**
     * @param service_subcategory_name The service_subcategory_name
     */
    public void setServiceSubCategoryName(String service_subcategory_name) {
        this.service_subcategory_name = service_subcategory_name;
    }

    /**
     * @return The service_subcategory_name_ta
     */
    public String getServiceSubCategoryNameTA() {
        return service_subcategory_name_ta;
    }

    /**
     * @param service_subcategory_name_ta The service_subcategory_name_ta
     */
    public void setServiceSubCategoryNameTA(String service_subcategory_name_ta) {
        this.service_subcategory_name_ta = service_subcategory_name_ta;
    }

    /**
     * @return The service_name
     */
    public String getServiceName() {
        return service_name;
    }

    /**
     * @param service_name The service_name
     */
    public void setServiceName(String service_name) {
        this.service_name = service_name;
    }

    /**
     * @return The service_name_ta
     */
    public String getServiceNameTA() {
        return service_name_ta;
    }

    /**
     * @param service_name_ta The service_name_ta
     */
    public void setServiceNameTA(String service_name_ta) {
        this.service_name_ta = service_name_ta;
    }

    /**
     * @return The service_order_from_time
     */
    public String getServiceOrderFromTime() {
        return service_order_from_time;
    }

    /**
     * @param service_order_from_time The service_order_from_time
     */
    public void setServiceOrderFromTime(String service_order_from_time) {
        this.service_order_from_time = service_order_from_time;
    }

    /**
     * @return The service_order_from_time
     */
    public String getServiceOrderToTime() {
        return service_order_to_time;
    }

    /**
     * @param service_order_to_time The service_order_to_time
     */
    public void setServiceOrderToTime(String service_order_to_time) {
        this.service_order_to_time = service_order_to_time;
    }

    /**
     * @return The service_expert_name
     */
    public String getServiceAssociateName() {
        return service_associate_name;
    }

    /**
     * @param service_associate_name The service_associate_name
     */
    public void setServiceAssociateName(String service_associate_name) {
        this.service_associate_name = service_associate_name;
    }
}
