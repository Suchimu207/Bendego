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
	
	private SubEstadosBatalha subEstadoAtual;
	private Monsters[] monstroSlotsAtivos;
	private Troop tropaCarregada;
	
	private BattlePreparation menu;
	private BattleField campoBatalha;
	private BattleTurn turno;
	
	private Monsters monstroMostrado;
	private Skills skillMostrada;
	
	private List<Effects> efeitos;
	
	private boolean vitória;
	
	public Battle(Troop tropaCarregada){
		this.subEstadoAtual = SubEstadosBatalha.PREPARO;
		this.menu = new BattlePreparation(this);
		this.turno = new BattleTurn(this);
		this.monstroSlotsAtivos = new Monsters[3];
		this.tropaCarregada = tropaCarregada;
		
		this.campoBatalha = null;
		this.vitória = false;
	}
	
	public Battle(Troop tropaCarregada, List<Effects> efeitos){
		this(tropaCarregada);
		if (efeitos == null || efeitos.size() <= 0) return;
		
		this.efeitos = efeitos;
	}
	
	private void atualizarEstadoBatalha(){
		if (subEstadoAtual == SubEstadosBatalha.CAMPO && campoBatalha != null){
			campoBatalha.processarTurno();
		}
    }
	
	// ==================== ESTADO ====================
	
	@Override
	public void atualizaEstado(){
		atualizarEstadoBatalha();
	}
	
	@Override
	public void desenhaEstado(){
		if (subEstadoAtual == null) return;
		
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
				if (campoBatalha != null){
					BattleResult.desenhaTelaVitoria(
					campoBatalha.getTropaExp(), 
					campoBatalha.getTropaOuro());
				}else BattleResult.desenhaTelaVitoria(0,0);
				break;
			case DERROTA:
				BattleResult.desenhaTelaDerrota();
				break;
		}
	}
	
	@Override
	public void recebeComando(int tecla, Set<Integer> teclasPressionadas){
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
			case KeyEvent.VK_SPACE: 
			if (subEstadoAtual == SubEstadosBatalha.CAMPO && campoBatalha != null){
				campoBatalha.ativarEspecial();
				return;
			}
			break;
			case KeyEvent.VK_SHIFT: teclaShift(); break;
			case KeyEvent.VK_E: teclaE(); break;
			case KeyEvent.VK_Q: teclaQ(); break;
		}
	}
	
	// ==================== TECLAS ====================
	
	private void teclaEsquerda(){
		Input.decrementarCursorX();
		if (campoBatalha != null && subEstadoAtual == SubEstadosBatalha.CAMPO_DETALHES){
			campoBatalha.alternarDetalhe(false);
		}
	}
	
	private void teclaDireita(){
		Input.incrementarCursorX();
		if (campoBatalha != null && subEstadoAtual == SubEstadosBatalha.CAMPO_DETALHES){
			campoBatalha.alternarDetalhe(true);
		}
	}
	
	private void teclaCima(){
		Input.decrementarCursorY();
	}
	
	private void teclaBaixo(){
		Input.incrementarCursorY();
	}
	
	private void teclaEnter(){
		if (subEstadoAtual == SubEstadosBatalha.VITORIA){
			Audio.pararMusicaFadeOut(2100);
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
	
	private void teclaShift(){
		if (subEstadoAtual == SubEstadosBatalha.CAMPO){
			if (campoBatalha != null){
				campoBatalha.recarregarEnergiaUsuário();
			}
		}
	}
	
	private void teclaE(){
		if (subEstadoAtual == SubEstadosBatalha.PREPARO){
			subEstadoAtual = null;
			Terminal.mudarEstado(Terminal.getEstadoAnterior());
		}
		
		if (subEstadoAtual == SubEstadosBatalha.CAMPO){
			if (campoBatalha != null){
				if (turno.isTurnoJogador()){
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
	
	private void teclaQ(){
		if (subEstadoAtual == SubEstadosBatalha.PREPARO){
			if (isEquipeSetada() && tropaCarregada != null){
				campoBatalha = new BattleField(this, this.monstroSlotsAtivos, this.tropaCarregada, this.efeitos, this.turno);
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
	
	private void alternarMonstroSlotsAtivos(){
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
			Input.decrementarCursorY();
		}else{
			if (primeiroVazio != -1){
				monstroSlotsAtivos[primeiroVazio] = monstroMostrado;
				Input.incrementarCursorY();
			}
		}
		
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private void reordenarMonstroSlotsAtivos(){
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
	
	protected boolean isEquipeSetada(){
		if (monstroSlotsAtivos == null) return false;
		
		for (Monsters monstro : monstroSlotsAtivos){
			if (monstro != null){
				return true;
			}
		}
		return false;
	}
	
	protected void exibirMensagemInimigo(String mensagem, Skills habilidadeUsada, String dano){
		if (campoBatalha != null){
			campoBatalha.setMensagemInimigo(mensagem, habilidadeUsada, dano);
		}
	}
	
	protected void exibirMensagemAliado(String mensagem, Skills habilidadeUsada, String dano){
		if (campoBatalha != null){
			campoBatalha.setMensagemAliado(mensagem, habilidadeUsada, dano);
		}
	}
	
	// ==================== OUTROS ====================
	
	protected Monsters[] getMonstroSlotsAtivos(){
		return monstroSlotsAtivos;
	}
	
	public Monsters[] getMonstrosAliados(){
		return monstroSlotsAtivos.clone();
	}
	
	protected Troop getTropaCarregada(){
		return this.tropaCarregada;
	}
	
	public Troop getMonstrosInimigos(){
		return this.tropaCarregada.clone();
	}
	
	protected SubEstadosBatalha getSubEstadoAtual(){
		return subEstadoAtual;
	}
	
	protected void setMonstroMostrado(Monsters monstroMostrado){
		this.monstroMostrado = monstroMostrado;
	}
	
	protected void setSubEstadoAtual(SubEstadosBatalha subEstadoAtual){
		this.subEstadoAtual = subEstadoAtual;
	}
	
	public boolean verificarVitória(){
		if (campoBatalha != null){
			this.vitória = campoBatalha.isVitóriaBatalha();
		}
		return this.vitória;
	}
	
	public boolean foiIniciada(){
		return campoBatalha != null;
	}
	
	public void resetarVitória(){
		this.vitória = false;
	}
	
	//===
}