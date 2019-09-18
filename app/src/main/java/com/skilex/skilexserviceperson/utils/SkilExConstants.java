package com.skilex.skilexserviceperson.utils;

public class SkilExConstants {

    //    URLS
    //    BASE URL
    private static final String BASE_URL = "https://skilex.in/";

    //BUILD URL
//    public static final String BUILD_URL = BASE_URL + "development/apisperson/";
    public static final String BUILD_URL = BASE_URL + "uat/apisperson/";

    // Alert Dialog Constants
    public static String ALERT_DIALOG_TITLE = "alertDialogTitle";
    public static String ALERT_DIALOG_MESSAGE = "alertDialogMessage";
    public static String ALERT_DIALOG_TAG = "alertDialogTag";
    public static String ALERT_DIALOG_POS_BUTTON = "alert_dialog_pos_button";
    public static String ALERT_DIALOG_NEG_BUTTON = "alert_dialog_neg_button";

    //    Service Params
    public static String PARAM_MESSAGE = "msg";

    //    Shared FCM ID
    public static final String KEY_FCM_ID = "fcm_id";

    //    Shared Phone No
    public static final String KEY_MOBILE_NUMBER = "mobile_number";

    //  Shared User Master Id
    public static final String KEY_USER_MASTER_ID = "user_master_id";

    //Login type
    public static final String PREF_LOGIN_TYPE = "login_type";

    //Active status
    public static final String PREF_ACTIVE_STATUS = "activeStatus";

    //NUMBER VERIFICATION URL FOR LOGIN
    public static final String MOBILE_VERIFICATION = "mobile_check/";

    //Login
    public static String LOGIN = "login/";

    // Login Parameters
    public static String PHONE_NUMBER = "phone_no";
    public static String OTP = "otp";
    public static String DEVICE_TOKEN = "device_token";
    public static String MOBILE_TYPE = "mobile_type";
    public static String USER_MASTER_ID = "user_master_id";
    public static String UNIQUE_NUMBER = "unique_number";
    public static String MOBILE_KEY = "mobile_key";
    public static String USER_STATUS = "user_stat";

    //Requested services list
    public static final String API_ASSIGNED_SERVICE = "list_assigned_services/";
    public static final String API_ASSIGNED_SERVICE_DETAILS = "detail_assigned_services/";

    //Ongoing service list
    public static final String API_ONGOING_SERVICE = "list_ongoing_services/";
    public static final String API_INITIATED_SERVICE_DETAIL = "detail_initiated_services/";
    public static final String API_SERVICE_PROCESS = "service_process/";
    public static final String API_REQUEST_OTP = "request_otp/";
    public static final String API_START_SERVICE = "start_services/";
    public static final String API_ONGOING_SERVICE_DETAILS = "detail_ongoing_services/";
    public static final String API_ONGOING_SERVICE_DETAIL_UPDATE = "update_ongoing_services/";
    public static final String API_ONGOING_SERVICE_COMPLETE = "complete_services/";
    public static final String UPLOAD_BILL_DOCUMENT = "upload_service_bills/";

    //Initiate service
    public static final String API_INITIATE_SERVICE = "initiate_services/";
    public static String SERVICE_ORDER_ID = "service_order_id";
    public static String SERVICE_OTP = "service_otp";
    public static String KEY_MATERIAL_NOTES = "material_notes";
    public static final String KEY_USER_TYPE = "user_type";

    //Cancel service
    public static final String API_CANCEL_REASON = "cancel_service_reasons/";
    public static final String API_CANCEL_SERVICE = "cancel_services/";
    public static String CANCEL_ID = "cancel_master_id";
    public static String CANCEL_COMMENTS = "comments";
    public static final String API_CANCELLED_SERVICE_LIST = "list_canceled_services/";

    //Completed service
    public static final String API_COMPLETED_SERVICE_LIST = "list_completed_services/";
    public static final String API_COMPLETED_SERVICE_DETAIL = "detail_completed_services/";

    //Login data
    public static final String PREF_FULL_NAME = "full_name";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_GENDER = "gender";
    public static final String PREF_PROFILE_PICTURE = "profile_picture";
    public static final String PREF_ADDRESS = "address";
//    public static final String PREF_ADDRESS = "address";
//    public static final String PREF_ADDRESS = "address";
//    public static final String PREF_ADDRESS = "address";

    //PROFILE UPDATE URL
    public static final String PROFILE_INFO = "user_info/";
    public static final String UPDATE_PROFILE = "profile_update/";
    public static final String UPLOAD_IMAGE = "profile_pic_upload/";




}
