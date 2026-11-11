package util.commands;

import bestiary.*;
import combat.*;
import main.Terminal;

import java.util.List;

public class BattleCommand implements Command {
    private Battle batalhaAtual;
	
    public BattleCommand() {}
    
    @Override
    public void executar(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("\n"+getUso());
            return;
        }
		
		if (Terminal.getEstadoAtual() instanceof Battle){
			batalhaAtual = (Battle) Terminal.getEstadoAtual();
		}else{
			System.out.println(">>Você não está em uma batalha no momento.");
			batalhaAtual = null;
			return;
		}			
		
        String subcomando = args[0];
        
        switch (subcomando){
            case "listAlly":
                if (args.length < 2){
                    System.out.println("Uso: battle listAlly <id_Campo>");
                    return;
                }
				int id = Integer.parseInt(args[1]);
				Monsters[] aliados = batalhaAtual.getMonstrosAliados();
				
				if (aliados == null || id < 0 || id >= aliados.length || aliados[id] == null){
					System.out.println(">>Monstro (ID_CAMPO:"+id+") não encontrado.");
					return;
				}
				
				Monsters monstroAliado = aliados[id];
				listarMonstro(monstroAliado, true);
                break;
                
            case "listEnemy":
				 if (args.length < 2){
                    System.out.println("Uso: battle listEnemy <id_Campo>");
                    return;
                }
				id = Integer.parseInt(args[1]);
				List<Monsters> inimigos = batalhaAtual.getMonstrosInimigos().getMonstros();
				
				if (inimigos == null || id < 0 || id >= inimigos.size() || inimigos.get(id) == null){
					System.out.println(">>Monstro (ID_CAMPO:"+id+") não encontrado.");
					return;
				}
				
				Monsters monstroInimigo = inimigos.get(id);
				listarMonstro(monstroInimigo, false);
                break;
				
            case "applyStatus":
                break;
             
            case "removeStatus":
                break;
        }
    }
    
    @Override
    public String getUso(){
        String uso = "Subcomandos: \n"+
        "listAlly <id_Campo> \n"+
		"listEnemy <id_Campo> \n"+
        "applyStatus <id_Campo> <id_Status> <duração> \n"+
        "removeStatus <id_Campo> <id_Status>";
        
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