package com.shardbytes.music.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.shardbytes.music.common.Song;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerController implements Initializable{
	
	@FXML private JFXButton playButton;
	@FXML private Label songLabel;
	@FXML private Label albumLabel;
	
	@FXML private JFXTextField searchTextField;
	@FXML private JFXListView searchResultsList;
	
	@FXML private AnchorPane titleBarPane;
	
	@FXML private void playButtonClicked() throws Exception{
		System.out.println(Networking.getInstance().getAllAlbums());
	}
	
	@FXML private void doSearch() throws Exception{
		ObservableList<Song> data = FXCollections.observableArrayList();
		for(Song song : Networking.getInstance().getSongSearch(searchTextField.getText())){
			System.out.println(song);
			data.add(song);
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
		
	}
	
}
