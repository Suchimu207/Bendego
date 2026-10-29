package main;

import static main.Terminal.mudarEstado;

import bestiary.Monsters;
import bestiary.Skills;
import main.inventory.Inventory;
import manager.MonstersManager;
import manager.SkillsManager;

import util.GameState;
import util.Grapchics;
import util.Input;

import world.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Comparator;

import java.util.Set;

import java.awt.event.KeyEvent;

import java.awt.Color;

public final class Shop implements GameState{
    private static class ItemLoja {
        private final int idMonstro;
        private final int preco;
        private final int idEstante;
        private boolean carrinhoItem; 
        
        ItemLoja(int idMonstro, int preco, int idEstante){
            if (idMonstro <= 0){
                this.idMonstro = 1;
            }else{
                this.idMonstro = idMonstro;
            }
            
            if (preco <= -1){
                this.preco = 0;
            }else{
                this.preco = preco;
            }
            
            this.idEstante = idEstante;
            this.carrinhoItem = false;
        }
        
        public boolean isItemCarrinho(){ 
            return carrinhoItem;
        }
        
        public void setItemCarrinho(boolean carrinhoAtivo){
            this.carrinhoItem = carrinhoAtivo;
        }
    }
	
	private enum SubEstadosLoja{
		DETALHES("Detalhes"),
		RECIBO("Recibo");
		
		private final String nome;
		
		SubEstadosLoja(String nome){
			this.nome = nome;
		}
		
		public String getEstadoNome(){
			return nome;
		}
	}
	
    // ==================== ATRIBUTOS ====================
	
	private static ArrayList<ItemLoja> estoque, carrinho, estoqueFiltrado;
	private static Monsters.Raridades filtroRaridadeAtual = null;
	
    private static int linhaItem, idEstanteAtual, totalPaginas, paginaAtual, 
    inicioLista, fimLista, tamanhoLoja, tamanhoRecibo, ouroGasto, total;
    
    private static String indicadorPagina;
	
	private static Monsters monstroVisualizado = null;
	
	private static SubEstadosLoja subEstadoAtual = null;
	
	private static final int MAX_MONSTERS_PAG = 30;
	
    public Shop(){
		Shop.limparCarrinho();
		filtroRaridadeAtual = null;
		aplicarFiltro();
    }
    
	// ==================== INICIALIZAÇÃO ====================
	
    protected static void inicializarLoja(){
        estoque = new ArrayList<>();
        carrinho = new ArrayList<>();
		
		filtroRaridadeAtual = null;
		
        idEstanteAtual = 1;
        paginaAtual = 1;
        ouroGasto = 0;
		indicadorPagina = "";
        
		int monstrosExistentes = MonstersManager.getMonstrosExistentesTamanho();
		
        for (int i = 1; i <= monstrosExistentes; i++){
			int preco = 150;
			
			Monsters infoMonstro = MonstersManager.getMonstro(i);
			if (infoMonstro == null) continue;
			
			if (infoMonstro.getRaridadeMonstro() == Monsters.Raridades.INCOMUM){
				preco = 350;
			}else if (infoMonstro.getRaridadeMonstro() == Monsters.Raridades.RARO){
				continue;
			}else if (infoMonstro.getRaridadeMonstro() == Monsters.Raridades.EPICO){
				continue;
			}else if (infoMonstro.getRaridadeMonstro() == Monsters.Raridades.LENDARIO){
				continue;
			}
			
            estoque.add(new ItemLoja(i, preco, idEstanteAtual++));
        }
		
		aplicarFiltro();
    }
	
	// ==================== ESTADO ====================
	
	@Override
	public void atualizaEstado(){
	}
	
	@Override
	public void desenhaEstado(){
		Grapchics.limpaTela();
		
		if (subEstadoAtual == null){
			Shop.desenhaLoja();
		}else if (subEstadoAtual == SubEstadosLoja.DETALHES){
			Shop.desenhaItemDetalhes();
		}else if (subEstadoAtual == SubEstadosLoja.RECIBO){
			Shop.desenhaLojaRecibo();
		}
		
		Grapchics.atualizarTela();
	}
	
	@Override
    public void recebeComando(int tecla, Set<Integer> teclasPressionadas){
		switch (tecla){
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT: teclaEsquerda(); break;
				
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT: teclaDireita(); break;
				
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP: teclaCima(); break;
				
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN: teclaBaixo(); break;
				
			case KeyEvent.VK_ENTER: teclaEnter(); break;
			case KeyEvent.VK_SHIFT: teclaShift(); break;
			case KeyEvent.VK_F: teclaFiltro(); break;
			case KeyEvent.VK_E: teclaInventário(); break;
			case KeyEvent.VK_Q: teclaComprar(); break;
			case KeyEvent.VK_ESCAPE: teclaEsc(); break;
		}
	}
	
	// ==================== TECLAS ====================
	
	private void teclaEsquerda(){
		if (subEstadoAtual == null){
			Shop.alternarPagina(true);
		}else if (subEstadoAtual == SubEstadosLoja.RECIBO){
			Input.decrementarCursorX();
		}
	}
	
	private void teclaDireita(){
		if (subEstadoAtual == null){
			Shop.alternarPagina(false);
		}else if (subEstadoAtual == SubEstadosLoja.RECIBO){
			Input.incrementarCursorX();
		}
	}
	
	private void teclaCima(){
		if (subEstadoAtual == null){
			Input.decrementarCursorY();
		}
	}
	
	private void teclaBaixo(){
		if (subEstadoAtual == null){
			Input.incrementarCursorY();
		}
	}
	
	private void teclaEnter(){
		if (subEstadoAtual == null || subEstadoAtual == SubEstadosLoja.DETALHES){
			Shop.alternarItemCarrinho();
		}
	}
	
	private void teclaShift(){
		if (subEstadoAtual == null){
			subEstadoAtual = SubEstadosLoja.DETALHES;
		}else if (subEstadoAtual == SubEstadosLoja.DETALHES){
			subEstadoAtual = null;
		}
	}
	
	private void teclaFiltro(){
		if (subEstadoAtual != null) return;
		
		if (filtroRaridadeAtual == null){
			filtroRaridadeAtual = Monsters.Raridades.COMUM;
		}else if (filtroRaridadeAtual == Monsters.Raridades.COMUM){
			filtroRaridadeAtual = Monsters.Raridades.INCOMUM;
		}else if (filtroRaridadeAtual == Monsters.Raridades.INCOMUM){
			filtroRaridadeAtual = Monsters.Raridades.RARO;
		}else if (filtroRaridadeAtual == Monsters.Raridades.RARO){
			filtroRaridadeAtual = Monsters.Raridades.EPICO;
		}else if (filtroRaridadeAtual == Monsters.Raridades.EPICO){
			filtroRaridadeAtual = Monsters.Raridades.LENDARIO;
		}else{
			filtroRaridadeAtual = null;
		}
		
		aplicarFiltro();
	}
	
	private void teclaInventário(){
		if (subEstadoAtual == null){
			mudarEstado(new Maps());
		}else if (subEstadoAtual == SubEstadosLoja.DETALHES){
			subEstadoAtual = null;
		}else if (subEstadoAtual == SubEstadosLoja.RECIBO){
			Shop.limparCarrinho();
			subEstadoAtual = null;
			mudarEstado(new Inventory());
		}
	}
	
	private void teclaComprar(){
		if (subEstadoAtual == null || subEstadoAtual == SubEstadosLoja.DETALHES){
			if (Shop.comprarMonstro()){
				Input.resetarCursor();
				subEstadoAtual = SubEstadosLoja.RECIBO;
			}
		}else if (subEstadoAtual == SubEstadosLoja.RECIBO){
			Shop.limparCarrinho();
			Input.resetarCursor();
			subEstadoAtual = null;
		}
	}
	
	private void teclaEsc(){
		if (subEstadoAtual == SubEstadosLoja.RECIBO){
			Shop.limparCarrinho();
			subEstadoAtual = null;
			mudarEstado(new Maps());
		}
	}
	
	// ==================== DESENHO ====================
	
    private static void desenhaLoja(){
		linhaItem = 0;
		tamanhoLoja = estoqueFiltrado.size();
        inicioLista = (paginaAtual - 1) * MAX_MONSTERS_PAG;
        fimLista = Math.min(inicioLista + MAX_MONSTERS_PAG, tamanhoLoja);
		totalPaginas = Math.max(1, (int) Math.ceil(tamanhoLoja / 30.0));
		
		if (Input.getCursorY() < inicioLista) Input.setCursorY(fimLista);
		if (Input.getCursorY() >= fimLista) Input.setCursorY(inicioLista);
		
		String pag = "Loja - Página";
		String barra = (char)47+"";
		int tamanhoPag = pag.length();
		
		Grapchics.desenhaCentroTTF(pag,linhaItem, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela(" "+paginaAtual+barra+totalPaginas,tamanhoPag+13, linhaItem++, Grapchics.BRANCO_CLARO);
		
        desenhaOpçõesLoja(false);
		
		Grapchics.desenhaTela("____________________", 0, linhaItem++, Grapchics.PRETO_CLARO);
		desenhaListaLoja();
        Grapchics.desenhaTela("____________________", 0, linhaItem, Grapchics.PRETO_CLARO);
		
        Grapchics.atualizarTela();
    }
	
	private static void desenhaListaLoja(){
		for (int i = inicioLista; i < fimLista; i++){
			ItemLoja item = estoqueFiltrado.get(i);
            Monsters infoMonstro = MonstersManager.getMonstro(item.idMonstro); 
            if (infoMonstro == null) continue;

            int textoMarcado = item.isItemCarrinho() ? 36 : 0;
			
            if (Input.getCursorY() == i){
                Grapchics.desenhaHibrido(infoMonstro.getNomeMonstro()+" - Preço: "+item.preco,textoMarcado, 1, 
				linhaItem++, Grapchics.AMARELO_CLARO);
				monstroVisualizado = infoMonstro;
				Input.setCursorX(monstroVisualizado.getIdMonstro());
            }else{
                Grapchics.desenhaHibrido(infoMonstro.getNomeMonstro()+" - Preço: "+item.preco,textoMarcado, 0, 
				linhaItem++, Grapchics.BRANCO_CLARO);
            }
        }
	}
	
	private static void desenhaItemDetalhes(){
		if (monstroVisualizado == null || estoque == null) return;
		
		Skills especial = monstroVisualizado.getHabilidadeEspecial();
		if (especial == null) return;
		
		ItemLoja item = getItemPorMonstroId(monstroVisualizado.getIdMonstro());
		if (item == null) return;
		
		int textoMarcado = item.isItemCarrinho() ? 36 : 0;
		
		linhaItem = 0;
		
		Grapchics.desenhaCentroTTF("Detalhes - Loja", linhaItem++, Grapchics.BRANCO_CLARO);
		desenhaOpçõesLoja(true);
		
		Grapchics.desenhaTela("____________________",0,linhaItem++, Grapchics.PRETO_CLARO);
		
		Grapchics.desenhaHibrido("Nome: "+monstroVisualizado.getNomeMonstro(),textoMarcado,0,linhaItem++, Grapchics.BRANCO_CLARO);
		
		Grapchics.desenhaTTF("Nível: "+monstroVisualizado.getNivelAtual(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("Classe: "+monstroVisualizado.getClasseAtualTexto(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		desenhaElementoMonstro(linhaItem);
		linhaItem++;
		
		Grapchics.desenhaTTF("Raridade: "+monstroVisualizado.getRaridadeMonstroTexto(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("Força: "+monstroVisualizado.getForcaAtual(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("Vida: "+monstroVisualizado.getVidaAtual(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("Velocidade: "+monstroVisualizado.getSpeedAtual(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("Estamina: "+monstroVisualizado.getEstaminaAtual(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF("Energia: "+monstroVisualizado.getBarraEspecialMaximo(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		
		Grapchics.desenhaTTF("Traços: "+monstroVisualizado.getNomesTracosPorIds(),0,linhaItem++, Grapchics.BRANCO_CLARO);
		
		Grapchics.desenhaTela("____________________",0,linhaItem++,Grapchics.PRETO_CLARO);
		linhaItem++;
		
		Grapchics.desenhaTela("____________________",0,linhaItem++,Grapchics.PRETO_CLARO);
		for (int i = 0; i < monstroVisualizado.getQuantidadeMaxSlotsHabilidade(); i++){
			Skills skillCarregada = monstroVisualizado.getHabilidadeAtiva(i);
			
			if (skillCarregada != null){
				Grapchics.desenhaTTF((i+1)+": ",0,linhaItem,Grapchics.BRANCO_CLARO);
				Grapchics.desenhaTTF(skillCarregada.getNomeHabilidade(),3,linhaItem++,skillCarregada.getCorHabilidade());
			}else{
				Grapchics.desenhaTTF("[VAZIO]",0,linhaItem++,Grapchics.PRETO_CLARO);
			}
		}
		Grapchics.desenhaTela("____________________",0,linhaItem++,Grapchics.PRETO_CLARO);
		linhaItem++;
		
		Grapchics.desenhaTTF("Especial:",0,linhaItem++,Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela("____________________",0,linhaItem++,Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF(especial.getNomeHabilidade(),0,linhaItem++,especial.getCorHabilidade());
		Grapchics.desenhaTela("____________________",0,linhaItem++,Grapchics.PRETO_CLARO);
	}
	
	private static void desenhaElementoMonstro(int linha){
		if (monstroVisualizado == null) return;
		
		Grapchics.desenhaTTF("Elementos: ", 0, linha, Grapchics.BRANCO_CLARO);

		Monsters.Elementos[] elementos = monstroVisualizado.getElementosAtuaisValores();
		if (elementos == null || elementos.length == 0) return;

		int colunaX = 11;

		for (int i = 0; i < elementos.length; i++){
			Monsters.Elementos elemento = elementos[i];
			if (elemento == null) continue;

			String nomeElemento = elemento.getElementoNome();
			Color corElemento = monstroVisualizado.getCorDoElemento(elemento.name());
			
			Grapchics.desenhaTTF(nomeElemento, colunaX, linha, corElemento);
			colunaX += nomeElemento.length();
			
			if (i < elementos.length - 1){
				Grapchics.desenhaTela((char)47, colunaX, linha, Grapchics.BRANCO_CLARO);
				colunaX += 1;
			}
		}
	}
	
	private static void desenhaLojaRecibo(){        
        tamanhoRecibo = carrinho.size();
		inicioLista = (paginaAtual - 1) * 24;
        fimLista = Math.min(inicioLista + 24, tamanhoRecibo);
        totalPaginas = Math.max(1, (int) Math.ceil(tamanhoRecibo / 24.0));
		
		String pag = "Recibo - Página";
		String barra = (char)47+"";
		int tamanhoPag = pag.length();
		
		Grapchics.desenhaCentroTTF(pag,0, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTela(" "+paginaAtual+barra+totalPaginas,tamanhoPag+12, 0, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("ESC: Sair", 0, 1, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("E: Abrir inventário", 0, 2, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTTF("Q: Continuar comprando", 0, 3, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Ouro atual: " + Player.getOuro(), 0, 4, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Ouro gasto:", 0, 5, Grapchics.BRANCO_CLARO);
		Grapchics.desenhaTTF(" " + ouroGasto,11,5, Grapchics.AMARELO_CLARO);
        Grapchics.desenhaTela("____________________", 0, 6, Grapchics.PRETO_CLARO);
        linhaItem = 7;
        
        desenhaListaRecibo();
        Grapchics.desenhaTela("____________________", 0, linhaItem, Grapchics.PRETO_CLARO);
    }
	
    private static void desenhaListaRecibo(){
        for (int i = inicioLista; i < fimLista; i++){
            ItemLoja item = carrinho.get(i);
            Monsters infoMonstro = MonstersManager.getMonstro(item.idMonstro);
            if (infoMonstro == null) continue;
            
			String nomeMonstro = infoMonstro.getNomeMonstro();
			int tamanhoNome = nomeMonstro.length();
			int tamanhoElementos = infoMonstro.getElementosAtuais().length();
			
            Grapchics.desenhaTTF(nomeMonstro, 0, linhaItem, Grapchics.BRANCO_CLARO);
			Grapchics.desenhaTTF("(", tamanhoNome+1, linhaItem, Grapchics.BRANCO_CLARO);
			
			Grapchics.desenhaTTF(infoMonstro.getElementosAtuais(), tamanhoNome+2, linhaItem, 
			infoMonstro.getCorDoElemento(infoMonstro.getElementosAtuais()));
			
			Grapchics.desenhaTTF(")", tamanhoNome+tamanhoElementos+2, linhaItem++, Grapchics.BRANCO_CLARO);
        }
    }
	
	private static void desenhaOpçõesLoja(boolean isLongeDoCaixa){
		if (isLongeDoCaixa){
			Grapchics.desenhaTTF("E: Voltar", 0, linhaItem++, Grapchics.PRETO_CLARO);
		}else{
			Grapchics.desenhaTTF("E: Sair", 0, linhaItem++, Grapchics.PRETO_CLARO);
		}
		
        if (carrinho != null && carrinho.size() >= 1){
			Grapchics.desenhaTTF("Q: Comprar", 0, linhaItem++, Grapchics.PRETO_CLARO);
		}
        
		if (!isLongeDoCaixa){
			String textoFiltro = "F: Mudar filtro (";
			if (filtroRaridadeAtual == null){
				textoFiltro += "Todos";
			}else{
				textoFiltro += filtroRaridadeAtual.getRaridadeNome();
			}
			
			Grapchics.desenhaTTF(textoFiltro+")", 0, linhaItem++, Grapchics.PRETO_CLARO);
			Grapchics.desenhaTTF("Shift: Ver detalhes", 0, linhaItem++, Grapchics.PRETO_CLARO);
		}
		
		Grapchics.desenhaTTF("ENTER: Colocar", 0, linhaItem, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTela(""+(char)47,14,linhaItem,Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Remover do carrinho", 15, linhaItem++, Grapchics.PRETO_CLARO);
		desenhaOuroCarrinho();
	}
	
	private static void desenhaOuroCarrinho(){
		Grapchics.desenhaTTF("Ouro: "+Player.getOuro(), 0, linhaItem++, Grapchics.BRANCO_CLARO);
		
		if (carrinho != null && !carrinho.isEmpty()){
			total = 0;
            for (int i = 0; i < carrinho.size(); i++){
                ItemLoja item = carrinho.get(i);
                if (item == null) continue;
                total += item.preco;
            }
			
			if (Player.getOuro() > total){
				Grapchics.desenhaTTF("Total: "+total, 0, linhaItem++, Grapchics.VERDE_CLARO);
			}else if (Player.getOuro() == total){
				Grapchics.desenhaTTF("Total: "+total, 0, linhaItem++, Grapchics.AMARELO_CLARO);
			}else if (Player.getOuro() < total){
				Grapchics.desenhaTTF("Total: "+total, 0, linhaItem++, Grapchics.VERMELHO_CLARO);
			}
		}
	}
	
	// ==================== AÇÕES DO JOGADOR ====================
	
    protected static boolean comprarMonstro(){
        if (carrinho != null && !carrinho.isEmpty()){
            int total = 0;
            for (int i = 0; i < carrinho.size(); i++){
                ItemLoja item = carrinho.get(i);
                if (item == null) continue;
                total += item.preco;
            }
            
            if (Player.getOuro() >= total){
				Player.perderOuro(total);
                ouroGasto = total;
                
                for (ItemLoja item : carrinho){
                    Inventory.adicionarMonstroInventário(item.idMonstro);
                }
                paginaAtual = 1;
                return true;
            }
        }
        return false;
    }
	
	protected static void alternarItemCarrinho(){
		if (monstroVisualizado == null) return;
		
        if (Input.getCursorY() >= 0 && Input.getCursorY() < estoque.size()){
			ItemLoja item = getItemPorMonstroId(monstroVisualizado.getIdMonstro());
			if (item == null) return;
            if (item.isItemCarrinho()){
                item.setItemCarrinho(false);
                carrinho.remove(item);
            }else{
                item.setItemCarrinho(true);
                carrinho.add(item);
            }
        }
    }
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	public static void adicionarItemEstoque(int idMonstro, int preco){
        if (estoque == null) inicializarLoja();
        
        for (ItemLoja item : estoque){
			if (item.idMonstro == idMonstro) return;
        }
        
        int novoIdEstante = estoque.size()+1;
        estoque.add(new ItemLoja(idMonstro, preco, novoIdEstante));
    }
	
	public static ItemLoja getItemPorMonstroId(int idMonstro){
		if (estoque == null || idMonstro <= 0){
			return null;
		}
		
		for (ItemLoja item : estoque){
			if (item != null && item.idMonstro == idMonstro){
				return item;
			}
		}
		return null;
	}
	
	private static void aplicarFiltro(){
		if (estoqueFiltrado == null) estoqueFiltrado = new ArrayList<>();
		estoqueFiltrado.clear();
		
		for (ItemLoja item : estoque){
			Monsters infoMonstro = MonstersManager.getMonstro(item.idMonstro);
			if (infoMonstro == null) continue;
			
			
			if (filtroRaridadeAtual == null || infoMonstro.getRaridadeMonstro() == filtroRaridadeAtual){
				estoqueFiltrado.add(item);
			}
		}
		
		paginaAtual = 1;
		Input.setCursorY(0); 
	}
		
	private static void limparCarrinho(){
        if (carrinho != null){
            for (ItemLoja item : carrinho){
                item.setItemCarrinho(false);
            }
            carrinho.clear();
        }
        paginaAtual = 1;
    }
	
    protected static void alternarPagina(boolean avançar){
        if (avançar){
            paginaAtual++;
            if (paginaAtual >= totalPaginas){
                paginaAtual = 1;
            }
        }else{
            paginaAtual--;
            if (paginaAtual < 1){
                paginaAtual = totalPaginas;
            }
        }
    }
    
    public static int getTamanhoEstoque(){
        return estoque.size();
    }
	
  //===	
}