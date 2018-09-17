package com.shardbytes.music.client;

import com.shardbytes.music.common.Song;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

public class CellData{
	
	@FXML private HBox hBox;
	@FXML private Label songTitleLabel;
	@FXML private Label authorLabel;
	@FXML private ImageView albumArtImage;
	
	private static HashMap<String, Image> albumArtCache = new HashMap<>(); //TODO: maybe save to file?
	//to save bandwith and shit
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
			
			if(albumArtCache.containsKey(song.getAlbum())){
				albumArtImage.setImage(albumArtCache.get(song.getAlbum()));
			}else{
				byte[] imageBytes = Networking.getInstance().getAlbumNonPrecise(song.getAlbum()).getAlbumArt();
				Image image = new Image(new ByteArrayInputStream(imageBytes));
				
				albumArtCache.put(song.getAlbum(), image);
				albumArtImage.setImage(image);
				
			}
			
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		
	}
	
	public HBox getBox(){
		return hBox;
	}
	
}
