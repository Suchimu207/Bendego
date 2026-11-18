package modes.assembly;

import main.Terminal;

import bestiary.*;
import combat.Battle;

import main.inventory.Inventory;
import main.Player;
import main.Shop;

import manager.MonstersManager;
import manager.TroopManager;

import util.Audio;
import util.GameState;
import util.Grapchics;
import util.Input;

import world.Maps;

import java.util.Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Random;

import java.awt.event.KeyEvent;

import java.awt.Color;

public class AssemblyMode implements GameState{
	private enum SubEstadosAssembleia{
		RECOMPENSA("Recompensa");
		
		private final String nome;
		
		SubEstadosAssembleia(String nome){
			this.nome = nome;
		}
		
		public String getSubEstadoNome(){
			return nome;
		}
	}
	
	private final Random random = new Random();
	
	private SubEstadosAssembleia subEstadoAtual;
	private Battle batalhaAtual;
	
    public AssemblyMode(){
		this.batalhaAtual = null;
		this.subEstadoAtual = null;
    }
	
	// ==================== ESTADO ====================
	
	@Override
	public void atualizaEstado(){
		verificarResultadoBatalha();
	}
	
	@Override
	public void desenhaEstado(){
		Grapchics.limpaTela();
		
		if (subEstadoAtual == null){
			desenhaEntrada();
		}
		
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
			case KeyEvent.VK_ESCAPE: teclaEsc(); break;
		}
	}
	
	// ==================== TECLAS ====================
	
	private void teclaEsquerda(){
	}
	
	private void teclaDireita(){
	}
	
	private void teclaCima(){
	}
	
	private void teclaBaixo(){
	}
	
	private void teclaEnter(){
		if (this.batalhaAtual != null) return;

		if (subEstadoAtual == null){
			Troop tropaDesafio = gerarTropaDesafio();
			
			if (tropaDesafio != null){
				this.batalhaAtual = new Battle(tropaDesafio);
				
				Terminal.setEstadoAnterior(this);
				Terminal.mudarEstado(this.batalhaAtual);
			}
		}
	}
	
	private void teclaShift(){
	}
	
	private void teclaInventário(){
		if (subEstadoAtual == null){
			Terminal.mudarEstado(new Maps());
		}
	}
	
	private void teclaEsc(){
	}
	
	// ==================== DESENHO ====================
	
	private void desenhaEntrada(){
		int linhaAtual = 0;
		
		Grapchics.desenhaCentroTTF("Assembleia - Entrada", linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("E: Sair", 0, linhaAtual++, Grapchics.PRETO_CLARO); 
		Grapchics.desenhaTTF("Enter: Aceitar desafio", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		
		Grapchics.desenhaTTF("Reputação: "+Player.getReputaçãoAtual(), 0, linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		
		linhaAtual+=10;
		
		Grapchics.desenhaCentroTTF("Equipe:",linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
			List<Monsters> equipe = Inventory.getEquipeLista();
			for (Monsters monstroEquipe : equipe){
				String nomeMonstroExibido = "";
				
				if (monstroEquipe != null){
					nomeMonstroExibido = monstroEquipe.getNomeMonstro()+" Nv"+monstroEquipe.getNivelAtual();
					Grapchics.desenhaTTF(nomeMonstroExibido, 0, linhaAtual++, Grapchics.BRANCO_CLARO);
				}else{
					Grapchics.desenhaTTF("[Vazio]", 0, linhaAtual++, Grapchics.PRETO_CLARO);
				}
			}
		Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);	
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private void verificarResultadoBatalha(){
		if (this.batalhaAtual != null){
			if (!this.batalhaAtual.foiIniciada()){
				this.batalhaAtual = null;
				return;
			}
			
			if (this.batalhaAtual.verificarVitória()){
				Player.aumentarReputação(20);
			}else{
				Player.aumentarReputação(10);
			}
			
			this.batalhaAtual.resetarVitória();
			this.batalhaAtual = null;
			Input.resetarCursor();
		}
	}
	
	private Troop gerarTropaDesafio(){
		List<Monsters> equipeJogador = Inventory.getEquipeLista();
		int somaNiveis = 0;
		int contadorMonstros = 0;
		
		for (Monsters m : equipeJogador){
			if (m != null){
				somaNiveis += m.getNivelAtual();
				contadorMonstros++;
			}
		}
		
		int nivelAlvo = (contadorMonstros > 0) ? (somaNiveis / contadorMonstros) + 1 : 1;
		
		List<Integer> idsValidos = new ArrayList<>();
		int totalMonstros = MonstersManager.getMonstrosExistentesTamanho();
		
		for (int i = 1; i <= totalMonstros; i++){
			Monsters base = MonstersManager.getMonstro(i);
			if (base != null && base.getRaridadeMonstro() != Monsters.Raridades.LENDARIO){
				idsValidos.add(i);
			}
		}
		
		if (idsValidos.isEmpty()) return TroopManager.getTroop(1); 
		
		List<Troop.Inimigo> inimigos = new ArrayList<>();
		
		for (int i = 0; i < 3; i++){
			int idSorteado = idsValidos.get(random.nextInt(idsValidos.size()));
			Monsters baseMonstro = MonstersManager.getMonstro(idSorteado);
			
			Monsters monstroCopia = new Monsters(baseMonstro);
			
			int nivelFinal = Math.min(Math.max(1, nivelAlvo), monstroCopia.getNivelMaximo());
			
			Troop.Inimigo inimigo = new Troop.Inimigo(monstroCopia, nivelFinal, false);
			
			monstroCopia.limparHabilidadesAtivas();
			
			List<Skills> desbloqueadas = new ArrayList<>(monstroCopia.getHabilidadesDesbloqueadas());
			Collections.shuffle(desbloqueadas, random);
			
			int slotsPreenchidos = 0;
			for (Skills skill : desbloqueadas){
				if (slotsPreenchidos >= monstroCopia.getQuantidadeMaxSlotsHabilidade()) break;
				
				if (!skill.isTipoEspecial(skill.getTipoHabilidade())){
					monstroCopia.adicionarHabilidadeAtiva(skill);
					slotsPreenchidos++;
				}
			}
			
			inimigos.add(inimigo);
		}
		
		return new Troop(-1, "Mr. CPU", inimigos, 0, 50);
	}
	
	// ==================== OUTROS ====================
	
   //===
}