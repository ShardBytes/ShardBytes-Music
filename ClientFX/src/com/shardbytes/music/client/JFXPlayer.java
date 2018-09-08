package com.shardbytes.music.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JFXPlayer extends Application{
	
	public static void main(String[] args){
		launch(args);
	}
	
	private Parent root;
	private Stage stage;
	private Scene scene;
	
	@Override
	public void start(Stage primaryStage) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerDesign.fxml"));
		root = loader.load();
		
		PlayerController controller = loader.getController();
		scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setTitle("ShardBytes Music");
		primaryStage.setResizable(false);
		primaryStage.show();
		
		var x = 10;
		var y = 20;
		
		var z = x + y;
		
		System.out.println(((Object)z).getClass().getName());
		System.out.println(z);
		
		stage = primaryStage;
		
	}
	
}
