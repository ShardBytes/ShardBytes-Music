package com.shardbytes.music.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.shardbytes.music.AudioPlayer;
import com.shardbytes.music.common.Song;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable{
	
	@FXML private JFXButton playButton;
	@FXML private Label songLabel;
	@FXML private Label albumLabel;
	
	@FXML private JFXTextField searchTextField;
	@FXML private JFXListView searchResultsList;
	@FXML private Label searchResultsLabel;
	
	@FXML private AnchorPane titleBarPane;
	@FXML private JFXTabPane tabPane;
	
	@FXML private void playButtonClicked() throws Exception{
		new Thread(() -> {
			try{
				AudioPlayer.getInstance().pause();
			}catch(JavaLayerException e){
				System.err.println(e.getMessage());
			}
			
		}).start();
		
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
						
						byte[] songBytes = Networking.getInstance().getSongBytes(artist, album, title);
						
						AudioPlayer player = AudioPlayer.getInstance();
						player.preloadAsBytes(new ByteArrayInputStream(songBytes), selected);
						player.play();
						
					}catch(Exception e){
						System.err.println(e.getMessage());
					}
					
				}).start();
				
				tabPane.getSelectionModel().select(0);
				
			}
			
		});
		
	}
	
	public void setSongData(Song song){
		Platform.runLater(() -> {
			songLabel.setText(song.getTitle());
			albumLabel.setText(song.getArtist() + " - " + song.getAlbum());
		});
		
	}
	
}
