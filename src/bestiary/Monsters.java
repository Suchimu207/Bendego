package bestiary;

import bestiary.traits.Traits;
import bestiary.traits.TraitEffect;
import manager.TraitsManager;

import combat.status.StatusBase;

import util.Grapchics;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import java.awt.Color;

public class Monsters {
	public enum Classes{
		ATACANTE("Atacante"),
		SUPORTE("Suporte"),
		GENERALISTA("Generalista"),
		SABOTADOR("Sabotador"),
		TANQUE("Tanque");
		
		private final String nomeClasse;
		
		Classes(String nomeClasse){
			this.nomeClasse = nomeClasse;
		}
		
		public String getClasseNome(){
			return nomeClasse;
		}
	}
	public enum Elementos{
		FOGO("Fogo"),
		NATUREZA("Natureza"),
		MAGIA("Magia"),
		METAL("Metal"),
		VENTO("Vento"),
		LUZ("Luz"),
		TREVAS("Trevas"),
		TERRA("Terra"),
		TROVAO("Trovão"),
		AGUA("Água"),
		FISICO("Físico"),
		ETER("Éter");
		
		private final String nomeElemento;
		
		Elementos(String nomeElemento){
			this.nomeElemento = nomeElemento;
		}
		
		public String getElementoNome(){
			return nomeElemento;
		}
		
		public boolean temVantagemContra(Elementos outro){
			if (outro == null) return false;
			
			switch (this){
				case FOGO:     return outro == NATUREZA;
				case NATUREZA: return outro == VENTO;
				case METAL:    return outro == MAGIA;
				case MAGIA:    return outro == FISICO;
				case VENTO:    return outro == LUZ;
				case LUZ:      return outro == TREVAS;
				case TREVAS:   return outro == TERRA;
				case TERRA:    return outro == TROVAO;
				case TROVAO:   return outro == AGUA;
				case AGUA:     return outro == FOGO;
				
				case ETER:     return outro == FOGO || outro == AGUA || outro == TERRA || outro == VENTO || outro == METAL;
				default:       return false;
			}
		}
		
		public boolean temFraquezaContra(Elementos outro){
			return outro != null && outro.temVantagemContra(this);
		}
		
		public double getMultiplicadorDano(Elementos elementoAlvo){
			if (this.temVantagemContra(elementoAlvo)){
				return 2;
			}else if (this.temFraquezaContra(elementoAlvo) || this == elementoAlvo){
				return 0.5;
			}
			return 1.0;
		}
	}
	public enum Raridades{
		COMUM("Comum"),
		INCOMUM("Incomum"),
		RARO("Raro"),
		EPICO("Épico"),
		LENDARIO("Lendário");
		
		private final String nomeRaridade;
		
		Raridades(String nomeRaridade){
			this.nomeRaridade = nomeRaridade;
		}
		
		public String getRaridadeNome(){
			return nomeRaridade;
		}
	}
	private enum SlotHabilidade{
		SLOT_1,
		SLOT_2,
		SLOT_3,
		SLOT_4,
	}
	
	// ==================== ATRIBUTOS ====================
	
	private int idMonstro;
	private String nomeMonstro;
	private final String instanceId;
	private int nivelBase, nivelAtual;
	private int expAtual;
	private boolean monstroEquipado, monstroFavorito;
	
	private int forçaBase, forçaAtual, forçaAtualCombate;
	private int vidaBase, vidaAtual, vidaAtualCombate, vidaAtualCombateMaxima;
	private int speedBase, speedAtual, speedAtualCombate;
	private int estaminaBase, estaminaAtual, estaminaAtualCombate;
	private int barraEspecialAtual, barraEspecialMaximo;
	private int escudoAtual;
	private int attunedPercent;
	private int statusArmor;
	private int provocationRate;
	private double damageBonus;
	private int[] traçosIds;
	
	private Classes classeAtual;
	private Elementos[] elementosAtuais;
	private Raridades raridadeMonstro;
	
	private final int NIVEL_MAXIMO = 40;
	
	private final int MIN_PROVOCATION_RATE = 100;
	private final int MAX_PROVOCATION_RATE = 999;
	
	private Map<Integer, List<Skills>> skillsTree = new HashMap<>();
	private List<Skills> skillsDesbloqueadas = new ArrayList<>();
	private EnumMap<SlotHabilidade, Skills> skillsAtivas = new EnumMap<>(SlotHabilidade.class);	
	
	private List<StatusBase> statusAtuais = new ArrayList<>();
	private List<StatusBase> imunidades = new ArrayList<>();
	private List<StatusBase> imunidadesTemp = new ArrayList<>();
	
	private List<Traits> traçosAtuais = new ArrayList<>();
	
	// ==================== CONSTRUTORES ==================== 
	
	public Monsters(int idMonstro, String nomeMonstro, Classes classeAtual, Elementos[] elementosAtuais,
	Raridades raridadeMonstro, int nivelBase, int forçaBase, int vidaBase, int speedBase, int estaminaBase, int[] traçosIds, int barraEspecialMaximo){
		this.instanceId = UUID.randomUUID().toString();
		try{
			if (idMonstro <= 0){
				throw new IllegalArgumentException("ID deve ser maior do que 0.");
			}
			this.idMonstro = idMonstro;
			
			this.nomeMonstro = nomeMonstro;
			this.classeAtual = classeAtual;
			
			if (elementosAtuais.length >= 3){
				throw new IllegalArgumentException("O monstro não pode ter mais de dois elementos.");
			}
			this.elementosAtuais = elementosAtuais;
			this.raridadeMonstro = raridadeMonstro;
			
			if (nivelBase <= 0){
				throw new IllegalArgumentException("Nivel base deve ser maior do que 0.");
			}
			this.nivelBase = nivelBase;
			this.nivelAtual = nivelBase;
			
			this.expAtual = 0;
			
			if (forçaBase <= 0 || vidaBase <= 0 || speedBase <= 0 || estaminaBase <= 0){
				throw new IllegalArgumentException("Atributos base devem ser maiores do que 0.");
			}
			this.forçaBase = forçaBase;
			this.forçaAtual = this.forçaBase;
			
			this.vidaBase = vidaBase;
			this.vidaAtual = this.vidaBase;
			
			this.speedBase = speedBase;
			this.speedAtual = this.speedBase;
			
			this.estaminaBase = estaminaBase;
			this.estaminaAtual = this.estaminaBase;
			
			this.barraEspecialAtual = 0;
			this.barraEspecialMaximo = barraEspecialMaximo;
			
			this.escudoAtual = 0;
			this.damageBonus = 1.0;
			
			this.traçosIds = traçosIds;
			
			this.monstroEquipado = false;
			this.monstroFavorito = false;
		}catch(IllegalArgumentException e){
			System.out.println("Erro ao criar monstro ID_"+idMonstro+": "+e.getMessage());
			System.exit(1);
		}
	}

   public Monsters(Monsters monstroRequerido){
	   this.instanceId = UUID.randomUUID().toString();
	   this.idMonstro = monstroRequerido.getIdMonstro();
	   this.nomeMonstro = monstroRequerido.getNomeMonstro();
	   this.classeAtual = monstroRequerido.getClasseAtual();
	   this.elementosAtuais = monstroRequerido.getElementosAtuaisValores();
	   this.raridadeMonstro = monstroRequerido.getRaridadeMonstro();
	   this.nivelBase = monstroRequerido.getNivelBase();
	   this.nivelAtual = monstroRequerido.getNivelAtual();
	   this.expAtual = monstroRequerido.getExpAtual();
	   this.forçaBase = monstroRequerido.getForcaBase();
	   this.forçaAtual = monstroRequerido.getForcaAtual();
	   this.vidaBase = monstroRequerido.getVidaBase();
	   this.vidaAtual = monstroRequerido.getVidaAtual();
	   this.speedBase = monstroRequerido.getSpeedBase();
	   this.speedAtual = monstroRequerido.getSpeedAtual();
	   this.estaminaBase = monstroRequerido.getEstaminaBase();
	   this.estaminaAtual = monstroRequerido.getEstaminaAtual();
	   this.barraEspecialAtual = monstroRequerido.getBarraEspecialAtual();
	   this.barraEspecialMaximo = monstroRequerido.getBarraEspecialMaximo();
	   this.escudoAtual = monstroRequerido.getEscudoAtual();
	   this.damageBonus = monstroRequerido.getDamageBonus();
	   this.provocationRate = monstroRequerido.getProvocationRate();
	   
	   this.traçosIds = monstroRequerido.getTracosIds();	   
	   if (this.traçosIds != null){
		for (int traçoId : this.traçosIds){
			Traits traço = TraitsManager.getTraço(traçoId);
			if (traço != null) this.adicionarTraço(traço);
			}
	   }
	   
	   this.statusAtuais = new ArrayList<>(monstroRequerido.getStatusAtuais());
	   this.monstroEquipado = false;
	   this.monstroFavorito = false;
	   
	   this.skillsTree = new HashMap<>();
	   for (Map.Entry<Integer, List<Skills>> entry : monstroRequerido.skillsTree.entrySet()){
			List<Skills> listaClonada = new ArrayList<>();
			for (Skills skillAtual : entry.getValue()){
				listaClonada.add(new Skills(skillAtual));
			}
			this.skillsTree.put(entry.getKey(), listaClonada);
		}
		
	   this.desbloquearHabilidades();
	}
	
	// ==================== HABILIDADES ====================
	
	public void adicionarHabilidadeArvore(int nivel, Skills skill){
		// A função só é executada se a chave "nivel" não existir.
		this.skillsTree.computeIfAbsent(nivel, k -> new ArrayList<>()).add(skill);
	}
	
	public void desbloquearHabilidades(){
		for (int nivel = 1; nivel <= this.nivelAtual; nivel++){
			if (this.skillsTree.containsKey(nivel)){
				List<Skills> habilidadesDoNivel = this.skillsTree.get(nivel);
            
				for (Skills skill : habilidadesDoNivel){
					if (!this.skillsDesbloqueadas.contains(skill)){
						this.skillsDesbloqueadas.add(skill);
						
						for (SlotHabilidade slot : SlotHabilidade.values()){
							if (!skillsAtivas.containsKey(slot) && !skill.isTipoEspecial(skill.getTipoHabilidade())){
								skillsAtivas.put(slot, skill);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public Map<Integer, List<Skills>> getHabilidadesArvore(){
		return skillsTree;
	}
	
	public Skills getHabilidadeArvoreId(int idHabilidade){
		for (List<Skills> lista : skillsTree.values()){
			for (Skills skill : lista){
				if (skill.getIdHabilidade() == idHabilidade){
					return skill;
				}
			}
		}
		return null;
	}
	
	public int getTamanhoSkillsTree(){
		int total = 0;
		for (List<Skills> lista : skillsTree.values()){
			total += lista.size();
		}
		return total;
	}
	
	public Skills getHabilidadeEspecial(){
		for (List<Skills> lista : this.skillsTree.values()){
			for (Skills skill : lista){
				if (skill != null && skill.isTipoEspecial(skill.getTipoHabilidade())){
					return skill;
				}
			}
		}
		return null;
	}
	
	public List<Skills> getHabilidadesDesbloqueadas(){
        return skillsDesbloqueadas;
    }
	
	public boolean isHabilidadeDesbloqueada(Skills habilidade){
		if (habilidade == null) return false;
		return skillsDesbloqueadas.contains(habilidade);
	}
	
	protected EnumMap<SlotHabilidade, Skills> getSkillsAtivas(){
		return skillsAtivas;
	}
	
	public SlotHabilidade getSlotPorIndice(int indice){
		SlotHabilidade[] slots = SlotHabilidade.values();
			if (indice >= 0 && indice < slots.length){
				return slots[indice];
			}
			return null;
	}
	
	public int getQuantidadeMaxSlotsHabilidade(){
		return SlotHabilidade.values().length;
	}
	
	public Skills getHabilidadeAtiva(int indice){
		SlotHabilidade slot = getSlotPorIndice(indice);
		return slot != null ? skillsAtivas.get(slot) : null;
	}
	
	public boolean isHabilidadeAtiva(Skills habilidade){
		if (habilidade == null) return false;
		
		for (Map.Entry<SlotHabilidade, Skills> entry : skillsAtivas.entrySet()){
			if (entry.getValue() == habilidade){
				return true;
			}
		}
		return false;
	}
	
	public boolean removerHabilidadeAtiva(Skills skill){
		for (Map.Entry<SlotHabilidade, Skills> entry : skillsAtivas.entrySet()){
			if (entry.getValue() == skill){
				skillsAtivas.remove(entry.getKey());
				return true;
			}
		}
		return false;
	}
	
	public void adicionarHabilidadeAtiva(Skills skill){
		for (SlotHabilidade slot : SlotHabilidade.values()){
			if (!skillsAtivas.containsKey(slot)){
				skillsAtivas.put(slot, skill);
				break;
			}
		}
	}
	
	public void reordenarSkillsAtivas(){
		List<Skills> lista = new ArrayList<>(skillsAtivas.values());
		skillsAtivas.clear();
		for (int i = 0; i < lista.size() && i < SlotHabilidade.values().length; i++){
			skillsAtivas.put(SlotHabilidade.values()[i], lista.get(i));
		}
	}
	
	public void limparHabilidadesAtivas(){
		skillsAtivas.clear();
	}
	
	public List<Integer> getHabilidadesAtivasIds(){
		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < getQuantidadeMaxSlotsHabilidade(); i++){
			Skills skill = getHabilidadeAtiva(i);
			if (skill != null) ids.add(skill.getIdHabilidade());
		}
		return ids;
	}
	
	public void desativarRecargas(){
		for (int i = 0; i < this.getQuantidadeMaxSlotsHabilidade(); i++){
			Skills skill = this.getHabilidadeAtiva(i);
			if (skill != null && skill.isRecarga()) skill.setRecargaAtual(0);
		}
	}
	
	public void reduzirRecargaHabilidades(){
		for (int i = 0; i < this.getQuantidadeMaxSlotsHabilidade(); i++){
			Skills skill = this.getHabilidadeAtiva(i);
			if (skill != null && skill.isRecarga()) skill.reduzirRecarga();
		}
	}
	
	public void carregarEspecial(int carga){
		if (carga > this.barraEspecialMaximo || carga <= 0) return;
		this.barraEspecialAtual = Math.min(this.barraEspecialMaximo, carga+this.barraEspecialAtual);
	}
	
	public void carregarEspecialCompleto(){
		this.zerarEspecial();
		this.barraEspecialAtual = this.barraEspecialMaximo;
	}
	
	public void zerarEspecial(){
		this.barraEspecialAtual = 0;
	}
	
	public boolean isEspecialCarregado(){
		if (this.barraEspecialAtual == this.barraEspecialMaximo) return true;
		return false;
	}
	
	public int getBarraEspecialAtual(){
		return barraEspecialAtual;
	}

	public int getBarraEspecialMaximo(){
		return barraEspecialMaximo;
	}
	
	public int getQuantidadeSlotsOcupados(){
		return skillsAtivas.size();
	}
	
	// ==================== EXPERIÊNCIA/NIVEL ====================
	
	public void ganharExp(int expGanha){
		if (this.nivelAtual >= NIVEL_MAXIMO) return;
		
		this.expAtual += expGanha;
		int expNecessaria = getExpNecessaria();
		
		while (this.expAtual >= expNecessaria && this.nivelAtual < NIVEL_MAXIMO){
			expAtual -= expNecessaria;
			subirNivel(1);
			
			if (nivelAtual < NIVEL_MAXIMO){
				expNecessaria = getExpNecessaria();
			}
		}
	}
	
	public int getExpNecessaria(){
		if (this.nivelAtual >= this.NIVEL_MAXIMO) return -1;
		return 100 * this.nivelAtual;
	}
	
	public void subirNivel(int quantidade){
		if (quantidade <= 0) return;
    
		int niveisReais = Math.min(quantidade, NIVEL_MAXIMO-this.nivelAtual);
    
		for (int i = 0; i < niveisReais; i++){
			this.nivelAtual++;
			this.forçaAtual += (int) Math.ceil((this.forçaBase/3));
			this.vidaAtual += (int) Math.ceil((this.vidaBase/3));
			this.speedAtual += (int) Math.ceil((this.speedBase/3));
		}
		this.desbloquearHabilidades();
	}
	
	public boolean isNivelMaximo(){
		if (this.nivelAtual == this.NIVEL_MAXIMO) return true;
		return false;
	}
	
	public int getNivelBase(){
		return nivelBase;
	}
	
	public int getNivelAtual(){
		return nivelAtual;
	}
	
	public int getExpAtual(){
		return expAtual;
	}
	
	public int getNivelMaximo(){
		return NIVEL_MAXIMO;
	}
	
	public void setNivelAtual(int nivelAtual){
		if (nivelAtual < 1) nivelAtual = 1;
		this.nivelAtual = nivelAtual;
	}
	
	public void setExpAtual(int expAtual){
		if (expAtual < 0) expAtual = 0;
		this.expAtual = expAtual;
	}
	
	// ==================== COMBATE ====================
	
	public void ganharVida(int cura){		
		if (cura <= 0) return;
    
		int vidaMaxima = this.getVidaAtualCombateMaxima();
		int novaVida = Math.min(vidaMaxima, this.getVidaAtualCombate()+cura);
    
		this.setVidaAtualCombate(novaVida);
	}
	
	public void perderVida(int dano){
		if (this.getVidaAtualCombate() <= 0 || dano <= 0) return;
		
		int danoRestante = dano;
		int escudoAtual = this.getEscudoAtual();
		
		if (escudoAtual > 0){
			if (escudoAtual >= danoRestante){
				this.setEscudoAtual(escudoAtual - danoRestante);
				danoRestante = 0;
			}else{
				this.setEscudoAtual(0);
				danoRestante -= escudoAtual;
			}
		}

		if (danoRestante > 0){
			int vidaRestante = this.getVidaAtualCombate() - danoRestante;
			this.setVidaAtualCombate(vidaRestante);
		}
	}
	
	public void perderVidaSemEscudo(int dano){
		if (this.getVidaAtualCombate() <= 0 || dano <= 0) return;
		
		int vidaRestante = this.getVidaAtualCombate() - dano;
		this.setVidaAtualCombate(vidaRestante);
	}
	
	public void ganharEstamina(int regen){
		if (regen <= 0) return;
    
		int estaminaMaxima = this.getEstaminaAtual();
		int estaminaRegen = Math.min(estaminaMaxima, this.getEstaminaAtualCombate()+regen);
		
		this.setEstaminaAtualCombate(estaminaRegen);
	}
	
	public void perderEstamina(int perda){
		if (perda <= 0) return;
		
		int novaEstamina = Math.max(0, this.getEstaminaAtualCombate()-perda);
		
		this.setEstaminaAtualCombate(novaEstamina);
	}
	
	public boolean podeAgir(){
		if (this.statusAtuais == null || this.statusAtuais.isEmpty()){
			return true;
		}
		
		for (StatusBase status : this.statusAtuais){
			if (status != null && status.isAtivo() && status.impedeAção()){
				return false;
			}
		}
		return true;
	}
	
	public void zerarEscudoAtual(){
		this.escudoAtual = 0;
	}
	
	public int getEscudoAtual(){
		return escudoAtual;
	}
	
	public double getDamageBonus(){
		return damageBonus;
	}
	
	public int getVidaAtualCombate(){
		return vidaAtualCombate;
	}
	
	public int getVidaAtualCombateMaxima(){
		return vidaAtualCombateMaxima;
	}
	
	public int getForcaAtualCombate(){
		return forçaAtualCombate;
	}
	
	public int getSpeedAtualCombate(){
		return speedAtualCombate;
	}
	
	public int getEstaminaAtualCombate(){
		return estaminaAtualCombate;
	}
	
	public void setEscudoAtual(int escudoAtual){
		if (escudoAtual < 0) escudoAtual = 0;
		this.escudoAtual = escudoAtual;
	}
	
	public void setDamageBonus(double damageBonus){
		if (damageBonus <= 0.0) damageBonus = 1.0;
		this.damageBonus = damageBonus;
	}
	
	public void setVidaAtualCombate(int vidaAtualCombate){
		if (vidaAtualCombate < 0) vidaAtualCombate = 0;
		this.vidaAtualCombate = vidaAtualCombate;
	}
	
	public void setVidaAtualCombateMaxima(int vidaAtualCombateMaxima){
		if (vidaAtualCombateMaxima < 0) vidaAtualCombateMaxima = 0;
		this.vidaAtualCombateMaxima = vidaAtualCombateMaxima;
	}
	
	public void setForcaAtualCombate(int forcaAtualCombate){
		if (forcaAtualCombate < 1) forcaAtualCombate = 1;
		this.forçaAtualCombate = forcaAtualCombate;
	}
	
	public void setSpeedAtualCombate(int speedAtualCombate){
		if (speedAtualCombate < 1) speedAtualCombate = 1;
		this.speedAtualCombate = speedAtualCombate;
	}
	
	public void setEstaminaAtualCombate(int estaminaAtualCombate){
		if (estaminaAtualCombate < 0) estaminaAtualCombate = 0; 
		this.estaminaAtualCombate = estaminaAtualCombate;
	}
	
	// ==================== VIDA ====================
	
	public int getVidaBase(){
		return vidaBase;
	}

	public int getVidaAtual(){
		return vidaAtual;
	}
	
	public void setVidaAtual(int vidaAtual){
		this.vidaAtual = vidaAtual;
	}
	
	// ==================== FORÇA ====================
	
	public int getForcaBase(){
		return forçaBase;
	}
	
	public int getForcaAtual(){
		return forçaAtual;
	}
	
	public void setForcaAtual(int forcaAtual){
		this.forçaAtual = forcaAtual;
	}
	
	// ==================== VELOCIDADE ====================
	
	public int getSpeedBase(){
		return speedBase;
	}
	
	public int getSpeedAtual(){
		return speedAtual;
	}
	
	public void setSpeedAtual(int speedAtual){
		this.speedAtual = speedAtual;
	}
	
	// ==================== ESTAMINA ====================
	
	public int getEstaminaBase(){
		return estaminaBase;
	}
	
	public int getEstaminaAtual(){
		return estaminaAtual;
	}
	
	public void setEstaminaBase(int estaminaBase){
		if (estaminaBase <= 0) estaminaBase = 100;
		this.estaminaBase = estaminaBase;
	}
	
	public void setEstaminaAtual(int estaminaAtual){
		if (estaminaAtual < 0) estaminaAtual = 0;
		this.estaminaAtual = estaminaAtual;
	}
	
	// ==================== CARACTERÍSTICAS ====================
	
	public int getIdMonstro(){
		return idMonstro;
	}

	public String getNomeMonstro(){
		return nomeMonstro;
	}
	
	public String getInstanceId(){
		return instanceId;
	}
	
	public Classes getClasseAtual(){
		return classeAtual;
	}
	
	public String getClasseAtualTexto(){
		return classeAtual.getClasseNome();
	}
	
	public Elementos[] getElementosAtuaisValores(){
		return elementosAtuais;
	}
	
	public String getElementosAtuais(){
		return Arrays.toString(elementosAtuais).replaceAll("[\\[\\]]", "");
	}
	
	public Color getCorDoElemento(String elementos){
		if (elementos.contains(Elementos.NATUREZA.toString())) return Grapchics.ELEMENTO_NATUREZA;
		if (elementos.contains(Elementos.FOGO.toString())) return Grapchics.ELEMENTO_FOGO;
		if (elementos.contains(Elementos.TERRA.toString())) return Grapchics.ELEMENTO_TERRA;
		if (elementos.contains(Elementos.TROVAO.toString())) return Grapchics.ELEMENTO_TROVAO;
		if (elementos.contains(Elementos.AGUA.toString())) return Grapchics.ELEMENTO_AGUA;
		if (elementos.contains(Elementos.LUZ.toString())) return Grapchics.ELEMENTO_LUZ;
		if (elementos.contains(Elementos.MAGIA.toString())) return Grapchics.ELEMENTO_MAGIA;
		if (elementos.contains(Elementos.TREVAS.toString())) return Grapchics.ELEMENTO_TREVAS;
		if (elementos.contains(Elementos.METAL.toString())) return Grapchics.ELEMENTO_METAL;
		if (elementos.contains(Elementos.VENTO.toString())) return Grapchics.ELEMENTO_VENTO;
		if (elementos.contains(Elementos.FISICO.toString())) return Grapchics.ELEMENTO_FISICO;
		return Grapchics.BRANCO_CLARO;
	}
	
	public Raridades getRaridadeMonstro(){
		return raridadeMonstro;
	}
	
	public String getRaridadeMonstroTexto(){
		return raridadeMonstro.getRaridadeNome();
	}
	
	public int getProvocationRate(){
		return provocationRate;
	}
	
	public void setProvocationRate(int provocationRate){
		if (provocationRate < this.MIN_PROVOCATION_RATE){
			provocationRate = this.MIN_PROVOCATION_RATE;
		}else if (provocationRate > this.MAX_PROVOCATION_RATE){
			provocationRate = this.MAX_PROVOCATION_RATE;
		}
		this.provocationRate = provocationRate;
	}
	
	// ==================== TRAÇOS ====================
	
	public void adicionarTraço(Traits traço){
		if (traço != null && !traçosAtuais.contains(traço)){
			traçosAtuais.add(traço);
			
			for (TraitEffect traçoEfeito : traço.getEfeitosTraço()){
				traçoEfeito.aplicar(this);
			}
		}
	}
	
	public void adicionarImunidadeTraço(StatusBase status){
		if (status != null && !imunidades.contains(status)){
			imunidades.add(status);
		}
	}
	
	public int[] getTracosIds(){
		return traçosIds;
	}
	
	public String getNomesTracosPorIds(){
		if (this.traçosIds == null || this.traçosIds.length == 0){
			return "Nenhum";
		}
		
		List<String> nomes = new ArrayList<>();
		for (int id : this.traçosIds){
			Traits traco = TraitsManager.getTraço(id);
			if (traco != null){
				nomes.add(traco.getNomeTraço());
			}
		}

		if (nomes.isEmpty()){
			return "Nenhum";
		}

		return String.join(", ", nomes);
	}
	
	public int getStatusArmor(){
		return statusArmor;
	}
	
	public int getHarmonizado(){
		return attunedPercent;
	}
	
	public List<Traits> getTraçosAtuais(){
		return traçosAtuais;
	}
	
	public String getNomesTraços(){
		List<Traits> tracos = this.getTraçosAtuais();
		if (tracos == null || tracos.isEmpty()){
			return "Nenhum";
		}

		List<String> nomes = new ArrayList<>();
		for (Traits traco : tracos){
			if (traco != null) nomes.add(traco.getNomeTraço()); 
		}
		return String.join(", ", nomes);
	}
	
	public void setHarmonizado(int attunedPercent){
		this.attunedPercent = attunedPercent;
	}
	
	public void setStatusArmor(int statusArmor){
		this.statusArmor = statusArmor;
	}
	
	// ==================== STATUS ====================
	
	public boolean isImune(StatusBase status){
		if (status == null) return false;
		int statusId = status.getId();
    
		for (StatusBase s : imunidades){
			if (s != null && s.getId() == statusId){
				return true;
			}
		}
		
		for (StatusBase s : imunidadesTemp){
			if (s != null && s.getId() == statusId){
				return true;
			}
		}
    
		return false;
	}
	
	public void receberStatus(StatusBase status){
		if (status == null || status.getDuraçãoBase() <= 0) return;
		
		if (!possuiStatus(status)){
			statusAtuais.add(status);
		}
	}
	
	public void removerStatus(StatusBase status){
		if (status == null || !this.statusAtuais.contains(status)){
			return;
		}
		
		this.statusAtuais.remove(status);
	}
	
	public void receberImunidadeTemp(StatusBase status){
		if (status == null || status.getDuraçãoBase() <= 0) return;
		
		if (!isImune(status)) {
			imunidadesTemp.add(status);
		}
	}
	
	public void removerImunidadeTemp(StatusBase status){
		if (status == null || status.getDuraçãoBase() <= 0) return;
		
		imunidadesTemp.remove(status);
	}
	
	public boolean possuiStatus(StatusBase status){
		int statusId = status.getId();
		
		for (StatusBase s : statusAtuais){
			if (s != null && s.getId() == statusId){
				return true;
			}
		}
		
		for (StatusBase s : imunidadesTemp){
			if (s != null && s.getId() == statusId){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean possuiStatus(int statusId){
		for (StatusBase s : statusAtuais){
			if (s != null && s.getId() == statusId){
				return true;
			}
		}
		
		for (StatusBase s : imunidadesTemp){
			if (s != null && s.getId() == statusId){
				return true;
			}
		}
    
		return false;
	}
	
	public StatusBase getStatus(int statusId){
		for (StatusBase s : statusAtuais){
			if (s != null && s.getId() == statusId && s.isAtivo()){
				return s;
			}
		}
		return null;
	}
	
	public void checarStatus(){
		if (this.statusAtuais.isEmpty() || this.vidaAtualCombate <= 0) return;
		
		for (StatusBase status : this.statusAtuais){
			if (status != null && status.isAtivo()){
				status.checar(this);
			}
		}
	}
	
	public void reduzirDuraçãoStatus(){
		if (this.statusAtuais.isEmpty()) return;
		
		List<StatusBase> copiaStatus = new ArrayList<>(this.statusAtuais);
		
		Iterator<StatusBase> iterator = copiaStatus.iterator();
		while (iterator.hasNext()){
			StatusBase status = iterator.next();
			if (status != null && status.isAtivo()){
				status.reduzirDuração(this);
				if (status.getDuraçãoAtual() <= 0){
					iterator.remove();
				}
			}
		}
		
		this.statusAtuais.removeIf(status -> !status.isAtivo());
	}
	
	public List<StatusBase> getStatusAtuais(){
		return statusAtuais;
	}
	
	public void limparStatus(){
		if (statusAtuais == null) return;
		statusAtuais.clear();
		imunidadesTemp.clear();
	}
	
	// ==================== OUTROS ====================
	
	public boolean isMonstroEquipado(){ 
		return monstroEquipado;
	}
	
	public boolean isMonstroFavorito(){ 
		return monstroFavorito;
	}
	
	public void setMonstroEquipado(boolean monstroEquipado){
		this.monstroEquipado = monstroEquipado;
	}
	
	public void setMonstroFavorito(boolean monstroFavorito){
		this.monstroFavorito = monstroFavorito;
	}
	
	//===
}