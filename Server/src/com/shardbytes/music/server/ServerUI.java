package com.shardbytes.music.server;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class ServerUI{
	
	Terminal terminal = null;
	Screen screen = null;
	TextGraphics graphics = null;
	int xSize;
	int ySize;
	
	void start(){
		try{
			terminal = new DefaultTerminalFactory().createTerminal();
			screen = new TerminalScreen(terminal);
			screen.startScreen();
			
			graphics = screen.newTextGraphics();
			xSize = screen.getTerminalSize().getColumns();
			ySize = screen.getTerminalSize().getRows();
			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
		drawUI();
		
	}
	
	void stop(){
		try{
			screen.close();
			terminal.close();
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	/**
	 * Creates the main UI.
	 */
	private void drawUI(){
		//Turn off the cursor
		screen.setCursorPosition(null);
		
		colour(0, 255, 255);
		print(2, 1, "ShardBytes music server");
		
		colour();
		box(0, 0, 26, 2);
		print(1, 4, "Connected clients:");
		
		fillColour(42, 42, 42);
		fill(1, 5, Math.round(xSize * 0.4f), Math.round(ySize * 0.90f));
		
		print(1, 5, "- 11charnickn (192.168.000.100)");
		
		redraw();
		
	}
	
	/**
	 * Compares the changes and draws them on terminal screen.
	 */
	private void redraw(){
		try{
			screen.refresh();
		}catch(IOException e){
			System.err.println(e.getMessage());
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
	
	private void fill(int x1, int y1, int x2, int y2){
		for(int x = x1; x < x2; x++){
			for(int y = y1; y < y2; y++){
				graphics.putString(x, y, " ");
				
			}
			
		}
		
	}
	
}
