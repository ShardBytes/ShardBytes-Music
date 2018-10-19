package com.shardbytes.music.client.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.shardbytes.music.client.audio.AudioPlayer;
import com.shardbytes.music.client.technicalUI.AlbumArtCache;
import com.shardbytes.music.client.technicalUI.JFXPlayer;
import com.shardbytes.music.client.technicalUI.ListViewCell;
import com.shardbytes.music.client.Networking;
import com.shardbytes.music.client.Vector2;
import com.shardbytes.music.common.Song;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable{
	
	//Main screen main controls
	@FXML private JFXButton playButton;
	@FXML private Label songLabel;
	@FXML private Label albumLabel;
	@FXML private ImageView albumArt;
	
	//Song position controls
	@FXML private Label timeCurrent;
	@FXML private Label timeLeft;
	@FXML private JFXSlider timeSlider;
	
	//Server search screen controls
	@FXML private JFXTextField searchTextField;
	@FXML private JFXListView searchResultsList;
	@FXML private Label searchResultsLabel;
	
	//Custom title bar
	@FXML private AnchorPane titleBarPane;
	@FXML private JFXTabPane tabPane;
	
	@FXML private void playButtonClicked(){
		AudioPlayer.getInstance().pause();
	}
	
	@FXML private void doSearch() throws Exception{
		/*
		 * Do not even bother with searching when user entered nothing
		 */
		if(searchTextField.getText().isEmpty()){
			searchResultsLabel.setText("Try searching or...");
			searchResultsList.setVisible(false);
			
			ObservableList<Song> data = FXCollections.observableArrayList();
			searchResultsList.setCellFactory(listView -> new ListViewCell());
			searchResultsList.setItems(data);
			
			return;
			
		}
		
		ObservableList<Song> data = FXCollections.observableArrayList();
		for(Song song : Networking.getInstance().getSongSearch(searchTextField.getText())){
			data.add(song);
		}
		
		if(data.size() != 0){
			searchResultsList.setVisible(true);
		}else{
			searchResultsList.setVisible(false);
			searchResultsLabel.setText("Nothing found! But you can still...");
		}
		
		searchResultsList.setCellFactory(listView -> new ListViewCell());
		searchResultsList.setItems(data);
		
	}
	
	/**
	 * Close button action
	 */
	@FXML private void closeButtonClicked(){
		Platform.exit();
	}
	
	/**
	 * Minimize/Iconify button action
	 */
	@FXML private void iconifyButtonClicked(){
		JFXPlayer.getStage().setIconified(true);
	}
	
	/**
	 * Window drag handling
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources){
		final Vector2 deltaVector = new Vector2();
		
		titleBarPane.setOnMousePressed(mouseEvent -> {
			Stage windowStage = JFXPlayer.getStage();
			deltaVector.x = windowStage.getX() - mouseEvent.getScreenX();
			deltaVector.y = windowStage.getY() - mouseEvent.getScreenY();
			
		});
		
		titleBarPane.setOnMouseDragged(mouseEvent -> {
			Stage windowStage = JFXPlayer.getStage();
			windowStage.setX(mouseEvent.getScreenX() + deltaVector.x);
			windowStage.setY(mouseEvent.getScreenY() + deltaVector.y);
			
		});
		
		searchResultsList.setOnMouseClicked(event -> {
			if(event.getClickCount() == 2){
				new Thread(() -> {
					try{
						Song selected = (Song)searchResultsList.getSelectionModel().getSelectedItem();
						String artist = selected.getArtist();
						String album = selected.getAlbum();
						String title = selected.getTitle();
						
						SimpleDoubleProperty simpleDoubleProperty = new SimpleDoubleProperty();
						simpleDoubleProperty.addListener((observable, oldValue, newValue) -> {
							timeSlider.setValue(newValue.doubleValue());
						});
						
						AudioPlayer player = AudioPlayer.getInstance();
						player.load(Networking.getInstance().getSongBytes(artist, album, title, simpleDoubleProperty));
						player.play();
						
					}catch(Exception e){
						System.err.println(e.getMessage());
					}
					
				}).start();
				
				tabPane.getSelectionModel().select(0);
				
			}
			
		});
		
	}
	
	/**
	 * Sets album art, title, album and artist name on the player screen
	 * @param song Song object to read data from
	 */
	public void setSongData(Song song){
		Platform.runLater(() -> {
			String album = song.getAlbum();
			
			songLabel.setText(song.getTitle());
			albumLabel.setText(song.getArtist() + " - " + album);
			albumArt.setImage(AlbumArtCache.getImage(album));
			
		});
		
	}
	
	public void setLoading(){
		
	}
	
	public void setTime(int time){
		
	}
	
}
