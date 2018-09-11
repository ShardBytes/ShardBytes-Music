package com.shardbytes.music.client;

import com.shardbytes.music.common.Album;
import com.shardbytes.music.common.Song;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Networking{
	
	private static Networking instance = new Networking();
	private Networking(){}
	
	private Socket socket;
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	
	private KeyPair keyPair;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	private PublicKey serverKey;
	
	boolean login(String name, String password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException{
		socket = new Socket("192.168.100.166", 8192);
		toServer = new ObjectOutputStream(socket.getOutputStream());
		fromServer = new ObjectInputStream(socket.getInputStream());
		
		keyPair = buildKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();
		
		send(publicKey);
		serverKey = getServerPublicKey();
		
		send(encrypt(privateKey, name));
		send(encrypt(privateKey, password));
		
		return reconstructObject(decrypt(serverKey, getMessage()), Boolean.class);
		
	}
	
	ArrayList<Album> getAllAlbums() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException, ClassNotFoundException, InvalidAlgorithmParameterException{
		if(socket != null){
			send(encrypt(privateKey, 2));
			
			byte[] ivBytes = reconstructObject(decrypt(serverKey, getMessage()), byte[].class);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
			SecretKey secKey = getServerAESKey();
			return reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), ArrayList.class);
			
		}
		return null;
		
	}
	
	ArrayList<Song> getAllSongs() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException, ClassNotFoundException, InvalidAlgorithmParameterException{
		if(socket != null){
			send(encrypt(privateKey, 1));
			
			byte[] ivBytes = reconstructObject(decrypt(serverKey, getMessage()), byte[].class);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
			SecretKey secKey = getServerAESKey();
			return reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), ArrayList.class);
			
		}
		return null;
		
	}
	
	private void send(Object message){
		try{
			toServer.writeObject(message);
		}catch(IOException e){
			System.err.println(e.getMessage()); //TODO: Better error handling system!
		}
		
	}
	
	private byte[] getMessage(){
		try{
			return (byte[])fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		return null;
		
	}
	
	private PublicKey getServerPublicKey(){
		try{
			return (PublicKey)fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		return null;
		
	}
	
	private SecretKey getServerAESKey(){
		try{
			byte[] decryptedKey =  reconstructObject(decrypt(serverKey, getMessage()), byte[].class);
			return new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
		}catch(IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
			System.err.println(e.getMessage());
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
	
	private byte[] encryptAES(SecretKey secKey, IvParameterSpec ivParameterSpec, Object message) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(message);
		
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.ENCRYPT_MODE, secKey, ivParameterSpec);
		
		return aesCipher.doFinal(byteArrayOutputStream.toByteArray());
		
	}
	
	private byte[] decryptAES(SecretKey originalKey, IvParameterSpec ivParameterSpec, byte[] encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException{
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.DECRYPT_MODE, originalKey, ivParameterSpec);
		
		return aesCipher.doFinal(encryptedData);
		
	}
	
	private <Type> Type reconstructObject(byte[] rawData, Class<Type> typeClass) throws IOException, ClassNotFoundException{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawData);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		
		return typeClass.cast(objectInputStream.readObject());
		
	}
	
	public static Networking getInstance(){
		return instance;
	}
	
}
