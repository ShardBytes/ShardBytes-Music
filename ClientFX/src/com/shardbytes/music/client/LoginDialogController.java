package com.shardbytes.music.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginDialogController implements Initializable{
	
	private Stage dialogStage;
	private Parent mainWindowStage;
	
	@FXML private JFXPasswordField passwordField;
	@FXML private JFXTextField usernameField;
	@FXML private JFXButton loginButton;
	@FXML private JFXButton cancelButton;
	@FXML private ProgressIndicator loginIndicator;
	
	@Override
	public void initialize(URL location, ResourceBundle resources){
		loginIndicator.setOpacity(0);
	}
	
	@FXML private void loginClicked(){
		usernameField.setOpacity(0);
		passwordField.setOpacity(0);
		loginButton.setOpacity(0);
		cancelButton.setOpacity(0);
		loginIndicator.setOpacity(1);
		
		String nickname = usernameField.getText();
		String password = passwordField.getText();
		
		new Thread(() -> {
			try{
				boolean successful = Networking.getInstance().login(nickname, password);
				if(successful){
					Platform.runLater(() -> {
						dialogStage.close();
						mainWindowStage.setEffect(null);
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
	
}
