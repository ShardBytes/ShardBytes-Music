package com.shardbytes.music.client.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.shardbytes.music.client.Networking;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginDialogController{
	
	private Stage dialogStage;
	private Parent mainWindowStage;
	
	private boolean loginState;
	
	@FXML private JFXPasswordField passwordField;
	@FXML private JFXTextField usernameField;
	@FXML private JFXButton loginButton;
	@FXML private JFXButton cancelButton;
	@FXML private AnchorPane anchorPane;
	
	@FXML private void loginClicked(){
		String nickname = usernameField.getText();
		String password = passwordField.getText();
		
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setProgress(-1);
		progressIndicator.setLayoutX(124);
		progressIndicator.setLayoutY(64);
		
		anchorPane.getChildren().remove(0, 6);
		anchorPane.getChildren().add(progressIndicator);
		
		new Thread(() -> {
			try{
				boolean successful = Networking.getInstance().login(nickname, password);
				if(successful){
					setLoginState(true);
					Platform.runLater(() -> {
						mainWindowStage.setEffect(null);
						dialogStage.close();
					});
					
				}else{
					setLoginState(false);
					Platform.runLater(() -> {
						Label wrongPasswordLabel = new Label("Wrong password!");
						wrongPasswordLabel.setLayoutX(96);
						wrongPasswordLabel.setLayoutY(64);
						wrongPasswordLabel.setStyle("-fx-text-fill: #FFFFFF");
						
						JFXButton retryButton = new JFXButton("Try again");
						retryButton.setLayoutX(113);
						retryButton.setLayoutY(100);
						retryButton.setButtonType(JFXButton.ButtonType.RAISED);
						retryButton.setStyle("-fx-text-fill: #FFFFFF;" + "-fx-background-color: #3E50B4");
						retryButton.setOnAction((event -> dialogStage.close()));
						
						anchorPane.getChildren().remove(progressIndicator);
						anchorPane.getChildren().add(wrongPasswordLabel);
						anchorPane.getChildren().add(retryButton);
					});
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}).start();
		
	}
	
	@FXML private void cancelClicked(){
		System.exit(0);
	}
	
	public void registerReference(Stage loginDialogStage, Parent root){
		dialogStage = loginDialogStage;
		mainWindowStage = root;
		
	}
	
	public boolean getLoginState(){
		return loginState;
	}
	
	private void setLoginState(boolean loginState){
		this.loginState = loginState;
	}
	
}
