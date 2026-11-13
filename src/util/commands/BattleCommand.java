package util.commands;

import bestiary.*;
import combat.*;
import combat.effects.*;
import combat.effects.strategies.*;
import main.Terminal;

import java.util.List;

public class BattleCommand implements Command {
    private Battle batalhaAtual;
	private Monsters[] aliados;
	private List<Monsters> inimigos;
	
    public BattleCommand() {}
    
    @Override
    public void executar(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("\n"+getUso());
            return;
        }
		
		if (Terminal.getEstadoAtual() instanceof Battle){
			batalhaAtual = (Battle) Terminal.getEstadoAtual();
			aliados = batalhaAtual.getMonstrosAliados();
			inimigos = batalhaAtual.getMonstrosInimigos().getMonstros();
		}else{
			System.out.println(">>Você não está em uma batalha no momento.");
			batalhaAtual = null;
			return;
		}			
		
        String subcomando = args[0];
        
        switch (subcomando){
			case "listUnit":
				if (args.length < 2){
                    System.out.println("Uso: battle listUnit <id_Campo>");
                    return;
                }
				int id = Integer.parseInt(args[1]);
				
				if (aliados == null || id < 0 || inimigos == null || aliados.length <= 0 || inimigos.size() <= 0){
					System.out.println(">>Monstro (ID_CAMPO:"+id+") não encontrado.");
					return;
				}
				
				switch (id){
					case 0:
					case 1:
					if (aliados[0] == null){
						return;
					}
					Monsters monstroAliado = aliados[0];
					listarMonstro(monstroAliado, true);
					break;
					
					case 2:
					if (aliados[1] == null){
						return;
					}
					monstroAliado = aliados[1];
					listarMonstro(monstroAliado, true);
					break;
					
					case 3:
					if (aliados[2] == null){
						return;
					}
					monstroAliado = aliados[2];
					listarMonstro(monstroAliado, true);
					break;
					
					case 4:
					if (inimigos.get(0) == null){
						return;
					}
					Monsters monstroInimigo = inimigos.get(0);
					listarMonstro(monstroInimigo, false);
					break;
					
					case 5:
					if (inimigos.get(1) == null){
						return;
					}
					monstroInimigo = inimigos.get(1);
					listarMonstro(monstroInimigo, false);
					break;
					
					case 6:
					if (inimigos.get(2) == null){
						return;
					}
					monstroInimigo = inimigos.get(2);
					listarMonstro(monstroInimigo, false);
					break;
				}
				break;
			    
            case "applyStatus":
				if (args.length < 4){
                    System.out.println("Uso: battle applyStatus <id_Campo> <id_Status> <duração>");
                    return;
                }
				
				int idCampo = Integer.parseInt(args[1]);
				int idStatus = Integer.parseInt(args[2]);
				int duracao = Integer.parseInt(args[3]);
				
				if (aliados == null || idCampo < 0 || inimigos == null || aliados.length <= 0 || inimigos.size() <= 0){
					System.out.println(">>Monstro (ID_CAMPO:"+idCampo+") não encontrado.");
					return;
				}
				
				Monsters alvo = null;
				
				switch (idCampo){
					case 0:
					case 1:
						if (aliados[0] != null) alvo = aliados[0];
						break;
					case 2:
						if (aliados[1] != null) alvo = aliados[1];
						break;
					case 3:
						if (aliados[2] != null) alvo = aliados[2];
						break;
					case 4:
						if (inimigos.get(0) != null) alvo = inimigos.get(0);
						break;
					case 5:
						if (inimigos.get(1) != null) alvo = inimigos.get(1);
						break;
					case 6:
						if (inimigos.get(2) != null) alvo = inimigos.get(2);
						break;
					}
				
				Effects efeitoStatus = new Effects("APPLY_STATUS", 1, idStatus, 100, duracao);
				
				EffectsStrategy estrategia = EffectsManager.getEfeito("APPLY_STATUS");
				if (estrategia != null){
					estrategia.aplicar(alvo, alvo, null, efeitoStatus, null);
					System.out.println(">>Status (ID:"+idStatus+") aplicado com sucesso ao monstro "+alvo.getNomeMonstro()+" por "+duracao+" turnos.");
				}
                break;
        }
    }
    
    @Override
    public String getUso(){
        String uso = "Subcomandos: \n"+
        "listUnit <id_Campo> \n"+
        "applyStatus <id_Campo> <id_Status> <duração> \n";
        
        return uso;
    }
    
    @Override
    public String getDescricao(){
        return "Gerencia unidades e status em batalha.";
    }
    
	private void listarMonstro(Monsters monstro, boolean isAliado){
		System.out.println("\n=== "+monstro.getNomeMonstro()+ " ===");
		
		System.out.println("ID: " + monstro.getIdMonstro());
		if (isAliado) System.out.println("Instance ID: "+ monstro.getInstanceId());
		
		System.out.println("\n[Vida]");
		System.out.println("Base: " + monstro.getVidaBase());
		System.out.println("Atual: " + monstro.getVidaAtual());
		System.out.println("Combate: " + monstro.getVidaAtualCombate()+"/"+monstro.getVidaAtualCombateMaxima());
		
		System.out.println("\n[Força]");
		System.out.println("Base: " + monstro.getForcaBase());
		System.out.println("Atual: " + monstro.getForcaAtual());
		System.out.println("Combate: " + monstro.getForcaAtualCombate());
		
		System.out.println("\n[Velocidade]");
		System.out.println("Base: " + monstro.getSpeedBase());
		System.out.println("Atual: " + monstro.getSpeedAtual());
		System.out.println("Combate: " + monstro.getSpeedAtualCombate());
		
		System.out.println("\n[Estamina]");
		System.out.println("Base: " + monstro.getEstaminaBase());
		System.out.println("Atual: " + monstro.getEstaminaAtual());
		System.out.println("Combate: " + monstro.getEstaminaAtualCombate());
		
		if (isAliado){
			System.out.println("\n[Especial e Modificadores]");
		}else{
			System.out.println("\n[Modificadores]");
		}
		
		if (isAliado) System.out.println("Barra Especial: " + monstro.getBarraEspecialAtual()+"/"+monstro.getBarraEspecialMaximo());
		System.out.println("Attuned (Harmonizado): "+monstro.getHarmonizado()+"%");
		System.out.println("Status Armor: " + monstro.getStatusArmor());
		System.out.println("Provocation Rate: " + monstro.getProvocationRate());
		System.out.println("Damage Bonus: " + monstro.getDamageBonus());
	}
	
    //===
}