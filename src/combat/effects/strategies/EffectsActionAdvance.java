package combat.effects.strategies;

import bestiary.Monsters;
import bestiary.Skills;
import combat.BattleTurn;
import combat.BattleUnit;
import combat.effects.Effects;
import combat.effects.EffectsStrategy;

public class EffectsActionAdvance implements EffectsStrategy {

    @Override
    public void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno){
		if (usuario == null || alvo == null || efeito == null || turno == null) return;
		
        BattleUnit unidade = turno.getUnidadePorMonstro(alvo);
        
		int efeitoValor = efeito.getValor();
		
		if (unidade != null && efeitoValor > 0){
            if (efeitoValor >= 100){
                turno.forcarProximoTurno(unidade);
            }else{
                unidade.avancarAção(efeitoValor);
            }
        }
    }
	
    @Override
    public String getNome() {
        return "ACTION_ADVANCE";
    }
	
	//===
}