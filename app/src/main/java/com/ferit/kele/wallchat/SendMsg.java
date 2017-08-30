package com.ferit.kele.wallchat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Kele on 12.8.2017..
 */

public class SendMsg extends AsyncTask<Void, Void, Void> {

    LocationManager locManager;
    Context mContext;
    Map mMap;
    String locProvider;
    DatabaseReference myRef, msg;
    private String tempKey;

    public SendMsg(Context context, Map map) {
        mMap = map;
        mContext = context;
        this.locManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
    }


    @Override
    protected Void doInBackground(Void... params) {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        locProvider = this.locManager.getBestProvider(criteria, true);
        mMap.put("Location", getLocation(locManager.getLastKnownLocation(locProvider)));
        myRef = FirebaseDatabase.getInstance().getReference().getRoot().child("WallChat-Room");
        tempKey = myRef.push().getKey();
        msg = myRef.child(tempKey);
        msg.updateChildren(mMap);
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private String getLocation(Location mLocation) {
        String place = "Nije definirano";
        if(Geocoder.isPresent()){
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                if (mLocation != null){
                    List<Address> near = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    if (near.size() > 0){
                        place = near.get(0).getLocality();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else place = "Geocoder not present";
        return place;
    }
}
