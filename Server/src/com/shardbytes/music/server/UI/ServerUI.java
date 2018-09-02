package com.shardbytes.music.server.UI;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.shardbytes.music.server.Server;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public final class ServerUI{
	
	private Terminal terminal = null;
	private Screen screen = null;
	private TextGraphics graphics = null;
	private int xSize;
	private int ySize;
	private boolean render;
	private ExceptionMessage currentMessage;
	
	private static ArrayList<ExceptionMessage> errorList = new ArrayList<>();
	
	public ServerUI(){
		try{
			terminal = new DefaultTerminalFactory().createTerminal();
			terminal.enterPrivateMode();
			
			screen = new TerminalScreen(terminal);
			screen.startScreen();
			
			graphics = screen.newTextGraphics();
			xSize = screen.getTerminalSize().getColumns();
			ySize = screen.getTerminalSize().getRows();
			render = true;
			
		}catch(IOException e){
			addExceptionMessage(e.getMessage());
		}
		//TODO: Change speed before production, uses too many CPU cycles right now when displaying a exception message (darkening)!
		startRender(60);
		
	}
	
	public static void addExceptionMessage(String message){
		errorList.add(new ExceptionMessage(message));
	}
	
	public void stop(){
		render = false;
	}
	
	/**
	 * Creates the main UI.
	 */
	private void drawUI(){
		//Turn off the cursor
		screen.setCursorPosition(null);
		
		background(48, 48, 48);
		
		fillColour(48, 48, 48);
		colour(0, 255, 255);
		print(2, 1, "ShardBytes music server");
		
		colour();
		box(0, 0, 26, 2);
		print(1, 4, "Connected clients:");
		print(Math.round(xSize * 0.5f), 4, "Status:");
		
		fillColour(66, 66, 66);
		fill(1, 5, Math.round(xSize * 0.4f), Math.round(ySize * 0.9f) >= ySize - 3 ? ySize - 3 : Math.round(ySize * 0.9f));
		fill(Math.round(xSize * 0.5f), 5, Math.round(xSize * 0.8f), Math.round(ySize * 0.9f) >= ySize - 3 ? ySize - 3 : Math.round(ySize * 0.9f));
		
		print(1, 5, Server.getClients().toString());
		
		fillColour(33, 33, 33);
		fill(0, ySize - 1, xSize - 1, ySize - 1);
		print(0, ySize - 1, "Esc - Stop and exit");
		
		colour();
		fillColour();
		
	}
	
	/**
	 * Compares the changes and draws them on terminal screen.
	 */
	private void refresh(){
		try{
			screen.refresh();
		}catch(IOException e){
			addExceptionMessage(e.getMessage());
		}
		
	}
	
	/**
	 * Prints text on a given position into terminal screen.
	 * @param x x axis position
	 * @param y y axis position
	 * @param text String to print
	 */
	private void print(int x, int y, String text){
		graphics.putString(x, y, text);
	}
	
	/**
	 * Sets text foreground colour. All values must be in range of 0-255.
	 * @param r Red colour value
	 * @param g Green colour value
	 * @param b Blue colour value
	 */
	private void colour(int r, int g, int b){
		graphics.setForegroundColor(new TextColor.RGB(r, g, b));
	}
	
	/**
	 * Resets text foreground colour to terminal default.
	 */
	private void colour(){
		graphics.setForegroundColor(TextColor.ANSI.DEFAULT);
	}
	
	/**
	 * Sets fill/background colour. All values must be in range of 0-255.
	 * @param r Red colour value
	 * @param g Green colour value
	 * @param b Blue colour value
	 */
	private void fillColour(int r, int g, int b){
		graphics.setBackgroundColor(new TextColor.RGB(r, g, b));
	}
	
	/**
	 * Resets fill/background colour to terminal default.
	 */
	private void fillColour(){
		graphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
	}
	
	/**
	 * Draws a box between two points on screen.
	 * @param x1 x axis position of the first point
	 * @param y1 y axis position of the first point
	 * @param x2 x axis position of the second point
	 * @param y2 y axis position of the second point
	 */
	private void box(int x1, int y1, int x2, int y2){
		graphics.drawLine(x1, y1, x2, y1, Symbols.DOUBLE_LINE_HORIZONTAL);
		graphics.drawLine(x1, y1, x1, y2, Symbols.DOUBLE_LINE_VERTICAL);
		graphics.drawLine(x1, y2, x2, y2, Symbols.DOUBLE_LINE_HORIZONTAL);
		graphics.drawLine(x2, y1, x2, y2, Symbols.DOUBLE_LINE_VERTICAL);
		
		graphics.putString(x1, y1, String.valueOf(Symbols.DOUBLE_LINE_TOP_LEFT_CORNER));
		graphics.putString(x2, y1, String.valueOf(Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER));
		graphics.putString(x1, y2, String.valueOf(Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER));
		graphics.putString(x2, y2, String.valueOf(Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER));
		
	}
	
	/**
	 * Fills a space between two points with colour.
	 * @see ServerUI#fillColour(int, int, int)
	 * @param x1 x axis position of the first point
	 * @param y1 y axis position of the first point
	 * @param x2 x axis position of the second point
	 * @param y2 y axis position of the second point
	 */
	private void fill(int x1, int y1, int x2, int y2){
		for(int x = x1; x <= x2; x++){
			for(int y = y1; y <= y2; y++){
				graphics.putString(x, y, " ");
				
			}
			
		}
		
	}
	
	/**
	 * Clears the screen.
	 */
	private void clear(){
		screen.clear();
	}
	
	/**
	 * Checks if the screen has resized and updates the size accordingly.
	 */
	private void resize(){
		TerminalSize terminalSize = screen.getTerminalSize();
		xSize = terminalSize.getColumns();
		ySize = terminalSize.getRows();
		screen.doResizeIfNecessary();
	}
	
	/**
	 * Disposes the terminal screen.
	 */
	private void dispose(){
		try{
			screen.close();
			terminal.exitPrivateMode();
			terminal.close();
		}catch(IOException e){
			addExceptionMessage(e.getMessage());
		}
		
	}
	
	/**
	 * Sets screen background colour.
	 * @param r Red colour value
	 * @param g Green colour value
	 * @param b Blue colour value
	 */
	private void background(int r, int g, int b){
		fillColour(r, g, b);
		fill(0, 0, xSize - 1, ySize - 1);
		fillColour();
	}
	
	/**
	 * Starts rendering the UI.
	 */
	private void startRender(int framerate){
		long sleepTime = Math.round(1000.0d / framerate);
		new Thread(() -> {
			try{
				while(render){
					clear();
					resize();
					drawUI();
					drawExceptions();
					checkInput();
					refresh();
					Thread.sleep(sleepTime);
					
				}
				dispose();
				
			}catch(InterruptedException e){
				addExceptionMessage(e.getMessage());
			}
			
		}).start();
		
	}
	
	/**
	 * Darkens everything already on screen and draws a window with an error message.
	 * @param message The error message
	 */
	private void drawErrorBox(String message){
		float offset = 1.8f;
		
		for(int x = 0; x < xSize; x++){
			for(int y = 0; y < ySize; y++){
				TextCharacter character = screen.getBackCharacter(x, y);
				Color backgroundColour = character.getBackgroundColor().toColor();
				Color fontColour = character.getForegroundColor().toColor();
				
				fillColour(	Math.round(backgroundColour.getRed() / offset) < 0 ? 0 : Math.round(backgroundColour.getRed() / offset),
							Math.round(backgroundColour.getGreen() / offset) < 0 ? 0 : Math.round(backgroundColour.getGreen() / offset),
							Math.round(backgroundColour.getBlue() / offset) < 0 ? 0 : Math.round(backgroundColour.getBlue() / offset));
				colour(	Math.round(fontColour.getRed() / offset) < 0 ? 0 : Math.round(fontColour.getRed() / offset),
						Math.round(fontColour.getGreen() / offset) < 0 ? 0 : Math.round(fontColour.getGreen() / offset),
						Math.round(fontColour.getBlue() / offset) < 0 ? 0 : Math.round(fontColour.getBlue() / offset));
				
				print(x, y, String.valueOf(character.getCharacter()));
				
			}
			
		}
		
		int messageLength = message.length();
		if(message.length() % 2 == 1){
			messageLength++;
		}
		
		int halfMessageLength = messageLength / 2;
		
		fillColour(48, 48, 48);
		colour(255, 255, 255);
		
		fill(Math.round(xSize / 2) - halfMessageLength, Math.round(ySize / 2) - 1, Math.round(xSize / 2) + halfMessageLength, Math.round(ySize / 2) + 1);
		box(Math.round(xSize / 2) - halfMessageLength - 1, Math.round(ySize / 2) - 2, Math.round(xSize / 2) + halfMessageLength + 1, Math.round(ySize / 2) + 2);
		
		print(Math.round(xSize / 2) - halfMessageLength + 1, Math.round(ySize / 2) - 2, "Error:");
		print(Math.round(xSize / 2) - halfMessageLength, Math.round(ySize / 2), message);
		refresh();
		
	}
	
	/**
	 * Checks all exceptions in a queue and draws them.
	 */
	private void drawExceptions(){
		if(!errorList.isEmpty()){
			ExceptionMessage e = errorList.get(0);
			if(e.isDismissed()){
				errorList.remove(e);
				drawExceptions();
			}else{
				if(!Objects.equals(currentMessage, e)){
					currentMessage = e;
				}
				drawErrorBox(e.getMessage());
			}
			
		}
		
	}
	
	/**
	 * Checks if a key is pressed and executes an action.
	 */
	private void checkInput(){
		try{
			KeyStroke keyStroke = terminal.pollInput();
			if(currentMessage != null && keyStroke != null){
				currentMessage.dismiss();
				currentMessage = null;
				return;
			}
			
			if(keyStroke != null){
				switch(keyStroke.getKeyType()){
					case EOF:
						stop();
						break;
						
					case Escape:
						stop();
						break;
						
				}
				
			}
			
		}catch(IOException e){
			addExceptionMessage(e.getMessage());
		}
		
	}
	
	public boolean getRenderStatus(){
		return render;
	}
	
}
