package bestiary;

import java.util.ArrayList;
import java.util.List;

public class Troop {
    public static class Inimigo {
        private final Monsters monstro;
        private final int nivel;
		
        public Inimigo(Monsters monstro, int nivel, boolean isCopia){
            this.monstro = monstro;
			
			if (nivel <= 0) nivel = 1;
            this.nivel = nivel;
			
			if (!isCopia) this.monstro.subirNivel(this.nivel-1);
        }
		
        public Monsters getMonstroTropa(){ 
			return monstro; 
		}
        public int getNivel(){ 
			return nivel; 
		}
    }

    private int id;
	private String nomeTropa;
    private List<Inimigo> inimigos;
    private int exp;
    private int ouro;
	
    public Troop(int id, String nomeTropa, List<Inimigo> inimigos, int exp, int ouro){
        this.id = id;
		this.nomeTropa = nomeTropa;
        this.inimigos = new ArrayList<>(inimigos);
        this.exp = exp;
        this.ouro = ouro;
    }

    public Troop(Troop troopRequerida){
        this.id = troopRequerida.getId();
		this.nomeTropa = troopRequerida.getNomeTropa();
        this.inimigos = new ArrayList<>();
        for (Inimigo i : troopRequerida.getInimigos()){
            this.inimigos.add(new Inimigo(i.getMonstroTropa(), i.getNivel(), true));
        }
        this.exp = troopRequerida.getExp();
        this.ouro = troopRequerida.getOuro();
    }
	
	@Override
	public Troop clone(){
		return new Troop(this);
	}
	
    public int getId(){ 
		return id; 
	}
	
	public String getNomeTropa(){
		return nomeTropa;
	}
	
    private List<Inimigo> getInimigos(){
		return new ArrayList<>(inimigos); 
	}
	
	public ArrayList<Monsters> getMonstros(){
		ArrayList<Monsters> listaMonstros = new ArrayList<>();
		for (Inimigo inimigo : inimigos){
			listaMonstros.add(inimigo.getMonstroTropa());
		}
		return listaMonstros;
	}
	
    public int getExp(){ 
		return exp; 
	}
	
    public int getOuro(){ 
		return ouro; 
	}
	
	//===
}