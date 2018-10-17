package com.shardbytes.music.server;

import com.shardbytes.music.common.DecompressedData;
import com.shardbytes.music.common.Song;
import com.shardbytes.music.server.Database.PasswordDB;
import com.shardbytes.music.server.Database.SongDB;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import static com.shardbytes.music.server.UI.ServerUI.log;
import static com.shardbytes.music.server.UI.ServerUI.addExceptionMessage;

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
			
			String name = reconstructObject(decrypt(privateKey, getMessage()), String.class);
			char[] password = reconstructObject(decrypt(privateKey, getMessage()), String.class).toCharArray();
			
			if(PasswordDB.getInstance().auth(name, password)){
				send(encrypt(clientKey, true));
			}else{
				send(encrypt(clientKey, false));
				connected = false;
			}
			
			
			
		}catch(Exception e){
			addExceptionMessage(e.getMessage());
		}
		
	}
	
	Client process(){
		if(connected){
			try{
				log(nickname + " connected.");
				
				while(connected){
					byte command = reconstructObject(decrypt(privateKey, getMessage()), Integer.class).byteValue();
					processCommand(command);
					
				}
				
			}catch(IOException | ClassNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | UnsupportedAudioFileException e){
				e.printStackTrace();
				addExceptionMessage(e.getMessage());
			}
			
		}
		return this;
		
	}
	
	private void processCommand(byte command) throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ClassNotFoundException, UnsupportedAudioFileException{
		switch(command){
			case 0:{
				log("0");
				
				break;
				
			}
			
			case 1:{
				log(nickname + " requested a song list. (1)");
				
				KeyGenerator generator = KeyGenerator.getInstance("AES");
				generator.init(128);
				SecretKey secKey = generator.generateKey();
				
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] ivBytes = new byte[16];
				secureRandom.nextBytes(ivBytes);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
				send(encrypt(clientKey, ivBytes));
				send(encrypt(clientKey, secKey.getEncoded()));
				
				send(encryptAES(secKey, ivParameterSpec, SongDB.getInstance().getSongList()));
				
				break;
				
			}
			
			case 2:{
				log(nickname + " requested a album list. (2)");
				
				KeyGenerator generator = KeyGenerator.getInstance("AES");
				generator.init(128);
				SecretKey secKey = generator.generateKey();
				
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] ivBytes = new byte[16];
				secureRandom.nextBytes(ivBytes);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
				send(encrypt(clientKey, ivBytes));
				send(encrypt(clientKey, secKey.getEncoded()));
				
				send(encryptAES(secKey, ivParameterSpec, SongDB.getInstance().getAlbumList()));
				
				break;
				
			}
			
			case 3:{
				log(nickname + " requested an album using non-precise method. (3)");
				
				KeyGenerator generator = KeyGenerator.getInstance("AES");
				generator.init(128);
				SecretKey secKey = generator.generateKey();
				
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] ivBytes = new byte[16];
				secureRandom.nextBytes(ivBytes);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
				send(encrypt(clientKey, ivBytes));
				send(encrypt(clientKey, secKey.getEncoded()));
				
				String title = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);
				
				send(encryptAES(secKey, ivParameterSpec, SongDB.getInstance().getAlbumNonPrecise(title)));
				
				break;
				
			}
			
			case 4:{
				log(nickname + " requested a song. (4)");
				
				KeyGenerator generator = KeyGenerator.getInstance("AES");
				generator.init(128);
				SecretKey secKey = generator.generateKey();
				
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] ivBytes = new byte[16];
				secureRandom.nextBytes(ivBytes);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
				send(encrypt(clientKey, ivBytes));
				send(encrypt(clientKey, secKey.getEncoded()));

				String artist = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);
				String album = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);
				String title = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);

				Song song = SongDB.getInstance().getSong(artist, album, title);
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(song.getFile());
				AudioFormat audioFormat = inputStream.getFormat();

				DecompressedData songData = SongDB.decompressSongToPCM(Files.readAllBytes(song.getFile().toPath()), audioFormat);

				send(encryptAES(secKey, ivParameterSpec, songData));

				inputStream.close();
				break;
				
			}
			
			case 5:{
				log(nickname + " searched for a song. (5)");
				
				KeyGenerator generator = KeyGenerator.getInstance("AES");
				generator.init(128);
				SecretKey secKey = generator.generateKey();
				
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] ivBytes = new byte[16];
				secureRandom.nextBytes(ivBytes);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
				send(encrypt(clientKey, ivBytes));
				send(encrypt(clientKey, secKey.getEncoded()));
				
				String searchString = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);
				
				send(encryptAES(secKey, ivParameterSpec, SongDB.getInstance().doSongSearch(searchString, 10)));
				
				break;
				
			}
			
			case 6:{
				log(nickname + " requested a song stream. (6)");
				
				KeyGenerator generator = KeyGenerator.getInstance("AES");
				generator.init(128);
				SecretKey secKey = generator.generateKey();
				
				SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
				byte[] ivBytes = new byte[16];
				secureRandom.nextBytes(ivBytes);
				IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
				send(encrypt(clientKey, ivBytes));
				send(encrypt(clientKey, secKey.getEncoded()));
				
				String artist = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);
				String album = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);
				String title = reconstructObject(decryptAES(secKey, ivParameterSpec, getMessage()), String.class);
				
				byte[] songBytes = Files.readAllBytes(SongDB.getInstance().getSong(artist, album, title).getFile().toPath());
				
				Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				aesCipher.init(Cipher.ENCRYPT_MODE, secKey, ivParameterSpec);
				
				try{
					CipherOutputStream stream = new CipherOutputStream(socket.getOutputStream(), aesCipher);
					stream.write(songBytes);
					stream.flush();
				}catch(Exception e){
					log("Audio stream to " + nickname + " closed.");
					log(socket.toString());
				}
				
				break;
				
			}
			
			case 60:{
				connected = false;
				log(nickname + " disconnected. (60)");
				
				break;
				
			}
			
		}
		
	}
	
	private PublicKey getClientPublicKey(){
		try{
			return (PublicKey)fromClient.readObject();
		}catch(IOException | ClassNotFoundException e){
			addExceptionMessage(e.getMessage());
		}
		return null;
		
	}
	
	private SecretKey getClientAESKey(){
		try{
			byte[] decryptedKey = decrypt(privateKey, getMessage());
			return new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
		}catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
			addExceptionMessage(e.getMessage());
		}
		return null;
		
	}
	
	private KeyPair buildKeyPair() throws NoSuchAlgorithmException{
		final int keySize = 2048;
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keySize);
		return keyPairGenerator.genKeyPair();
		
	}
	
	private byte[] encrypt(PublicKey otherSidePublicKey, Object message) throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(message);
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, otherSidePublicKey);
		
		return cipher.doFinal(byteArrayOutputStream.toByteArray());
		
	}
	
	private byte[] decrypt(PrivateKey privateKey, byte[] encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		return cipher.doFinal(encryptedData);
		
	}
	
	private byte[] encryptAES(SecretKey secKey, IvParameterSpec iv, Object message) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(message);
		
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.ENCRYPT_MODE, secKey, iv);
		
		return aesCipher.doFinal(byteArrayOutputStream.toByteArray());
		
	}
	
	private byte[] decryptAES(SecretKey originalKey, IvParameterSpec iv, byte[] encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException{
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.DECRYPT_MODE, originalKey, iv);
		
		return aesCipher.doFinal(encryptedData);
		
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
			addExceptionMessage(e.getMessage());
		}
		
	}
	
	private byte[] getMessage(){
		try{
			return (byte[])fromClient.readObject();
		}catch(IOException | ClassNotFoundException e){
			addExceptionMessage(e.getMessage());
		}
		return null;
		
	}
	
	@Override
	public String toString(){
		return "- " + socket.getInetAddress().getHostAddress() + " (" + nickname + ")";
	}
	
}
