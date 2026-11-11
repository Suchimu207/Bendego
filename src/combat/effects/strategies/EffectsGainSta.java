package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;

import combat.BattleTurn;

import combat.effects.Effects;
import combat.effects.EffectsStrategy;

public class EffectsGainSta implements EffectsStrategy {
    
    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
        if (alvo == null || usuario == null || habilidade == null || efeito == null) return;
        
        int adiçãoPorcentagem = efeito.getValor();
		if (adiçãoPorcentagem <= 0) return;
		
		int estaminaAtual = alvo.getEstaminaAtual();
		int estaminaAtualCombate = alvo.getEstaminaAtualCombate();
		
		int adiçãoRealizada = (int) Math.ceil(estaminaAtual * (adiçãoPorcentagem / 100.0));
		
		alvo.ganharEstamina(adiçãoRealizada);
    }
    
    @Override
    public String getNome(){
        return "Ganhar estamina";
    }
    
	//===
}