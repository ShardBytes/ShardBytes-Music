package com.shardbytes.music.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class JFXPlayer extends Application{
	
	public static void main(String[] args) throws Exception{
		Configs.getInstance().load();
		launch(args);
		Networking.getInstance().disconnect();
		System.exit(0);
	}
	
	private Parent root;
	private static Stage stage;
	private Scene scene;
	private static PlayerController controller;
	
	@Override
	public void start(Stage primaryStage) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerDesign.fxml"));
		root = loader.load();
		
		controller = loader.getController();
		scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.setTitle("ShardBytes Music");
		primaryStage.setResizable(false);
		
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(Color.TRANSPARENT);
		
		primaryStage.show();
		stage = primaryStage;
		
		createLoginDialog(primaryStage);
		
	}
	
	private void createLoginDialog(Stage primaryStage) throws IOException{
		Stage loginDialog = new Stage(StageStyle.TRANSPARENT);
		FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("LoginDialog.fxml"));
		Parent dialogRoot = dialogLoader.load();
		Scene dialogScene = new Scene(dialogRoot);
		LoginDialogController dialogController = dialogLoader.getController();
		
		ColorAdjust adjust = new ColorAdjust(0.0d, -0.9d, -0.4d, 0.0d);
		GaussianBlur blur = new GaussianBlur(10);
		adjust.setInput(blur);
		root.setEffect(adjust);
		
		loginDialog.initModality(Modality.APPLICATION_MODAL);
		loginDialog.initOwner(primaryStage);
		loginDialog.setAlwaysOnTop(true);
		loginDialog.setResizable(false);
		loginDialog.setScene(dialogScene);
		
		ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
			double stageWidth = newValue.doubleValue();
			loginDialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - stageWidth / 2);
		};
		ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
			double stageHeight = newValue.doubleValue();
			loginDialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - stageHeight / 2);
		};
		
		loginDialog.widthProperty().addListener(widthListener);
		loginDialog.heightProperty().addListener(heightListener);
		
		loginDialog.setOnShown((event -> {
			loginDialog.widthProperty().removeListener(widthListener);
			loginDialog.heightProperty().removeListener(heightListener);
		}));
		
		dialogController.registerReference(loginDialog, root);
		
		dialogScene.setFill(Color.TRANSPARENT);
		
		AtomicBoolean successfulLogin = new AtomicBoolean(false);
		loginDialog.setOnHiding((event) -> successfulLogin.set(dialogController.getLoginState()));
		
		loginDialog.showAndWait();
		
		if(!successfulLogin.get()){
			createLoginDialog(primaryStage);
		}
		
	}
	
	public static Stage getStage(){
		return stage;
	}
	
	public static PlayerController getController(){
		return controller;
	}
	
}
