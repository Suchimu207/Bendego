package util;

import manager.DataManager;
import util.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class CommandManager {
    private static final Map<String, Command> comandos = new HashMap<>();
    private static boolean ativo = false;
    private static Thread threadComandos;
    
    private CommandManager() {}
    
    public static void iniciar(){
        if (ativo) return;
        ativo = true;
        
        registrarComandos();
        
        threadComandos = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (ativo){
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) continue;
                
                String[] partes = input.split(" ");
                String comando = partes[0].toLowerCase();
                String[] args = new String[partes.length - 1];
                System.arraycopy(partes, 1, args, 0, args.length);
                
                executarComando(comando, args);
            }
        });
        
        threadComandos.setDaemon(true);
        threadComandos.start();
        System.out.println(">>Sistema de comandos iniciado. Digite 'help' para ajuda.");
    }
    
    private static void registrarComandos(){
        comandos.put("player", new PlayerCommand());
        comandos.put("battle", new BattleCommand());
    }
    
    private static void executarComando(String comando, String[] args){
		if (comandosUtilidade(comando)) return;
		
        Command cmd = comandos.get(comando);
        if (cmd == null){
            System.out.println("Comando desconhecido. Digite 'help' para ver a lista.");
            return;
        }
        
        try{
            cmd.executar(args);
        }catch (Exception e){
            System.out.println("Erro ao executar comando: "+e.getMessage());
			System.out.println("Uso: "+cmd.getUso());
        }
    }
    
	private static boolean comandosUtilidade(String comando){
		boolean retorno = false;
		
		switch (comando){
			case "help":
				comandoHelp();
				retorno = true;
			break;
			case "cls":
            case "clear":
				Debug.limpaPrompt();
				retorno = true;
            break;
			case "reload":
				Debug.limpaPrompt();
				DataManager.carregarDados();
				retorno = true;
            break;
        }
		return retorno;
	}
	
	private static void comandoHelp(){
		String uso = "Comandos: \n"+
		">>cls|clear: Limpa o prompt. \n"+
		">>reload: Recarrega os dados do jogo. \n"+
		">>player: "+new PlayerCommand().getDescricao()+"\n"+
		">>battle: "+new BattleCommand().getDescricao();
		
        System.out.println("\n"+uso);
    }
	
    public static void parar(){
        ativo = false;
        if (threadComandos != null){
            threadComandos.interrupt();
        }
    }
	
	//===
}