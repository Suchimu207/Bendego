package modes.arena;

import bestiary.Troop;
import manager.TroopManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tournament {
	private final String nomeTorneio;
	private final int torneiosRequeridos;
    private final Map<Integer, Troop> batalhas;
	private final int recompensaMonstro;
	private boolean concluido;
	
    public Tournament(String nomeTorneio, int torneiosRequeridos, int[] tropas, int recompensaMonstro){
		this.nomeTorneio = nomeTorneio;
		this.torneiosRequeridos = torneiosRequeridos;
        this.batalhas = new LinkedHashMap<>();
		this.recompensaMonstro = recompensaMonstro;
		this.concluido = false;
		
        setarBatalhas(tropas);
    }
	
    private void setarBatalhas(int[] tropas){
		if (tropas == null) return;
		
       for (int i = 0; i < tropas.length; i++){
            int idTropa = tropas[i];
            int rodada = i + 1;
            this.batalhas.put(rodada, TroopManager.getTroop(idTropa));
        }
    }
	
	protected String getNomeTorneio(){
        return this.nomeTorneio;
    }
	
	protected int getTorneiosRequeridos(){
		return this.torneiosRequeridos;
	}
	
    protected Troop getBatalha(int chave){
        return this.batalhas.get(chave);
    }
	
    protected int getTotalBatalhas(){
        return this.batalhas.size();
    }
	
	protected int getRecompensaMonstro(){
        return this.recompensaMonstro;
    }
	
	protected boolean isConcluido(){
		return this.concluido;
	}
	
	protected void setConcluido(boolean concluido){
		this.concluido = concluido;
	}
	
	//===
}