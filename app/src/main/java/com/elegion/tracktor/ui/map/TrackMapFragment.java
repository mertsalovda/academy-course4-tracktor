package com.elegion.tracktor.ui.map;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.di.ViewModelModule;
import com.elegion.tracktor.event.AddPositionToRouteEvent;
import com.elegion.tracktor.event.GetRouteEvent;
import com.elegion.tracktor.event.StartTrackEvent;
import com.elegion.tracktor.event.StopTrackEvent;
import com.elegion.tracktor.event.UpdateRouteEvent;
import com.elegion.tracktor.ui.results.ResultsActivity;
import com.elegion.tracktor.util.ScreenshotMaker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * @author Azret Magometov
 */
public class TrackMapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    public static final int DEFAULT_ZOOM = 15;

    private boolean mIsStopped = true;

    private GoogleMap mMap;

    @Inject
    MainViewModel mMainViewModel;
    private float mStartMarker;
    private float mEndMarker;
    private List<LatLng> mRoute = new ArrayList<>();
    private float mWidthLine = 10f;
    private int mColorLine = Color.BLACK;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void configure() {
        getMapAsync(this);
        Scope scope = Toothpick.openScope(CounterFragment.class).installModules(new ViewModelModule(this));
        Toothpick.inject(this, scope);
        updateSettings();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this::initMap);
    }

    private void initMap() {
        Context context = getContext();
        if (context != null
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new GetRouteEvent());
        if (mIsStopped && !mRoute.isEmpty()) {
            onUpdateRoute(new UpdateRouteEvent(mRoute, 0));
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPositionToRoute(AddPositionToRouteEvent event) {
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .add(event.getLastPosition(), event.getNewPosition()));
        polyline.setWidth(mWidthLine);
        polyline.setColor(mColorLine);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.getNewPosition(), DEFAULT_ZOOM));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateRoute(UpdateRouteEvent event) {
        mMap.clear();
        updateSettings();
        mRoute = event.getRoute();
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .addAll(mRoute));
        polyline.setWidth(mWidthLine);
        polyline.setColor(mColorLine);
        addMarker(mRoute.get(0), getString(R.string.start), mStartMarker);
        zoomRoute(mRoute);
        if (mIsStopped) {
            addMarker(mRoute.get(mRoute.size() - 1), getString(R.string.end), mEndMarker);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRoute(StartTrackEvent event) {
        mIsStopped = false;
        mMap.clear();
        updateSettings();
        addMarker(event.getStartPosition(), getString(R.string.start), mStartMarker);
    }

    private void updateSettings() {
        mStartMarker = Float.parseFloat(mMainViewModel.mPreferences.getString("start_marker", getString(R.string.pref_default_value_start_marker)));
        mEndMarker = Float.parseFloat(mMainViewModel.mPreferences.getString("end_marker", getString(R.string.pref_default_value_end_marker)));
        mWidthLine = Float.parseFloat(mMainViewModel.mPreferences.getString("width_line", getString(R.string.pref_default_value_width_line)));
        mColorLine = Color.parseColor(mMainViewModel.mPreferences.getString("color_line", getString(R.string.pref_default_value_color_line)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopRoute(StopTrackEvent event) {
        mIsStopped = true;
        List<LatLng> route = event.getRoute();
        if (route.isEmpty()) {
            Toast.makeText(getContext(), R.string.dont_stay, Toast.LENGTH_SHORT).show();
        } else {
            updateSettings();
            addMarker(route.get(route.size() - 1), getString(R.string.end), mEndMarker);

            takeMapScreenshot(route, bitmap -> {
                int quality = Integer.parseInt(mMainViewModel.mPreferences.getString("compression", getString(R.string.pref_default_value_compression)));
                String base64image = ScreenshotMaker.toBase64(bitmap, quality);
                long resultId = mMainViewModel.saveResults(base64image);
                ResultsActivity.start(getContext(), resultId);
            });
        }
    }

    private void addMarker(LatLng position, String text, float marker) {
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.defaultMarker(marker))
                .title(text));
    }

    private void takeMapScreenshot(List<LatLng> route, GoogleMap.SnapshotReadyCallback snapshotCallback) {
        zoomRoute(route);
        mMap.snapshot(snapshotCallback);
    }

    private void zoomRoute(List<LatLng> route) {
        if (route.size() == 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.get(0), DEFAULT_ZOOM));
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : route) {
                builder.include(point);
            }
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
            mMap.moveCamera(cu);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

}
