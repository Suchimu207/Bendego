package combat;

import bestiary.Monsters;
import bestiary.Skills;
import combat.status.StatusBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.Audio;

public final class BattleAI {
	// ==================== ATRIBUTOS ====================
	
	private static final Random random = new Random();
	private static final int PROVOCATION_STATUS_ID = 11;
	
	private final Battle batalha;
	private final BattleTurn turno;
	
	private BattleUnit unidadeAlvo;
	private Skills ultimaSkill = null;
	private BattleActionResult resultadoAção = null;
	
	protected BattleAI(Battle batalha, BattleTurn turno){
		this.batalha = batalha;
		this.turno = turno;
	}
	
	// ==================== AÇÕES DO INIMIGO ====================
	
	protected void turnoInimigo(){
		limparEstadoAnterior();
		
		BattleUnit unidadeAtual = turno.getUnidadeAtual();
        if (unidadeAtual == null) return;
        
        Monsters monstroInimigo = unidadeAtual.getMonstro();
		if (monstroInimigo == null) return;
		
		if (monstroInimigo.getEstaminaAtualCombate() <= 0){
			recarregarEnergiaUsuário(monstroInimigo);
			return;
		}
		
		Skills habilidade = getHabilidades(monstroInimigo);
		
		if (habilidade != null){
			List<Monsters> alvos = getAlvosHabilidade(monstroInimigo, habilidade);
			
			if (!alvos.isEmpty()){
				for (Monsters alvo : alvos){
					this.unidadeAlvo = turno.getUnidadePorMonstro(alvo);
					if (unidadeAlvo != null) unidadeAlvo.setAlvo(true);
				}
				
				this.resultadoAção = BattleAction.executarHabilidade(monstroInimigo, alvos, habilidade, turno);
				
				if (!this.resultadoAção.isAcerto()) Audio.tocarSom("Miss", 0.1f);
				if (this.resultadoAção.isAcerto() && this.resultadoAção.getDanoRealizado() > 0) Audio.tocarSom("Damage", 0.1f);
				
				int danoRealizado = resultadoAção.getDanoRealizado();
				String nomeMonstro = monstroInimigo.getNomeMonstro()+" usou ";
				this.ultimaSkill = habilidade;
				
				String dano = null;
				if (danoRealizado > 0) dano = ">>Causou "+danoRealizado+" de dano.";
				
				batalha.exibirMensagemInimigo(nomeMonstro, ultimaSkill, dano);
				alvos.clear();
				return;
			}
		}
		
		recarregarEnergiaUsuário(monstroInimigo);
    }
	
	private void recarregarEnergiaUsuário(Monsters monstro){
		BattleAction.recarregarEnergia(monstro);
		Audio.tocarSom("Charge", 0.1f);
		
		String frase = monstro.getNomeMonstro()+ " recarrega.";
		batalha.exibirMensagemInimigo(frase,null,null);
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	private void limparEstadoAnterior(){
        this.ultimaSkill = null;
        this.resultadoAção = null;
        this.unidadeAlvo = null;
    }
	
	private Skills getHabilidades(Monsters monstro){
        List<Skills> habilidadesValidas = new ArrayList<>();
        
        for (int i = 0; i < monstro.getQuantidadeMaxSlotsHabilidade(); i++){
            Skills skill = monstro.getHabilidadeAtiva(i);
            
            if (skill != null && BattleAction.verificarCustoHabilidade(monstro, skill)){
                habilidadesValidas.add(skill);
            }
        }
        
        if (habilidadesValidas.isEmpty()) return null;
        
        return habilidadesValidas.get(random.nextInt(habilidadesValidas.size()));
    }
	
	private List<Monsters> getAlvosHabilidade(Monsters usuario, Skills habilidade){
		List<Monsters> alvos = new ArrayList<>();
		
		List<Monsters> timeJogador = new ArrayList<>();
		List<Monsters> timeInimigo = new ArrayList<>(); 
		
		for (BattleUnit u : turno.getUnidades()){
			if (u.getMonstro() != null && u.getMonstro().getVidaAtualCombate() > 0){
				if (u.isAliado()){
					timeJogador.add(u.getMonstro());
				}else{
					timeInimigo.add(u.getMonstro());
				}
			}
		}
    
		Skills.TipoAlvo tipoAlvo = habilidade.getAlvoHabilidadeTipo();
    
		switch (tipoAlvo){
			case INIMIGO_UNICO:
				if (!timeJogador.isEmpty()){
					List<Monsters> alvosComProvocacao = new ArrayList<>();
					for (Monsters monstro : timeJogador){
						if (monstro.possuiStatus(PROVOCATION_STATUS_ID)){
							alvosComProvocacao.add(monstro);
						}
					}
					
					if (!alvosComProvocacao.isEmpty()){
						alvos.add(alvosComProvocacao.get(random.nextInt(alvosComProvocacao.size())));
					}else{
						int totalProvocacao = 0;
						for (Monsters monstro : timeJogador){
							totalProvocacao += monstro.getProvocationRate();
						}
						
						if (totalProvocacao > 0){
							int valorSorteado = random.nextInt(totalProvocacao);
							int acumulador = 0;
							for (Monsters monstro : timeJogador){
								acumulador += monstro.getProvocationRate();
								if (valorSorteado < acumulador){
									alvos.add(monstro);
									break; 
								}
							}
						}else{
							alvos.add(timeJogador.get(random.nextInt(timeJogador.size())));
						}
					}
				}
				break;
			case ALIADO_UNICO:
				if (!timeInimigo.isEmpty()) alvos.add(timeInimigo.get(random.nextInt(timeInimigo.size())));
				break;
			case USUARIO:
				alvos.add(usuario);
				break;
			case INIMIGO_AREA:
				alvos.addAll(timeJogador);
				break;
			case ALIADO_AREA:
				alvos.addAll(timeInimigo);
				break;
			case CAMPO:
				alvos.addAll(timeJogador);
				alvos.addAll(timeInimigo);
				break;
		}
		return alvos;
	}
	
	// ==================== OUTROS ====================
	
	protected BattleActionResult getResultadoAção(){
		return this.resultadoAção;
	}
	
	protected void setUltimaSkill(Skills ultimaSkill){
		this.ultimaSkill = ultimaSkill;
	}
	
	protected void setResultadoAção(BattleActionResult resultadoAção){
		this.resultadoAção = resultadoAção;
	}
	
    //===
}