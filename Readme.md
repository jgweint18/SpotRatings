# SpotStats

A JavaFX music rating application. Still in progress.

Currently runs using Maven. Run using `mvn clean compile exec:java`

To add songs from an existing Spotify playlist, export it from https://www.spotlistr.com/export/spotify-playlist and load it using the Load File button. Make sure the file follows the format: `Artist, Song Title`

## Features

### Save Ratings
Saves your current song list and ratings to a file. Use Load File to bring them back later. The file uses `|` as a separator so it can be told apart from a regular playlist.

### Load File
Loads either a plain playlist CSV or a previously saved ratings file. The program detects the format automatically. Japanese characters and other unicode are supported.

### Add Songs
Opens a new scene with a search bar connected to the iTunes API. Search by song title or artist name. Select a song from the dropdown to see the album cover, artist, album name, release year, and genre. You can rate the song before adding it. There is also an Add Full Album button that adds every track from the album at once. Press Back to return to the main table without losing your library.

### Rating Songs
Double click any cell in the Rating column to edit it. Ratings are between 0 and 10 and support decimals.

### Search Bar
Filters the table in real time by song title or artist name.

### Album Art
Album covers are displayed in the table. When loading a playlist from a file, the program fetches album art from iTunes automatically in the background.

## Still looking to implement
- Spotify API integration (??)
- More sorting options (filter by rating?)
- Stats and insights about songs added