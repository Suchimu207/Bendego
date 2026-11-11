package combat;

import bestiary.Monsters;
import bestiary.Skills;

import combat.effects.Effects;
import combat.effects.EffectsManager;
import combat.effects.EffectsStrategy;

import combat.status.StatusBase;
import combat.status.strategies.StatusFraquezaElemental;

import java.util.List;
import java.util.ArrayList;

import java.util.Random;

public final class BattleAction {
	private static final int CONSTANTE = 10;
	private static final Random random = new Random();
	
	private static int[] resultados = new int[3];
	
	private BattleAction(){
	}
	
	protected static void recarregarEnergia(Monsters usuario){
		int maxEstamina = usuario.getEstaminaAtual();
		int regneração = (int) Math.ceil(maxEstamina * 0.5); 
		int novaEstamina = regneração + usuario.getEstaminaAtualCombate();
		
		usuario.ganharEstamina(novaEstamina);
	}
	
	// ==================== VERIFICAÇÃO ====================
	
	protected static boolean verificarCustoHabilidade(Monsters usuario, Skills habilidade){
		if (usuario == null || habilidade == null) return false;
		
		int estaminaAtualCombate = usuario.getEstaminaAtualCombate();
		int energiaHabilidade = habilidade.getEnergiaHabilidade();
		
		if (estaminaAtualCombate >= energiaHabilidade){
			return true;
		}
		
		return false;
	}
	
	private static boolean verificarPrecisao(Monsters usuario, List<Monsters> alvos, Skills habilidade){
		if (usuario == null || habilidade == null) return false;
		
		if (habilidade.getTipoHabilidade() == Skills.TipoHabilidade.ESPECIAL || 
		habilidade.getTipoHabilidade() == Skills.TipoHabilidade.DEFENSIVA) return true;
		
		int precisaoBase = habilidade.getPrecisaoBase();
		int precisaoAtual = habilidade.getPrecisaoAtual();
		
		if (precisaoBase <= 0 || precisaoAtual <= 0) return false;
		
		int roll = random.nextInt(100) + 1;
        return roll <= precisaoAtual;
	}
	
	// ==================== EXECUÇÃO ====================
	
    protected static BattleActionResult executarHabilidade(Monsters usuario, List<Monsters> alvos, Skills habilidade, BattleTurn turno){		
		int estaminaAtualCombate = usuario.getEstaminaAtualCombate();
		int energiaHabilidade = habilidade.getEnergiaHabilidade();
		resultados[0] = 0;
		resultados[1] = BattleActionResult.EFETIVO;
		
        usuario.setEstaminaAtualCombate(estaminaAtualCombate - energiaHabilidade);
        
		if (!verificarPrecisao(usuario, alvos, habilidade)){
			return new BattleActionResult(resultados[0], resultados[1], false);
		}
		
        if (habilidade.getPoderHabilidade() > 0) resultados[0] = calcularDano(usuario, alvos, habilidade);
		
		aplicarEfeitos(usuario, alvos, habilidade, turno);
		
		carregarEspecialMonstros(usuario, alvos, habilidade);
		
		habilidade.ativarRecarga();
		return new BattleActionResult(resultados[0], resultados[1], true);
    }
	
	private static void carregarEspecialMonstros(Monsters usuario, List<Monsters> alvos, Skills habilidade){
		Skills.TipoAlvo alvo = habilidade.getAlvoHabilidadeValor();
		
		switch(alvo){
			case Skills.TipoAlvo.USUARIO:
				usuario.carregarEspecial(2);
			break;
			default:
				usuario.carregarEspecial(5);
				for (Monsters monstro : alvos){
					if (monstro.getClasseAtual() == Monsters.Classes.TANQUE){
						monstro.carregarEspecial(4);
					}else monstro.carregarEspecial(2);
				}
			break;
		}
		
		if (habilidade.isTipoEspecial(habilidade.getTipoHabilidade())){
			usuario.zerarEspecial();
		}
	}
	
	// ==================== CÁLCULO ====================
	
	private static int calcularDano(Monsters usuario, List<Monsters> alvos, Skills habilidade){
		int forçaMonstro = usuario.getForcaAtualCombate();
		int poderHabilidade = habilidade.getPoderHabilidade();
		int nivelMonstro = usuario.getNivelAtual();
		double danoAumentado = usuario.getDamageBonus();
		
		Monsters.Elementos elementoAtaque = habilidade.getElementoHabilidadeTipo();
		
		if (elementoAtaque != null && usuario.getElementosAtuaisValores() != null){
			for (Monsters.Elementos elementoUsuario : usuario.getElementosAtuaisValores()){
				if (elementoUsuario == elementoAtaque){
					poderHabilidade += 5;
					break;
				}
			}
		}
		
		double multiplicadorNivel = 1.0 + (nivelMonstro / 100.0);
        double danoBase = Math.ceil((forçaMonstro / 1000.0) * (poderHabilidade) * CONSTANTE * multiplicadorNivel);
		int danoSomado = 0;
		
		for (Monsters monstro : alvos){
			regenerarEstamina(monstro, elementoAtaque);
			
			double multiplicadorElemental = calcularMultiplicadorElemental(monstro, elementoAtaque);
			
			if (multiplicadorElemental == 1.0){
				resultados[1] = BattleActionResult.EFETIVO;
			}else if (multiplicadorElemental < 1.0){
				resultados[1] = BattleActionResult.NÃO_EFETIVO;
			}else if (multiplicadorElemental > 1.0){
				resultados[1] = BattleActionResult.SUPER_EFETIVO;
			}
			
			int danoFinal = (int) ((danoBase * danoAumentado) * multiplicadorElemental);
			danoSomado += danoFinal;
			monstro.perderVida(danoFinal);
			
			if (habilidade.getLifeSteal() > 0){
				int cura = (int) Math.ceil(danoFinal * (habilidade.getLifeSteal() / 100.0));
				usuario.ganharVida(cura);
			}
			
		}
		return danoSomado;
    }
	
	private static double calcularMultiplicadorElemental(Monsters monstro, Monsters.Elementos elementoAtaque){
		double multiplicadorElemental = 1.0;
		
		for (Monsters.Elementos elementoDefensor : monstro.getElementosAtuaisValores()){
			multiplicadorElemental *= elementoAtaque.getMultiplicadorDano(elementoDefensor);
		}
		
		for (StatusBase status : monstro.getStatusAtuais()){
			if (status instanceof StatusFraquezaElemental fraq && fraq.isAtivo()){
				if (fraq.getElementoFraqueza() == elementoAtaque){
					multiplicadorElemental += 0.5;
				}
			}
		}
		
		return multiplicadorElemental;
	}
	
	// ==================== OUTROS ====================
	
	private static void regenerarEstamina(Monsters monstro, Monsters.Elementos elementoAtaque){
		if (monstro.getHarmonizado() <= 0) return;
		
		int harmonizado = monstro.getHarmonizado();
		
		for (Monsters.Elementos elementoDefensor : monstro.getElementosAtuaisValores()){
			if (elementoAtaque.temFraquezaContra(elementoDefensor) || elementoDefensor == elementoAtaque){
				int novaEstamina = (int) Math.ceil(monstro.getEstaminaAtual() * (harmonizado / 100.0));
				monstro.ganharEstamina(novaEstamina);
			}
		}
	}
	
	private static void aplicarEfeitos(Monsters usuario, List<Monsters> alvosHabilidade, Skills habilidade, BattleTurn turno){
		for (Effects dados : habilidade.getEfeitos()){
			EffectsStrategy efeito = EffectsManager.getEfeito(dados.getTipo());
			if (efeito != null){
				List<Monsters> alvosDoEfeito = determinarAlvosEfeito(usuario, alvosHabilidade, dados.getAlvo(), turno);
				
				for (Monsters monstro : alvosDoEfeito){
					if (monstro != null && monstro.getVidaAtualCombate() > 0){
						efeito.aplicar(usuario, monstro, habilidade, dados, turno);
					}
				}
			}
		}
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private static List<Monsters> determinarAlvosEfeito(Monsters usuario, List<Monsters> alvosHabilidade, int alvoEfeito, BattleTurn turno){
		List<Monsters> alvos = new ArrayList<>();
		
		List<Monsters> aliadosVivos = new ArrayList<>();
		List<Monsters> inimigosVivos = new ArrayList<>();
		
		for (BattleUnit unidade : turno.getUnidades()){
			Monsters monstro = unidade.getMonstro();
			if (monstro != null && monstro.getVidaAtualCombate() > 0){
				if (unidade.isAliado()){
					aliadosVivos.add(monstro);
				}else{
					inimigosVivos.add(monstro);
				}
			}
		}
		
		if (alvoEfeito == Effects.MESMO_ALVO){
			return alvosHabilidade;
		}else if (alvoEfeito == Effects.ALIADO_UNICO || alvoEfeito == Effects.INIMIGO_UNICO){
			return alvosHabilidade.isEmpty() ? alvos : alvosHabilidade;
		}else if (alvoEfeito == Effects.ALIADO_AREA){
			alvos.addAll(aliadosVivos);
		}else if (alvoEfeito == Effects.INIMIGO_AREA){
			alvos.addAll(inimigosVivos);
		}else if (alvoEfeito == Effects.USUARIO){
			alvos.add(usuario);
		}else{
			alvos.addAll(alvosHabilidade);
		}		
		return alvos;
	}
	
	//===
}