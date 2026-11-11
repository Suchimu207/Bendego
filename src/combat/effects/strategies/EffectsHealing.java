package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;

import combat.BattleTurn;

import combat.effects.Effects;
import combat.effects.EffectsStrategy;

public class EffectsHealing implements EffectsStrategy {
    
    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
        if (alvo == null || usuario == null || efeito == null) return;
        
        int curaPorcentagem = efeito.getValor();
		if (curaPorcentagem <= 0) return;
		
		int vidaAtual = alvo.getVidaAtualCombateMaxima();
		int vidaAtualCombate = alvo.getVidaAtualCombate();
        if (vidaAtualCombate <= 0) return;
		
		int curaRealizada = (int) Math.ceil(vidaAtual * (curaPorcentagem / 100.0));
		
		alvo.setVidaAtualCombate(Math.min(vidaAtual, vidaAtualCombate+curaRealizada));
    }
    
    @Override
    public String getNome(){
        return "CURA";
    }
    
	//===
}