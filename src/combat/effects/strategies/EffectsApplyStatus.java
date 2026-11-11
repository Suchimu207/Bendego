package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;

import combat.effects.Effects;
import combat.effects.EffectsStrategy;

import manager.StatusManager;
import combat.BattleTurn;
import combat.status.StatusBase;

import java.util.Random;

public class EffectsApplyStatus implements EffectsStrategy {
	private static final Random random = new Random();
	
    private int efeitoValor, efeitoTurnos, efeitoChance;
	
	private Monsters usuario, alvo;
	private Skills habilidade;
	private Effects efeito;
	private StatusBase status;
	
	public EffectsApplyStatus(){}
	
    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
        if (usuario == null || alvo == null || efeito == null) return;
		
		this.usuario = usuario;
		this.alvo = alvo;
		this.habilidade = habilidade;
		this.efeito = efeito;
		
		efeitoValor = this.efeito.getValor();
		efeitoTurnos = this.efeito.getTurnos();
		efeitoChance = this.efeito.getChance();
		
		this.status = StatusManager.getStatusPorId(efeitoValor);
		
		if (this.status == null || efeitoTurnos <= 0 || this.alvo.isImune(status) || efeitoChance <= 0) return;
		
		if (this.status.isPositivo()){
			processarStatus(alvo, status, efeitoValor, efeitoTurnos);
		}else{
			if (!calcularChance(alvo, efeitoChance)){
				return;
			}
			processarStatus(alvo, status, efeitoValor, efeitoTurnos);
		}
    }
    
	private boolean calcularChance(Monsters alvo, int efeitoChance){
		int chanceBase = Math.max(1, Math.min(100, efeitoChance));
		int statusArmor = alvo.getStatusArmor();
		
		int chanceFinal = Math.max(1, chanceBase - statusArmor);
		int roll = random.nextInt(100) + 1;
		
		return roll <= chanceFinal;
	}
	
	private void processarStatus(Monsters alvo, StatusBase status, int efeitoValor, int efeitoTurnos){
		if (!alvo.possuiStatus(status)){
			status.aplicar(alvo, efeitoTurnos);
		}else if(alvo.possuiStatus(status)){
			StatusBase statusExistente = alvo.getStatus(efeitoValor);
            if (statusExistente != null) statusExistente.renovarDuração();
		}
	}
	
    @Override
    public String getNome(){
        return "APPLY_STATUS";
    }
    
	//===
}