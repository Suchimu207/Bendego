package util.description;

import bestiary.Skills;
import combat.effects.Effects;
import combat.status.StatusBase;
import manager.StatusManager;
import util.Grapchics;

import java.awt.Color;
import java.util.List;

public class SkillDescription {
	private static int posiçãoLinhaSkillsAtivas;
	
    private SkillDescription(){
    }
    
    public static int infoHabilidade(Skills skill, int posiçãoInicial, boolean isBatalha){
        posiçãoLinhaSkillsAtivas = posiçãoInicial;
        Skills skillMostrada = skill;
        
        if (skillMostrada != null){
			if (!isBatalha){
				Grapchics.desenhaTTF("Alvo: " + skillMostrada.getAlvoHabilidade(), 0, posiçãoLinhaSkillsAtivas++, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
			}
			
            if (skillMostrada.getPoderHabilidade() > 0){
                Grapchics.desenhaTTF("Poder: " + skillMostrada.getPoderHabilidade(), 0, posiçãoLinhaSkillsAtivas++, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
            }
            if (skillMostrada.getPrecisaoBase() > 0){
                Grapchics.desenhaTTF("Precisão: " + skillMostrada.getPrecisaoBase(), 0, posiçãoLinhaSkillsAtivas++, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
            }
            if (skillMostrada.getEstaminaHabilidade() > 0){
                Grapchics.desenhaTTF("Estamina: ", 0, posiçãoLinhaSkillsAtivas, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
				Grapchics.desenhaTTF(""+skillMostrada.getEstaminaHabilidade(), 10, posiçãoLinhaSkillsAtivas++, Grapchics.AZUL_CLARO, Grapchics.FUNDO);
            }
            if (skillMostrada.getRecargaHabilidade() > 0){
                Grapchics.desenhaTTF("Recarga: " + skillMostrada.getRecargaHabilidade(), 0, posiçãoLinhaSkillsAtivas++, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
            }
			
            List<Effects> efeitos = skillMostrada.getEfeitos();
            if (efeitos != null && !efeitos.isEmpty()){
				posiçãoLinhaSkillsAtivas++;
				Grapchics.desenhaTela("---", 0, posiçãoLinhaSkillsAtivas++, Grapchics.PRETO_CLARO);
                for (Effects efeito : efeitos){
                    String textoEfeito;
                    
                    if ("APPLY_STATUS".equalsIgnoreCase(efeito.getTipo())){
                        formatarAplicarStatus(efeito);
                    }else{
                        textoEfeito = formatarEfeitoPadrão(efeito);
						Grapchics.desenhaTTF(textoEfeito, 0, posiçãoLinhaSkillsAtivas++, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
                    }
                }
            }
        }
		return posiçãoLinhaSkillsAtivas;
    }
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private static void formatarAplicarStatus(Effects efeito){
        StatusBase status = StatusManager.getStatusPorId(efeito.getValor());
        String nomeStatus = (status != null) ? status.getNome() : "STATUS_" + efeito.getValor();
        
        Color corStatus = Grapchics.BRANCO_CLARO;
        if (status != null){
            corStatus = status.isPositivo() ? Grapchics.VERDE_CLARO : Grapchics.VERMELHO_CLARO;
        }
        
        Grapchics.desenhaTTF(nomeStatus, 0, posiçãoLinhaSkillsAtivas, corStatus, Grapchics.FUNDO);
        
        StringBuilder complemento = new StringBuilder(" (");
        int infoQuant = 0;
        
        if (efeito.getChance() > 0 && efeito.getChance() < 100){
            complemento.append(efeito.getChance()).append("%");
            infoQuant++;
        }
        
        String alvoTexto = getAlvoTexto(efeito.getAlvo());
        if (!alvoTexto.isEmpty()){
            if (infoQuant > 0) complemento.append(", ");
            complemento.append(alvoTexto);
            infoQuant++;
        }
        
        if (efeito.getTurnos() > 0){
            if (infoQuant > 0) complemento.append(", ");
            complemento.append(efeito.getTurnos()).append("t");
        }
        
        complemento.append(")");
        
        int posXComplemento = nomeStatus.length();
        Grapchics.desenhaTTF(complemento.toString(), posXComplemento, posiçãoLinhaSkillsAtivas++, Grapchics.BRANCO_CLARO, Grapchics.FUNDO);
    }
	
	private static String formatarEfeitoPadrão(Effects efeito){
		String tipoRaw = efeito.getTipo();
		String tipoFormatado = "";
		
		if (tipoRaw != null && !tipoRaw.isEmpty()){
			tipoFormatado = tipoRaw.substring(0, 1).toUpperCase()+tipoRaw.substring(1).toLowerCase().replace("_", " ");;
		}
		
        StringBuilder info = new StringBuilder(tipoFormatado);
        int infoQuant = 0;
        
        info.append(" (");
        
        if (efeito.getChance() > 0 && efeito.getChance() < 100){
            info.append(efeito.getChance()).append("%");
            infoQuant++;
        }
        if (efeito.getValor() > 0){
            if (infoQuant > 0) info.append(", ");
            info.append(efeito.getValor());
            infoQuant++;
        }
        
        String alvoTexto = getAlvoTexto(efeito.getAlvo());
        if (!alvoTexto.isEmpty()){
            if (infoQuant > 0) info.append(", ");
            info.append(alvoTexto);
            infoQuant++;
        }
        
        if (efeito.getTurnos() > 0){
            if (infoQuant > 0) info.append(", ");
            info.append(efeito.getTurnos()).append("t");
        }
        
        info.append(")");
        return info.toString();
    }
	
	private static String getAlvoTexto(int alvo){
        switch (alvo){
            case Effects.MESMO_ALVO: return "MESMO ALVO";
            case Effects.ALIADO_UNICO: return "ALIADO UNICO";
            case Effects.ALIADO_AREA: return "ALIADO AREA";
            case Effects.INIMIGO_UNICO: return "INIMIGO UNICO";
            case Effects.INIMIGO_AREA: return "INIMIGO AREA";
            case Effects.USUARIO: return "USUARIO";
            default: return "";
        }
    }
	
    //===
}