package main.inventory;

import static main.Terminal.mudarEstado;

import bestiary.Monsters;
import manager.MonstersManager;
import manager.MonstersDescriptionManager;
import bestiary.Skills;

import util.GameState;
import util.Grapchics;
import util.Input;

import world.Maps;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Set;

import java.awt.event.KeyEvent;

public final class Inventory implements GameState{
	protected static enum SlotEquipe{
        SLOT_1, SLOT_2, SLOT_3,
        SLOT_4, SLOT_5, SLOT_6;
    }
	private enum SubEstadosInventário{
		DETALHES("Detalhes"),
		HABILIDADES("Habilidades"),
		BIOGRAFIA("Biografia");
		
		private final String nome;
		
		SubEstadosInventário(String nome){
			this.nome = nome;
		}
		
		public String getSubEstadoNome(){
			return nome;
		}
	}
	
	// ==================== ATRIBUTOS ====================
	
	private static Map<Integer, Monsters> monstrosInventario;
    private static List<Monsters> monstrosOrdenados;
    private static List<Skills> skillsTree, skillsDesbloqueadas;
	private static EnumMap<SlotEquipe, Monsters> equipeTabela;
    private static SubEstadosInventário subEstadoAtual = null;
	private static SlotEquipe slotEncontrado;

    private static Monsters monstroCarregado;
	private static Skills skillCarregada, skillMostrada;
	
	private static int offsetBiografia = 0;
	private static int idMonstroSelecionado = 1;
	
    private static int idInventario, tamanhoInventario, linhaAtual, paginaAtual, totalPaginas,
    inicioLista, fimLista, posiçãoLinhaInventário, posiçãoLinhaEquipe, posiçãoLinhaSkillsAtivas;
    private static String nomeMonstroExibido;
	
	public Inventory(){
		Inventory.subEstadoAtual = null;
	}
	
	// ==================== INICIALIZAÇÃO ====================
	
	public final static void inicializarInventario(boolean equipe){
        monstrosInventario = new HashMap<Integer, Monsters>();
        monstrosOrdenados = new ArrayList<Monsters>();
        equipeTabela = new EnumMap<>(SlotEquipe.class);
		skillsTree = new ArrayList<>();
		skillsDesbloqueadas = new ArrayList<>();
		
        idInventario = 1;
        paginaAtual = 1;
        inicioLista = 1;
        fimLista = 1;
		if (equipe) montarEquipeInicial();
    }
	
	private static void montarEquipeInicial(){
		Inventory.adicionarMonstroInventário(40);
		Monsters monstro = Inventory.getMonstroInventario(1);
	}
	
	// ==================== ESTADO ====================
	
	@Override
	public void atualizaEstado(){
		if (monstrosInventario == null || monstrosInventario.isEmpty()) return;
		
		if (subEstadoAtual == null){
            totalPaginas = (int) Math.ceil(monstrosInventario.size() / 24.0);
            if (paginaAtual > totalPaginas) paginaAtual = totalPaginas;
            if (paginaAtual < 1) paginaAtual = 1;

            int inicioLista = (paginaAtual - 1) * 24;
            int fimLista = Math.min(inicioLista + 24, monstrosInventario.size());
            
            if (Input.getCursorY() < inicioLista + 1) Input.setCursorY(fimLista);
            if (Input.getCursorY() > fimLista) Input.setCursorY(inicioLista + 1);
            
            idMonstroSelecionado = Input.getCursorY();
        }else if (subEstadoAtual == SubEstadosInventário.DETALHES || subEstadoAtual == SubEstadosInventário.BIOGRAFIA){
            if (Input.getCursorX() <= 0) Input.setCursorX(1);
            if (Input.getCursorX() > monstrosInventario.size()) Input.setCursorX(monstrosInventario.size());
            
            idMonstroSelecionado = Input.getCursorX();
            monstroCarregado = getMonstroInventario(idMonstroSelecionado);
        }else if (subEstadoAtual == SubEstadosInventário.HABILIDADES){
            if (Input.getCursorY() < 5) Input.setCursorY(5); 
        }
	}
	
	@Override
	public void desenhaEstado(){
		Grapchics.limpaTela();
		
		if (subEstadoAtual == null){
			InventoryScreen.desenhaInventário(monstrosInventario, equipeTabela, paginaAtual, totalPaginas, Input.getCursorY());
		}else if (subEstadoAtual == SubEstadosInventário.DETALHES){
			InventoryScreen.desenhaMonstroDetalhes(monstroCarregado);
		}else if (subEstadoAtual == SubEstadosInventário.HABILIDADES){
			skillMostrada = InventoryScreen.desenhaHabilidadeDetalhes(monstroCarregado, Input.getCursorY());
		}else if (subEstadoAtual == SubEstadosInventário.BIOGRAFIA){
			InventoryScreen.desenhaBiografiaDetalhes(monstroCarregado, offsetBiografia);
		}
		
		Grapchics.atualizarTela();
	}
	
	@Override
    public void recebeComando(int tecla, Set<Integer> teclasPressionadas){
		switch (tecla){
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:teclaEsquerda(); break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT: teclaDireita(); break;
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP: teclaCima(); break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN: teclaBaixo(); break;
			case KeyEvent.VK_ENTER: teclaEnter(); break;
			case KeyEvent.VK_SHIFT: teclaShift(); break;
			case KeyEvent.VK_E: teclaInventário(); break;
			case KeyEvent.VK_Q: teclaQ(); break;
		}
	}
	
	// ==================== TECLAS ====================
	
	private void teclaEsquerda(){
		if (subEstadoAtual == null){
			Inventory.alternarPagina(false);
		}else if (subEstadoAtual == SubEstadosInventário.DETALHES){
			Input.decrementarCursorX();
			idMonstroSelecionado = Input.getCursorX();
		}else if (subEstadoAtual == SubEstadosInventário.HABILIDADES){
			Input.decrementarCursorX();
		}else if (subEstadoAtual == SubEstadosInventário.BIOGRAFIA){
			Input.decrementarCursorX();
			idMonstroSelecionado = Input.getCursorX();
			offsetBiografia = 0;
		}
	}
	
	private void teclaDireita(){
		if (subEstadoAtual == null){
			Inventory.alternarPagina(true);
		}else if (subEstadoAtual == SubEstadosInventário.DETALHES){
			Input.incrementarCursorX();
			idMonstroSelecionado = Input.getCursorX();
		}else if (subEstadoAtual == SubEstadosInventário.HABILIDADES){
			Input.incrementarCursorX();
		}else if (subEstadoAtual == SubEstadosInventário.BIOGRAFIA){
			Input.incrementarCursorX();
			idMonstroSelecionado = Input.getCursorX();
			offsetBiografia = 0;
		}
	}
	
	private void teclaCima(){
		if (subEstadoAtual == SubEstadosInventário.BIOGRAFIA){
			if (offsetBiografia > 0) offsetBiografia--;
		}else{
			Input.decrementarCursorY();
		}
	}
	
	private void teclaBaixo(){
		if (subEstadoAtual == SubEstadosInventário.BIOGRAFIA){
			if (monstroCarregado != null){
				String desc = MonstersDescriptionManager.getDescrição(monstroCarregado.getIdMonstro());
				if (desc != null){
                    int maxLinhasVisiveis = 32;
                    if (offsetBiografia < (desc.length() / 38) + 5) offsetBiografia++;
                }
			}
		}else{
			Input.incrementarCursorY();
		}
	}
	
	private void teclaEnter(){
		if (subEstadoAtual == null){
			Inventory.alternarMonstroTabela(idMonstroSelecionado);
		}else if (subEstadoAtual == SubEstadosInventário.DETALHES){
			Inventory.alternarMonstroFavorito(idMonstroSelecionado);
		}else if (subEstadoAtual == SubEstadosInventário.HABILIDADES){
			Inventory.alternarHabilidadeAtiva();
		}
	}
	
	private void teclaShift(){
		if (subEstadoAtual == null){
			Input.setCursorX(idMonstroSelecionado);
			subEstadoAtual = SubEstadosInventário.DETALHES;
		}else if (subEstadoAtual == SubEstadosInventário.DETALHES){
			Input.setCursorAnteriorY(Input.getCursorY());
			subEstadoAtual = SubEstadosInventário.HABILIDADES;
		}
	}
	
	private void teclaInventário(){
		if (subEstadoAtual == null){
			subEstadoAtual = null;
			mudarEstado(new Maps());
		}else if (subEstadoAtual == SubEstadosInventário.DETALHES){
			subEstadoAtual = null;
		}else if (subEstadoAtual == SubEstadosInventário.HABILIDADES){
			Input.setCursorY(Input.getCursorAnteriorY());
			subEstadoAtual = SubEstadosInventário.DETALHES;
		}else if (subEstadoAtual == SubEstadosInventário.BIOGRAFIA){
			subEstadoAtual = SubEstadosInventário.DETALHES;
		}
	}
	
	private void teclaQ(){
		if (subEstadoAtual == SubEstadosInventário.DETALHES){
			offsetBiografia = 0;
			subEstadoAtual = SubEstadosInventário.BIOGRAFIA;
		}
	}
	
	// ==================== AÇÕES DO JOGADOR ====================
	
	private static void alternarHabilidadeAtiva(){
		if (monstroCarregado == null || skillMostrada == null) return;
		
		int maxSlots = monstroCarregado.getQuantidadeMaxSlotsHabilidade();
		int slotsOcupados = monstroCarregado.getQuantidadeSlotsOcupados();
		boolean isEspecial = skillMostrada.isTipoEspecial(skillMostrada.getTipoHabilidade());
		boolean isAtiva = monstroCarregado.isHabilidadeAtiva(skillMostrada);
		boolean isDesbloqueada = monstroCarregado.isHabilidadeDesbloqueada(skillMostrada);
		
		if (isEspecial){
			return; 
		}
		
		if (isAtiva && slotsOcupados >= 2){
			if (monstroCarregado.removerHabilidadeAtiva(skillMostrada)){
				monstroCarregado.reordenarSkillsAtivas(); 
			}
		}else if (!isAtiva && isDesbloqueada){
			monstroCarregado.adicionarHabilidadeAtiva(skillMostrada);
		}
	}
	
    private static void alternarMonstroTabela(int id){
        Monsters monstro = monstrosInventario.get(id);
        if (monstro == null)
            return;

        if (monstro.isMonstroEquipado()){
            slotEncontrado = null;
            for (Map.Entry<SlotEquipe, Monsters> entry : equipeTabela.entrySet()){
                if (entry.getValue() == monstro){
                    slotEncontrado = entry.getKey();
                    break;
                }
            }

            if (slotEncontrado != null && equipeTabela.size() >= 2){
                equipeTabela.remove(slotEncontrado);
                monstro.setMonstroEquipado(false);
                reordenarEquipe();
            }
        }else{
            for (SlotEquipe slot : SlotEquipe.values()){
                if (!equipeTabela.containsKey(slot)){
                    equipeTabela.put(slot, monstro);
                    monstro.setMonstroEquipado(true);
                    break;
                }
            }
        }
    }

    private static void alternarMonstroFavorito(int id){
        Monsters monstro = monstrosInventario.get(id);
        if (monstro == null) return;

        monstro.setMonstroFavorito(!monstro.isMonstroFavorito());
    }
	
    private static void alternarPagina(boolean avancar){
        if (avancar){
            paginaAtual++;
            if (paginaAtual > totalPaginas) paginaAtual = 1;
        }else{
            paginaAtual--;
            if (paginaAtual < 1) paginaAtual = totalPaginas;
        }
    }
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	public static void adicionarMonstroInventário(int id){
		if (monstrosInventario == null) return;
		
        Monsters monstroRequerido = MonstersManager.getMonstro(id);
        monstroCarregado = new Monsters(monstroRequerido);
        monstrosInventario.put(idInventario++, monstroCarregado);
		
        for (SlotEquipe slot : SlotEquipe.values()){
            if (!equipeTabela.containsKey(slot)){
                equipeTabela.put(slot, monstroCarregado);
                monstroCarregado.setMonstroEquipado(true);
                break;
            }
        }
    }
	
	public static Monsters adicionarMonstroSemEquipar(int idEspecie){
		if (monstrosInventario == null) return null;
 
		Monsters monstroRequerido = MonstersManager.getMonstro(idEspecie);
		if (monstroRequerido == null) return null;
 
		Monsters novoMonstro = new Monsters(monstroRequerido);
		monstrosInventario.put(idInventario++, novoMonstro);
		return novoMonstro;
	}
	
	public static void equiparProximoSlotLivre(Monsters monstro){
		if (monstro == null || equipeTabela == null) return;
 
		for (SlotEquipe slot : SlotEquipe.values()){
			if (!equipeTabela.containsKey(slot)){
				equipeTabela.put(slot, monstro);
				monstro.setMonstroEquipado(true);
				break;
			}
		}
	}
	
    public static void removerMonstroInventário(int id){
        monstroCarregado = monstrosInventario.get(id);
        if (monstroCarregado == null) return;
		
        monstrosInventario.remove(id);
        monstroCarregado.setMonstroEquipado(false);

        slotEncontrado = null;
        for (Map.Entry<SlotEquipe, Monsters> entry : equipeTabela.entrySet()){
            if (entry.getValue() == monstroCarregado){
                slotEncontrado = entry.getKey();
                break;
            }
        }
        if (slotEncontrado != null){
            equipeTabela.remove(slotEncontrado);
            reordenarEquipe();
        }

        // Reordena inventário.
        Monsters[] monstrosAtuais = monstrosInventario.values().toArray(new Monsters[0]);
        monstrosInventario.clear();
        idInventario = 1;
        for (int i = 0; i < monstrosAtuais.length; i++){
            monstrosInventario.put(idInventario++, monstrosAtuais[i]);
        }
    }
	
	private static void removerTodosMonstrosInventário(int id){
        monstroCarregado = monstrosInventario.get(id);
        if (monstroCarregado == null) return;
		
        monstrosInventario.remove(id);
        monstroCarregado.setMonstroEquipado(false);

        slotEncontrado = null;
        for (Map.Entry<SlotEquipe, Monsters> entry : equipeTabela.entrySet()){
            if (entry.getValue() == monstroCarregado){
                slotEncontrado = entry.getKey();
                break;
            }
        }
        if (slotEncontrado != null){
            equipeTabela.remove(slotEncontrado);
        }
    }
	
	private static void reordenarEquipe(){
		Monsters[] monstrosAtuais = equipeTabela.values().toArray(new Monsters[0]);
		equipeTabela.clear();
		
		SlotEquipe[] slots = SlotEquipe.values();
		for (int i = 0; i < monstrosAtuais.length && i < slots.length; i++){
			equipeTabela.put(slots[i], monstrosAtuais[i]);
		}
	}
	
	private static void reordenarListaInventario(){
        monstrosOrdenados.clear();

        for (int i = 1; i <= monstrosInventario.size(); i++){
            monstroCarregado = monstrosInventario.get(i);
            if (monstroCarregado != null && (monstroCarregado.isMonstroEquipado() || monstroCarregado.isMonstroFavorito())){
                monstrosOrdenados.add(monstroCarregado);
            }
        }

        for (int i = 1; i <= monstrosInventario.size(); i++){
            monstroCarregado = monstrosInventario.get(i);
            if (monstroCarregado != null && !monstroCarregado.isMonstroEquipado() && !monstroCarregado.isMonstroFavorito()){
                monstrosOrdenados.add(monstroCarregado);
            }
        }

        monstrosInventario.clear();
        idInventario = 1;
        for (Monsters m : monstrosOrdenados){
            monstrosInventario.put(idInventario++, m);
        }
    }
	
	// ==================== OUTROS ====================
	
	protected static Map<Integer, Monsters> getMonstrosInventario(){
        return monstrosInventario;
    }
	
	public static Map<Integer, Monsters> getTodosMonstros(){
        return monstrosInventario;
    }
	
    protected static EnumMap<SlotEquipe, Monsters> getEquipeTabela(){
        return equipeTabela;
    }

    public static int getTamanhoInventario(){
        return monstrosInventario != null ? monstrosInventario.size() : 0;
    }

    public static Monsters getMonstroInventario(int id){
        return monstrosInventario != null ? monstrosInventario.get(id) : null;
    }
	
	public static void preencherInventario(){
        if (monstrosInventario == null) return;
		
		int monstrosExistentes = MonstersManager.getMonstrosExistentesTamanho();
		for (int i = 1; i <= monstrosExistentes; i++){
			adicionarMonstroInventário(i);
		}
    }
	
	public static void limparInventario(){
        if (monstrosInventario == null) return;
		
		int tamanhoInventárioAtual = getTamanhoInventario();
		for (int i = 1; i <= tamanhoInventárioAtual; i++){
			removerTodosMonstrosInventário(i);
		}
    }
	
	public static List<Monsters> getEquipeLista(){
		List<Monsters> lista = new ArrayList<>();
		for (SlotEquipe slot : SlotEquipe.values()){
			lista.add(equipeTabela.get(slot));
		}
		return lista;
	}
	
	public static int getTamanhoEquipe(){
		return equipeTabela.size();
	}

	public static boolean temSlotVazio(){
		return equipeTabela.size() < SlotEquipe.values().length;
	}
	
	//===
}