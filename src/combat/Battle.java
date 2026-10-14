package combat;

import main.Terminal;

import bestiary.*;
import combat.*;

import combat.effects.Effects;

import main.Player;
import main.inventory.Inventory;

import manager.TroopManager;

import util.Audio;
import util.GameState;
import util.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.awt.event.KeyEvent;

public final class Battle implements GameState{
	protected enum SubEstadosBatalha{
		PREPARO("Preparo"),
		CAMPO("Campo"),
		CAMPO_DETALHES("Campo_Detalhes"),
		VITORIA("Vitória"),
		DERROTA("Derrota");
		
		private final String nome;
		
		SubEstadosBatalha(String nome){
			this.nome = nome;
		}
		
		public String getSubEstadoNome(){
			return nome;
		}
	}
	
	private static SubEstadosBatalha subEstadoAtual;
	private static Monsters[] monstroSlotsAtivos;
	private static Troop tropaCarregada;
	
	private static BattlePreparation menu;
	private static BattleField campoBatalha;

	private static Monsters monstroMostrado;
	private static Skills skillMostrada;
	
	private static List<Effects> efeitos;
	
	private static boolean vitória;
	
	public Battle(Troop tropaCarregada){
		Battle.subEstadoAtual = SubEstadosBatalha.PREPARO;
		Battle.menu = new BattlePreparation();
		Battle.monstroSlotsAtivos = new Monsters[3];
		Battle.tropaCarregada = tropaCarregada;
		
		Battle.campoBatalha = null;
		Battle.vitória = false;
	}
	
	public Battle(Troop tropaCarregada, List<Effects> efeitos){
		this(tropaCarregada);
		if (efeitos == null || efeitos.size() <= 0) return;
		
		Battle.efeitos = efeitos;
	}
	
	private static void atualizarEstadoBatalha(){
		if (subEstadoAtual == SubEstadosBatalha.CAMPO && campoBatalha != null){
			campoBatalha.processarTurno();
		}
    }
	
	// ==================== ESTADO ====================
	
	@Override
	public void atualizaEstado(){
		Battle.atualizarEstadoBatalha();
	}
	
	@Override
	public void desenhaEstado(){
		switch (subEstadoAtual){
			case PREPARO:
				menu.desenhaTelaPreparo();
				break;
			case CAMPO:
				if (campoBatalha != null){
					campoBatalha.desenhaBatalha();
				}
				break;
			case CAMPO_DETALHES:
				if (campoBatalha != null){
					campoBatalha.desenhaDetalhes();
				}
				break;
			case VITORIA:
				BattleResult.desenhaTelaVitoria();
				break;
			case DERROTA:
				BattleResult.desenhaTelaDerrota();
				break;
		}
	}
	
	@Override
	public void recebeComando(int tecla, Set<Integer> teclasPressionadas){
		if (teclasPressionadas != null && 
			teclasPressionadas.contains(KeyEvent.VK_E) &&
			teclasPressionadas.contains(KeyEvent.VK_Q)){
			
			if (subEstadoAtual == SubEstadosBatalha.CAMPO && campoBatalha != null){
				campoBatalha.ativarEspecial();
				return;
			}
		}
		
		switch (tecla){
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:teclaEsquerda(); break;
			
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT: teclaDireita(); break;
			
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP: teclaCima(); break;
			
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN: teclaBaixo(); break;
			
			case KeyEvent.VK_ENTER: teclaEnter(); break;
			case KeyEvent.VK_SHIFT: teclaShift(); break;
			case KeyEvent.VK_E: teclaE(); break;
			case KeyEvent.VK_Q: teclaQ(); break;
		}
	}
	
	// ==================== TECLAS ====================
	
	private static void teclaEsquerda(){
		Input.decrementarCursorX();
		if (campoBatalha != null && subEstadoAtual == SubEstadosBatalha.CAMPO_DETALHES){
			campoBatalha.alternarDetalhe(false);
		}
	}
	
	private static void teclaDireita(){
		Input.incrementarCursorX();
		if (campoBatalha != null && subEstadoAtual == SubEstadosBatalha.CAMPO_DETALHES){
			campoBatalha.alternarDetalhe(true);
		}
	}
	
	private static void teclaCima(){
		Input.decrementarCursorY();
	}
	
	private static void teclaBaixo(){
		Input.incrementarCursorY();
	}
	
	private static void teclaEnter(){
		if (subEstadoAtual == SubEstadosBatalha.VITORIA){
			Terminal.mudarEstado(Terminal.getEstadoAnterior());
		}
		if (subEstadoAtual == SubEstadosBatalha.DERROTA){
			Terminal.mudarEstado(Terminal.getEstadoAnterior());
		}
		
		if (subEstadoAtual == SubEstadosBatalha.PREPARO){
			alternarMonstroSlotsAtivos();
		}
		if (subEstadoAtual == SubEstadosBatalha.CAMPO){
			if (campoBatalha != null){
				campoBatalha.selecionarComandoBatalha();
			}
		}
	}
	
	private static void teclaShift(){
		if (subEstadoAtual == SubEstadosBatalha.CAMPO){
			if (campoBatalha != null){
				campoBatalha.recarregarEnergiaUsuário();
			}
		}
	}
	
	private static void teclaE(){
		if (subEstadoAtual == SubEstadosBatalha.PREPARO){
			subEstadoAtual = null;
			Terminal.mudarEstado(Terminal.getEstadoAnterior());
		}
		
		if (subEstadoAtual == SubEstadosBatalha.CAMPO){
			if (campoBatalha != null){
				if (BattleTurn.isTurnoJogador()){
					Input.resetarCursor();
					int detalheAtual = BattleField.TipoDetalhe.ALIADO.getValor();
					campoBatalha.setDetalheAtual(detalheAtual);
					subEstadoAtual = SubEstadosBatalha.CAMPO_DETALHES;
				}
			}
		}else if (subEstadoAtual == SubEstadosBatalha.CAMPO_DETALHES){
			if (campoBatalha != null){
				Input.resetarCursor();
				subEstadoAtual = SubEstadosBatalha.CAMPO;
			}
		}
	}
	
	private static void teclaQ(){
		if (subEstadoAtual == SubEstadosBatalha.PREPARO){
			if (isEquipeSetada() && tropaCarregada != null){
				campoBatalha = new BattleField(Battle.monstroSlotsAtivos, Battle.tropaCarregada, Battle.efeitos);
				subEstadoAtual = SubEstadosBatalha.CAMPO;
			}
		}
		if (subEstadoAtual == SubEstadosBatalha.CAMPO){
			if (campoBatalha != null){
				if (campoBatalha.isSelecionarAlvo()){
					Audio.tocarSom("Cancel", 0.3f);
					campoBatalha.setSelecionarAlvo(false);
				}
			}
		}
	}
	
	// ==================== AÇÕES DO JOGADOR ====================
	
	private static void alternarMonstroSlotsAtivos(){
		if (monstroMostrado == null) return;
		
		int maxSlotsAtivos = monstroSlotsAtivos.length;
		int slotExistente = -1;
		int primeiroVazio = -1;

		for (int i = 0; i <= maxSlotsAtivos-1; i++){
			if (monstroSlotsAtivos[i] == monstroMostrado){
				slotExistente = i;
			}
			if (monstroSlotsAtivos[i] == null && primeiroVazio == -1){
				primeiroVazio = i;
			}
		}
		
		if (slotExistente != -1){
			monstroSlotsAtivos[slotExistente] = null;
			reordenarMonstroSlotsAtivos();
		}else{
			if (primeiroVazio != -1){
				monstroSlotsAtivos[primeiroVazio] = monstroMostrado;
			}
		}
		
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private static void reordenarMonstroSlotsAtivos(){
		int writeIndex = 0;
		for (int i = 0; i <= monstroSlotsAtivos.length-1; i++){
			if (monstroSlotsAtivos[i] != null){
				monstroSlotsAtivos[writeIndex++] = monstroSlotsAtivos[i];
			}
		}
		while (writeIndex < monstroSlotsAtivos.length){
			monstroSlotsAtivos[writeIndex++] = null;
		}
	}
	
	protected static boolean isEquipeSetada(){
		if (monstroSlotsAtivos == null) return false;
		
		for (Monsters monstro : monstroSlotsAtivos){
			if (monstro != null){
				return true;
			}
		}
		return false;
	}
	
	protected static void exibirMensagemInimigo(String mensagem, Skills habilidadeUsada, String dano){
		if (campoBatalha != null){
			campoBatalha.setMensagemInimigo(mensagem, habilidadeUsada, dano);
		}
	}
	
	protected static void exibirMensagemAliado(String mensagem, Skills habilidadeUsada, String dano){
		if (campoBatalha != null){
			campoBatalha.setMensagemAliado(mensagem, habilidadeUsada, dano);
		}
	}
	
	// ==================== OUTROS ====================
	
	protected static Monsters[] getMonstroSlotsAtivos(){
		return monstroSlotsAtivos;
	}
	
	protected static Troop getTropaCarregada(){
		return Battle.tropaCarregada;
	}
	
	protected SubEstadosBatalha getSubEstadoAtual(){
		return subEstadoAtual;
	}
	
	protected static void setMonstroMostrado(Monsters monstroMostrado){
		Battle.monstroMostrado = monstroMostrado;
	}
	
	protected static void setSubEstadoAtual(SubEstadosBatalha subEstadoAtual){
		Battle.subEstadoAtual = subEstadoAtual;
	}
	
	public static boolean verificarVitória(){
		if (campoBatalha != null){
			Battle.vitória = campoBatalha.isVitóriaBatalha();
		}
		return Battle.vitória;
	}
	
	public static void resetarVitória(){
		Battle.vitória = false;
	}
	
	//===
}