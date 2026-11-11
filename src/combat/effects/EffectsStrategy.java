package combat.effects;

import bestiary.Monsters;
import bestiary.Skills;

import combat.BattleTurn;

public interface EffectsStrategy {
    void aplicar(Monsters usuario, Monsters alvo, Skills habilidade, Effects efeito, BattleTurn turno);
    String getNome();
}