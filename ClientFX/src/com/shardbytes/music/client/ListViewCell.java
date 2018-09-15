package com.shardbytes.music.client;

import com.shardbytes.music.common.Song;
import javafx.application.Platform;
import javafx.scene.control.ListCell;

public class ListViewCell extends ListCell<Song>{
	
	@Override
	public void updateItem(Song item, boolean empty){
		super.updateItem(item, empty);
		
		if(item != null){
			CellData data = new CellData();
			data.setInfo(item);
			
			//setStyle("-fx-padding: 5px;");
			setGraphic(data.getBox(JFXPlayer.getController().getList().getLayoutX() - 2));
			//TODO: AHHHHH
			
		}
		
	}
	
}
