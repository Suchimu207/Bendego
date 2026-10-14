package combat;

import main.inventory.Inventory;
import bestiary.Monsters;
import bestiary.Skills;
import bestiary.Troop;

import util.Grapchics;
import util.Input;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.awt.Color;

public final class BattlePreparation {
	private Skills skillMostrada;
	private Monsters[] monstroSlotsAtivos;
	
	private int linhaAtual, linhaInicial, linhaMax,
	posiçãoLinhaX, posiçãoLinhaY, posiçãoLinhaBatalha;
	
	protected BattlePreparation(){
	}
	
	public void desenhaTelaPreparo(){
		Grapchics.limpaTela();
		
		Battle.setMonstroMostrado(null);
		
		Troop tropaCarregada = Battle.getTropaCarregada();
		Monsters[] monstroSlotsAtivos = Battle.getMonstroSlotsAtivos();
		
		if (tropaCarregada == null || monstroSlotsAtivos == null) return;
		
		int maxSlotsAtivos = monstroSlotsAtivos.length;
		int linhaAtual = 1;
		
		Grapchics.desenhaCentroTTF("Batalha",0, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("E: Voltar", 0,linhaAtual++, Grapchics.PRETO_CLARO);
		
		if (Battle.isEquipeSetada()){
			Grapchics.desenhaTTF("Q: Iniciar batalha",0,linhaAtual++, Grapchics.PRETO_CLARO);
		}
		
		Grapchics.desenhaTTF("Enter: Escolher integrante",0,linhaAtual++, Grapchics.PRETO_CLARO);		
		Grapchics.desenhaTTF("Oponente: "+tropaCarregada.getNomeTropa(),0,linhaAtual++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		
		for (Monsters monstro : tropaCarregada.getMonstros()){
			String nomeMonstro = monstro.getNomeMonstro()+" Nv"+monstro.getNivelAtual();
			
			Grapchics.desenhaTTF(nomeMonstro,0,linhaAtual,Grapchics.BRANCO_CLARO);
			
			desenhaMonstroElementos(monstro, linhaAtual, nomeMonstro);
			linhaAtual++;
		}
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		linhaAtual += 1;
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		linhaInicial = linhaAtual;
		for (int i = 0; i <= maxSlotsAtivos-1; i++){
			Monsters monstro = monstroSlotsAtivos[i];
			if(monstro == null){
				if (Input.getCursorY() == linhaAtual){
					Grapchics.desenhaTTF("["+(i+1)+"]", 0, linhaAtual++, Grapchics.AMARELO_CLARO);
					Battle.setMonstroMostrado(null);
				}else{
					Grapchics.desenhaTTF("["+(i+1)+"]", 0, linhaAtual++, Grapchics.PRETO_CLARO);
				}
			}else{
				if (Input.getCursorY() == linhaAtual){
					Grapchics.desenhaTTF(monstro.getNomeMonstro()+" Nv"+monstro.getNivelAtual(), 0, linhaAtual++, 
					Grapchics.AMARELO_CLARO);
					Battle.setMonstroMostrado(monstro);
				}else{
					Grapchics.desenhaTTF(monstro.getNomeMonstro()+" Nv"+monstro.getNivelAtual(), 0, linhaAtual++, 
					Grapchics.BRANCO_CLARO);
				}
			}
		}
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		linhaAtual += 1;
		
		
		Grapchics.desenhaTela("____________________",0,linhaAtual++, Grapchics.PRETO_CLARO);
		List<Monsters> equipe = Inventory.getEquipeLista();
		for (Monsters monstroEquipe : equipe){
			String nomeMonstroExibido = "";
			
			if (monstroEquipe != null){
				if (Input.getCursorY() == linhaAtual){
					Battle.setMonstroMostrado(null);
					nomeMonstroExibido = monstroEquipe.getNomeMonstro()+" Nv"+monstroEquipe.getNivelAtual();
					Grapchics.desenhaTTF(nomeMonstroExibido, 0, linhaAtual++, Grapchics.AMARELO_CLARO);
					Battle.setMonstroMostrado(monstroEquipe);
				}else{
					nomeMonstroExibido = monstroEquipe.getNomeMonstro()+" Nv"+monstroEquipe.getNivelAtual();
					Grapchics.desenhaTTF(nomeMonstroExibido, 0, linhaAtual++, Grapchics.BRANCO_CLARO);
				}
			}else{
				if (Input.getCursorY() == linhaAtual){
					Grapchics.desenhaTTF("[Vazio]", 0, linhaAtual++, Grapchics.AMARELO_CLARO);
					Battle.setMonstroMostrado(null);
				}else{
					Grapchics.desenhaTTF("[Vazio]", 0, linhaAtual++, Grapchics.PRETO_CLARO);
				}
			}
		}
		linhaMax = linhaAtual;
		Grapchics.desenhaTela("____________________",0,linhaAtual, Grapchics.PRETO_CLARO);
		
		if (Input.getCursorY() > linhaMax-1){
			Input.setCursorY(linhaInicial);
		}else if (Input.getCursorY() < linhaInicial){
			Input.setCursorY(linhaMax-1);
		}
		
		Grapchics.atualizarTela();
	}
	
	private void desenhaMonstroElementos(Monsters monstro, int linhaAtual, String nomeMonstro){
		int tamanhoTexto = nomeMonstro.length();
		
		Monsters.Elementos[] elementos = monstro.getElementosAtuaisValores();
		if (elementos == null || elementos.length == 0) return;
		
		Grapchics.desenhaTTF("(", tamanhoTexto+1, linhaAtual, Grapchics.BRANCO_CLARO);
		
		int colunaX = tamanhoTexto+2;
		int tamanhoElementos = 0;
		
		for (int i = 0; i < elementos.length; i++){
			Monsters.Elementos elemento = elementos[i];
			if (elemento == null) continue;

			String nomeElemento = elemento.getElementoNome();
			tamanhoElementos += nomeElemento.length();
			
			Color corElemento = monstro.getCorDoElemento(elemento.name());
			
			Grapchics.desenhaTTF(nomeElemento, colunaX, linhaAtual, corElemento);
			colunaX += nomeElemento.length();
			
			if (i < elementos.length - 1){
				Grapchics.desenhaTela((char)47, colunaX, linhaAtual, Grapchics.BRANCO_CLARO);
				colunaX += 1;
				tamanhoElementos += 1;
			}
		}
		Grapchics.desenhaTTF(")",tamanhoTexto+tamanhoElementos+2,linhaAtual,Grapchics.BRANCO_CLARO);
	}
	
	//===
}