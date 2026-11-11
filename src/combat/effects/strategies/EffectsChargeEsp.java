package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;

import combat.BattleTurn;

import combat.effects.Effects;
import combat.effects.EffectsStrategy;

public class EffectsChargeEsp implements EffectsStrategy {
    
    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
        if (alvo == null || usuario == null || habilidade == null || efeito == null) return;
        
        int adição = efeito.getValor();
		if (adição <= 0) return;
		
		alvo.carregarEspecial(adição);
    }
    
    @Override
    public String getNome(){
        return "Carregar especial";
    }
    
	//===
}