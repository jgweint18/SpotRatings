public class Song {
    private String artist;
    private String title;
    private double rating;
    private String artworkUrl;
    private String album;

    public Song(String artist, String title, double rating, String artworkUrl, String album) {
        this.artist = artist;
        this.title = title;
        this.rating = rating;
        this.artworkUrl = artworkUrl;
        this.album = album;
    }

    public Song(String artist, String title, double rating, String artworkUrl) {
        this.artist = artist;
        this.title = title;
        this.rating = rating;
        this.artworkUrl = artworkUrl;
        this.album = null;
    }

    // keep old constructor for loading from file
    public Song(String artist, String title, double rating) {
        this.artist = artist;
        this.title = title;
        this.rating = rating;
        this.artworkUrl = null;
        this.album = null;
    }

    public String getArtist() { return artist; }
    public String getTitle() { return title; }
    public double getRating() { return rating; }
    public String getArtworkUrl() { return artworkUrl; }
    public String getAlbum() { return album; }

    public void setRating(double rating) { this.rating = rating; }
    public void setArtworkUrl(String artworkUrl) { this.artworkUrl = artworkUrl; }
    public void setAlbum(String album) { this.album = album; }
}