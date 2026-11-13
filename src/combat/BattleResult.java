package combat;

import util.Grapchics;

public final class BattleResult {
    
    private BattleResult(){
    }
    
    public static void desenhaTelaVitoria(int exp, int ouro){
        Grapchics.limpaTela();
        Grapchics.desenhaCentro("Victory!!", 10, Grapchics.BRANCO_CLARO);
		
		Grapchics.desenhaCentroTTF("EXP: "+exp, 15, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaCentroTTF("Ouro: "+ouro, 16, Grapchics.BRANCO_CLARO);
		
        Grapchics.atualizarTela();
    }
    
    public static void desenhaTelaDerrota(){
        Grapchics.limpaTela();
        Grapchics.desenhaCentroTTF("Derrota", 10, Grapchics.BRANCO_CLARO);
        Grapchics.atualizarTela();
    }
	
	//===
}