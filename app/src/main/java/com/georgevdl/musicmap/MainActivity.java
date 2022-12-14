package com.georgevdl.musicmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.georgevdl.musicmap.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.elevation.SurfaceColors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private com.georgevdl.musicmap.ui.add.AddViewModel homeViewModel;
    //private Location currentBestLocation = null;
    LocationManager locationManager;
    MyLocationListener locationListener;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private String currentURL = "";
    private Track currentTrack = null;
    Location lastKnownLocation = null;
    private final int REQUEST_MANUAL_LOCATION = 2;

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        //getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_add, R.id.navigation_my_map, R.id.navigation_global_map)
                .build();*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInAnonymously:success");
                                currentUser = mAuth.getCurrentUser();
                                mDatabase = Utils.getDatabase().getInstance().getReference();
                                mDatabase.child("trackLocations").keepSynced(true);
                                mDatabase.child("tracks").keepSynced(true);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                /*Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();*/
                            }
                        }
                    });
        }

        mDatabase = Utils.getDatabase().getInstance().getReference();

        if (savedInstanceState == null)
            getSharedSong();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Replace the activity if the user shared a track
        if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
            finishAndRemoveTask();
            startActivity(getIntent());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_MANUAL_LOCATION): {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    lastKnownLocation = new Location("");
                    lastKnownLocation.setLatitude(latitude);
                    lastKnownLocation.setLongitude(longitude);
                    lastKnownLocation.setTime(System.currentTimeMillis() / 1000L);
                    //Toast.makeText(this, "Manual location: " + latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                    homeViewModel.setManuallyPickLocationButtonVisibility(Button.GONE);
                    homeViewModel.setTextStatus("Ready to add to map\n" + latitude + ", " + longitude);
                    homeViewModel.setAddToMyMapButtonVisibility(Button.VISIBLE);
                }
                break;
            }
        }
    }

    /**
     * Gets the track's Shazam URL from the intent and downloads its metadata
     */
    private void getSharedSong() {

        Intent intent = getIntent();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            String shazamURLFilter = "https://www.shazam.com/track/";
            int URLBeginsAt = sharedText.lastIndexOf(shazamURLFilter);
            if (URLBeginsAt < 0) {
                Toast exitToast = Toast.makeText(getApplicationContext(), R.string.toast_message_invalid_url, Toast.LENGTH_LONG);
                exitToast.show();
                finishAndRemoveTask();
                return;
            }
            String shazamURL = sharedText.substring(URLBeginsAt);

            if (shazamURL == null || shazamURL.length() == 0)
                return;

            homeViewModel.setProgressBarVisibility(TextView.VISIBLE);
            homeViewModel.setStatusTextVisibility(TextView.VISIBLE);

            WebView myWebView = (WebView) findViewById(R.id.webview);
            myWebView.getSettings().setJavaScriptEnabled(true);
            //myWebView.evaluateJavascript("enable();", null);
            myWebView.getSettings().setDomStorageEnabled(true);
            MyJavaScriptInterface jInterface = new MyJavaScriptInterface(this, shazamURL);

            myWebView.addJavascriptInterface(jInterface, "HtmlViewer");
            myWebView.setWebViewClient(new WebViewClient() {
                int counter = 0;
                int scriptCounter = 0;

                @Override
                public void onLoadResource(WebView view, String url) {
                    if (url.contains("favicon.ico") && counter++ == 2) {
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            myWebView.loadUrl("javascript:window.HtmlViewer.showHTML('" + MyJavaScriptInterface.titleStart + "'+document.evaluate('/html/body/div[4]/div/main/div/div[1]/div/article[2]/div[2]/div[2]/div[1]/div/div/h1', document, null, XPathResult.STRING_TYPE, null).stringValue);");
                            myWebView.loadUrl("javascript:window.HtmlViewer.showHTML('" + MyJavaScriptInterface.artistStart + "'+document.evaluate('/html/body/div[4]/div/main/div/div[1]/div/article[2]/div[2]/div[2]/div[1]/div/div/h2/meta/@content', document, null, XPathResult.STRING_TYPE, null).stringValue);");
                            myWebView.loadUrl("javascript:window.HtmlViewer.showHTML('" + MyJavaScriptInterface.genreStart + "'+document.evaluate('/html/body/div[4]/div/main/div/div[1]/div/article[2]/div[2]/div[2]/div[1]/div/div/h3', document, null, XPathResult.STRING_TYPE, null).stringValue);");
                            myWebView.loadUrl("javascript:window.HtmlViewer.showHTML('" + MyJavaScriptInterface.albumArtURLStart + "'+document.evaluate('/html/body/div[4]/div/main/div/div[1]/div/article[2]/div[2]/div[1]/img/@src', document, null, XPathResult.STRING_TYPE, null).stringValue);");
                            myWebView.loadUrl("javascript:window.HtmlViewer.showHTML('" + MyJavaScriptInterface.lyricsStart + "'+document.evaluate('/html/body/div[4]/div/main/div/div[3]/div[2]/div/article/div/div/p', document, null, XPathResult.STRING_TYPE, null).stringValue);");
                            myWebView.loadUrl("javascript:window.HtmlViewer.showHTML(document.evaluate('/html/body/div[4]/div/main/div/div[3]/div[2]/div/article/div/div/div/div', document, null, XPathResult.STRING_TYPE, null).stringValue);");
                        }, 200);
                    }
                }
            });
            myWebView.loadUrl(shazamURL);
        }
    }

    public void setHomeViewModel(com.georgevdl.musicmap.ui.add.AddViewModel avm) {
        homeViewModel = avm;
    }

    public synchronized void setTrackInfo(String[] s) {

        if (!MyJavaScriptInterface.titleStart.equals(s[0]) && !s[5].equals(currentURL)) {

            currentURL = s[5];
            String tmp = currentURL.substring(currentURL.lastIndexOf("/track/") + 7);
            String idStr = tmp.substring(0, tmp.indexOf('/'));
            int id = Integer.parseInt(idStr);
            currentTrack = new Track(s[0], s[1], s[2], s[3], s[4], id);

            showTrackInfo(currentTrack);
            homeViewModel.setProgressBarVisibility(ProgressBar.VISIBLE);
            homeViewModel.setStatusTextVisibility(TextView.VISIBLE);

            prepareLocation(null);

            /*WebView myWebView = (WebView) findViewById(R.id.webview);
            myWebView.stopLoading();*/
        }
    }

    public void showTrackInfo(Track track) {

        homeViewModel.setTryGPSAgainButtonVisibility(Button.GONE);
        homeViewModel.setManuallyPickLocationButtonVisibility(Button.GONE);
        homeViewModel.setAddToMyMapButtonVisibility(Button.GONE);
        homeViewModel.setProgressBarVisibility(ProgressBar.GONE);

        homeViewModel.setStatusTextVisibility(TextView.GONE);

        homeViewModel.setStartVisibility(TextView.GONE);
        homeViewModel.setResultsVisibility(TextView.VISIBLE);
        homeViewModel.setTextTitleResult(track.mTitle);
        homeViewModel.setTextArtistResult(track.mArtist);

        if (track.mGenre.length() > 0) {
            homeViewModel.setTextGenreResult(track.mGenre);
        } else {
            homeViewModel.setTextGenreResult("Unknown");
        }

        if (track.mAlbumArtURL.length() > 0) {
            homeViewModel.setTextAlbumArtURLResult(track.mAlbumArtURL);
        } else {
            homeViewModel.setTextAlbumArtURLResult("Unknown");
        }

        if (track.mLyrics.length() > 0) {
            homeViewModel.setTextLyricsResult(track.mLyrics);
            homeViewModel.setLyricsVisibility(TextView.VISIBLE);
        } else {
            homeViewModel.setLyricsVisibility(TextView.GONE);
        }
    }

    public void manuallyPickLocation(View v) {
        //Toast.makeText(this, "Picking location manually", Toast.LENGTH_SHORT).show();
        stopLocation();
        Intent myIntent = new Intent(MainActivity.this, LocationPickerOSMDroidActivity.class);
        if (lastKnownLocation != null) {
            myIntent.putExtra("startLoc", lastKnownLocation);
        }
        myIntent.putExtra("title", homeViewModel.getTextTitleResult().getValue());
        myIntent.putExtra("artist", homeViewModel.getTextArtistResult().getValue());
        myIntent.putExtra("genre", homeViewModel.getTextGenreResult().getValue());
        MainActivity.this.startActivityForResult(myIntent, REQUEST_MANUAL_LOCATION);
    }

    private boolean startLocation() {
        homeViewModel.setManuallyPickLocationButtonVisibility(Button.VISIBLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            requestLocationPermission();
            return false;
        }
        int providerCounter = 0;
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS Location Provider Error", Toast.LENGTH_SHORT).show();
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            providerCounter++;
        }
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //Toast.makeText(this, "Network Location Provider Error", Toast.LENGTH_SHORT).show();
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            providerCounter++;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!locationManager.isProviderEnabled(LocationManager.FUSED_PROVIDER)) {
                //Toast.makeText(this, "Fused Location Provider Error", Toast.LENGTH_SHORT).show();
            } else {
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 1000, 0, locationListener);
                providerCounter++;
            }
        }
        return providerCounter > 0;
    }

    public void prepareLocation(View v){
        homeViewModel.setTryGPSAgainButtonVisibility(Button.GONE);
        homeViewModel.setProgressBarVisibility(ProgressBar.VISIBLE);
        homeViewModel.setTextStatus("Waiting for location");

        if (!startLocation()) {
            homeViewModel.setTextStatus("Either the location services are off or the precise location permission has not been granted.\nEnable it and try again or pick your location manually");
            homeViewModel.setTryGPSAgainButtonVisibility(Button.VISIBLE);
            homeViewModel.setProgressBarVisibility(ProgressBar.INVISIBLE);
        }
    }

    private void stopLocation(){
        if(locationManager != null && locationListener != null)
            locationManager.removeUpdates(locationListener);
        homeViewModel.setProgressBarVisibility(ProgressBar.INVISIBLE);
    }

    public Location getLocation(){
        return locationListener.getLocation();
    }

    public void onNewLocation(){
        lastKnownLocation = locationListener.getLocation();
        if (lastKnownLocation != null && lastKnownLocation.getAccuracy() < 10) {
            stopLocation();
            homeViewModel.setTextStatus("Got precise location. Ready to add track to map");

            homeViewModel.setAddToMyMapButtonVisibility(Button.VISIBLE);
        }
    }

    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void addToMyMap(View v) {
        stopLocation();
        homeViewModel.setTryGPSAgainButtonVisibility(Button.GONE);
        homeViewModel.setManuallyPickLocationButtonVisibility(Button.GONE);
        homeViewModel.setAddToMyMapButtonVisibility(Button.GONE);
        homeViewModel.setStatusTextVisibility(TextView.GONE);
        homeViewModel.insert(currentTrack);
        homeViewModel.insert(new TrackLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), lastKnownLocation.getTime(), currentTrack.mId));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
    }

    public void signIn(View v) {
        if (currentUser == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.AnonymousBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();
            signInLauncher.launch(signInIntent);
        } else if (currentUser.isAnonymous()) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();
            signInLauncher.launch(signInIntent);
        }
    }

    public void addToGlobalMap(View v) {
        if (homeViewModel.getButtonAddToMyMapVisibility().getValue() == Button.VISIBLE) {
            addToMyMap(null);
        } else {
            stopLocation();
        }
        if (currentTrack == null)
            return;
        if (currentUser == null) {
            Toast.makeText(this, "Couldn't upload to global map: Not logged in", Toast.LENGTH_SHORT);
            return;
        }
        if (currentTrack.mGenre != null)
            mDatabase.child("genres").child(currentTrack.mGenre).setValue(true);
        OnlineTrackLocation newOnlineTrackLoc = new OnlineTrackLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), lastKnownLocation.getTime(), currentTrack.mId, currentTrack.mGenre, currentUser.getUid());
        String tmpKey = mDatabase.child("trackLocations").push().getKey();
        mDatabase.child("trackLocations").child(tmpKey).setValue(newOnlineTrackLoc);
        mDatabase.child("tracks").child("" + currentTrack.mId).setValue(new OnlineTrack(currentTrack.mTitle, currentTrack.mArtist, currentTrack.mGenre, currentTrack.mAlbumArtURL));
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // TODO
            // Successfully signed in
            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
}