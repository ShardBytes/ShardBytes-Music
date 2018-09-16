package com.shardbytes.music.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.shardbytes.music.common.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerController{
	
	@FXML private JFXButton playButton;
	@FXML private Label songLabel;
	@FXML private Label albumLabel;
	@FXML private JFXTextField searchTextField;
	@FXML private JFXListView searchResultsList;
	
	@FXML private void playButtonClicked() throws Exception{
		System.out.println(Networking.getInstance().getAllAlbums());
	}
	
	@FXML private void doSearch() throws Exception{
		ObservableList<Song> data = FXCollections.observableArrayList();
		for(Song song : Networking.getInstance().getAllSongs()){
			data.add(song);
		}
		
		searchResultsList.setCellFactory(listView -> new ListViewCell());
		searchResultsList.setItems(data);
		
	}
	
}
