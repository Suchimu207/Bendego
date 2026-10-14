package main;

import combat.Battle;

import manager.*;

import util.CommandManager;
import util.Debug;

import save.Version;

import world.Maps;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;

import java.io.FileReader;
import java.io.IOException;

import java.nio.file.*; 

import javax.swing.*;
import java.awt.*;

import org.json.JSONObject;

/*
* @author Carlos S. Rehem
*/

public final class Main {
	private final static class Game {
		private static Path caminho;
		private static String conteudoJson;
		private static JSONObject gameJson;
		
		private static void carregarGameJson(){
			try{
				caminho = Paths.get("data", "system", "game.json");
				conteudoJson = Files.readString(caminho);
				gameJson = new JSONObject(conteudoJson);
			}catch(IOException e){
				System.out.println("Erro ao carregar game.json: "+e.getMessage());
				System.exit(1);
			}
		}
	}
	
	static{Game.carregarGameJson();}
	private final static Version versãoAtual = new Version(Game.gameJson.getString("version"));
	
    public static void main(String[] args){
		Debug.limpaPrompt();
		
		String mapaInicial = "Lobby";
		boolean rodandoJogo = true;
		boolean modoDebug = false;
		final String VERSION = Game.gameJson.getString("version");
		final String TITLE = Game.gameJson.getString("title");
		final String FULL_TITLE = TITLE+" - "+VERSION;
		
		DataManager.carregarDados();
		
		Terminal terminal = new Terminal(FULL_TITLE, mapaInicial);
		
		// =======================================
		
        for (String arg : args){
            if (arg.equalsIgnoreCase("--debug") || arg.equalsIgnoreCase("-d")){
                modoDebug = true;
                break;
            }
        }
		if (modoDebug) CommandManager.iniciar();	
		
		// =======================================
		
		Thread jogo = new Thread(new Runnable(){
			public void run(){
				// 60 FPS == 16.66666 MS.
				final long TARGET_TIME = 1000000000 / 60; 
				
				long tempoUltimoCalculoFPS = System.nanoTime();
				long tempoInicioFrame = 0;
				int contadorFrames = 0;

				try {
					while(rodandoJogo){
						tempoInicioFrame = System.nanoTime();
						
						terminal.atualizaEstado();
						terminal.desenhaEstado();
						
						//Contador de FPS.
						contadorFrames++;
						if (System.nanoTime() - tempoUltimoCalculoFPS >= 1000000000){
							Debug.mostrarTelaDebug(contadorFrames);
							contadorFrames = 0;
							tempoUltimoCalculoFPS = System.nanoTime();
						}
						
						// Quanto tempo o processamento acima demorou.
						long tempoGasto = System.nanoTime() - tempoInicioFrame;
						
						// Calcula quanto tempo resta para completar o frame de 60 FPS.
						long tempoRestanteSleep = TARGET_TIME - tempoGasto;
						
						// Se ainda sobrou tempo, a thread dorme apenas o necessário.
						if (tempoRestanteSleep > 0) {
							// Converte nanosegundos de volta para milissegundos e nanosegundos residuais.
							Thread.sleep(tempoRestanteSleep / 1000000, (int) (tempoRestanteSleep % 1000000));
						}
					}
				}catch (InterruptedException e){
					Thread.currentThread().interrupt();
					System.out.println("Thread interrompida.");
				}
			}
		});
		jogo.start();
		
		//===
    }
	
	public static Version getGameVersion(){
		return Main.versãoAtual;
	}
}