package com.shardbytes.music.client;

import com.shardbytes.music.common.Song;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client{
	
	private Socket socket;
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	
	//Swing
	private JTextField nicknameField;
	
	public static void main(String[] args){
		
		new Client().startGUI();
		
	}
	
	/**
	 * Quick, dirty and temporary Swing UI for better control
	 */
	private void startGUI(){
		JFrame frame = new JFrame("ShardBytes Music client");
		frame.setSize(800, 600);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton connectButton = new JButton("Connect to server");
		connectButton.addActionListener(this::connectToServer);
		frame.getContentPane().add(connectButton);
		
		JButton disconnectButton = new JButton("Disconnect from server");
		disconnectButton.addActionListener(this::disconnectFromServer);
		frame.getContentPane().add(disconnectButton);
		
		JLabel nicklabel = new JLabel("Nickname:");
		frame.getContentPane().add(nicklabel);
		
		nicknameField = new JTextField("");
		nicknameField.setPreferredSize(new Dimension(200, nicknameField.getPreferredSize().height));
		frame.getContentPane().add(nicknameField);
		
		JButton send0 = new JButton("Send 0");
		send0.addActionListener((e) -> sendMessage(0));
		frame.getContentPane().add(send0);
		
		JButton send1 = new JButton("Send 1");
		send1.addActionListener((e) -> {
			sendMessage(1);
			System.out.println(getSongList());
		});
		frame.getContentPane().add(send1);
		
		frame.setVisible(true);
	}
	
	private void connectToServer(ActionEvent event){
		try{
			socket = new Socket("192.168.100.166", 8192);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
			
			String message = getMessage();
			if(message.equals("getNickname")){
				sendMessage(nicknameField.getText());
			}
			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	private void disconnectFromServer(ActionEvent event){
		sendMessage(60);
	}
	
	private String getMessage(){
		try{
			return (String)fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	private ArrayList<Song> getSongList(){
		try{
			return (ArrayList<Song>)fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	private void sendMessage(Object message){
		try{
			toServer.writeObject(message);
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
}
