package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;

import combat.effects.Effects;
import combat.effects.EffectsStrategy;

import manager.StatusManager;
import combat.BattleTurn;
import combat.status.StatusBase;

public class EffectsApplyShield implements EffectsStrategy {
	private static final int LIMITE_ESCUDO = Integer.MAX_VALUE;	
	private StatusBase status;
	
    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
        if (alvo == null || usuario == null || efeito == null) return;
        
        int efeitoValor = efeito.getValor();
		
		int vidaAtual = usuario.getVidaAtualCombateMaxima();
		
		if (alvo.getEscudoAtual() == LIMITE_ESCUDO) return;
		
		status = StatusManager.getStatusPorId(0);
		if (status == null) return;
		
		if (!alvo.possuiStatus(status)){
			status.aplicar(alvo, 3);
        }else{
            StatusBase statusExistente = alvo.getStatus(0);
            if (statusExistente != null) statusExistente.renovarDuração();
        }
		
        int valorEscudo = (int) Math.ceil(vidaAtual * (efeitoValor / 100.0));
		
		int novoEscudo = Math.min(LIMITE_ESCUDO, alvo.getEscudoAtual()+valorEscudo);
		
		alvo.setEscudoAtual(novoEscudo);
    }
    
    @Override
    public String getNome(){
        return "APPLY_SHIELD";
    }
    
	//===
}