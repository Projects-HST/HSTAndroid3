package com.skilex.skilexserviceperson.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceStorage {

    /*To save FCM key locally*/
    public static void saveGCM(Context context, String gcmId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.KEY_FCM_ID, gcmId);
        editor.apply();
    }

    public static String getGCM(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SkilExConstants.KEY_FCM_ID, "");
    }
    /*End*/

    /*To store mobile number*/
    public static void saveMobileNo(Context context, String type) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.KEY_MOBILE_NUMBER, type);
        editor.apply();
    }

    public static String getMobileNo(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String mobileNo;
        mobileNo = sharedPreferences.getString(SkilExConstants.KEY_MOBILE_NUMBER, "");
        return mobileNo;
    }
    /*End*/

    /*To store user master id*/
    public static void saveUserMasterId(Context context, String userMasterId) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.KEY_USER_MASTER_ID, userMasterId);
        editor.apply();
    }

    public static String getUserMasterId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String userMasterId;
        userMasterId = sharedPreferences.getString(SkilExConstants.KEY_USER_MASTER_ID, "");
        return userMasterId;
    }
    /*End*/

    /*Login type*/
    public static void saveLoginType(Context context, String loginType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.PREF_LOGIN_TYPE, loginType);
        editor.apply();
    }

    public static String getLoginType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String loginType;
        loginType = sharedPreferences.getString(SkilExConstants.PREF_LOGIN_TYPE, "");
        return loginType;
    }
    /*End*/

    /*Active status*/
    public static void saveActiveStatus(Context context, String loginType) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.PREF_ACTIVE_STATUS, loginType);
        editor.apply();
    }

    public static String getActiveStatus(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String loginType;
        loginType = sharedPreferences.getString(SkilExConstants.PREF_ACTIVE_STATUS, "");
        return loginType;
    }
    /*End*/

    /*Full name*/
    public static void saveFullName(Context context, String fullName) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.PREF_FULL_NAME, fullName);
        editor.apply();
    }

    public static String getFullName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String fullName;
        fullName = sharedPreferences.getString(SkilExConstants.PREF_FULL_NAME, "");
        return fullName;
    }
    /*End*/

    /*Gender*/
    public static void saveGender(Context context, String gender) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.PREF_GENDER, gender);
        editor.apply();
    }

    public static String getGender(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String gender;
        gender = sharedPreferences.getString(SkilExConstants.PREF_GENDER, "");
        return gender;
    }
    /*End*/

    /*Email*/
    public static void saveEmail(Context context, String email) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.PREF_EMAIL, email);
        editor.apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String email;
        email = sharedPreferences.getString(SkilExConstants.PREF_EMAIL, "");
        return email;
    }
    /*End*/

    /*Profile picture*/
    public static void saveProfilePicture(Context context, String profilePicture) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.PREF_PROFILE_PICTURE, profilePicture);
        editor.apply();
    }

    public static String getProfilePicture(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String profilePicture;
        profilePicture = sharedPreferences.getString(SkilExConstants.PREF_PROFILE_PICTURE, "");
        return profilePicture;
    }
    /*End*/

    /*Address*/
    public static void saveAddress(Context context, String address) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SkilExConstants.PREF_ADDRESS, address);
        editor.apply();
    }

    public static String getAddress(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String address;
        address = sharedPreferences.getString(SkilExConstants.PREF_ADDRESS, "");
        return address;
    }
    /*End*/

}
