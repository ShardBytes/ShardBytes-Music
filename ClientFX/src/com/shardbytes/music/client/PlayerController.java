package com.shardbytes.music.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PlayerController{
	
	@FXML private JFXButton playButton;
	@FXML private Label songLabel;
	@FXML private Label albumLabel;
	@FXML private JFXTextField searchTextField;
	@FXML private JFXListView searchResultsList;
	
	@FXML private void playButtonClicked(){
		
	}
	
	@FXML private void doSearch() throws Exception{
		System.out.println(Networking.getInstance().getAllSongs());
	}
	
}
