package com.georgevdl.musicmap.ui.global_map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.georgevdl.musicmap.MainActivity;
import com.georgevdl.musicmap.OnlineTrack;
import com.georgevdl.musicmap.OnlineTrackLocation;
import com.georgevdl.musicmap.R;
import com.georgevdl.musicmap.Utils;
import com.georgevdl.musicmap.databinding.FragmentGlobalmapBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class GlobalMapFragment extends Fragment {

    private FragmentGlobalmapBinding binding;
    // ===========================================================
    // Constants
    // ===========================================================

    private static final String PREFS_NAME = "com.georgevdl.musicmap.prefs";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";

    private static final int MENU_ABOUT = Menu.FIRST + 1;
    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id

    // ===========================================================
    // Fields
    // ===========================================================
    private SharedPreferences mPrefs;
    private MapView map;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private CopyrightOverlay mCopyrightOverlay;

    private Marker.OnMarkerClickListener markerListener = new Marker.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker, MapView mapView) {
            //marker.showInfoWindow();
            mapView.getController().animateTo(marker.getPosition());
            if (marker.getId().startsWith("track_")) {
                DatabaseReference ref = Utils.getDatabase().getInstance().getReference().child("tracks");
                marker.setInfoWindow(new MarkerInfoWindow(R.layout.custom_bubble, map));
                ref.child(marker.getId().substring(6)).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                OnlineTrack onlineTrack = dataSnapshot.getValue(OnlineTrack.class);
                                if (onlineTrack == null)
                                    return;

                                marker.setTitle(onlineTrack.title);
                                marker.setSnippet(onlineTrack.artist);
                                marker.setSubDescription(onlineTrack.genre);
                                marker.closeInfoWindow();
                                marker.showInfoWindow();

                                Glide.with(getContext())
                                        .asDrawable()
                                        .load(onlineTrack.albumArtURL)
                                        .dontTransform()
                                        .into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                                                marker.setImage(resource);
                                                marker.closeInfoWindow();
                                                marker.showInfoWindow();
                                            }

                                            @Override
                                            public void onLoadCleared(Drawable placeholder) {
                                            }
                                        /*@Override
                                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                            Toast.makeText(getContext(), "Failed to get album art", Toast.LENGTH_SHORT).show();
                                        }*/
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });


                //myMapViewModel.getTrackById(trackId).observe(getViewLifecycleOwner(), trackObserver);
            }
            return true;
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GlobalMapViewModel notificationsViewModel =
                new ViewModelProvider(this).get(GlobalMapViewModel.class);

        binding = FragmentGlobalmapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Context ctx = ((MainActivity) requireActivity()).getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        map = binding.globalMap;
        map.setDestroyMode(false);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            /**
             * mouse wheel zooming ftw
             * http://stackoverflow.com/questions/11024809/how-can-my-view-respond-to-a-mousewheel
             * @param v
             * @param event
             * @return
             */
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_SCROLL:
                            if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                                map.getController().zoomOut();
                            else {
                                //this part just centers the map on the current mouse location before the zoom action occurs
                                IGeoPoint iGeoPoint = map.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                                map.getController().animateTo(iGeoPoint);
                                map.getController().zoomIn();
                            }
                            return true;
                    }
                }
                return false;
            }
        });

        map.setMultiTouchControls(true);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        DatabaseReference ref = Utils.getDatabase().getInstance().getReference().child("trackLocations");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            OnlineTrackLocation result = postSnapshot.getValue(OnlineTrackLocation.class);
                            GeoPoint startPoint = new GeoPoint(result.latitude, result.longitude);
                            Marker startMarker = new Marker(map);
                            startMarker.setPosition(startPoint);
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            startMarker.setTitle("Loading...");
                            startMarker.setId("track_" + result.trackId);
                            startMarker.setOnMarkerClickListener(markerListener);
                            map.getOverlays().add(startMarker);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


        //My Location
        //note you have handle the permissions yourself, the overlay did not do it for you
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);

        //Copyright overlay
        mCopyrightOverlay = new CopyrightOverlay(context);
        //i hate this very much, but it seems as if certain versions of android and/or
        //device types handle screen offsets differently
        map.getOverlays().add(this.mCopyrightOverlay);

        //needed for pinch zooms
        map.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        map.setTilesScaledToDpi(true);

        //the rest of this is restoring the last map location the user looked at
        final float zoomLevel = mPrefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1);
        map.getController().setZoom(zoomLevel);
        final float orientation = mPrefs.getFloat(PREFS_ORIENTATION, 0);
        map.setMapOrientation(orientation, false);
        final String latitudeString = mPrefs.getString(PREFS_LATITUDE_STRING, "1.0");
        final String longitudeString = mPrefs.getString(PREFS_LONGITUDE_STRING, "1.0");
        final double latitude = Double.valueOf(latitudeString);
        final double longitude = Double.valueOf(longitudeString);
        map.setExpectedCenter(new GeoPoint(latitude, longitude));

        //setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        //save the current location
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, map.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, map.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(map.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(map.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) map.getZoomLevelDouble());
        edit.commit();

        map.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //this part terminates all of the overlays and background threads for osmdroid
        //only needed when you programmatically create the map
        map.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            map.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }

        map.onResume();
    }

    public void zoomIn() {
        map.getController().zoomIn();
    }

    public void zoomOut() {
        map.getController().zoomOut();
    }

    // @Override
    // public boolean onTrackballEvent(final MotionEvent event) {
    // return this.mMapView.onTrackballEvent(event);
    // }
    public void invalidateMapView() {
        map.invalidate();
    }

}