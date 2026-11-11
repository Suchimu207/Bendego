package util.commands;

import main.inventory.Inventory;
import main.Player;
import bestiary.Monsters;
import manager.MonstersManager;

public class PlayerCommand implements Command {
	public PlayerCommand(){}
	
    @Override
    public void executar(String[] args) throws Exception{
        if (args.length < 1){
            System.out.println("\n"+getUso());
            return;
        }
        
        String subcomando = args[0];
        
        switch (subcomando){
            case "addMonster":
                if (args.length < 2){
                    System.out.println("Uso: player addMonster <id>");
                    return;
                }
                int id = Integer.parseInt(args[1]);
                Monsters monstro = MonstersManager.getMonstro(id);
                if (monstro != null){
                    Inventory.adicionarMonstroInventário(id);
                    System.out.println(">>"+monstro.getNomeMonstro()+" (ID:" + id + ") adicionado ao inventário.");
                }else{
                    System.out.println(">>Monstro (ID:"+id+") não encontrado.");
                }
                break;
            
			case "addAllMonsters":
                if (args.length > 1){
                    System.out.println("Uso: player addAllMonsters");
                    return;
                }
				Inventory.preencherInventario();
				System.out.println(">>Inventário preenchido.");
                break;
			
			case "removeMonster":
                if (args.length < 2){
                    System.out.println("Uso: player removeMonster <id_Inventário>");
                    return;
                }
                id = Integer.parseInt(args[1]);
                monstro = Inventory.getMonstroInventario(id);
                if (monstro != null){
                    Inventory.removerMonstroInventário(id);
                    System.out.println(">>"+monstro.getNomeMonstro()+" (ID_Inventário:" + id + ") removido do inventário.");
                }else{
                    System.out.println(">>Monstro (ID_Inventário:"+id+") não encontrado.");
                }
                break;
			
			case "removeAllMonsters":
                if (args.length > 1){
                    System.out.println("Uso: player removeAllMonsters");
                    return;
                }
                Inventory.limparInventario();
				System.out.println(">>Inventário limpo.");
                break;
			
			case "gainGold":
                if (args.length < 2){
                    System.out.println("Uso: player gainGold <quantidade>");
                    return;
                }
                int quantidade = Integer.parseInt(args[1]);
                Player.ganharOuro(quantidade);
                System.out.println(">>Ganho: "+quantidade+" de ouro.");
                System.out.println(">>Ouro atual: "+Player.getOuro());
                break;
			
			case "loseGold":
                if (args.length < 2){
                    System.out.println("Uso: player loseGold <quantidade>");
                    return;
                }
                quantidade = Integer.parseInt(args[1]);
                Player.perderOuro(quantidade);
                System.out.println(">>Perdido: "+quantidade+" de ouro.");
                System.out.println(">>Ouro atual: "+Player.getOuro());
                break;
				
			case "monster":
				if (args.length < 2){
					System.out.println("Uso: player monster <levelUp|levelUpAll>");
					return;
				}
				String comandoMonstro = args[1];
				
				if (comandoMonstro.equalsIgnoreCase("levelUp")){
					if (args.length < 4){
						System.out.println("Uso: player monster levelUp <id_Inventário> <nível>");
						return;
					}
					id = Integer.parseInt(args[2]);
					int valor = Integer.parseInt(args[3]);
					monstro = Inventory.getMonstroInventario(id);
					if (monstro == null){
						System.out.println("Monstro (ID_Inventário:"+id+")"+" não encontrado.");
					}else{
						if (monstro.isNivelMaximo()){
							System.out.println(monstro.getNomeMonstro()+" (ID_Inventário:"+id+")"+
							" está no nível máximo ("+monstro.getNivelMaximo()+").");
						}else{
							monstro.subirNivel(valor);
							System.out.println(monstro.getNomeMonstro()+" (ID_Inventário:"+id+")"+" subiu para o nível "+monstro.getNivelAtual()+".");
						}
					}
				}
				
				if (comandoMonstro.equalsIgnoreCase("levelUpAll")){
					if (args.length < 3){
						System.out.println("Uso: player monster levelUpAll <nível>");
						return;
					}
					int valor = Integer.parseInt(args[2]);
					int tamanhoInventário = Inventory.getTamanhoInventario();
					
					for (int m = 1; m <= tamanhoInventário; m++){
						monstro = Inventory.getMonstroInventario(m);
						if (monstro == null) continue;
						
						if (monstro.isNivelMaximo()){
							System.out.println(monstro.getNomeMonstro()+" (ID_Inventário:"+ m+")"+
							" está no nível máximo ("+monstro.getNivelMaximo()+").");
						}else{
							monstro.subirNivel(valor);
							System.out.println(monstro.getNomeMonstro()+" (ID_Inventário:"+ m+")"+
							" subiu para o nível "+monstro.getNivelAtual()+".");
						}
					}
				}
				
				break;
        }
    }
    
    @Override
    public String getUso(){
		String uso = "Subcomandos: \n"+
		"addMonster <id> \n"+
		"addAllMonsters \n"+
		"removeMonster <id_Inventário> \n"+
		"removeAllMonsters \n"+
		"gainGold <quantidade> \n"+
		"loseGold <quantidade> \n"+
		"monster levelUp <id_Inventário> <nível> \n"+
		"monster levelUpAll <nível>";
		
        return uso;
    }
    
    @Override
    public String getDescricao(){
        return "Gerencia o jogador e seu inventário.";
    }
	
	//===
}