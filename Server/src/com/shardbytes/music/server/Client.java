package com.shardbytes.music.server;

import com.shardbytes.music.server.Database.PasswordDB;
import com.shardbytes.music.server.Database.SongDB;
import com.shardbytes.music.server.UI.ServerUI;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Client{
	
	private Socket socket;
	private ObjectOutputStream toClient;
	private ObjectInputStream fromClient;
	private String nickname;
	private boolean connected = true;
	
	private KeyPair keyPair;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	private PublicKey clientKey;
	
	public Client(Socket clientSocket){
		socket = clientSocket;
		try{
			toClient = new ObjectOutputStream(socket.getOutputStream());
			fromClient = new ObjectInputStream(socket.getInputStream());
			nickname = socket.getInetAddress().getHostAddress();
			
			keyPair = buildKeyPair();
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
			
			clientKey = getClientPublicKey();
			send(publicKey);
			
			String name = reconstructObject(decrypt(clientKey, getMessage()), String.class);
			char[] password = reconstructObject(decrypt(clientKey, getMessage()), String.class).toCharArray();
			
			if(PasswordDB.getInstance().auth(name, password)){
				send(encrypt(privateKey, true));
			}else{
				send(encrypt(privateKey, false));
				connected = false;
			}
			
		}catch(Exception e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
	}
	
	Client process(){
		if(connected){
			try{
				ServerUI.log(nickname + " connected.");
				
				while(connected){
					byte command = ((Integer)fromClient.readObject()).byteValue();
					processCommand(command);
					
				}
				
			}catch(IOException | ClassNotFoundException e){
				ServerUI.addExceptionMessage(e.getMessage());
			}
			
		}
		return this;
		
	}
	
	private void processCommand(byte command){
		switch(command){
			case 0:
				ServerUI.log("0");
				break;
				
			case 1:
				ServerUI.log(nickname + " requested a song list. (1)");
				send(SongDB.getInstance().getSongList());
				break;
				
			case 2:
				ServerUI.log(nickname + " requested a album list. (2)");
				send(SongDB.getInstance().getAlbumList());
				break;
				
			case 3:
				ServerUI.log(nickname + " requested an album. (3)");
				try{
					String albumTitle = (String)fromClient.readObject();
					String albumAuthor = (String)fromClient.readObject();
					send(SongDB.getInstance().getAlbum(albumTitle, albumAuthor));
				}catch(IOException | ClassNotFoundException e){
					ServerUI.addExceptionMessage(e.getMessage());
				}
				break;
				
			case 4:
				ServerUI.log(nickname + " requested a song. (4)");
				try{
					String author = (String)fromClient.readObject();
					String album = (String)fromClient.readObject();
					String title = (String)fromClient.readObject();
					send(Files.readAllBytes(SongDB.getInstance().getSong(author, album, title).getFile().toPath()));
					
				}catch(IOException | ClassNotFoundException e){
					ServerUI.addExceptionMessage(e.getMessage());
				}
				
			case 60:
				connected = false;
				ServerUI.log(nickname + " disconnected. (60)");
				break;
		}
		
	}
	
	private PublicKey getClientPublicKey(){
		try{
			return (PublicKey)fromClient.readObject();
		}catch(IOException | ClassNotFoundException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		return null;
		
	}
	
	private KeyPair buildKeyPair() throws NoSuchAlgorithmException{
		final int keySize = 2048;
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keySize);
		return keyPairGenerator.genKeyPair();
		
	}
	
	private byte[] encrypt(PrivateKey privateKey, Object message) throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(message);
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		
		return cipher.doFinal(byteArrayOutputStream.toByteArray());
		
	}
	
	private byte[] decrypt(PublicKey publicKey, byte[] encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		
		return cipher.doFinal(encryptedData);
		
	}
	
	private <Type> Type reconstructObject(byte[] rawData, Class<Type> typeClass) throws IOException, ClassNotFoundException{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		
		return typeClass.cast(objectInputStream.readObject());
		
	}
	
	private void send(Object o){
		try{
			toClient.writeObject(o);
		}catch(IOException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		
	}
	
	private byte[] getMessage(){
		try{
			return (byte[])fromClient.readObject();
		}catch(IOException | ClassNotFoundException e){
			ServerUI.addExceptionMessage(e.getMessage());
		}
		return null;
		
	}
	
	@Override
	public String toString(){
		return "- " + socket.getInetAddress().getHostAddress() + " (" + nickname + ")";
	}
	
}
