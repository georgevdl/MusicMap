package com.georgevdl.musicmap.ui.my_map;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.georgevdl.musicmap.MainActivity;
import com.georgevdl.musicmap.databinding.FragmentMymapBinding;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MyMapFragment extends Fragment {

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


    private FragmentMymapBinding binding;
    private CopyrightOverlay mCopyrightOverlay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyMapViewModel myMapViewModel =
                new ViewModelProvider(this).get(MyMapViewModel.class);

        binding = FragmentMymapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Context ctx = ((MainActivity) requireActivity()).getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        map = binding.myMap;
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
        //Copyright overlay
        mCopyrightOverlay = new CopyrightOverlay(ctx);
        map.getOverlays().add(this.mCopyrightOverlay);


        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


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


        //On screen compass
        mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context),
                map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(this.mCompassOverlay);


        //map scale
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(this.mScaleBarOverlay);


        //support for map rotation
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(this.mRotationGestureOverlay);


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

        setHasOptionsMenu(true);
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