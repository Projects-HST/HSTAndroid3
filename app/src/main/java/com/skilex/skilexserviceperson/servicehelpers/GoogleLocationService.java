package com.skilex.skilexserviceperson.servicehelpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.skilex.skilexserviceperson.bean.database.SQLiteHelper;
import com.skilex.skilexserviceperson.helper.AlertDialogHelper;
import com.skilex.skilexserviceperson.helper.ProgressDialogHelper;
import com.skilex.skilexserviceperson.serviceinterfaces.IServiceListener;
import com.skilex.skilexserviceperson.utils.PreferenceStorage;
import com.skilex.skilexserviceperson.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.util.Log.d;

public class GoogleLocationService extends Service implements LocationListener, IServiceListener {

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location lLocation;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 200;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    public Location previousBestLoc = null;
    SQLiteHelper database;
    private static final int ONE_MINUTES = 1000;
    private boolean isFirstTimePreviousBest = true;
    private boolean isFirstTimeRecordUpdateToServer = true;
    private String gpsStatus = "N";

    private String LOG_TAG = null;
    public int counter = 0;

    public GoogleLocationService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public GoogleLocationService() {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 0, notify_interval);
        intent = new Intent(str_receiver);
        database = new SQLiteHelper(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*Log.i(LOG_TAG, "In onStartCommand");
        //ur actual code
        return START_STICKY;*/
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);

//        Intent intent = new Intent("com.android.ServiceStopped");
//        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
//        super.onDestroy();
        startService(new Intent(this, GoogleLocationService.class)); // add this line
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.i("***********************", "Location changed");
        if (isBetterLocation(lLocation, previousBestLoc)) {
            loc.getLatitude();
            loc.getLongitude();
            String latitude = "";
            String longitude = "";
            latitude = String.valueOf(loc.getLatitude());
            longitude = String.valueOf(loc.getLongitude());

            database.deleteAllCurrentBestLocation();

            long l = database.current_best_location_insert(latitude, longitude);
            //If everything went fine lets get latitude and longitude
            intent.putExtra("Latitude", loc.getLatitude());
            intent.putExtra("Longitude", loc.getLongitude());
            intent.putExtra("Provider", loc.getProvider());
            sendBroadcast(intent);
            if (isFirstTimePreviousBest) {
                database.deleteAllPreviousBestLocation();
                long l1 = database.previous_best_location_insert(latitude, longitude);
                previousBestLoc = loc;
                isFirstTimePreviousBest = false;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

            gpsStatus = "N";

        } else {

            /*if (isNetworkEnable) {
//                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    lLocation = null;
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
                    if (locationManager != null) {
                        lLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (lLocation != null) {

                            Log.e("latitude", lLocation.getLatitude() + "");
                            Log.e("longitude", lLocation.getLongitude() + "");

                            latitude = lLocation.getLatitude();
                            longitude = lLocation.getLongitude();
                            fn_update(lLocation);
//                        if (isBetterLocation(location, previousBestLoc)) {}
                        }
//                    }
                }
            }*/


            if (isGPSEnable) {
                gpsStatus = "Y";
//                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                lLocation = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
                if (locationManager != null) {
                    lLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lLocation != null) {
                        Log.e("latitude", lLocation.getLatitude() + "");
                        Log.e("longitude", lLocation.getLongitude() + "");
                        latitude = lLocation.getLatitude();
                        longitude = lLocation.getLongitude();
                        fn_update(lLocation);
//                        if (isBetterLocation(location, previousBestLoc)) {}
                    }
//                    }
                }
            }
        }
    }



    @Override
    public void onResponse(JSONObject response) {
        String xx = "ok";

        String yy = xx;
    }

    @Override
    public void onError(String error) {

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });
        }
    }

    private void fn_update(Location location) {

        intent.putExtra("latutide", location.getLatitude() + "");
        intent.putExtra("longitude", location.getLongitude() + "");

        sendBroadcast(intent);

//        Toast.makeText(getApplicationContext(), "" + location.getLatitude() + " & " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 2 second
        timer.schedule(timerTask, 500, 500); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    protected boolean isBetterLocation(Location currentBestlocation, Location
            previousBestLocation) {
        if (previousBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        String checkUserId = PreferenceStorage.getUserMasterId(getApplicationContext());
        double currentBestLat = 0.00;
        double currentBestLong = 0.00;
        double previousBestLat = 0.00;
        double previousBestLong = 0.00;

        Cursor c = database.getCurrentBestLocationTopValue();
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {

                    currentBestLat = Double.parseDouble(c.getString(1));
                    currentBestLong = Double.parseDouble(c.getString(2));

                } while (c.moveToNext());
            }
        }

        Cursor c1 = database.getPreviousBestLocationTopValue();
        if (c1.getCount() > 0) {
            if (c1.moveToFirst()) {
                do {

                    previousBestLat = Double.parseDouble(c1.getString(1));
                    previousBestLong = Double.parseDouble(c1.getString(2));

                } while (c1.moveToNext());
            }
        }
        double distance = 0.00;
        if (currentBestLat != 0.00 || currentBestLong != 0.00 || previousBestLat != 0.00 ||
                previousBestLong != 0.00) {

            /*Location loc1 = new Location("");
            loc1.setLatitude(previousBestLat);
            loc1.setLongitude(previousBestLong);

            Location loc2 = new Location("");
            loc2.setLatitude(currentBestLat);
            loc2.setLongitude(currentBestLong);

            float distanceInMeters = loc1.distanceTo(loc2);*/

            distance = distance(previousBestLat, previousBestLong, currentBestLat, currentBestLong);
//            distance = distanceInMeters;
        }
//        Toast.makeText(getApplicationContext(), "Latitude : " + currentLatitude + " " + "Longitude : " + currentLongitude, Toast.LENGTH_SHORT).show();

        // Check whether the new location fix is newer or older
        long timeDelta = currentBestlocation.getTime() - previousBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            if (isGPSEnable) {
                if (distance > 0.01) {
                    if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
//                        Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                        isFirstTimePreviousBest = false;
                        database.deleteAllPreviousBestLocation();
                        long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
                        previousBestLoc = currentBestlocation;
                        callService(distance, currentBestlocation);
                    }
                } else {
//                    if (distance == 0.00) {
                    if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
                        if (isFirstTimeRecordUpdateToServer) {
//                            Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                            isFirstTimeRecordUpdateToServer = false;
                            database.deleteAllPreviousBestLocation();
                            long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
//                            previousBestLoc = currentBestlocation;
                            callService(distance, currentBestlocation);
                        }
//                        }
                    }
                }
                return true;

                // If the new location is more than two minutes older, it must be worse
            }
        } else if (isSignificantlyOlder) {
            if (isGPSEnable) {
//                if (distance == 0.00) {
                if (!checkUserId.equalsIgnoreCase("") || checkUserId != null) {
                    if (isFirstTimeRecordUpdateToServer) {
//                        Toast.makeText(this, "Location sent", Toast.LENGTH_LONG).show();
                        isFirstTimeRecordUpdateToServer = false;
                        database.deleteAllPreviousBestLocation();
                        long l = database.previous_best_location_insert("" + currentBestlocation.getLatitude(), "" + currentBestlocation.getLongitude());
                        previousBestLoc = currentBestlocation;
                        callService(distance, currentBestlocation);
                    }
//                    }
                }
                return false;
            }
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (currentBestlocation.getAccuracy() - previousBestLoc.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(currentBestlocation.getProvider(),
                previousBestLoc.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        double roundDist = round(dist, 6);

        return roundDist;  // output distance, in MILES
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void callService(double distance, Location location) {
        double currentLatitude = location.getLatitude();
        String lat = Double.toString(currentLatitude);
        double currentLongitude = location.getLongitude();
        String lon = Double.toString(currentLongitude);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        String locationAddress = getCompleteAddressString(currentLatitude, currentLongitude);
        String dist = Double.toString(distance);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.KEY_USER_MASTER_ID, PreferenceStorage.getUserMasterId(getApplicationContext()));
            jsonObject.put(SkilExConstants.PARAMS_LATITUDE, currentLatitude);
            jsonObject.put(SkilExConstants.PARAMS_LONGITUDE, currentLongitude);
//            jsonObject.put(SkilExConstants.PARAMS_DATETIME, currentDateandTime);
//            jsonObject.put(SkilExConstants.PARAMS_LOCATION, locationAddress);
//            jsonObject.put(SkilExConstants.PARAMS_DISTANCE, dist);
//            jsonObject.put(SkilExConstants.PARAMS_SERVICE_ORDER_ID, PreferenceStorage.getServiceOrderId(getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SkilExConstants.BUILD_URL + SkilExConstants.API_PERSON_TRACKING;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

        /*long x = database.store_location_data_insert(PreferenceStorage.getUserMasterId(getApplicationContext()), lat, lon, locationAddress,
                currentDateandTime, dist, PreferenceStorage.getServiceOrderId(getApplicationContext()), gpsStatus);*/

//        System.out.println("Stored Id : " + x);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("MyCurrentloctionaddress", "Canont get Address!");
        }
        return strAdd;
    }
}
