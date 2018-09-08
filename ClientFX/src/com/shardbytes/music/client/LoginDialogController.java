package com.shardbytes.music.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class LoginDialogController{
	
	@FXML private JFXPasswordField passwordField;
	@FXML private JFXTextField usernameField;
	@FXML private JFXButton loginButton;
	@FXML private JFXButton cancelButton;
	
	@FXML private void loginClicked(){
		String nickname = usernameField.getText();
		String password = passwordField.getText();
	}
	
	@FXML private void cancelClicked(){
		Platform.exit();
	}
	
}
