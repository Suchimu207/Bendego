package bestiary;

import combat.effects.Effects;

import util.Grapchics;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.awt.Color;

public class Skills {
	public enum TipoHabilidade{
		ESPECIAL("Especial"),
		OFENSIVA("Ofensiva"),
		DEFENSIVA("Defensiva"),
		NEUTRO("Neutra");
		
		private final String nomeTipo;
		
		TipoHabilidade(String nomeTipo){
			this.nomeTipo = nomeTipo;
		}
		
		public String getTipoNome(){
			return nomeTipo;
		}
	}
	public enum TipoAlvo{
		USUARIO("Usuário"),
		ALIADO_UNICO("Um aliado"),
		ALIADO_AREA("Múltiplos aliados"),
		INIMIGO_UNICO("Um inimigo"),
		INIMIGO_AREA("Múltiplos inimigos"),
		CAMPO("Múltiplos alvos");
		
		private final String nomeTipo;
		
		TipoAlvo(String nomeTipo){
			this.nomeTipo = nomeTipo;
		}
		
		public String getTipoNome(){
			return nomeTipo;
		}
	}
	
	private int idHabilidade;
	private String nomeHabilidade;
	private String descriçãoHabilidade;
	
	private int poderHabilidade;
	private int precisãoBase;
	private int precisãoAtual;
	private int estaminaHabilidade;
	
	private TipoHabilidade tipoHabilidade;
	private TipoAlvo alvoHabilidade;
	private List<Effects> efeitos = new ArrayList<>();
	private Monsters.Elementos elementoHabilidade;
	
	private int recargaHabilidade;
	private int recargaAtual;
	private boolean recargaPendente;
	
	private int nivelNecessario;
	private double lifeSteal;
	
	public Skills(int idHabilidade, String nomeHabilidade, Monsters.Elementos elementoHabilidade,
	TipoHabilidade tipoHabilidade, TipoAlvo alvoHabilidade, int poderHabilidade, int precisãoBase,
	int estaminaHabilidade, int recargaHabilidade, double lifeSteal){
		try{
			if (idHabilidade <= 0){
				throw new IllegalArgumentException("ID deve ser maior do que 0.");
			}
		this.idHabilidade = idHabilidade;
		
		this.nomeHabilidade = nomeHabilidade;
		this.elementoHabilidade = elementoHabilidade;
		this.tipoHabilidade = tipoHabilidade;
		this.alvoHabilidade = alvoHabilidade;
		
			if (poderHabilidade < 0 || precisãoBase < 0 || estaminaHabilidade < 0 || recargaHabilidade < 0){
				throw new IllegalArgumentException("Valores precisam ser iguais ou maiores do que zero.");
			}
		this.poderHabilidade = poderHabilidade;
		this.precisãoBase = precisãoBase;
		this.precisãoAtual = this.precisãoBase;
		this.estaminaHabilidade = estaminaHabilidade;
		this.recargaHabilidade = recargaHabilidade;
		this.recargaAtual = 0;
		this.recargaPendente = false;
		this.lifeSteal = lifeSteal;
		
		this.nivelNecessario = 1;
		}catch(IllegalArgumentException e){
			System.out.println("Erro ao criar habilidade ID_"+idHabilidade+": "+e.getMessage());
			System.exit(1);
		}
	}
	
	public Skills(Skills skillRequerida){
		this.idHabilidade = skillRequerida.getIdHabilidade();
		this.nomeHabilidade = skillRequerida.getNomeHabilidade();
		this.elementoHabilidade = skillRequerida.getElementoHabilidadeTipo();
		this.tipoHabilidade = skillRequerida.getTipoHabilidade();
		this.alvoHabilidade = skillRequerida.getAlvoHabilidadeTipo();
		this.poderHabilidade = skillRequerida.getPoderHabilidade();
		this.precisãoBase = skillRequerida.getPrecisaoBase();
		this.precisãoAtual = skillRequerida.getPrecisaoAtual();
		this.estaminaHabilidade = skillRequerida.getEstaminaHabilidade();
		this.recargaHabilidade = skillRequerida.getRecargaHabilidade();
		this.recargaAtual = skillRequerida.getRecargaAtual();
		this.recargaPendente = skillRequerida.isRecargaPendente();
		this.lifeSteal = skillRequerida.getLifeSteal();
		this.nivelNecessario = skillRequerida.getNivelNecessario();
		
		this.efeitos = new ArrayList<>();
		for (Effects e : skillRequerida.getEfeitos()){
			this.efeitos.add(new Effects(e.getTipo(), e.getAlvo(), e.getValor(), e.getChance(), e.getTurnos()));
		}
	}
	
	public Skills(){
	}
	
	public int getIdHabilidade(){
		return idHabilidade;
	}

	public String getNomeHabilidade(){
		return nomeHabilidade;
	}

	public String getElementoHabilidade(){
		return elementoHabilidade.toString();
	}

	public Monsters.Elementos getElementoHabilidadeTipo(){
		return elementoHabilidade;
	}
	
	public TipoHabilidade getTipoHabilidade(){
		return tipoHabilidade;
	}
	
	public TipoAlvo getAlvoHabilidadeTipo(){
		return alvoHabilidade;
	}
	
	public String getAlvoHabilidade(){
		return alvoHabilidade.toString();
	}
	
	public TipoAlvo getAlvoHabilidadeValor(){
		return alvoHabilidade;
	}
	
	public void adicionarEfeito(Effects efeito){
		if (efeito != null) this.efeitos.add(efeito);
	}
	
	public void ativarRecarga(){
		if (this.recargaHabilidade > 0 && this.recargaAtual <= 0){
			this.recargaAtual = this.recargaHabilidade;
			this.recargaPendente = true;
		}
	}
	
	public boolean isRecarga(){
		return this.recargaAtual > 0 || this.recargaPendente;
	}
	
	public boolean isRecargaPendente(){
		return this.recargaPendente;
	}
	
	public void reduzirRecarga(){
		if (this.recargaPendente){
			this.recargaAtual = this.recargaHabilidade;
			this.recargaPendente = false;
		}else if (this.recargaAtual > 0){
			this.recargaAtual--;
		}
	}
	
	public Color getCorHabilidade(){
		if (getElementoHabilidade().contains(Monsters.Elementos.NATUREZA.toString())) return Grapchics.ELEMENTO_NATUREZA;
		if (getElementoHabilidade().contains(Monsters.Elementos.FOGO.toString())) return Grapchics.ELEMENTO_FOGO;
		if (getElementoHabilidade().contains(Monsters.Elementos.TERRA.toString())) return Grapchics.ELEMENTO_TERRA;
		if (getElementoHabilidade().contains(Monsters.Elementos.TROVAO.toString())) return Grapchics.ELEMENTO_TROVAO;
		if (getElementoHabilidade().contains(Monsters.Elementos.AGUA.toString())) return Grapchics.ELEMENTO_AGUA;
		if (getElementoHabilidade().contains(Monsters.Elementos.LUZ.toString())) return Grapchics.ELEMENTO_LUZ;
		if (getElementoHabilidade().contains(Monsters.Elementos.MAGIA.toString())) return Grapchics.ELEMENTO_MAGIA;
		if (getElementoHabilidade().contains(Monsters.Elementos.TREVAS.toString())) return Grapchics.ELEMENTO_TREVAS;
		if (getElementoHabilidade().contains(Monsters.Elementos.METAL.toString())) return Grapchics.ELEMENTO_METAL;
		if (getElementoHabilidade().contains(Monsters.Elementos.VENTO.toString())) return Grapchics.ELEMENTO_VENTO;
		if (getElementoHabilidade().contains(Monsters.Elementos.FISICO.toString())) return Grapchics.ELEMENTO_FISICO;
		return Grapchics.BRANCO_CLARO;
	}
	
	public List<Effects> getEfeitos(){
		return efeitos;
	}
	
	public int getPoderHabilidade(){
		return poderHabilidade;
	}

	public int getPrecisaoBase(){
		return precisãoBase;
	}
	
	public int getPrecisaoAtual(){
		return precisãoAtual;
	}

	public int getEstaminaHabilidade(){
		return estaminaHabilidade;
	}
	
	public int getRecargaHabilidade(){
		return recargaHabilidade;
	}
	
	public int getRecargaAtual(){
		return recargaAtual;
	}
	
	public int getNivelNecessario(){
		return this.nivelNecessario;
	}
	
	public double getLifeSteal(){
		return lifeSteal;
	}
	
	public void setLifeSteal(int lifeSteal){
		if (lifeSteal < 0) lifeSteal = 0;
		this.lifeSteal = lifeSteal;
	}
	
	public void setNivelNecessario(int nivelNecessario){
		this.nivelNecessario = nivelNecessario;
	}
	
	public void setRecargaAtual(int recargaAtual){
		this.recargaAtual = recargaAtual;
	}
	
	public void setPrecisaoAtual(int precisãoAtual){
		if (precisãoAtual < 0) precisãoAtual = 0;
		this.precisãoAtual = precisãoAtual;
	}
	
	public boolean isTipoEspecial(TipoHabilidade tipoHabilidade){
		if (tipoHabilidade == TipoHabilidade.ESPECIAL){
			return true;
		}else return false;
	}
	
	//===
}