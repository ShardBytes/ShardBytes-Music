package com.shardbytes.music.client;

import com.shardbytes.music.common.Song;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CellData{
	
	@FXML private HBox hBox;
	@FXML private Label songTitleLabel;
	@FXML private Label authorLabel;
	
	public CellData(){
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListViewCell.fxml"));
		fxmlLoader.setController(this);
		
		try{
			fxmlLoader.load();
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	public void setInfo(Song song){
		songTitleLabel.setText(song.getTitle());
		authorLabel.setText(song.getArtist());
		
	}
	
	public HBox getBox(double xSize){
		hBox.setScaleX(xSize);
		return hBox;
	}
	
}
