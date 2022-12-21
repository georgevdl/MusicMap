# Music Map

Connect your music to the places where you discovered it. Identify a song, then share it to this
application and add it to your own Music Map. You could also add it to the Global map which contains
songs shared by all users.  
Discover new music in new places by browsing the Global Map.

Future feature: Filter the global map results by genre or date of discovery

### Download

The application is in early development and may contain bugs.  
Download [app-release.apk](app/release/app-release.apk?raw=true)

### Screenshots

The screenshots are outdated and only show the "My Map" function. The "Global Map" function has been
implemented and can be tested by installing the above APK.

After the user shares the link to a track the application will start downloading its metadata
![](Screenshots/1.png)

Once this finishes the results will be shown and the app will try to get the user location. If there
are any issues related to location services and/or their permissions, the following will appear. The
user could fix these issues and try again or pick a location manually.
![](Screenshots/2.png?raw=true)

Manual location picker  
The user could drag the marker to their current location
![](Screenshots/3.png?raw=true)

If location services work the app will try to automatically determine the user's location.  
If the accuracy is not good enough, the user could pick the location manually.
![](Screenshots/4.png?raw=true)

Once the app gets a precise location, the user can add the result to their map.  
They could still tap on the manual location button to verify its correctness and improve it if
needed.
![](Screenshots/5.png?raw=true)

By navigating to the "My Map" fragment, the user could see all their tracks in the places that they
were identified.
![](Screenshots/6.png?raw=true)
![](Screenshots/7.png?raw=true)

When a user taps a map marker, a pop up information window appears with the song's metadata,
including its album art. If they switch to the "Track" fragment through the bottom navigation bar,
they could see more details about the last track they tapped on the map.
![](Screenshots/8.png?raw=true)