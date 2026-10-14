package manager;

public final class DataManager {

	private DataManager(){
	}
	
    public static void carregarDados(){
		MapsManager.carregarMapas();
		NPCManager.carregarNPCs();
		System.out.println("");
		
		SkillsManager.carregarHabilidades();
		System.out.println("");
		
		StatusManager.carregarStatus();
		System.out.println("");
		
		TraitsManager.carregarTraços();
		System.out.println("");
		
		MonstersManager.carregarMonstros();
		System.out.println("");
		
		MonstersDescriptionManager.carregarDescrições();
		System.out.println("");
		
		TroopManager.carregarTropas();
		System.out.println("");
		
		ArenaModeManager.carregarTorneios();
		System.out.println("");
    }
	
	//===
}