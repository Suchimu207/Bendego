package combat;

import util.Grapchics;

import java.util.ArrayList;
import java.util.List;

import java.awt.Color;

public final class BattleResult {
    
    private BattleResult(){
    }
    
    public static void desenhaTelaVitoria(int exp, int ouro, List<BattleField.ExpGanhoResultado> resultados){
        Grapchics.limpaTela();
        
		Grapchics.desenhaCentro("Victory!!", 4, Grapchics.BRANCO_CLARO);
		
		Grapchics.desenhaCentroTTF("Ouro: "+ouro, 8, Grapchics.BRANCO_CLARO);
		
		Grapchics.desenhaCentro("____________________", 10, Grapchics.PRETO_CLARO);
        
		int linhaAtual = 13;
		
		if (resultados != null && !resultados.isEmpty()){
            for (BattleField.ExpGanhoResultado resultado : resultados){
                if (resultado == null || resultado.getMonstroRes() == null) continue;
                
                String texto = resultado.getMonstroRes().getNomeMonstro()+", EXP:+"+resultado.getExpGanho();
                Color corTexto = resultado.isPresenteNaBatalha() ? Grapchics.BRANCO_CLARO : Grapchics.PRETO_CLARO;
				
                if (resultado.getNiveisSubidos() > 0){
                    texto = resultado.getMonstroRes().getNomeMonstro()+" [Subiu "+resultado.getNiveisSubidos()+" nível!]";
                    Grapchics.desenhaCentroTTF(texto, linhaAtual++, Grapchics.VERDE_CLARO);
                }else Grapchics.desenhaCentroTTF(texto, linhaAtual++, corTexto);
            }
        }
		
        Grapchics.desenhaCentro("____________________", linhaAtual + 1, Grapchics.PRETO_CLARO);
        Grapchics.desenhaCentroTTF("[ENTER]: Continuar.", linhaAtual + 3, Grapchics.AMARELO_CLARO);
		
        Grapchics.atualizarTela();
    }
    
    public static void desenhaTelaDerrota(){
        Grapchics.limpaTela();
        Grapchics.desenhaCentro("Derrota", 10, Grapchics.BRANCO_CLARO);
		
		Grapchics.desenhaCentroTTF("[ENTER]: Continuar.", 15, Grapchics.AMARELO_CLARO);
		
        Grapchics.atualizarTela();
    }
	
	//===
}