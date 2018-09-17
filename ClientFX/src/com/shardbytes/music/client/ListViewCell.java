package com.shardbytes.music.client;

import com.shardbytes.music.common.Song;
import javafx.scene.control.ListCell;

public class ListViewCell extends ListCell<Song>{
	
	@Override
	public void updateItem(Song item, boolean empty){
		super.updateItem(item, empty);
		
		if(empty){
			setStyle("-fx-background-color: #303030");
			setGraphic(null);
			return;
		}
		
		if(item != null){
			CellData data = new CellData();
			data.setInfo(item);
			
			setStyle("-fx-background-color: #303030");
			setGraphic(data.getBox());
			
		}
		
	}
	
}
