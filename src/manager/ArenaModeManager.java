package manager;

import modes.arena.Tournament;
import combat.effects.Effects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArenaModeManager {
	private static Map<Integer, Tournament> torneios;
	private static List<Effects> buffs;
	
	private ArenaModeManager(){}
	
	public static void carregarTorneios(){
		setarTorneios();
		setarBônus();
	}
	
	// ==================== INICIALIZAÇÃO ====================
	
	private static void setarTorneios(){
		if (torneios == null){
			torneios = new LinkedHashMap<>();
			
			torneios.put(1, 
			new Tournament("Newbies´s Tournament", 0, new int[]{1, 2, 3, 4, 5, 6, 7},
			23)
			);
			
			torneios.put(2, 
			new Tournament("Greenwood Village Contest", 1, new int[]{8, 18, 9, 10, 11, 12},
			25)
			);
			
			torneios.put(3, 
			new Tournament("Adventure in Ashwood Forest", 2, new int[]{13, 14, 19, 15, 16, 17},
			14)
			);
			
			System.out.println(">>Torneios setados: "+torneios.size());
		}
	}
	
	private static void setarBônus(){
		if (buffs != null) return;
		
		buffs = new ArrayList<Effects>();
		
		Effects statusRegen = new Effects("APPLY_STATUS", 1, 8, 100, 3);
		buffs.add(statusRegen);
		
		Effects statusRegenSta = new Effects("APPLY_STATUS", 1, 13, 100, 3);
		buffs.add(statusRegenSta);
		
		Effects statusRapidez = new Effects("APPLY_STATUS", 1, 25, 100, 3);
		buffs.add(statusRapidez);
		
		Effects statusDano25 = new Effects("APPLY_STATUS", 1, 26, 100, 3);
		buffs.add(statusDano25);
		
		Effects statusDano50 = new Effects("APPLY_STATUS", 1, 27, 100, 2);
		buffs.add(statusDano50);
		
		Effects statusDano100 = new Effects("APPLY_STATUS", 1, 28, 100, 1);
		buffs.add(statusDano100);
	}
	
	// ==================== GETTERS ====================
	
	public static Map<Integer, Tournament> getTorneios(){
		return torneios;
	}
	
	public static Tournament getTorneio(int id){
		if (torneios != null) return torneios.get(id);
		return null;
	}
	
	public static List<Effects> getBuffs(){
		return buffs;
	}
	
	//===
}