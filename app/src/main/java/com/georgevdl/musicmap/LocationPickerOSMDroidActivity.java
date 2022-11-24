package com.georgevdl.musicmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class LocationPickerOSMDroidActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private MyLocationNewOverlay mLocationOverlay;
    Marker startMarker;
    private CopyrightOverlay mCopyrightOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_location_picker_osmdroid);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        Intent intent = getIntent();

        startMarker = new Marker(map);
        Location startLoc = intent.getParcelableExtra("startLoc");
        GeoPoint startPoint;
        if(startLoc != null)
            startPoint = new GeoPoint(startLoc);
        else
            startPoint = new GeoPoint(0.0,0.0);

        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle(intent.getStringExtra("title") + "\n" + intent.getStringExtra("artist"));
        startMarker.setSubDescription(intent.getStringExtra("genre"));
        //startMarker.setIcon(getResources().getDrawable(R.drawable.marker_kml_point).mutate());
        //startMarker.setImage(getResources().getDrawable(R.drawable.ic_launcher));
        //startMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble_black, map));
        startMarker.setDraggable(true);
        map.getOverlays().add(startMarker);

        //scales tiles to the current screen's DPI, helps with readability of labels
        map.setTilesScaledToDpi(true);

        //Copyright overlay
        mCopyrightOverlay = new CopyrightOverlay(ctx);
        map.getOverlays().add(this.mCopyrightOverlay);
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void returnLocation(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", startMarker.getPosition().getLatitude());
        resultIntent.putExtra("longitude", startMarker.getPosition().getLongitude());
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }

}