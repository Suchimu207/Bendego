package main;

import static main.Terminal.mudarEstado;

import main.inventory.Inventory;

import manager.SaveManager;

import util.Audio;
import util.CommandManager;
import util.GameState;
import util.Grapchics;
import util.Input;

import world.Maps;

import java.awt.event.KeyEvent;

import java.util.Set;

public final class Title implements GameState{
    private static String TITLE_NAME;
	
	public Title(){
	}
	
	// ==================== ESTADO ====================
	
	@Override
	public void atualizaEstado(){
	}
	
	@Override
	public void desenhaEstado(){
		Title.desenhaTítulo();
	}
	
	@Override
    public void recebeComando(int tecla, Set<Integer> teclasPressionadas){
		switch (tecla){
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT: teclaEsquerda(); break;
			
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT: teclaDireita(); break;
			
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP: teclaCima(); break;
			
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN: teclaBaixo(); break;
			
			case KeyEvent.VK_ENTER: teclaEnter(); break;
		}
	}
	
	// ==================== TECLAS ====================
	
	private void teclaEsquerda(){
	}
	
	private void teclaDireita(){
	}
	
	private void teclaCima(){
		Input.decrementarCursorY();
	}
	
	private void teclaBaixo(){
		Input.incrementarCursorY();
	}
	
	private void teclaEnter(){
		if (Input.getCursorY() == 1){
			Audio.tocarSom("NewGame", 0.1f);
			Player.setarJogador();
			Inventory.inicializarInventario(true);
			Grapchics.limpaTela();
			mudarEstado(new Maps());
        }else if (Input.getCursorY() == 2){
			if (SaveManager.existeSave()){
				if (SaveManager.carregar()){
					Audio.tocarSom("Confirm", 0.1f);
					Grapchics.limpaTela();
					mudarEstado(new Maps());
				}
			}
		}else if (Input.getCursorY() == 3){
			CommandManager.parar();
			System.exit(0); // Provisório.
		}
	}
	
	// ==================== DESENHO ====================
	
	private static void desenhaTítulo(){
		if (TITLE_NAME == null) return;
		
		Grapchics.limpaTela();
		
		Grapchics.desenhaCentro(TITLE_NAME, 14, Grapchics.BRANCO_CLARO);
		
		if (Input.getCursorY() >= 4 || Input.getCursorY() == 0){
			Input.setCursorY(1);	
		}else if (Input.getCursorY() < 0){
			Input.setCursorY(3);
		}
		
		if (Input.getCursorY() == 1){
			Grapchics.desenhaCentroTTF(">>Novo jogo<<", 18, Grapchics.AMARELO_CLARO);
		}else{
			Grapchics.desenhaCentroTTF("Novo jogo", 18, Grapchics.BRANCO_CLARO);
		}
		
		if (Input.getCursorY() == 2){
			Grapchics.desenhaCentroTTF(">>Continuar<<", 20, Grapchics.AMARELO_CLARO);
		}else{
			if (SaveManager.existeSave()){
				Grapchics.desenhaCentroTTF("Continuar", 20, Grapchics.BRANCO_CLARO);
			}else Grapchics.desenhaCentroTTF("Continuar", 20, Grapchics.PRETO_CLARO);
		}
		
		if (Input.getCursorY() == 3){
			Grapchics.desenhaCentroTTF(">>Sair<<     ", 22, Grapchics.AMARELO_CLARO);
		}else{
			Grapchics.desenhaCentroTTF("Sair     ", 22, Grapchics.BRANCO_CLARO);
		}
		
		Grapchics.desenhaTTF("Desenvolvido por Carlos S. Rehem.",0,39, Grapchics.BRANCO_CLARO);
		Grapchics.atualizarTela();
	}
	
	// ==================== OUTROS ====================
	
	protected static void setTITLE_NAME(String TITLE_NAME){
		Title.TITLE_NAME = TITLE_NAME;
	}
	
	//===
}