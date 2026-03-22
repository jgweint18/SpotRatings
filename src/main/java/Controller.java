import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.InputStreamReader;
import java.io.FileInputStream;

public class Controller implements Initializable {

    @FXML
    private TableView<Song> songTable;

    @FXML
    private TableColumn<Song, String> artistCol;

    @FXML
    private TableColumn<Song, String> songCol;

    @FXML
    private TableColumn<Song, Double> ratingCol;

    @FXML
    private TableColumn<Song, String> artworkCol;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> searchResults;

    @FXML
    private ImageView albumCover;

    @FXML
    private Label songTitle, artistName, albumName, releaseYear, genre;

    @FXML
    private TextField ratingField;

    // List that the TableView displays
    public static ObservableList<Song> songList = FXCollections.observableArrayList();

    private java.util.ArrayList<com.fasterxml.jackson.databind.JsonNode> iTunesResults = new java.util.ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (songTable != null) { // Checks if in Main scene
            artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
            songCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
            ratingCol.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
            ratingCol.setOnEditCommit(event -> {
                double newRating = event.getNewValue();
                if (newRating < 0) newRating = 0;
                if (newRating > 10) newRating = 10;
                event.getRowValue().setRating(newRating);
            });
            artworkCol.setCellValueFactory(new PropertyValueFactory<>("artworkUrl"));
            artworkCol.setCellFactory(col -> new TableCell<Song, String>() {
                private final ImageView imageView = new ImageView();
                @Override
                protected void updateItem(String url, boolean empty) {
                    super.updateItem(url, empty);
                    if (empty || url == null) {
                        setGraphic(null);
                    } else {
                        imageView.setImage(new javafx.scene.image.Image(url.replace("100x100", "50x50"), true));
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        setGraphic(imageView);
                    }
                }
            });
            songTable.setEditable(true);
            songTable.setItems(songList);
        }
    }

    // Load playlist from file
    public void loadPlaylist(ActionEvent e) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        songList.clear();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.contains("|")) {
                    // ratings file
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        String artist = parts[0].trim();
                        String title = parts[1].trim();
                        double rating = Double.parseDouble(parts[2].trim());
                        songList.add(new Song(artist, title, rating));
                    }
                } else {
                    // plain playlist
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String artist = parts[0].trim();
                        String title = parts[1].trim();
                        songList.add(new Song(artist, title, 0.0));
                    }
                }
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addSong(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/AddSongs.fxml"));
            Parent root = loader.load();
            searchField.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveRatings(ActionEvent e) {
        if (songList.isEmpty()) {
            System.out.println("No songs to save.");
            return;
        }
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Save Ratings");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            for (Song s : songList) {
                bw.write(s.getArtist() + "|" + s.getTitle() + "|" + s.getRating());
                bw.newLine();
            }
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void goBack(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Main.fxml"));
            Parent root = loader.load();
            searchField.getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void searchSong(ActionEvent e) {
        String query = searchField.getText();
        if (query == null || query.isEmpty()) {
            return;
        }
        try {
            // encode the query for URL
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
            String url = "https://itunes.apple.com/search?term=" + encodedQuery + "&entity=song&limit=10";

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .build();

            java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient()
                    .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = om.readTree(response.body());
            com.fasterxml.jackson.databind.JsonNode results = root.get("results");

            searchResults.getItems().clear();
            iTunesResults.clear();
            searchResults.setCellFactory(lv -> new ListCell<String>() {
                private final ImageView imageView = new ImageView();
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        int index = getIndex();
                        if (index >= 0 && index < iTunesResults.size()) {
                            String artUrl = iTunesResults.get(index).get("artworkUrl100").asText().replace("100x100", "30x30");
                            imageView.setImage(new javafx.scene.image.Image(artUrl, true));
                            imageView.setFitHeight(30);
                            imageView.setFitWidth(30);
                        }
                        setText(item);
                        setGraphic(imageView);
                    }
                }
            });

            for (com.fasterxml.jackson.databind.JsonNode result : results) {
                String trackName = result.get("trackName").asText();
                String artist = result.get("artistName").asText();
                searchResults.getItems().add(artist + " - " + trackName);
                iTunesResults.add(result);
            }

            // when user selects a song from dropdown, show its info
            searchResults.setOnAction(event -> showSongInfo());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showSongInfo() {
        int index = searchResults.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        com.fasterxml.jackson.databind.JsonNode selected = iTunesResults.get(index);

        songTitle.setText(selected.get("trackName").asText());
        artistName.setText(selected.get("artistName").asText());
        albumName.setText(selected.get("collectionName").asText());
        releaseYear.setText(selected.get("releaseDate").asText().substring(0, 4));
        if (selected.has("primaryGenreName")) {
            genre.setText(selected.get("primaryGenreName").asText());
        }

        // load album cover — replace 100x100 with 600x600 for higher resolution
        String artUrl = selected.get("artworkUrl100").asText().replace("100x100", "600x600");
        albumCover.setImage(new javafx.scene.image.Image(artUrl, true));
    }

    public void addSelectedSong(ActionEvent e) {
        int index = searchResults.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        com.fasterxml.jackson.databind.JsonNode selected = iTunesResults.get(index);
        String artist = selected.get("artistName").asText();
        String title = selected.get("trackName").asText();
        double rating = 0.0;
        if (ratingField.getText() != null && !ratingField.getText().isEmpty()) {
            try {
                rating = Double.parseDouble(ratingField.getText());
                if (rating < 0) rating = 0;
                if (rating > 10) rating = 10;
            } catch (NumberFormatException ex) {
                rating = 0.0;
            }
        }
        String artUrl = selected.get("artworkUrl100").asText();
// check if song already exists
        boolean found = false;
        for (Song s : songList) {
            if (s.getArtist().equals(artist) && s.getTitle().equals(title)) {
                s.setRating(rating);
                s.setArtworkUrl(artUrl);
                found = true;
                break;
            }
        }
        if (!found) {
            songList.add(new Song(artist, title, rating, artUrl));
        }
        ratingField.clear();
    }
    public void addFullAlbum(ActionEvent e) {
        int index = searchResults.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        com.fasterxml.jackson.databind.JsonNode selected = iTunesResults.get(index);
        String collectionId = selected.get("collectionId").asText();

        try {
            String url = "https://itunes.apple.com/lookup?id=" + collectionId + "&entity=song";

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .build();

            java.net.http.HttpResponse<String> response = java.net.http.HttpClient.newHttpClient()
                    .send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = om.readTree(response.body());
            com.fasterxml.jackson.databind.JsonNode results = root.get("results");

            for (com.fasterxml.jackson.databind.JsonNode track : results) {
                // skip the first result which is the album itself not a song
                if (!track.has("trackName")) {
                    continue;
                }
                String artist = track.get("artistName").asText();
                String title = track.get("trackName").asText();

                // check if song already exists
                boolean found = false;
                for (Song s : songList) {
                    if (s.getArtist().equals(artist) && s.getTitle().equals(title)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    String artUrl = track.get("artworkUrl100").asText();
                    songList.add(new Song(artist, title, 0.0, artUrl));                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}