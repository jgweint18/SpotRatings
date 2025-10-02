The project is still in progress. 
Currently runs using VSCode
Run using make run
To use, make sure you enter filenames with the full extension to open and save them. 
Press Q to quit at the menu
When taking in a file, the program looks for a specific format:
Songname , Artist
This can be achieved by exporting your playlist here:
https://www.spotlistr.com/
Still looking to implement:
  A system that lets you rate individual songs instead of the whole playlist,
  Potentially switching from maps to classes
  Reverse the order of sort by rating
  
Function Breakdown:

  A. Add songs
    Adds songs to the file taken from the input
    This asks for both the song title and artist
    Can add multiple songs at once
    Type "N" (case sensitive) to leave
  1. Rate Songs
     First, enter a filename.
     Then it goes through songs in order
     Assign a rating 0-5 (or whatever you want, it's a double variable)
     It then adds the songs and ratings to a map
  2. Show Ratings
       Displays ratings in 2 different ways
       1. List each song
            Lists the songs in file order and their rating after
       2. Sort by rating
            Displays the songs sorted by their rating
            Displays the rating, then the songs under it
  3. Sort By Artist
       Takes in the filename again for songs
       Displays all songs sorted by their artist
       No ratings here
  4. Save Ratings to file
       User inputs a filename to create and saves the current map with song ratings to.
       It says end with CSV, but that isn't necessary.
  5. Import ratings from file
       Imports ratings from a file created by this program
       Formatting must be the same as the program makes
