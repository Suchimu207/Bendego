package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;

import combat.BattleTurn;

import combat.effects.Effects;
import combat.effects.EffectsStrategy;

import manager.StatusManager;
import combat.status.StatusBase;

public class EffectsRemoveNegativeStatus implements EffectsStrategy {
	
    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
        if (alvo == null || usuario == null || habilidade == null || efeito == null) return;
		
		alvo.getStatusAtuais().removeIf(status -> 
            status != null && status.isAtivo() && !status.isPositivo()
        );
    }
	
    @Override
    public String getNome(){
        return "REMOVE_NEGATIVE_STATUS";
    }
    
	//===
}