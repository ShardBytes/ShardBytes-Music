package com.shardbytes.music.client;

import com.shardbytes.music.common.Song;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CellData{
	
	@FXML private HBox hBox;
	@FXML private Label songTitleLabel;
	@FXML private Label authorLabel;
	@FXML private ImageView albumArtImage;
	
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
		try{
			songTitleLabel.setText(song.getTitle());
			authorLabel.setText(song.getArtist());
			
			albumArtImage.setImage(AlbumArtCache.getImage(song.getAlbum()));
			
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		
	}
	
	public HBox getBox(){
		return hBox;
	}
	
}
