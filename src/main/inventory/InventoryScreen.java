package main.inventory;

import bestiary.Monsters;
import bestiary.Skills;
import manager.MonstersDescriptionManager;
import util.Grapchics;
import util.description.SkillDescription;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class InventoryScreen {

    private InventoryScreen(){
    }
	
    public static void desenhaInventário(Map<Integer, Monsters> monstrosInventario, Map<Inventory.SlotEquipe, Monsters> equipeTabela, int paginaAtual, 
	int totalPaginas, int cursorY){
        if (monstrosInventario == null || monstrosInventario.isEmpty()){
            Grapchics.desenhaCentroTTF("Inventário vazio.", 10, Grapchics.BRANCO_CLARO);
            return;
        }

        String pag = "Inventário - Página";
        Grapchics.desenhaCentroTTF(pag, 0, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTela(" " + paginaAtual + "/" + totalPaginas, pag.length() + 10, 0, Grapchics.BRANCO_CLARO);
		
        Grapchics.desenhaTTF("E: Voltar", 0, 1, Grapchics.PRETO_CLARO);
		
		String equip = "Enter: Equipar";
		
        Grapchics.desenhaTTF(equip, 0, 2, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTela("/", equip.length(), 2, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Desequipar", equip.length()+1, 2, Grapchics.PRETO_CLARO);
		
		Grapchics.desenhaTTF("Shift: Ver detalhes", 0, 3, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTela("____________________", 0, 4, Grapchics.PRETO_CLARO);

        int linhaFimLista = desenhaListaInventario(monstrosInventario, paginaAtual, cursorY);
        
        Grapchics.desenhaTela("____________________", 0, linhaFimLista, Grapchics.PRETO_CLARO);

        int posicaoLinhaEquipe = 33;
        Grapchics.desenhaCentroTTF("Equipe:", 31, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTela("____________________", 0, 32, Grapchics.PRETO_CLARO);
        
        for (Inventory.SlotEquipe slot : Inventory.SlotEquipe.values()){
            Monsters monstroEquipe = equipeTabela.get(slot);
            if (monstroEquipe != null){
                String nomeMonstroExibido = monstroEquipe.getNomeMonstro() + " Nv" + monstroEquipe.getNivelAtual();
                Grapchics.desenhaTTF(nomeMonstroExibido, 0, posicaoLinhaEquipe++, Grapchics.BRANCO_CLARO);
            }else{
                Grapchics.desenhaTTF("[Vazio]", 0, posicaoLinhaEquipe++, Grapchics.PRETO_CLARO);
            }
        }
        Grapchics.desenhaTela("____________________", 0, posicaoLinhaEquipe, Grapchics.PRETO_CLARO);
    }
	
    private static int desenhaListaInventario(Map<Integer, Monsters> monstrosInventario, int paginaAtual, int cursorY){
        int inicioLista = (paginaAtual - 1) * 24;
        int fimLista = Math.min(inicioLista + 24, monstrosInventario.size());
        int linhaAtual = 5;

        for (int i = inicioLista + 1; i <= fimLista; i++){
            Monsters monstro = monstrosInventario.get(i);
            if (monstro == null) continue;

            boolean selecionado = (i == cursorY);
            int indicadorEquipado = monstro.isMonstroEquipado() ? 69 : 0;
            int indicadorFavorito = monstro.isMonstroFavorito() ? 3 : 0;
            String nomeExibido = monstro.getNomeMonstro() + " Nv" + monstro.getNivelAtual();

            if (selecionado){
                Grapchics.desenhaHibrido(nomeExibido, indicadorEquipado, indicadorFavorito, 1, linhaAtual++, Grapchics.AMARELO_CLARO);
            }else{
                Color cor = (monstro.isMonstroEquipado() || monstro.isMonstroFavorito()) ? Grapchics.BRANCO_CLARO : Grapchics.PRETO_CLARO;
                Grapchics.desenhaHibrido(nomeExibido, indicadorEquipado, indicadorFavorito, 0, linhaAtual++, cor);
            }
        }
        return linhaAtual;
    }
	
    public static void desenhaMonstroDetalhes(Monsters monstroCarregado){
        if (monstroCarregado == null) return;

        int indicadorFavorito = monstroCarregado.isMonstroFavorito() ? 3 : 0;

        Grapchics.desenhaCentroTTF("Detalhes - Inventário", 0, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("E: Voltar", 0, 1, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTTF("Q: Biografia", 0, 2, Grapchics.PRETO_CLARO);
		
		String mark = "Enter: Marcar";
		
        Grapchics.desenhaTTF(mark, 0, 3, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTela("/", mark.length(), 3, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTTF("Desmarcar favorito", mark.length()+1, 3, Grapchics.PRETO_CLARO);
		
		Grapchics.desenhaTTF("Shift: Ver habilidades", 0, 4, Grapchics.PRETO_CLARO);

        Grapchics.desenhaTela("____________________", 0, 5, Grapchics.PRETO_CLARO);

        Grapchics.desenhaHibrido("Nome: " + monstroCarregado.getNomeMonstro(), indicadorFavorito, 0, 6, Grapchics.BRANCO_CLARO);

        Grapchics.desenhaTTF("Nível: " + monstroCarregado.getNivelAtual(), 0, 7, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Classe: " + monstroCarregado.getClasseAtualTexto(), 0, 8, Grapchics.BRANCO_CLARO);
        desenhaElementoMonstro(monstroCarregado, 9);
        Grapchics.desenhaTTF("Raridade: " + monstroCarregado.getRaridadeMonstroTexto(), 0, 10, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Força: " + monstroCarregado.getForcaAtual(), 0, 11, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Vida: " + monstroCarregado.getVidaAtual(), 0, 12, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Velocidade: " + monstroCarregado.getSpeedAtual(), 0, 13, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Estamina: " + monstroCarregado.getEstaminaAtual(), 0, 14, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Energia: " + monstroCarregado.getBarraEspecialMaximo(), 0, 15, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("Traços: " + monstroCarregado.getNomesTraços(), 0, 16, Grapchics.BRANCO_CLARO);

        Grapchics.desenhaTela("____________________", 0, 17, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTela("____________________", 0, 19, Grapchics.PRETO_CLARO);
        desenhaExp(monstroCarregado, 20);
        Grapchics.desenhaTela("____________________", 0, 21, Grapchics.PRETO_CLARO);

        Grapchics.desenhaTTF("Habilidades:", 0, 23, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTela("____________________", 0, 24, Grapchics.PRETO_CLARO);
        
        desenhaListaHabilidade(monstroCarregado, 25);
    }

    private static void desenhaExp(Monsters monstroCarregado, int linha){
        if (monstroCarregado.isNivelMaximo()){
            Grapchics.desenhaTTF("NÍVEL MÁXIMO", 0, linha, Grapchics.BRANCO_CLARO);
        }else{
            String nextLevel = "Próximo nível: " + monstroCarregado.getExpAtual();
            int tamanhoTexto = nextLevel.length();
            Grapchics.desenhaTTF(nextLevel, 0, linha, Grapchics.BRANCO_CLARO);
            Grapchics.desenhaTela("" + (char) 47 + monstroCarregado.getExpNecessaria(), tamanhoTexto, linha, Grapchics.BRANCO_CLARO);
            Grapchics.desenhaTTF(monstroCarregado.getExpNecessaria() + " EXP", tamanhoTexto + 1, linha, Grapchics.BRANCO_CLARO);
        }
    }
	
    private static void desenhaElementoMonstro(Monsters monstroCarregado, int linha){
        Grapchics.desenhaTTF("Elementos: ", 0, linha, Grapchics.BRANCO_CLARO);
        Monsters.Elementos[] elementos = monstroCarregado.getElementosAtuaisValores();
        if (elementos == null || elementos.length == 0) return;

        int colunaX = 11;
        for (int i = 0; i < elementos.length; i++){
            Monsters.Elementos elemento = elementos[i];
            if (elemento == null) continue;

            String nomeElemento = elemento.getElementoNome();
            Color corElemento = monstroCarregado.getCorDoElemento(elemento.name());

            Grapchics.desenhaTTF(nomeElemento, colunaX, linha, corElemento);
            colunaX += nomeElemento.length();

            if (i < elementos.length - 1){
                Grapchics.desenhaTela((char) 47, colunaX, linha, Grapchics.BRANCO_CLARO);
                colunaX += 1;
            }
        }
    }
	
    private static void desenhaListaHabilidade(Monsters monstroCarregado, int linha){
        for (int i = 0; i < monstroCarregado.getQuantidadeMaxSlotsHabilidade(); i++){
            Skills skill = monstroCarregado.getHabilidadeAtiva(i);
            if (skill != null){
                Grapchics.desenhaTTF((i + 1) + ": " + skill.getNomeHabilidade(), 0, linha, Grapchics.BRANCO_CLARO);
                Grapchics.desenhaTTF(skill.getNomeHabilidade(), 3, linha++, skill.getCorHabilidade());
            }else{
                Grapchics.desenhaTTF("[VAZIO]", 0, linha++, Grapchics.PRETO_CLARO);
            }
        }
        Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
		linha++;
		
        Skills especial = monstroCarregado.getHabilidadeEspecial();
        if (especial != null && especial.isTipoEspecial(especial.getTipoHabilidade())){
            Grapchics.desenhaTTF("Especial:", 0, linha++, Grapchics.BRANCO_CLARO);
            Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
            Grapchics.desenhaTTF(especial.getNomeHabilidade(), 0, linha++, especial.getCorHabilidade());
            Grapchics.desenhaTela("____________________", 0, linha, Grapchics.PRETO_CLARO);
        }
    }
	
    public static void desenhaBiografiaDetalhes(Monsters monstroCarregado, int offsetBiografia){
        if (monstroCarregado == null) return;

        Grapchics.desenhaCentroTTF("Biografia - Inventário", 0, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("E: Voltar", 0, 1, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTTF("Monstro: " + monstroCarregado.getNomeMonstro(), 0, 2, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTela("____________________", 0, 3, Grapchics.PRETO_CLARO);
        
        int linhaY = 4;
        String descricao = MonstersDescriptionManager.getDescrição(monstroCarregado.getIdMonstro());
        if (descricao != null && !descricao.isEmpty()){
            List<String> linhasFormatadas = quebrarTexto(descricao, 38);
            int maxLinhasVisiveis = 33;
            for (int i = offsetBiografia; i < linhasFormatadas.size() && (linhaY - 5) < maxLinhasVisiveis; i++) {
                Grapchics.desenhaTTF(linhasFormatadas.get(i), 0, linhaY++, Grapchics.BRANCO_CLARO);
            }
        }else{
            Grapchics.desenhaTTF("[PLACEHOLDER].", 0, linhaY++, Grapchics.PRETO_CLARO);
        }
        Grapchics.desenhaTela("____________________", 0, linhaY, Grapchics.PRETO_CLARO);
    }

    private static List<String> quebrarTexto(String texto, int larguraMaxima){
        List<String> resultado = new ArrayList<>();
        if (texto == null || texto.isEmpty()) return resultado;

        String[] linhasOriginais = texto.split("\r?\n");
        for (String linhaOriginal : linhasOriginais) {
            String[] palavras = linhaOriginal.split(" ");
            StringBuilder linhaAtual = new StringBuilder();

            for (String palavra : palavras) {
                if (linhaAtual.length() + palavra.length() + 1 > larguraMaxima){
                    resultado.add(linhaAtual.toString());
                    linhaAtual = new StringBuilder(palavra);
                }else{
                    if (linhaAtual.length() > 0) linhaAtual.append(" ");
                    linhaAtual.append(palavra);
                }
            }
            if (linhaAtual.length() > 0) resultado.add(linhaAtual.toString());
        }
        return resultado;
    }
	
    public static Skills desenhaHabilidadeDetalhes(Monsters monstroCarregado, int cursorY){
        if (monstroCarregado == null) return null;
        
        Skills habilidadeFocada = null;

        Grapchics.desenhaCentroTTF("Habilidades", 0, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTTF("E: Voltar", 0, 1, Grapchics.PRETO_CLARO);
		
		String activate = "Enter: Ativar";
		
        Grapchics.desenhaTTF(activate, 0, 2, Grapchics.PRETO_CLARO);
        Grapchics.desenhaTela("/", activate.length(), 2, Grapchics.PRETO_CLARO);
		Grapchics.desenhaTTF("Desativar habilidade", activate.length()+1, 2, Grapchics.PRETO_CLARO);
		
		Grapchics.desenhaTTF("Monstro: " + monstroCarregado.getNomeMonstro() + " Nv" + monstroCarregado.getNivelAtual(), 0, 3, Grapchics.BRANCO_CLARO);
        Grapchics.desenhaTela("____________________", 0, 4, Grapchics.PRETO_CLARO);

        int linha = 5;
        
        for (int i = 0; i < monstroCarregado.getQuantidadeMaxSlotsHabilidade(); i++){
            Skills skill = monstroCarregado.getHabilidadeAtiva(i);
            if (skill != null){
                if (cursorY == linha){
                    Grapchics.desenhaTTF((i + 1) + ": ", 0, linha, Grapchics.BRANCO_CLARO);
                    Grapchics.desenhaTTF(skill.getNomeHabilidade(), 4, linha++, Grapchics.AMARELO_CLARO);
                    habilidadeFocada = skill;
                }else{
                    Grapchics.desenhaTTF((i + 1) + ": ", 0, linha, Grapchics.BRANCO_CLARO);
                    Grapchics.desenhaTTF(skill.getNomeHabilidade(), 3, linha++, skill.getCorHabilidade());
                }
            }else{
                if (cursorY == linha){
                    Grapchics.desenhaTTF("[VAZIO]", 0, linha++, Grapchics.AMARELO_CLARO);
                }else{
                    Grapchics.desenhaTTF("[VAZIO]", 0, linha++, Grapchics.PRETO_CLARO);
                }
            }
        }
		
        Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
        
        Map<Integer, List<Skills>> arvoreHabilidades = monstroCarregado.getHabilidadesArvore();
        if (arvoreHabilidades != null){
            List<Skills> todasSkills = new ArrayList<>();
            for (List<Skills> listaSkills : arvoreHabilidades.values()){
                if (listaSkills != null) todasSkills.addAll(listaSkills);
            }
            todasSkills.sort(Comparator.comparingInt(Skills::getNivelNecessario));

            for (Skills skill : todasSkills){
                if (skill == null) continue;
                if (!skill.isTipoEspecial(skill.getTipoHabilidade()) && !monstroCarregado.isHabilidadeAtiva(skill)){
                    if (monstroCarregado.getNivelAtual() >= skill.getNivelNecessario()){
                        if (cursorY == linha){
                            Grapchics.desenhaTTF(skill.getNomeHabilidade() + " (Nv" + skill.getNivelNecessario() + ")", 1, linha++, Grapchics.AMARELO_CLARO);
                            habilidadeFocada = skill;
                        }else{
                            Grapchics.desenhaTTF(skill.getNomeHabilidade(), 0, linha, skill.getCorHabilidade());
                            Grapchics.desenhaTTF("(Nv" + skill.getNivelNecessario() + ")", skill.getNomeHabilidade().length() + 1, linha++, Grapchics.BRANCO_CLARO);
                        }
                    }else{
                        if (cursorY == linha){
                            Grapchics.desenhaTTF(skill.getNomeHabilidade() + " (Nv" + skill.getNivelNecessario() + ")", 1, linha++, Grapchics.AMARELO_CLARO);
                            habilidadeFocada = skill;
                        }else{
                            Grapchics.desenhaTTF(skill.getNomeHabilidade() + " (Nv" + skill.getNivelNecessario() + ")", 0, linha++, Grapchics.PRETO_CLARO);
                        }
                    }
                }
            }
        }
        
        Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
        linha++;
        
        Skills especial = monstroCarregado.getHabilidadeEspecial();
        if (especial != null){
            Grapchics.desenhaTTF("Especial:", 0, linha++, Grapchics.BRANCO_CLARO);
            Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
            if (cursorY == linha){
                Grapchics.desenhaTTF(especial.getNomeHabilidade(), 1, linha++, Grapchics.AMARELO_CLARO);
                habilidadeFocada = especial;
            }else{
                Grapchics.desenhaTTF(especial.getNomeHabilidade(), 0, linha++, especial.getCorHabilidade());
            }
            Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
        }
        linha++;
        
        if (habilidadeFocada != null){
            Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
            linha = SkillDescription.infoHabilidade(habilidadeFocada, linha, false);
            Grapchics.desenhaTela("____________________", 0, linha++, Grapchics.PRETO_CLARO);
        }
        
        return habilidadeFocada;
    }
	
	//===
}