package com.shardbytes.music.client;

import com.oracle.tools.packager.Log;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.JOptionPane;
import java.io.IOException;

public class JFXPlayer extends Application{
	
	public static void main(String[] args){
		launch(args);
		System.exit(0);
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
		loginDialog.showAndWait();
		
	}
	
}
