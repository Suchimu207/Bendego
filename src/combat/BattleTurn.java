package combat;

import bestiary.Monsters;
import bestiary.Skills;

import java.util.*;

public final class BattleTurn {
	private List<BattleUnit> unidades = new ArrayList<>();
	private BattleUnit unidadeAtual, unidadeJogadorAtual;
	private int avAtual;
	private boolean turnoJogador, aguardandoTurno;
	
	private final Battle batalha; 
	
	private BattleUnit proximaUnidadeForçada = null;
	
	protected BattleTurn(Battle batalha){
		this.batalha = batalha;
		this.unidades = new ArrayList<>();
	}
	
	// avançarTurno() → Jogador/IA age → finalizarTurno() → turnoRealizado() → [Começo]
	
	protected void ordenarActionValue(List<BattleUnit> unidades){
        Collections.sort(unidades, (u1, u2) ->{
            int compare = Integer.compare(u1.getActionValue(), u2.getActionValue());
            if (compare == 0){
                // Desempate: aliados primeiro, depois inimigos.
                if (u1.isAliado() != u2.isAliado()){
                    return u1.isAliado() ? -1 : 1;
                }
                return Integer.compare(u1.getId(), u2.getId());
            }
            return compare;
        });
    }
	
    protected void avançarTurno(){
        if (unidades.isEmpty()) return;
        
		if (proximaUnidadeForçada != null && unidades.contains(proximaUnidadeForçada)){
            unidadeAtual = proximaUnidadeForçada;
            proximaUnidadeForçada = null;
        }else{
			ordenarActionValue(unidades);
			unidadeAtual = unidades.get(0);
		}
		
        avAtual = unidadeAtual.getActionValue();
		
		Monsters monstro = unidadeAtual.getMonstro();
		monstro.checarStatus();
		
		turnoJogador = unidadeAtual.isAliado();
		if (turnoJogador){
			unidadeJogadorAtual = unidadeAtual;
            aguardandoTurno = true;
        }else{
			aguardandoTurno = false;
		}
		
		if (!monstro.podeAgir()){
			String frase = monstro.getNomeMonstro()+" perdeu a rodada.";
			if (unidadeAtual.isAliado()){
				batalha.exibirMensagemAliado(frase, null, null);
			}else{
				batalha.exibirMensagemInimigo(frase, null, null);
			}
			return;
		}
    }
	
	protected void finalizarTurno(){
		Monsters monstro = unidadeAtual.getMonstro();
		
		monstro.reduzirDuraçãoStatus();
		
		unidades.removeIf(u -> u.getMonstro().getVidaAtualCombate() <= 0);
		
		controlarRecargaHabilidade();
		
		for (BattleUnit u : unidades){
			u.setAlvo(false);
		}
		
		turnoRealizado();
        avançarTurno();
    }
	
	private void turnoRealizado(){
		for (BattleUnit user : unidades){
			if (user != unidadeAtual){
				user.setActionValueAtual(user.getActionValue() - avAtual);
			}
		}
		unidadeAtual.setActionValueAtual(unidadeAtual.calcularActionValue());
	}
	
	private void controlarRecargaHabilidade(){
		if (unidadeAtual != null){
			Monsters monstro = unidadeAtual.getMonstro();
			monstro.reduzirRecargaHabilidades();
		}
	}
	
	public void forcarProximoTurno(BattleUnit unidade){
        proximaUnidadeForçada = unidade;
    }
	
	protected List<BattleUnit> getUnidades(){
		return unidades;
	}	
	
	protected BattleUnit getUnidadeAtual(){
		return unidadeAtual;
	}
	
	protected BattleUnit getUnidadeJogadorAtual(){
		return unidadeJogadorAtual;
	}
	
	public BattleUnit getUnidadePorMonstro(Monsters monstro){
		for (BattleUnit unidade : unidades){
			if (unidade.getMonstro() == monstro){
				return unidade;
			}
		}
		return null;
	}
	
	protected boolean isTurnoJogador(){
		return turnoJogador;
	}
	
	protected boolean isAguardandoTurno(){
		return aguardandoTurno;
	}
	
	protected void setAguardandoTurno(Boolean aguardandoTurno){
		this.aguardandoTurno = aguardandoTurno;
	}
	
	//===
}