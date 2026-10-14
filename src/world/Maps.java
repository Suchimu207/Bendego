package world;

import static main.Terminal.mudarEstado;

import main.inventory.Inventory;
import main.Player;
import main.Shop;
import main.Title;

import manager.MapsManager;
import manager.NPCManager;
import manager.SaveManager;

import modes.arena.ArenaMode;

import util.GameState;
import util.Grapchics;
import util.Input;

import java.util.HashMap;
import java.util.Map;

import java.awt.event.KeyEvent;

import java.util.List;
import java.util.Set;

public final class Maps implements GameState{
	public static final char PAREDE = '#';
	public static final char BATALHA = '!';
	public static final char OCORRÊNCIA = '?';
	public static final char LOJA = '$';
	public static final char ARENA = 'A';
	public static final char PORTAL = '-';
	
	private static Map<String, String> mapasExistentes; 
	private static String mapaAtual, mapaInicial, mapaVerificado;
	private static int iLinha, jColuna = 0;
	private static boolean bloqueioJogador;
	
	public Maps(){
		if (mapasExistentes == null){
			mapasExistentes = MapsManager.getMapasExistentes();
		}
		
		if (Maps.mapaAtual == null && Maps.mapaInicial != null){
			Maps.mapaAtual = Maps.mapaInicial;
		}
	}
	
	// ==================== ESTADO ====================
	
	@Override
	public void atualizaEstado(){
	}
	
	@Override
	public void desenhaEstado(){
		Grapchics.limpaTela();
		
		Maps.desenhaMapa(Maps.mapaAtual, Player.getJogadorX(), Player.getJogadorY());
		Maps.desenhaInfo();
		
		Grapchics.atualizarTela();
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
			case KeyEvent.VK_SHIFT: teclaShift(); break;
			case KeyEvent.VK_E: teclaInventário(); break;
			case KeyEvent.VK_Q: teclaSalvar(); break;
			case KeyEvent.VK_ESCAPE: teclaEsc(); break;
		}
	}
	
	// ==================== TECLAS ====================
	
	private void teclaEsquerda(){
		if (!Maps.ehParede(Maps.mapaAtual, Player.getJogadorX() - 1, Player.getJogadorY())){
			Player.setJogadorX(Player.getJogadorX()-1);
        }
	}
	
	private void teclaDireita(){
		if (!Maps.ehParede(Maps.mapaAtual, Player.getJogadorX() + 1, Player.getJogadorY())){
			Player.setJogadorX(Player.getJogadorX()+1);
		}
	}
	
	private void teclaCima(){
		if (!Maps.ehParede(Maps.mapaAtual, Player.getJogadorX(), Player.getJogadorY() - 1)){
			Player.setJogadorY(Player.getJogadorY()-1);
        }
	}
	
	private void teclaBaixo(){
		if (!Maps.ehParede(Maps.mapaAtual, Player.getJogadorX(), Player.getJogadorY() + 1)){
            Player.setJogadorY(Player.getJogadorY()+1);
        }
	}
	
	private void teclaEnter(){
		if (Maps.ehEvento(Maps.mapaAtual, Player.getJogadorX(), Player.getJogadorY()) == Maps.LOJA){
			mudarEstado(new Shop());
		}
		if (Maps.ehEvento(Maps.mapaAtual, Player.getJogadorX(), Player.getJogadorY()) == Maps.ARENA){
			mudarEstado(new ArenaMode());
		}
	}
	
	private void teclaShift(){}
	
	private void teclaInventário(){
		mudarEstado(new Inventory());
	}
	
	private void teclaSalvar(){
		SaveManager.salvar();
	}
	
	private void teclaEsc(){
		mudarEstado(new Title());
	}
	
	// ==================== DESENHO ====================
	
	private static void desenhaMapa(String mapaNome, int jogadorX, int jogadorY){	
		String mapaDesenhado = mapasExistentes.get(mapaNome+".txt");
		
		if (mapaDesenhado == null){
			System.out.println(">>Nenhum mapa para desenhar.");
			return;
		}
		
		List<NPC> npcsDoMapa = NPCManager.getNPCs(mapaNome);
		
		String[] linhas = mapaDesenhado.split("\\R");
		
		for (iLinha = 0; iLinha < linhas.length; iLinha++){
			char[] caracteres = linhas[iLinha].toCharArray();
			for (jColuna = 0; jColuna < caracteres.length; jColuna++){
				char tile = linhas[iLinha].charAt(jColuna);
				
				boolean temNpcAqui = false;
				for (NPC npc : npcsDoMapa){
					if (npc.getNpcX() == jColuna && npc.getNpcY() == iLinha){
						temNpcAqui = true;
						break;
					}
				}
				
				if (jColuna == jogadorX && iLinha == jogadorY){
					Grapchics.desenhaTela('@', jogadorX, jogadorY, Grapchics.BRANCO_CLARO);
				}else if(temNpcAqui){
					Grapchics.desenhaTela((char)2, jColuna, iLinha, Grapchics.BRANCO_CLARO);
				}else{
					switch(tile){
					case Maps.PAREDE:
					Grapchics.desenhaTela('#', jColuna, iLinha, Grapchics.PRETO_CLARO);
					break;
					case '.':
					Grapchics.desenhaTela('.', jColuna, iLinha, Grapchics.PRETO_CLARO);
					break;
					case Maps.LOJA:
					Grapchics.desenhaTela('$', jColuna, iLinha, Grapchics.AMARELO_CLARO);
					break;
					case ']':
					Grapchics.desenhaTela(']', jColuna, iLinha, Grapchics.BRANCO_CLARO);
					break;
					case '[':
					Grapchics.desenhaTela('[', jColuna, iLinha, Grapchics.BRANCO_CLARO);
					break;
					case Maps.PORTAL:
					Grapchics.desenhaTela('-', jColuna, iLinha, Grapchics.CIANO_CLARO);
					break;
					case Maps.ARENA:
					Grapchics.desenhaTela('A', jColuna, iLinha, Grapchics.AMARELO_CLARO);
					break;
					case Maps.BATALHA:
					Grapchics.desenhaTela('!', jColuna, iLinha, Grapchics.AMARELO_CLARO);
					break;
					case Maps.OCORRÊNCIA:
					Grapchics.desenhaTela('?', jColuna, iLinha, Grapchics.AMARELO_CLARO);
					break;
					}
				}
			}
		}
		//===
	}
	
	private static void desenhaInfo(){
		desenhaMapaNome();
		
		Grapchics.desenhaTTF("ESC: Título",0,35, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("E: Inventário",0,36, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Q: Salvar progresso",0,37, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Enter: Interagir",0,38, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Ouro: "+Player.getOuro(),0,39, Grapchics.BRANCO_CLARO);
	}
	
	private static void desenhaMapaNome(){
		if (mapaAtual == null) return;
			
		if (mapaAtual.equalsIgnoreCase("lobby")){
			Grapchics.desenhaCentroTTF("Instituto da Guerra: Lobby",21, Grapchics.BRANCO_CLARO);
		}
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private static boolean ehParede(String mapaNome, int jogadorX, int jogadorY){
		mapaVerificado = mapasExistentes.get(mapaNome + ".txt");
		if (mapaVerificado == null){
			return true;
		}
		
		String[] linhas = mapaVerificado.split("\\R");
		
		if (jogadorY < 0 || jogadorY >= linhas.length){
			return true;
		}
		String linhaAlvo = linhas[jogadorY];
		if (jogadorX < 0 || jogadorX >= linhaAlvo.length()){
			return true;
		}
		
		return linhaAlvo.charAt(jogadorX) == Maps.PAREDE;
	}
	
	private static char ehEvento(String mapaNome, int jogadorX, int jogadorY){
		mapaVerificado = mapasExistentes.get(mapaNome + ".txt");
		if (mapaVerificado == null){
			return '.';
		}
		
		String[] linhas = mapaVerificado.split("\\R");
		
		if (jogadorY < 0 || jogadorY >= linhas.length){
			return '.';
		}
		String linhaAlvo = linhas[jogadorY];
		if (jogadorX < 0 || jogadorX >= linhaAlvo.length()){
			return '.';
		}
		
		if (linhaAlvo.charAt(jogadorX) == Maps.LOJA){
			return Maps.LOJA;
		}else if (linhaAlvo.charAt(jogadorX) == Maps.BATALHA){
			return Maps.BATALHA;
		}else if (linhaAlvo.charAt(jogadorX) == Maps.ARENA){
			return Maps.ARENA;
		}
		return '.';
	}
	
	// ==================== OUTROS ====================
	
	public static void setMapaInicial(String mapaInicial){
		Maps.mapaInicial = mapaInicial;
	}
	
	//===
}