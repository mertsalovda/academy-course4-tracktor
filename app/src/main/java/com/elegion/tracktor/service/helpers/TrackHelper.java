package com.elegion.tracktor.service.helpers;

import android.Manifest;
import android.app.Service;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.event.AddPositionToRouteEvent;
import com.elegion.tracktor.event.StartTrackEvent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class TrackHelper {
    private Service mService;

    private List<LatLng> mRoute = new ArrayList<>();
    private Location mLastLocation;
    private LatLng mLastPosition;
    private double mDistance;

    public static final int UPDATE_INTERVAL = 15_000;
    public static final int UPDATE_FASTEST_INTERVAL = 5_000;
    public static final int UPDATE_MIN_DISTANCE = 20;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {

                if (isFirstPoint()) {
                    addPointToRoute(locationResult.getLastLocation());
                    EventBus.getDefault().post(new StartTrackEvent(mLastPosition));

                } else {

                    Location newLocation = locationResult.getLastLocation();
                    LatLng newPosition = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

                    if (positionChanged(newPosition)) {
                        mRoute.add(newPosition);
                        LatLng prevPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        mDistance += SphericalUtil.computeDistanceBetween(prevPosition, newPosition);
                        EventBus.getDefault().post(new AddPositionToRouteEvent(prevPosition, newPosition, mDistance));
                    }

                    mLastLocation = newLocation;
                    mLastPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                }
            }
        }
    };

    public TrackHelper(Service service) {
        mService = service;
        init();
    }

    private void init() {
        final LocationRequest locationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(UPDATE_FASTEST_INTERVAL)
                .setSmallestDisplacement(UPDATE_MIN_DISTANCE)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mService);
        if (ContextCompat.checkSelfPermission(mService, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        } else {
            Toast.makeText(mService, R.string.permissions_denied, Toast.LENGTH_SHORT).show();
        }
    }

    public double getDistance() {
        return mDistance;
    }

    public List<LatLng> getRoute() {
        return mRoute;
    }

    private boolean positionChanged(LatLng newPosition) {
        return mLastLocation.getLongitude() != newPosition.longitude || mLastLocation.getLatitude() != newPosition.latitude;
    }

    private void addPointToRoute(Location lastLocation) {
        mLastLocation = lastLocation;
        mLastPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mRoute.add(mLastPosition);
    }

    public boolean isFirstPoint() {
        return mRoute.size() == 0 && mLastLocation == null && mLastPosition == null;
    }

    public void destroy() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}
