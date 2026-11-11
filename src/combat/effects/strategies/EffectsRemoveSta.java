package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;

import combat.BattleTurn;

import combat.effects.Effects;
import combat.effects.EffectsStrategy;

public class EffectsRemoveSta implements EffectsStrategy {
    
    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
        if (alvo == null || usuario == null || habilidade == null || efeito == null) return;
        
        int remoçãoPorcentagem = efeito.getValor();
		if (remoçãoPorcentagem <= 0) return;
		
		int estaminaAtual = alvo.getEstaminaAtual();
		int estaminaAtualCombate = alvo.getEstaminaAtualCombate();
		
		int remoçãoRealizada = (int) Math.ceil(estaminaAtual * (remoçãoPorcentagem / 100.0));
		
		alvo.setEstaminaAtualCombate(Math.max(0, estaminaAtualCombate-remoçãoRealizada));
    }
    
    @Override
    public String getNome(){
        return "Remover estamina";
    }
    
	//===
}