package combat;

import bestiary.Monsters;
import bestiary.Skills;
import bestiary.Troop;

import util.description.SkillDescription;

import combat.effects.Effects;
import combat.effects.EffectsManager;
import combat.effects.EffectsStrategy;

import combat.status.StatusBase;

import combat.effects.strategies.EffectsApplyStatus;

import main.Player;

import util.Audio;
import util.Grapchics;
import util.Input;
import util.Debug;

import java.util.ArrayList;
import java.util.List;

import java.awt.Color;

public final class BattleField {
	public enum TipoDetalhe{
		ALIADO(0),
		INIMIGO(1);
		
		private final int valor;
		
		TipoDetalhe(int valor){
			this.valor = valor;
		}
		
		public int getValor(){
			return valor;
		}
	}
	
	// ==================== ATRIBUTOS ====================
	
	private Monsters[] aliados, aliadosDerrotados;
	private int maxAliados;
	private List<Integer> posiçõesAliadosX;
	private List<Integer> posiçõesAliadosY; 
	private List<Effects> efeitosIniciais = null; 
	
	private Troop tropa;
	private Monsters monstroSelecionado, monstroVisualizado;
	private List<Monsters> monstrosAlvos;
	private Skills skillSelecionada, skillEspecial;
	private Skills skillUsada = null;
	private BattleActionResult resultadoAção = null;
	
	private List<Monsters> inimigos;
	private int maxInimigos;
	private List<Integer> posiçõesInimigosX;
	private List<Integer> posiçõesInimigosY;
	private BattleAI inimigoAI;
	
	private boolean selecionarAlvo;
	private boolean aguardandoInimigo = false;
	private boolean aguardandoAliado = false; 
	private boolean especialAtivo = false;
	private boolean vitóriaBatalha = false;
	private boolean batalhaFinalizada = false;
	
	private String mensagemTurnoInimigo = null;
	private String danoTurnoInimigo = null;
	private String mensagemTurnoAliado = null;
	private String danoTurnoAliado = null;
	
	private int linhaInicial, linhaMáxima, linhaAtual = 0;
	
	private int detalheAtual;
	
	// ==================== CONSTRUTOR ====================
	
	protected BattleField(Monsters[] aliados, Troop tropa){
		setarCampo(aliados, tropa);
		prepararMonstros();
		inicializarActionValue();
	}
	
	protected BattleField(Monsters[] aliados, Troop tropa, List<Effects> efeitosIniciais){
		setarCampo(aliados, tropa);
		this.efeitosIniciais = efeitosIniciais;	
		
		prepararMonstros();
		inicializarActionValue();
	}
	
	// ==================== PREPARAÇÃO ====================
	
	private void setarCampo(Monsters[] aliados, Troop tropa){
		this.aliados = aliados;
		this.aliadosDerrotados = new Monsters[3];
		this.maxAliados = aliados.length;
		this.posiçõesAliadosX = new ArrayList<Integer>();
		this.posiçõesAliadosY = new ArrayList<Integer>();
		
		this.tropa = tropa;
		this.monstrosAlvos = new ArrayList<Monsters>();
		
		this.inimigos = tropa.getMonstros();
		this.maxInimigos = tropa.getMonstros().size();
		this.posiçõesInimigosX = new ArrayList<Integer>();
		this.posiçõesInimigosY = new ArrayList<Integer>();
		this.inimigoAI = new BattleAI();
		
		this.batalhaFinalizada = false;
	}
	
	private void prepararMonstros(){
		Debug.limpaPrompt();
		System.out.println("");
		
		for (int i = 0; i <= maxAliados-1; i++){
			Monsters monstro = aliados[i];
			if(monstro == null) continue;
			
			monstro.setForcaAtualCombate(monstro.getForcaAtual());
			monstro.setVidaAtualCombate(monstro.getVidaAtual());
			monstro.setVidaAtualCombateMaxima(monstro.getVidaAtual());
			monstro.setSpeedAtualCombate(monstro.getSpeedAtual());
			monstro.setEstaminaAtualCombate(monstro.getEstaminaAtual());
			monstro.desativarRecargas();
			monstro.limparStatus();
			monstro.zerarEscudoAtual();
			
			if (this.efeitosIniciais == null || this.efeitosIniciais.size() <= 0) continue;
			
			for (Effects efeitoAtual : efeitosIniciais){
				EffectsStrategy efeito = EffectsManager.getEfeito(efeitoAtual.getTipo());
				
				if (efeito != null){
					efeito.aplicar(monstro, monstro, null, efeitoAtual);
				}
			}
		}
		System.out.println(">>Aliados inicializados.");
        System.out.println("");
		
		for (Monsters monstro : inimigos){
			if(monstro == null) continue;
			monstro.setForcaAtualCombate(monstro.getForcaAtual());
			monstro.setVidaAtualCombate(monstro.getVidaAtual());
			monstro.setVidaAtualCombateMaxima(monstro.getVidaAtual());
			monstro.setSpeedAtualCombate(monstro.getSpeedAtual());
			monstro.setEstaminaAtualCombate(monstro.getEstaminaAtual());
			monstro.desativarRecargas();
			monstro.limparStatus();
			monstro.zerarEscudoAtual();
		}
		System.out.println(">>Inimigos inicializados.");
        System.out.println("");
	}
	
	private void inicializarActionValue(){
		BattleTurn.getUnidades().clear();
		
        for (Monsters monstro : aliados){
            if (monstro != null){
                BattleTurn.getUnidades().add(new BattleUnit(monstro, true));
            }
        }
        
        for (Monsters monstro : inimigos){
            if (monstro != null){
                BattleTurn.getUnidades().add(new BattleUnit(monstro, false));
            }
        }
        
        BattleTurn.ordenarActionValue(BattleTurn.getUnidades());
        
        BattleTurn.avançarTurno();
    }
	
	// ==================== DESENHO ====================
	
	protected void desenhaBatalha(){
		Grapchics.limpaTela();
		
		linhaInicial = 0;
		linhaAtual = linhaInicial;
		
		desenhaBarraActionValue();
		if (!aguardandoAliado){
			if (selecionarAlvo){
				Grapchics.desenhaTTF("Q: Voltar", 0, linhaAtual++, Grapchics.PRETO_CLARO);
			}
			if (BattleTurn.isTurnoJogador()){
				Grapchics.desenhaTTF("E: Detalhes", 0, linhaAtual++, Grapchics.PRETO_CLARO);
			}
			if (especialAtivo && BattleTurn.isTurnoJogador() && !selecionarAlvo){
				Grapchics.desenhaTTF("Espaço: Ativar especial", 0, linhaAtual++, Grapchics.PRETO_CLARO);
			}
			if (BattleTurn.isTurnoJogador()){
				Grapchics.desenhaTTF("Shift: Recarregar estamina", 0, linhaAtual++, Grapchics.PRETO_CLARO);
			}
		}
		
		linhaAtual += 5;
		desenhaAliados();
		desenhaInimigos();
		
		if(selecionarAlvo && !posiçõesInimigosX.isEmpty()){
			desenhaSetaBatalha();
		}
		
		linhaAtual += 10;
		if (!aguardandoAliado){
			if (BattleTurn.isAguardandoTurno() && BattleTurn.isTurnoJogador()){
				if (!selecionarAlvo){
					desenhaBatalhaComandos();
				}else{
					desenhaComandoDetalhe();
				}
			}
		}
		desenhaLogBatalha();
		
		Grapchics.atualizarTela();
	}
	
	private void desenhaLogBatalha(){		
		if (aguardandoInimigo && mensagemTurnoInimigo != null){
			Grapchics.desenhaTela("____________________", 0,linhaAtual++, Grapchics.PRETO_CLARO);
			Grapchics.desenhaTela((char)6, 0, linhaAtual, Grapchics.VERMELHO_CLARO);
			desenhaHabilidadeUsadaInimigo();
			
			if (danoTurnoInimigo != "" && danoTurnoInimigo != null) Grapchics.desenhaTTF(danoTurnoInimigo, 0, linhaAtual++, Grapchics.BRANCO_CLARO);
			this.resultadoAção = inimigoAI.getResultadoAção();
			
			if (resultadoAção != null && !resultadoAção.isAcerto()) Grapchics.desenhaTTF(">>Errou!", 0, linhaAtual++, Grapchics.BRANCO_CLARO);
			if (resultadoAção != null && resultadoAção.isAcerto()) desenhaEfetividade();
			
			Grapchics.desenhaTTF("[ENTER]  ", 0, linhaAtual++, Grapchics.AMARELO_CLARO);
			Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		}
		
		if (aguardandoAliado && mensagemTurnoAliado != null){
			Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
			Grapchics.desenhaTela((char)4, 0, linhaAtual, Grapchics.AZUL_CLARO);
			desenhaHabilidadeUsadaAliado();
			
			if (danoTurnoAliado != "" && danoTurnoAliado != null) Grapchics.desenhaTTF(danoTurnoAliado, 0, linhaAtual++, Grapchics.BRANCO_CLARO);
			if (resultadoAção != null && !resultadoAção.isAcerto()) Grapchics.desenhaTTF(">>Errou!", 0, linhaAtual++, Grapchics.BRANCO_CLARO);
			if (resultadoAção != null && resultadoAção.isAcerto()) desenhaEfetividade();
			
			Grapchics.desenhaTTF("[ENTER]  ", 0, linhaAtual++,  Grapchics.AMARELO_CLARO);
			Grapchics.desenhaTela("____________________", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		}
	}
	
	private void desenhaEfetividade(){
		if (resultadoAção.getEfetividade() == BattleActionResult.NÃO_EFETIVO){
			Grapchics.desenhaTTF(">>", 0, linhaAtual, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTTF("Não efetivo...", 2, linhaAtual++, Grapchics.PRETO_CLARO);
		}else if (resultadoAção.getEfetividade() == BattleActionResult.SUPER_EFETIVO){
			Grapchics.desenhaTTF(">>Super efetivo!", 0, linhaAtual++, Grapchics.BRANCO_CLARO);
		}
	}
	
	private void desenhaHabilidadeUsadaInimigo(){
		if (mensagemTurnoInimigo == null){
			return;
		}
		
		if (skillUsada == null){
			Grapchics.desenhaTTF(mensagemTurnoInimigo, 1, linhaAtual++, Grapchics.BRANCO_CLARO);
			return;
		}
	
		String mensagem = mensagemTurnoInimigo;
		int tamanhoMensagem = mensagem.length();
		String nomeHabilidade = skillUsada.getNomeHabilidade();
		int tamanhoNomeHabilidade = nomeHabilidade.length();
		Color corHabilidade = skillUsada.getCorHabilidade();
		
		Grapchics.desenhaTTF(mensagem, 1, linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF(nomeHabilidade, 0, linhaAtual, corHabilidade);
		Grapchics.desenhaTela((char)19, tamanhoNomeHabilidade, linhaAtual++, Grapchics.BRANCO_CLARO);
	}
	
	private void desenhaHabilidadeUsadaAliado(){
		if (mensagemTurnoAliado == null){
			return;
		}
		
		if (skillUsada == null){
			Grapchics.desenhaTTF(mensagemTurnoAliado, 1, linhaAtual++, Grapchics.BRANCO_CLARO);
			return;
		}
	
		String mensagem = mensagemTurnoAliado;
		int tamanhoMensagem = mensagem.length();
		String nomeHabilidade = skillUsada.getNomeHabilidade();
		int tamanhoNomeHabilidade = nomeHabilidade.length();
		Color corHabilidade = skillUsada.getCorHabilidade();
		
		Grapchics.desenhaTTF(mensagem, 1, linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF(nomeHabilidade, 0, linhaAtual, corHabilidade);
		Grapchics.desenhaTela((char)19, tamanhoNomeHabilidade, linhaAtual++, Grapchics.BRANCO_CLARO);
	}
	
	private void desenhaAliados(){
		posiçõesAliadosX.clear();
		posiçõesAliadosY.clear();
		
		int jogadorMonstrosX = 3;
		
		if (aliados[0] != null){
			desenhaMonstroBatalha(aliados[0], jogadorMonstrosX-2, linhaAtual-4);
			posiçõesAliadosX.add(jogadorMonstrosX-2);
			posiçõesAliadosY.add(linhaAtual-4);
		}
		if (maxAliados >= 2){
			if (aliados[1] != null){
			desenhaMonstroBatalha(aliados[1], jogadorMonstrosX, linhaAtual+1);
			posiçõesAliadosX.add(jogadorMonstrosX);
			posiçõesAliadosY.add(linhaAtual+1);
			}
		}
		if (maxAliados >= 3){
			if (aliados[2] != null){
			desenhaMonstroBatalha(aliados[2], jogadorMonstrosX-2, linhaAtual+6);
			posiçõesAliadosX.add(jogadorMonstrosX-2);
			posiçõesAliadosY.add(linhaAtual+6);
			}
		}
	}
	
	private void desenhaInimigos(){
		posiçõesInimigosX.clear();
		posiçõesInimigosY.clear();
		
		if (inimigos.size() <= 0) return;
		
		if(inimigos.get(0) != null){
			desenhaMonstroBatalha(inimigos.get(0), 24, linhaAtual-4);
			posiçõesInimigosX.add(24);
			posiçõesInimigosY.add(linhaAtual-4);
		}
		
		if (inimigos.size() >= 2){
			if(inimigos.get(1) != null){
				desenhaMonstroBatalha(inimigos.get(1), 22, linhaAtual+1);
				posiçõesInimigosX.add(22);
				posiçõesInimigosY.add(linhaAtual+1);
			}
		}
		
		if (inimigos.size() >= 3){
			if(inimigos.get(2) != null){
				desenhaMonstroBatalha(inimigos.get(2), 24, linhaAtual+6);
				posiçõesInimigosX.add(24);
				posiçõesInimigosY.add(linhaAtual+6);
			}
		}
	}
	
	private void desenhaBarraActionValue(){
		int count = 0;
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
        for (BattleUnit unidade : BattleTurn.getUnidades()){
            if (count >= 6) break;
            Monsters monstro = unidade.getMonstro();
            if (monstro == null) continue;
            
            String nome = monstro.getNomeMonstro();
            int av = unidade.getActionValue();
			boolean ehAliado = unidade.isAliado();
			Monsters monstroUnidade = BattleTurn.getUnidadeAtual().getMonstro();
			if (monstroUnidade == null) continue; 
			boolean unidadeAtual = (monstroUnidade == monstro);
			
			boolean isAlvo = (unidade != null && unidade.isAlvo());
			
            String texto = nome + " (AV: " + av + ")";
            if (unidade == BattleTurn.getUnidadeAtual()){
				texto += (char)17;
            }
            
			if (ehAliado && unidadeAtual){
				Grapchics.desenhaTela((char)4+texto, 0, linhaAtual, Grapchics.AZUL_CLARO);
				Grapchics.desenhaTTF(texto, 1, linhaAtual++, Grapchics.BRANCO_CLARO);
				
			}else if (ehAliado && !unidadeAtual){
				Grapchics.desenhaTela(isAlvo ? (char)26+texto : (char)4+texto, 0, linhaAtual, isAlvo ? Grapchics.AMARELO_CLARO : Grapchics.AZUL_CLARO);
				Grapchics.desenhaTTF(texto, 1, linhaAtual++, Grapchics.PRETO_CLARO);
				
			}else if (!ehAliado && !unidadeAtual){
				Grapchics.desenhaTela(isAlvo ? (char)26+texto : (char)6+texto, 0, linhaAtual, isAlvo ? Grapchics.AMARELO_CLARO : Grapchics.VERMELHO_CLARO);
				Grapchics.desenhaTTF(texto, 1, linhaAtual++,  Grapchics.PRETO_CLARO);
				
			}else if (!ehAliado && unidadeAtual){
				Grapchics.desenhaTela((char)6, 0, linhaAtual, Grapchics.VERMELHO_CLARO);
				Grapchics.desenhaTTF(texto, 1, linhaAtual++, Grapchics.BRANCO_CLARO);
			}
			
			count++;
        }
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
    }
	
	private void desenhaMonstroBatalha(Monsters monstro, int x, int y){
		if (monstro == null) return;
		
		Monsters monstroUnidade = BattleTurn.getUnidadeAtual().getMonstro();
		boolean unidadeAtual = (monstroUnidade == monstro);
		
		BattleUnit unidadeAlvo = BattleTurn.getUnidadePorMonstro(monstro);
		boolean isAlvo = (unidadeAlvo != null && unidadeAlvo.isAlvo());
		
		Color corTexto = (unidadeAtual || isAlvo) ? Grapchics.BRANCO_CLARO : Grapchics.PRETO_CLARO;
		Color corNome = unidadeAtual ? Grapchics.AMARELO_CLARO : Grapchics.PRETO_CLARO;
		
		if (!unidadeAtual && isAlvo){
			Grapchics.desenhaTela((char) 25, x+4, y-1, Grapchics.AMARELO_CLARO);
		}
		
		Grapchics.desenhaTTF(monstro.getNomeMonstro(), x, y, corNome);
		
		// ====================
		
		int linha = y + 1;
		linha = desenhaBarraVida(monstro, x, linha, corTexto);
		
		if (monstro.getEscudoAtual() > 0){
			Grapchics.desenhaTela("Escudo: " + monstro.getEscudoAtual(), x, linha++, corTexto);
		}
		
		desenhaBarraEstamina(monstro, x, linha, corTexto);
	}
	
	private int desenhaBarraVida(Monsters monstro, int x, int y, Color corTexto){
		int atual = monstro.getVidaAtualCombate();
		int maximo = monstro.getVidaAtualCombateMaxima();
		double percentual = maximo > 0 ? (double) atual / maximo : 0;

		final int LARGURA_BARRA = 6;

		Grapchics.desenhaBarra(x, y, LARGURA_BARRA, percentual, corPorPercentualVida(percentual), Grapchics.PRETO);
		Grapchics.desenhaTela(atual + "/" + maximo, x+LARGURA_BARRA, y, corTexto);		
		return y + 1;
	}
	
	private void desenhaBarraEstamina(Monsters monstro, int x, int y, Color corTexto){
		int atual = monstro.getEstaminaAtualCombate();
		int maximo = monstro.getEstaminaAtual();
		double percentual = maximo > 0 ? (double) atual / maximo : 0;

		final int LARGURA_BARRA = 6;

		Grapchics.desenhaBarra(x, y, LARGURA_BARRA, percentual, Grapchics.AZUL_CLARO, Grapchics.PRETO);
		Grapchics.desenhaTela(atual + "/" + maximo, x+LARGURA_BARRA, y, corTexto);
	}
	
	private Color corPorPercentualVida(double percentual){
		if (percentual > 0.5) return Grapchics.VERDE_CLARO;
		if (percentual > 0.10) return Grapchics.VERMELHO_CLARO;
		return Grapchics.VERDE_CLARO;
	}
	
	private void desenhaSetaBatalha(){    
		if (skillSelecionada == null) return;
		
		Skills.TipoAlvo tipoAlvo = skillSelecionada.getAlvoHabilidadeTipo();
		int x = 0;
		int y = 0;
		
		monstrosAlvos.clear();
		
		switch(tipoAlvo){
			case INIMIGO_UNICO:
			if (Input.getCursorX() < 0) Input.setCursorX(maxInimigos - 1);
			if (Input.getCursorX() >= maxInimigos) Input.setCursorX(0);
			
			x = posiçõesInimigosX.get(Input.getCursorX());
			y = posiçõesInimigosY.get(Input.getCursorX());
			
			monstroSelecionado = inimigos.get(Input.getCursorX());
			if (monstroSelecionado == null) return;
			
			monstrosAlvos.add(monstroSelecionado);
			
			Grapchics.desenhaTela((char) 25, x+4, y - 1, Grapchics.AMARELO_CLARO);
			Grapchics.desenhaTTF(monstroSelecionado.getNomeMonstro(), x, y, 
			Grapchics.AMARELO_CLARO);
			break;
			
			case ALIADO_UNICO:
			if (Input.getCursorX() < 0) Input.setCursorX(posiçõesAliadosX.size() - 1);
            if (Input.getCursorX() >= posiçõesAliadosX.size()) Input.setCursorX(0);
			
			x = posiçõesAliadosX.get(Input.getCursorX());
            y = posiçõesAliadosY.get(Input.getCursorX());
			
			monstroSelecionado = obterAliadoPorIndiceValido(Input.getCursorX());
			if (monstroSelecionado == null) return;
			
			monstrosAlvos.add(monstroSelecionado);
			
			Grapchics.desenhaTela((char) 25, x+4, y - 1, Grapchics.AMARELO_CLARO);
			Grapchics.desenhaTTF(monstroSelecionado.getNomeMonstro(), x, y, 
			Grapchics.AMARELO_CLARO);
			break;
			
			case USUARIO:
            monstroSelecionado = BattleTurn.getUnidadeJogadorAtual().getMonstro();
			if (monstroSelecionado == null) return;
			
			monstrosAlvos.add(monstroSelecionado);
			
			int indiceAliado = -1;
			int contadorValidos = 0;
			for (int i = 0; i < maxAliados; i++){
				if (aliados[i] != null){
					if (aliados[i] == monstroSelecionado){
						indiceAliado = contadorValidos;
						break;
					}
					contadorValidos++;
				}
			}
			
			if (indiceAliado != -1 && indiceAliado < posiçõesAliadosX.size()){
				x = posiçõesAliadosX.get(indiceAliado);
				y = posiçõesAliadosY.get(indiceAliado);
				
				Grapchics.desenhaTela((char) 25, x + 4, y - 1, Grapchics.AMARELO_CLARO);
			}
            break;
			
			case INIMIGO_AREA:
            for (int i = 0; i < posiçõesInimigosX.size(); i++){
                Grapchics.desenhaTela((char) 25, posiçõesInimigosX.get(i)+4, 
				posiçõesInimigosY.get(i)-1, Grapchics.AMARELO_CLARO);
            }
			
			for (Monsters m : inimigos){
                if (m != null && m.getVidaAtualCombate() > 0) monstrosAlvos.add(m);
            }
            break;
			
			case ALIADO_AREA:
            for (int i = 0; i < posiçõesAliadosX.size(); i++){
                Grapchics.desenhaTela((char) 25, posiçõesAliadosX.get(i)+4, 
				posiçõesAliadosY.get(i)-1, Grapchics.AMARELO_CLARO);
            }
			
			for (Monsters m : aliados){
                if (m != null && m.getVidaAtualCombate() > 0) monstrosAlvos.add(m);
            }
            break;
			
			case CAMPO:
            for (int i = 0; i < posiçõesInimigosX.size(); i++){
				Grapchics.desenhaTela((char) 25, posiçõesInimigosX.get(i)+4, 
				posiçõesInimigosY.get(i)-1, Grapchics.AMARELO_CLARO);
			}				
            for (int i = 0; i < posiçõesAliadosX.size(); i++){
				Grapchics.desenhaTela((char) 25, posiçõesAliadosX.get(i)+4, 
				posiçõesAliadosY.get(i)-1, Grapchics.AMARELO_CLARO);
			}
			
			for (Monsters m : inimigos){
                if (m != null && m.getVidaAtualCombate() > 0) monstrosAlvos.add(m);
            }
            for (Monsters m : aliados){
                if (m != null && m.getVidaAtualCombate() > 0) monstrosAlvos.add(m);
			}
            break;
		}
	}
	
	private void desenhaBatalhaComandos(){
		Monsters monstroAtual = BattleTurn.getUnidadeJogadorAtual().getMonstro();
		if (monstroAtual == null) return;
		int tamanhoSkills = monstroAtual.getQuantidadeMaxSlotsHabilidade();
		
		if (maxAliados >= 3) linhaAtual++;
		skillEspecial = monstroAtual.getHabilidadeEspecial();
		desenhaMonstroEspecial(monstroAtual, skillEspecial);
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		for (int i = 0; i <= tamanhoSkills-1; i++){
			Skills skillCarregada = monstroAtual.getHabilidadeAtiva(i);
			
			if (skillCarregada != null){
				if (Input.getCursorY() == i){					
					if (skillCarregada.isRecarga() && skillCarregada.getRecargaAtual() > 0){
						Grapchics.desenhaTTF((i+1)+": ",0,linhaAtual, Grapchics.PRETO_CLARO);
						Grapchics.desenhaTTF(skillCarregada.getNomeHabilidade()+" - Recarga:"+skillCarregada.getRecargaAtual(),4,linhaAtual++,
						Grapchics.AMARELO_CLARO);
						
						skillSelecionada = null;
					}else{
						Grapchics.desenhaTTF((i+1)+": ",0,linhaAtual, Grapchics.BRANCO_CLARO);
						Grapchics.desenhaTTF(skillCarregada.getNomeHabilidade(),4,linhaAtual++,Grapchics.AMARELO_CLARO);
						
						skillSelecionada = skillCarregada;
					}						
				}else{
					if (skillCarregada.isRecarga() && skillCarregada.getRecargaAtual() > 0){
						Grapchics.desenhaTTF((i+1)+": ",0,linhaAtual, Grapchics.PRETO_CLARO);
						Grapchics.desenhaTTF(skillCarregada.getNomeHabilidade()+" - Recarga:"+skillCarregada.getRecargaAtual(),3,linhaAtual++,
						Grapchics.PRETO_CLARO);
						
					}else{
						Grapchics.desenhaTTF((i+1)+": ",0,linhaAtual, Grapchics.BRANCO_CLARO);
						Grapchics.desenhaTTF(skillCarregada.getNomeHabilidade(),3,linhaAtual++,skillCarregada.getCorHabilidade());
					}
				}
			}else{
				if (Input.getCursorY() == i){
					Grapchics.desenhaTTF("[VAZIO]",0,linhaAtual++,
					Grapchics.AMARELO_CLARO);
					skillSelecionada = null;
				}else{
					Grapchics.desenhaTTF("[VAZIO]",0,linhaAtual++,Grapchics.PRETO_CLARO);
				}
			}
		}
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		
		if (Input.getCursorY() > tamanhoSkills-1){
			Input.setCursorY(0);
		}else if (Input.getCursorY() < 0){
			Input.setCursorY(tamanhoSkills-1);
		}
	}
	
	private void desenhaMonstroEspecial(Monsters monstro, Skills habilidade){
		if (monstro == null || habilidade == null) return;
		
		int barraEspecialAtual = monstro.getBarraEspecialAtual();
		int barraEspecialMaxima = monstro.getBarraEspecialMaximo();
		boolean especialDisponivel = monstro.isEspecialCarregado();
		
		if (especialDisponivel){
			Grapchics.desenhaTTF(">>"+habilidade.getNomeHabilidade()+"<<",0,linhaAtual++, habilidade.getCorHabilidade());
			skillEspecial = habilidade;
			especialAtivo = true;
		}else{
			String nomeEspecial = habilidade.getNomeHabilidade()+" "+barraEspecialAtual;
			int tamanhoNomeEspecial = nomeEspecial.length();
			
			Grapchics.desenhaTTF(nomeEspecial,0,linhaAtual, Grapchics.PRETO_CLARO);
			Grapchics.desenhaTela((char)47, tamanhoNomeEspecial, linhaAtual, Grapchics.PRETO_CLARO);
			Grapchics.desenhaTTF(""+barraEspecialMaxima,tamanhoNomeEspecial+1,linhaAtual++, Grapchics.PRETO_CLARO);
			skillEspecial = null;
		}
	}
	
	private void desenhaComandoDetalhe(){
		if (skillSelecionada == null) return;
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTela(">>",0,linhaAtual, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
		Grapchics.desenhaTTF(skillSelecionada.getNomeHabilidade(),2,linhaAtual++, skillSelecionada.getCorHabilidade(), Grapchics.FUNDO);
		
		linhaAtual = SkillDescription.infoHabilidade(skillSelecionada, linhaAtual, true);
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
	}
	
	protected void desenhaDetalhes(){
		Grapchics.limpaTela();
		
		linhaInicial = 0;
		linhaAtual = linhaInicial;
		boolean isAliado = false;
		
		if (detalheAtual == TipoDetalhe.ALIADO.getValor()){
			Grapchics.desenhaCentroTTF("Detalhes - Aliados",linhaAtual++, Grapchics.BRANCO_CLARO);
		}else if (detalheAtual == TipoDetalhe.INIMIGO.getValor()){
			Grapchics.desenhaCentroTTF("Detalhes - Inimigos",linhaAtual++, Grapchics.BRANCO_CLARO);
		}
		
		Grapchics.desenhaTTF("E: Voltar",0,linhaAtual++, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Alternar página: ",0,linhaAtual, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTela((char)27+"/"+(char)26,17,linhaAtual++, Grapchics.PRETO_CLARO);
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		linhaInicial = linhaAtual;
		
		if (detalheAtual == TipoDetalhe.ALIADO.getValor()){
			desenhaDetalhesAliados();
			isAliado = true;
		}else if (detalheAtual == TipoDetalhe.INIMIGO.getValor()){
			desenhaDetalhesInimigos();
			isAliado = false;
		}
		
		linhaMáxima = linhaAtual-1;
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		linhaAtual++;
		
		desenhaMonstroDetalhes(isAliado);
		
		if (Input.getCursorY() == 0){
			Input.setCursorY(linhaInicial);
		}else if (Input.getCursorY() > linhaMáxima){
			Input.setCursorY(linhaInicial);
		}else if (Input.getCursorY() < linhaInicial){
			Input.setCursorY(linhaMáxima);
		}
		
		Grapchics.atualizarTela();
	}
	
	private void desenhaDetalhesAliados(){
		if (aliados[0] != null){
			desenhaMonstroNome(aliados[0]);
		}
		if (aliados[1] != null){
			desenhaMonstroNome(aliados[1]);
		}
		if (aliados[2] != null){
			desenhaMonstroNome(aliados[2]);
		}
	}
	
	private void desenhaDetalhesInimigos(){
		for (Monsters monstro : inimigos){
			desenhaMonstroNome(monstro);
		}
	}
	
	private void desenhaMonstroNome(Monsters monstro){
		if (monstro == null) return;
		
		String texto = monstro.getNomeMonstro()+" Nv"+monstro.getNivelAtual();		
		int tamanhoTexto = texto.length();
		int tamanhoElemento = monstro.getElementosAtuais().length();
		
		if (Input.getCursorY() == linhaAtual){
			Grapchics.desenhaTTF(texto,0,linhaAtual, Grapchics.AMARELO_CLARO);
			monstroVisualizado = monstro;
		}else{
			Grapchics.desenhaTTF(texto,0,linhaAtual, Grapchics.BRANCO_CLARO);
		}
		
		desenhaMonstroElementos(monstro, linhaAtual, texto);
		linhaAtual++;
	}
	
	private void desenhaMonstroElementos(Monsters monstro, int linhaAtual, String nomeMonstro){
		int tamanhoTexto = nomeMonstro.length();
		
		Monsters.Elementos[] elementos = monstro.getElementosAtuaisValores();
		if (elementos == null || elementos.length == 0) return;
		
		Grapchics.desenhaTTF("(", tamanhoTexto+1, linhaAtual, Grapchics.BRANCO_CLARO);
		
		int colunaX = tamanhoTexto+2;
		int tamanhoElementos = 0;
		
		for (int i = 0; i < elementos.length; i++){
			Monsters.Elementos elemento = elementos[i];
			if (elemento == null) continue;

			String nomeElemento = elemento.getElementoNome();
			tamanhoElementos += nomeElemento.length();
			
			Color corElemento = monstro.getCorDoElemento(elemento.name());
			
			Grapchics.desenhaTTF(nomeElemento, colunaX, linhaAtual, corElemento);
			colunaX += nomeElemento.length();
			
			if (i < elementos.length - 1){
				Grapchics.desenhaTela((char)47, colunaX, linhaAtual, Grapchics.BRANCO_CLARO);
				colunaX += 1;
				tamanhoElementos += 1;
			}
		}
		Grapchics.desenhaTTF(")",tamanhoTexto+tamanhoElementos+2,linhaAtual,Grapchics.BRANCO_CLARO);
	}
	
	private void desenhaMonstroDetalhes(boolean isAliado){
		if (monstroVisualizado == null) return;
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		String PV = "PV: "+monstroVisualizado.getVidaAtualCombate();
		int tamanhoPV = PV.length();
		
		Grapchics.desenhaTTF(PV,0,linhaAtual, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela((char)47,tamanhoPV,linhaAtual, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF(""+monstroVisualizado.getVidaAtualCombateMaxima(),tamanhoPV+1,linhaAtual++, Grapchics.BRANCO_CLARO);
		
		if (monstroVisualizado.getEscudoAtual() > 0){
			Grapchics.desenhaTTF("Escudo: "+monstroVisualizado.getEscudoAtual(),0,linhaAtual++, Grapchics.BRANCO_CLARO);
		}
		
		String STA = "Estamina: "+monstroVisualizado.getEstaminaAtualCombate();
		int tamanhoSTA = STA.length();
		
		Grapchics.desenhaTTF(STA,0,linhaAtual, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela((char)47,tamanhoSTA,linhaAtual, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF(""+monstroVisualizado.getEstaminaAtual(),tamanhoSTA+1,linhaAtual++, Grapchics.BRANCO_CLARO);
		
		if (isAliado){
			String energia = "Energia: "+monstroVisualizado.getBarraEspecialAtual();
			int tamanhoEnergia = energia.length();
			
			Grapchics.desenhaTTF(energia,0,linhaAtual, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTela((char)47,tamanhoEnergia,linhaAtual, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTTF(""+monstroVisualizado.getBarraEspecialMaximo(),tamanhoEnergia+1,linhaAtual++, Grapchics.BRANCO_CLARO);
		}
		
		if (monstroVisualizado.getForcaAtualCombate() == monstroVisualizado.getForcaAtual()){
			Grapchics.desenhaTTF("Força: "+monstroVisualizado.getForcaAtualCombate(),0,linhaAtual++, Grapchics.BRANCO_CLARO);
			
		}else if (monstroVisualizado.getForcaAtualCombate() > monstroVisualizado.getForcaAtual()){
			Grapchics.desenhaTTF("Força: ",0,linhaAtual, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTTF(""+monstroVisualizado.getForcaAtualCombate(),7,linhaAtual++, Grapchics.VERDE_CLARO);
			
		}else if (monstroVisualizado.getForcaAtualCombate() < monstroVisualizado.getForcaAtual()){
			Grapchics.desenhaTTF("Força: ",0,linhaAtual, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTTF(""+monstroVisualizado.getForcaAtualCombate(),7,linhaAtual++, Grapchics.VERMELHO_CLARO);
		}
		
		if (monstroVisualizado.getSpeedAtualCombate() == monstroVisualizado.getSpeedAtual()){
			Grapchics.desenhaTTF("Velocidade: "+monstroVisualizado.getSpeedAtualCombate(),0,linhaAtual++, Grapchics.BRANCO_CLARO);
			
		}else if (monstroVisualizado.getSpeedAtualCombate() > monstroVisualizado.getSpeedAtual()){
			Grapchics.desenhaTTF("Velocidade: ",0,linhaAtual, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTTF(""+monstroVisualizado.getSpeedAtualCombate(),12,linhaAtual++, Grapchics.VERDE_CLARO);
			
		}else if (monstroVisualizado.getSpeedAtualCombate() < monstroVisualizado.getSpeedAtual()){
			Grapchics.desenhaTTF("Velocidade: ",0,linhaAtual, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTTF(""+monstroVisualizado.getSpeedAtualCombate(),12,linhaAtual++, Grapchics.VERMELHO_CLARO);
			
		}
		
		Grapchics.desenhaTTF("Traço: "+monstroVisualizado.getNomesTraços(),0,linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		
		for (int i = 0; i < monstroVisualizado.getQuantidadeMaxSlotsHabilidade(); i++){
			Skills skillCarregada = monstroVisualizado.getHabilidadeAtiva(i);
			if (skillCarregada != null){
				Grapchics.desenhaTTF((i+1)+": "+skillCarregada.getNomeHabilidade(),0,linhaAtual,Grapchics.BRANCO_CLARO);
				Grapchics.desenhaTTF(skillCarregada.getNomeHabilidade(),3,linhaAtual++,skillCarregada.getCorHabilidade());
			}else{
				Grapchics.desenhaTTF("[VAZIO]",0,linhaAtual++,Grapchics.PRETO_CLARO);
			}
		}
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		
		List<StatusBase> listaStatus = monstroVisualizado.getStatusAtuais();
		if (listaStatus.isEmpty()){
			Grapchics.desenhaTTF("Status: Nenhum", 0, linhaAtual++, Grapchics.PRETO_CLARO);
		}else{
			String indicador = "";
			int tamanhoIndicador = 0;
			for (int i = 0; i < listaStatus.size(); i++){
				StringBuilder statusTexto = new StringBuilder("");
				StatusBase status = listaStatus.get(i);
				
				if (status != null && statusTexto != null && status.isAtivo()){
					statusTexto.append(status.getNome())
					.append(" (").append(status.getDuraçãoAtual()).append("t)");
				}
				
				if (status.isPositivo() && status != null){
					indicador = "[+] ";
					Grapchics.desenhaTela(indicador, 0, linhaAtual, Grapchics.VERDE_CLARO);
				}else if (!status.isPositivo() && status != null){
					indicador = "[-] ";
					Grapchics.desenhaTela(indicador, 0, linhaAtual, Grapchics.VERMELHO_CLARO);
				}
				tamanhoIndicador = indicador.length();
				
				Grapchics.desenhaTTF(statusTexto.toString(), tamanhoIndicador, linhaAtual++, Grapchics.BRANCO_CLARO);
			}
		}
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
	}
	
	// ==================== AÇÕES DO JOGADOR ====================
	
	protected void selecionarComandoBatalha(){
		if (aguardandoInimigo){
			confirmarMensagemInimigo();
			Input.resetarCursor();
			Audio.tocarSom("Confirm", 0.1f);
			return;
		}
		
		if (aguardandoAliado){
			confirmarMensagemAliado();
			Input.resetarCursor();
			Audio.tocarSom("Confirm", 0.1f);
			return;
		}
		
        if (!BattleTurn.isAguardandoTurno() || !BattleTurn.isTurnoJogador()) return;
        if (skillSelecionada == null) return;
        if (BattleTurn.getUnidadeJogadorAtual() == null) return;
        
        if (!selecionarAlvo){
            selecionarAlvo = true;
			Audio.tocarSom("Confirm", 0.1f);
            return;
        }
        
        if (selecionarAlvo && !monstrosAlvos.isEmpty() && skillSelecionada != null){
            Monsters usuario = BattleTurn.getUnidadeJogadorAtual().getMonstro();
			
            if (BattleAction.verificarCustoHabilidade(usuario, skillSelecionada)){
				for (Monsters alvo : monstrosAlvos){
					BattleUnit unidadeAlvo = BattleTurn.getUnidadePorMonstro(alvo);
					if (unidadeAlvo != null) unidadeAlvo.setAlvo(true);
				}
				
				resultadoAção = BattleAction.executarHabilidade(usuario, monstrosAlvos, skillSelecionada);
				
				if (!resultadoAção.isAcerto()) Audio.tocarSom("Miss", 0.1f);
				if (resultadoAção.isAcerto() && resultadoAção.getDanoRealizado() > 0) Audio.tocarSom("Damage", 0.1f);
				
                int danoRealizado = resultadoAção.getDanoRealizado();
				String nomeMonstro = usuario.getNomeMonstro() + " usou ";
				Skills ultimaSkill = skillSelecionada;
				skillSelecionada = null;
				
				String dano = null;
				if (danoRealizado > 0) dano = ">>Causou " + danoRealizado + " de dano.";
				
				if (especialAtivo) especialAtivo = false;
				selecionarAlvo = false;
				Battle.exibirMensagemAliado(nomeMonstro, ultimaSkill, dano);
            }
        }
    }
	
	protected void recarregarEnergiaUsuário(){		
        if (!BattleTurn.isAguardandoTurno() || !BattleTurn.isTurnoJogador()) return;
        if (BattleTurn.getUnidadeJogadorAtual() == null) return;
        Monsters usuario = BattleTurn.getUnidadeJogadorAtual().getMonstro();
        
        BattleAction.recarregarEnergia(usuario);
		if (selecionarAlvo) selecionarAlvo = false;
		skillUsada = null;
		
		Audio.tocarSom("Charge", 0.1f);
		
		String frase = usuario.getNomeMonstro()+" recarrega.";
		Battle.exibirMensagemAliado(frase, null, null);
    }
	
	protected void ativarEspecial(){
		if (aguardandoAliado){
			confirmarMensagemAliado();
			Audio.tocarSom("Confirm", 0.1f);
			return;
		}
		
		if (!BattleTurn.isAguardandoTurno() || !BattleTurn.isTurnoJogador()) return;
		if (skillEspecial == null || selecionarAlvo) return;
		if (BattleTurn.getUnidadeJogadorAtual() == null) return;
		
		skillSelecionada = skillEspecial;
		
		Audio.tocarSom("Special", 0.1f);
		
		if (!selecionarAlvo && especialAtivo){
            selecionarAlvo = true;
        }
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private void verificarMonstros(){
		maxInimigos = inimigos.size();
		maxAliados = aliados.length;
		
		for (int i = inimigos.size() - 1; i >= 0; i--){
			Monsters monstro = inimigos.get(i);
			if (monstro == null) continue;
			
			if (monstro.getVidaAtualCombate() <= 0){
				inimigos.remove(i);
			}
		}
		
		for (int i = 0; i <= maxAliados-1; i++){
			if(aliados[i] == null) continue;
			
			if (aliados[i].getVidaAtualCombate() <= 0){
				aliadosDerrotados[i] = aliados[i];
				aliados[i] = null;
			}
		}
	}
	
	private boolean verificarFimBatalha(){
		if (todosInimigosDerrotados()){
			vitóriaBatalha = true;
			batalhaFinalizada = true;
			return true;
		}
		
		if (todosAliadosDerrotados()){
			vitóriaBatalha = false;
			batalhaFinalizada = true;
            return true;
        }
		
		return false;
	}
	
	private void finalizarBatalha(){
		if (vitóriaBatalha){
			Battle.setSubEstadoAtual(Battle.SubEstadosBatalha.VITORIA);
			
			int exp = tropa.getExp();
			int ouro = tropa.getOuro();
			
			maxAliados = aliados.length;
			for (int i = 0; i <= maxAliados-1; i++){
				if(aliados[i] == null) continue;
				aliados[i].ganharExp(exp);
			}
			Player.ganharOuro(ouro);
		}else{
			Battle.setSubEstadoAtual(Battle.SubEstadosBatalha.DERROTA);
		}
	}
	
	private boolean todosInimigosDerrotados(){
		for (Monsters monstro : inimigos){
			if (monstro != null && monstro.getVidaAtualCombate() > 0){
				return false;
			}
		}
		return true;
	}
	
	private boolean todosAliadosDerrotados(){
        for (Monsters monstro : aliados){
            if (monstro != null && monstro.getVidaAtualCombate() > 0){
                return false;
            }
        }
        return true;
    }
	
	private Monsters obterAliadoPorIndiceValido(int indiceCursor){
		if (indiceCursor > maxAliados-1 || indiceCursor < 0) return null;
		
		int contadorValidos = 0;
		for (int i = 0; i <= maxAliados-1; i++){
			if (aliados[i] != null){
				if (contadorValidos == indiceCursor){
					return aliados[i];
				}
				contadorValidos++;
			}
		}
		return null;
	}
	
	protected void alternarDetalhe(boolean avancar){
		if (avancar){
			if (detalheAtual == TipoDetalhe.ALIADO.getValor()){
				detalheAtual = TipoDetalhe.INIMIGO.getValor();
			}else{
				detalheAtual = TipoDetalhe.ALIADO.getValor();
			}
		}else{
			if (detalheAtual == TipoDetalhe.ALIADO.getValor()){
				detalheAtual = TipoDetalhe.INIMIGO.getValor();
			}else{
				detalheAtual = TipoDetalhe.ALIADO.getValor();
			}
		}
	}
	
	protected void setMensagemInimigo(String mensagem, Skills habilidadeUsada, String dano){
		this.mensagemTurnoInimigo = mensagem;
		if (dano != null) this.danoTurnoInimigo = dano;
		if (habilidadeUsada != null) this.skillUsada = habilidadeUsada;
		this.aguardandoInimigo = true;
	}

	protected void setMensagemAliado(String mensagem, Skills habilidadeUsada, String dano){
		this.mensagemTurnoAliado = mensagem;
		if (dano != null) this.danoTurnoAliado = dano;
		if (habilidadeUsada != null) this.skillUsada = habilidadeUsada;
		this.aguardandoAliado = true;
	}
	
	protected boolean isAguardandoConfirmação(){
		return aguardandoInimigo;
	}
	
	protected void confirmarMensagemInimigo(){
		this.mensagemTurnoInimigo = null;
		this.danoTurnoInimigo = null;
		this.aguardandoInimigo = false;
		this.resultadoAção = null;
		
		BattleTurn.finalizarTurno();
		if (this.batalhaFinalizada) finalizarBatalha();
	}
	
	protected void confirmarMensagemAliado(){
		this.mensagemTurnoAliado = null;
		this.danoTurnoAliado = null;
		this.aguardandoAliado = false;
		this.resultadoAção = null;
		
        skillSelecionada = null;
		skillUsada = null;
		BattleTurn.setAguardandoTurno(false);
		monstrosAlvos.clear();
		
		BattleTurn.finalizarTurno();
		if (this.batalhaFinalizada) finalizarBatalha();
	}
	
	protected void processarTurno(){
		verificarMonstros();
		
		if (!BattleTurn.isAguardandoTurno() && !BattleTurn.isTurnoJogador()){
			if (!isAguardandoConfirmação()){
				inimigoAI.turnoInimigo();
			}
		}
		verificarFimBatalha();
	}
	
	// ==================== OUTROS ====================
	
	protected void setSelecionarAlvo(boolean selecionarAlvo){
		this.selecionarAlvo = selecionarAlvo;
	}
	
	protected void setDetalheAtual(int detalheAtual){
		if (detalheAtual < 0) detalheAtual = 0;
		this.detalheAtual = detalheAtual;
	}
	
	protected boolean isSelecionarAlvo(){
		return this.selecionarAlvo;
	}
	
	protected boolean isAguardandoInimigo(){
		return aguardandoInimigo;
	}

	protected boolean isAguardandoAliado(){
		return aguardandoAliado;
	}
	
	protected boolean isVitóriaBatalha(){
		return vitóriaBatalha;
	}
	
	//===
}