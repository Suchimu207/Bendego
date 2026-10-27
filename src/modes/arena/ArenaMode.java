package modes.arena;

import main.Terminal;

import bestiary.*;
import combat.Battle;
import combat.effects.Effects;

import main.inventory.Inventory;
import main.Player;
import main.Shop;

import manager.ArenaModeManager;
import manager.MonstersManager;

import util.Audio;
import util.GameState;
import util.Grapchics;
import util.Input;

import world.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Random;

import java.awt.event.KeyEvent;

import java.awt.Color;

public class ArenaMode implements GameState{
	private enum SubEstadosArena{
		TORNEIO("Torneio"),
		RECOMPENSA("Recompensa");
		
		private final String nome;
		
		SubEstadosArena(String nome){
			this.nome = nome;
		}
		
		public String getSubEstadoNome(){
			return nome;
		}
	}
	
	private static final Random random = new Random();
	
	private static SubEstadosArena subEstadoAtual = null;
	private static Map<Integer, Tournament> torneios;
	private static List<Effects> buffs;
	private static List<Effects> buffsAtuais;
	private static Tournament torneioAtual;
	
	private static int torneioSelecionado = 0;
	private static int rodadaAtual = -1;
	private static int rodadaBuff = 0;
	
	private static Monsters monstroDesbloqueado = null;
	
	private static boolean batalha = false;
	
    public ArenaMode(){
		monstroDesbloqueado = null;
		buffsAtuais = new ArrayList<Effects>();
		rodadaBuff = 0;
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
			desenhaTorneios();
		}else if (subEstadoAtual == SubEstadosArena.TORNEIO){
			desenhaTorneioAtual();
		}else if (subEstadoAtual == SubEstadosArena.RECOMPENSA){
			desenhaRecompensa();
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
		if (subEstadoAtual == null){
			Input.decrementarCursorY();
			torneioSelecionado = -1;
		}
	}
	
	private void teclaBaixo(){
		if (subEstadoAtual == null){
			Input.incrementarCursorY();
			torneioSelecionado = -1;
		}
	}
	
	private void teclaEnter(){
		if (ArenaMode.batalha) return;
		
		if (subEstadoAtual == null){
            if (torneioSelecionado > 0 && ArenaModeManager.getTorneios().containsKey(torneioSelecionado)){
				Tournament torneio = ArenaModeManager.getTorneio(torneioSelecionado);
				if (Player.getTorneiosVencidos() >= torneio.getTorneiosRequeridos()){
					this.torneioAtual = ArenaModeManager.getTorneio(torneioSelecionado);
					subEstadoAtual = SubEstadosArena.TORNEIO;
					Input.resetarCursor();
					ArenaMode.rodadaAtual = 1;
					Input.setCursorY(rodadaAtual);
				}
            }
        }else if (subEstadoAtual == SubEstadosArena.TORNEIO){
			if (rodadaBuff == 3){
				adicionarBuff();
				rodadaBuff = 0;
			}
			ArenaMode.batalha = true;
			Terminal.setEstadoAnterior(this);
			Terminal.mudarEstado(new Battle(torneioAtual.getBatalha(rodadaAtual), buffsAtuais
			));
        }else if (subEstadoAtual == SubEstadosArena.RECOMPENSA){
			subEstadoAtual = null;
			torneioAtual = null;
			Input.resetarCursor();
		}
	}
	
	private void teclaShift(){
	}
	
	private void teclaInventário(){
		if (ArenaMode.subEstadoAtual == SubEstadosArena.TORNEIO){
            ArenaMode.subEstadoAtual = null;
			buffsAtuais.clear();
            Input.resetarCursor();
            Input.setCursorY(4);
        }else{
			ArenaMode.subEstadoAtual = null;
			torneioSelecionado = -1;
            Terminal.mudarEstado(new Maps());
        }
	}
	
	private void teclaEsc(){
	}
	
	// ==================== DESENHO ====================
	
	private static void desenhaTorneios(){
		torneios = ArenaModeManager.getTorneios();
		if (torneios == null || torneios.size() <= 0) return;
		
		int linhaAtual = 0;
		int linhaInicio = 0;
		int linhaFim = 0;
		
		Grapchics.desenhaCentroTTF("Arena - Torneios", linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("E: Sair", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Enter: Selecionar torneio", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		
		linhaInicio = linhaAtual;
		
		for (Map.Entry<Integer, Tournament> entry : torneios.entrySet()){
            int indice = entry.getKey();
            Tournament torneio = entry.getValue();
			
			int torneioFinalizado = torneio.isConcluido() ? 15 : 0;
			boolean desbloqueado = Player.getTorneiosVencidos() >= torneio.getTorneiosRequeridos();
			
            if (Input.getCursorY() == linhaAtual){
				if (desbloqueado){
					Grapchics.desenhaHibrido(torneio.getNomeTorneio(), torneioFinalizado, 1, linhaAtual++, Grapchics.AMARELO_CLARO);
					torneioSelecionado = indice;
				}else{
					Grapchics.desenhaHibrido(torneio.getNomeTorneio(), torneioFinalizado, 1, linhaAtual++, Grapchics.AMARELO_CLARO);
				}
            }else{
				Color cor = desbloqueado ? Grapchics.BRANCO_CLARO : Grapchics.PRETO_CLARO;
				Grapchics.desenhaHibrido(torneio.getNomeTorneio(), torneioFinalizado, 0, linhaAtual++, cor);
            }
        }
		
		linhaFim = linhaAtual;
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		linhaAtual += 10;
		
		Grapchics.desenhaCentroTTF("Equipe:",linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		
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
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		
		if (Input.getCursorY() == 0) Input.setCursorY(linhaInicio);
		if (Input.getCursorY() < linhaInicio){
			Input.setCursorY(linhaFim-1);
		}else if (Input.getCursorY() > linhaFim-1){
			Input.setCursorY(linhaInicio);
		}
	}
	
	private static void desenhaTorneioAtual(){
		if (torneioAtual == null) return;
		
        int linhaAtual = 0;
		int tamanhoNomeTorneio = torneioAtual.getNomeTorneio().length();
		String linhaDinâmica = "_".repeat(tamanhoNomeTorneio);
		
		Grapchics.desenhaCentro(linhaDinâmica, linhaAtual++, Grapchics.PRETO_CLARO);
        Grapchics.desenhaCentroTTF(torneioAtual.getNomeTorneio(), linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaCentro(linhaDinâmica, linhaAtual++, Grapchics.PRETO_CLARO);
		
		linhaAtual++;
		
        Grapchics.desenhaTTF("E: Voltar", 0, linhaAtual++, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTTF("Enter: Iniciar Rodada", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		
        Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		
		int totalBatalhas = torneioAtual.getTotalBatalhas();
		int rodadaBônus = 1;
		
        for (int i = totalBatalhas; i >= 1; i--){
			rodadaBônus++;
			
            Troop tropaInimiga = torneioAtual.getBatalha(i);
            String infoTropa = "Rodada "+i+":"+tropaInimiga.getNomeTropa();
			int tamanhoTexto = infoTropa.length();
			
			if (Input.getCursorY() == i){
				if (rodadaBônus == 3 && rodadaBuff == 3){
					Grapchics.desenhaTTF("[???]", 0, linhaAtual++, Grapchics.AMARELO_CLARO);
					Grapchics.desenhaTTF(infoTropa, 0, linhaAtual++, Grapchics.BRANCO_CLARO);
					rodadaBônus = 0;
					continue;
				}
				
				if (rodadaBônus == 3 && rodadaBuff != 3){
					Grapchics.desenhaTTF("[???]", 0, linhaAtual++, Grapchics.BRANCO_CLARO);
					rodadaBônus = 0;
				}
				Grapchics.desenhaTTF(infoTropa, 0, linhaAtual, Grapchics.AMARELO_CLARO);
				Grapchics.desenhaTela((char)17, tamanhoTexto+1, linhaAtual++, Grapchics.AMARELO_CLARO);
			}else{
				if (rodadaBônus == 3){
					Grapchics.desenhaTTF("[???]", 0, linhaAtual++, Grapchics.BRANCO_CLARO);
					rodadaBônus = 0;
				}
				
				Grapchics.desenhaTTF(infoTropa, 0, linhaAtual++, Grapchics.BRANCO_CLARO);
			}
        }
		
        Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
	}
	
	private static void desenhaRecompensa(){
		if (monstroDesbloqueado == null) return;
		Grapchics.desenhaCentroTTF("Monstro desbloqueado: ", 10, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaCentroTTF(monstroDesbloqueado.getNomeMonstro(), 11, Grapchics.BRANCO_CLARO);
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private static void adicionarBuff(){
		List<Effects> buffsGlobais = ArenaModeManager.getBuffs();
		if (buffsGlobais == null || buffsGlobais.size() <= 0) return;
		
		for (int i = 0; i <= buffsGlobais.size(); i++){
			int id = random.nextInt(buffsGlobais.size());
			Effects efeito = buffsGlobais.get(id);
			if (!buffsAtuais.contains(efeito)){
				buffsAtuais.add(efeito);
				break;
			}
		}
	}
	
	private static void torneioVencido(){
		if (torneioAtual != null && rodadaAtual > torneioAtual.getTotalBatalhas()){
			if (!torneioAtual.isConcluido()){
				subEstadoAtual = SubEstadosArena.RECOMPENSA;
				
				int idRecompensa = torneioAtual.getRecompensaMonstro();
				
				Inventory.adicionarMonstroInventário(idRecompensa);
				Shop.adicionarItemEstoque(idRecompensa, 500);
				
				Audio.tocarSom("Item", 0.2f);
				
				monstroDesbloqueado = MonstersManager.getMonstro(idRecompensa);
				
				Player.registrarTorneioVencido();
				torneioAtual.setConcluido(true);
			}
			if (buffsAtuais != null) buffsAtuais.clear();
		}
	}
	
	private static void verificarResultadoBatalha(){
		if (ArenaMode.batalha){
			if (Battle.verificarVitória()){
				int cursor = 0;
				
				ArenaMode.rodadaAtual++;
				ArenaMode.rodadaBuff++;
				
				Input.setCursorY(ArenaMode.rodadaAtual);
				
				if (subEstadoAtual == SubEstadosArena.TORNEIO){
					torneioVencido();
				}
            
				Battle.resetarVitória();
			}else{
				ArenaMode.rodadaAtual = 1;
				rodadaBuff = 0;
				subEstadoAtual = null;
				if (buffsAtuais != null) buffsAtuais.clear();
				Input.resetarCursor();
				Battle.resetarVitória();
			}
		}
		ArenaMode.batalha = false;
	}
	
	// ==================== OUTROS ====================
	
   //===
}