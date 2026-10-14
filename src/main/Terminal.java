package main;

import util.Debug;
import util.GameState;
import util.Grapchics;
import util.Input;

import world.Maps;

import asciiPanel.AsciiTTFFont;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.HashSet;
import java.util.Set;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

public final class Terminal implements KeyListener {	
    private final JFrame frame;
	private static GameState estadoAtual, estadoAnterior;
	
	private final String TITLE;
	
	private final Set<Integer> teclasPressionadas = new HashSet<>();
	
	protected Terminal(String TITLE, String mapaInicial){
		this.TITLE = TITLE;
		Maps.setMapaInicial(mapaInicial);
		frame = new JFrame(TITLE);
		
		setarFonte();
		setarJogo();
		setarJanela();
	}
	
	public static void mudarEstado(GameState novoEstado){		
        Grapchics.limpaTela();
		Input.resetarCursor();
        estadoAtual = novoEstado;
    }
	
	public static void setEstadoAnterior(GameState novoEstado){
		estadoAnterior = novoEstado;
	}
	
	public static GameState getEstadoAnterior(){
		GameState estado = Terminal.estadoAnterior;
		Terminal.estadoAnterior = null;
		return estado;
	}
	
	// ==================== INICIALIZAÇÃO ====================
	
	private void setarFonte(){
		Font fontePadrao;
		Font fonteItalico;
		
		try{
			Path caminho = Paths.get("data", "font", "mac's Minecraft.ttf");
			
			AsciiTTFFont ttf = AsciiTTFFont.loadFromFile(caminho.toString(), 0, 0);
			
			fontePadrao = ttf.getFont();
			fontePadrao = new Font("mac's Minecraft", Font.BOLD, 14);
			
			fonteItalico = fontePadrao.deriveFont(Font.ITALIC, 14);
			System.out.println(">>Fonte carregada com sucesso.");
			System.out.println("");
		}catch (Exception e){
			System.err.println(">>Erro ao carregar fonte TTF: " + e.getMessage());
			System.out.println("");
			System.err.println(">>Usando fonte fallback (Monospaced).");
			
			fontePadrao = new Font("Monospaced", Font.PLAIN, 14);
			fonteItalico = new Font("Monospaced", Font.ITALIC, 14);
		}
		
		Grapchics.setFontePadrao(fontePadrao);
		Grapchics.setFonteItalico(fonteItalico);
	}
	
	private void setarJogo(){
		mudarEstado(new Title());
		Title.setTITLE_NAME(TITLE);
		
		Shop.inicializarLoja();
	}
	
	private void setarJanela(){
		frame.add(Grapchics.getTela());
        frame.setResizable(false);  
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setLocationRelativeTo(null);
		
		frame.addKeyListener(this);
        frame.setFocusable(true);     
        frame.toFront();
		frame.requestFocus();
		
		frame.setVisible(true);
	}
	
	// ==================== ESTADO ====================
	
	protected void desenhaEstado(){	
		if (estadoAtual != null){
			estadoAtual.desenhaEstado();
		}
	}
	
	protected void atualizaEstado(){	
		if (estadoAtual != null){
			estadoAtual.atualizaEstado();
		}
	}
	
	// ==================== TECLAS ====================
	
	@Override
	public void keyPressed(KeyEvent e){
		int tecla = e.getKeyCode();
		boolean teclaRepetida = teclasPressionadas.contains(tecla);
		teclasPressionadas.add(tecla);
		
		switch (tecla){
			case KeyEvent.VK_ALT:
			Debug.setAtivaTelaDebug(!Debug.isAtivaTelaDebug());
			break;
		}
		
		if (teclaRepetida) return;
		
		if (estadoAtual != null){
			estadoAtual.recebeComando(tecla, teclasPressionadas);
		}
	}
	
	@Override
    public void keyReleased(KeyEvent e){
        teclasPressionadas.remove(e.getKeyCode());
	}
    
    @Override
    public void keyTyped(KeyEvent e) {}
	
	//===
}