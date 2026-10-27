package util;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import asciiPanel.AsciiTTFFont;

import java.awt.Color;
import java.awt.Font;

public final class Grapchics {
	private final static int TILE_SIZE_X = 40;
	private final static int TILE_SIZE_Y = 40;
	
	public final static int CAMADA_PRINCIPAL = 0;
	public final static int CAMADA_BARRAS = 1;
	public final static int CAMADA_NUMEROS_BARRA = 2;
	
	private final static char BLOCO_CHEIO = (char) 219;
	private final static char BLOCO_VAZIO = (char) 177;
	
	private final static AsciiPanel tela = new AsciiPanel(TILE_SIZE_X, TILE_SIZE_Y, AsciiFont.CP437_16x16);
	
	// ==================== CORES ====================
	
	public final static Color PRETO = new Color(10, 10, 10);
	public final static Color VERMELHO = new Color(69, 5, 5);
	public final static Color VERDE = new Color(6, 76, 6);
	public final static Color AMARELO = new Color(173, 173, 11);
	public final static Color AZUL = new Color(5, 5, 76);
	public final static Color MAGENTA = new Color(76, 5, 76);
	public final static Color CIANO = new Color(5, 76, 76);
	public final static Color BRANCO = new Color(235, 235, 235);
	
	public final static Color PRETO_CLARO = new Color(153, 153, 153);
	public final static Color VERMELHO_CLARO = new Color(236, 9, 9);
	public final static Color VERDE_CLARO = new Color(138, 249, 138);
	public final static Color AMARELO_CLARO = new Color(248, 248, 98);
	public final static Color AZUL_CLARO = new Color(73, 73, 253);
	public final static Color MAGENTA_CLARO = new Color(250, 53, 253);
	public final static Color CIANO_CLARO = new Color(154, 254, 254);
	public final static Color BRANCO_CLARO = new Color(247, 250, 250);
	
	public final static Color FUNDO = new Color(26, 30, 30);
	public final static Color TRANSPARENTE = AsciiPanel.transparent;
	
	public final static Color ELEMENTO_NATUREZA = new Color(100, 247, 100);
	public final static Color ELEMENTO_FOGO = new Color(235, 71, 71);
	public final static Color ELEMENTO_TERRA = new Color(197, 170, 150);
	public final static Color ELEMENTO_TROVAO = new Color(250, 250, 137);
	public final static Color ELEMENTO_AGUA = new Color(58, 64, 248);
	public final static Color ELEMENTO_LUZ = new Color(243, 247, 247);
	public final static Color ELEMENTO_MAGIA = new Color(247, 136, 251);
	public final static Color ELEMENTO_TREVAS = new Color(92, 92, 92);
	public final static Color ELEMENTO_METAL = new Color(173, 173, 173);
	public final static Color ELEMENTO_VENTO = new Color(177, 251, 177);
	public final static Color ELEMENTO_FISICO = new Color(249, 192, 78);
	
	// ==================== FONTES ====================
	
	private final static Font fonteFallback = new Font("Consolas", Font.BOLD, 14);
	
	private static Font fontePadrão = fonteFallback;
	private static Font fonteItálico = fonteFallback;
	
	// ==================== INICIALIZAÇÃO ====================
	
	static{
		tela.setDefaultBackgroundColor(PRETO);
	}

	private Grapchics(){
	}
	
	// ==================== DESENHO CP437 (BITMAP) ====================
	
	public static void desenhaTela(char desenho, int posX, int posY){
		tela.write(desenho, posX, posY);
	}
	
	public static void desenhaTela(char desenho, int posX, int posY, Color corAtual){
		tela.write(desenho, posX, posY, corAtual);
	}
	
	public static void desenhaTela(char desenho, int posX, int posY, Color corFonte, Color corFundo){
		tela.write(desenho, posX, posY, corFonte, corFundo);
	}
	
	public static void desenhaTela(String desenho, int posX, int posY){
		tela.write(desenho, posX, posY);
	}

	public static void desenhaTela(String desenho, int posX, int posY, Color corAtual){
		tela.write(desenho, posX, posY, corAtual);
	}
	
	public static void desenhaTela(String desenho, int posX, int posY, Color corFonte, Color corFundo){
		tela.write(desenho, posX, posY, corFonte, corFundo);
	}

	public static void desenhaCentro(String desenho, int linha, Color corAtual){
		tela.writeCenter(desenho, linha, corAtual);
	}
	
	public static void desenhaCentro(String desenho, int linha, Color corFonte, Color corFundo){
		tela.writeCenter(desenho, linha, corFonte, corFundo);
	}
	
	// ==================== DESENHO TTF ====================
	
	public static void desenhaTTF(char desenho, int posX, int posY){
		tela.writeTTF(desenho, posX, posY, fontePadrão);
	}
	
	public static void desenhaTTF(char desenho, int posX, int posY, Color corFonte){
		tela.writeTTF(desenho, posX, posY, corFonte, fontePadrão);
	}
	
	public static void desenhaTTF(char desenho, int posX, int posY, Color corFonte, Font fonte){
		tela.writeTTF(desenho, posX, posY, corFonte, fonte);
	}
	
	public static void desenhaTTF(char desenho, int posX, int posY, Color corFonte, Color corFundo, Font fonte){
		tela.writeTTF(desenho, posX, posY, corFonte, corFundo, fonte);
	}
	
	public static void desenhaTTF(String desenho, int posX, int posY){
		tela.writeTTF(desenho, posX, posY, fontePadrão);
	}
	
	public static void desenhaTTF(String desenho, int posX, int posY, Color corFonte){
		tela.writeTTF(desenho, posX, posY, corFonte, fontePadrão);
	}
	
	public static void desenhaTTF(String desenho, int posX, int posY, Color corFonte, Color corFundo){
		tela.writeTTF(desenho, posX, posY, corFonte, corFundo, fontePadrão);
	}
	
	public static void desenhaTTF(String desenho, int posX, int posY, Color corFonte, Font fonte){
		tela.writeTTF(desenho, posX, posY, corFonte, fonte);
	}
	
	public static void desenhaTTF(String desenho, int posX, int posY, Color corFonte, Color corFundo, Font fonte){
		tela.writeTTF(desenho, posX, posY, corFonte, corFundo, fonte);
	}
	
	public static void desenhaCentroTTF(String desenho, int linha){
		tela.writeCenterTTF(desenho, linha, fontePadrão);
	}
	
	public static void desenhaCentroTTF(String desenho, int linha, Color corFonte){
		tela.writeCenterTTF(desenho, linha, corFonte, fontePadrão);
	}
	
	public static void desenhaCentroTTF(String desenho, int linha, Color corFonte, Font fonte){
		tela.writeCenterTTF(desenho, linha, corFonte, fonte);
	}
	
	public static void desenhaCentroTTF(String desenho, int linha, Color corFonte, Color corFundo){
		tela.writeCenterTTF(desenho, linha, corFonte, corFundo, fontePadrão);
	}
	
	public static void desenhaCentroTTF(String desenho, int linha, Color corFonte, Color corFundo, Font fonte){
		tela.writeCenterTTF(desenho, linha, corFonte, corFundo, fonte);
	}
	
	// ==================== DESENHO HÍBRIDO ====================
	
	public static void desenhaHibrido(String texto, int caracteresCP437, int posX, int posY, Color corFonte){
		tela.writeTTF(texto, posX, posY, corFonte, PRETO, fontePadrão);
		if (caracteresCP437 > 0){
			int tamanhoTexto = texto.length();
			tela.write("["+(char)caracteresCP437+"]", tamanhoTexto+1, posY, corFonte);
		}
	}
	
	public static void desenhaHibrido(String texto, int c1, int c2, int posX, int posY, Color corFonte){
		tela.writeTTF(texto, posX, posY, corFonte, PRETO, fontePadrão);
		int tamanhoTexto = texto.length();
		if (c1 > 0 && c2 > 0){
			tela.write("["+(char)c1+"]", tamanhoTexto+1, posY, corFonte);
			tela.write("["+(char)c2+"]", tamanhoTexto+4, posY, corFonte);
		}else if (c1 > 0){
			tela.write("["+(char)c1+"]", tamanhoTexto+1, posY, corFonte);
		}else if (c2 > 0){
			tela.write("["+(char)c2+"]", tamanhoTexto+1, posY, corFonte);
		}
		
	}
	
	// ==================== DESENHO DE BARRAS ====================
	
	public static void desenhaBarra(int x, int y, int largura, double percentual, Color corCheio, Color corVazio){
		int camadaAnterior = tela.getActiveLayer();
		tela.setActiveLayer(CAMADA_BARRAS);

		double percentualClamp = Math.max(0.0, Math.min(1.0, percentual));
		int preenchido = (int) Math.round(largura * percentualClamp);

		for (int i = 0; i < largura; i++){
			if (i < preenchido){
				tela.write(BLOCO_CHEIO, x + i, y, corCheio);
			}else{
				tela.write(BLOCO_VAZIO, x + i, y, corVazio);
			}
		}
		
		tela.setActiveLayer(camadaAnterior);
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	public static void limpaTela(){
		int camadaOriginal = tela.getActiveLayer();
		int numLayers = tela.getNumLayers();
		for (int i = 0; i < numLayers; i++){
			tela.setActiveLayer(i);
			tela.clear();
		}
		tela.setActiveLayer(camadaOriginal); 
		// tela.clear();
	}
	
	public static void atualizarTela(){
		tela.repaint();
	}
	
	public static void setarCamada(int camada){
		tela.setActiveLayer(camada);
	}
	
	public static Font getFonteFallback(){
		return fonteFallback;
	}
	
	public static int getTileSizeX(){
		return TILE_SIZE_X;
	}
	
	public static int getTileSizeY(){
		return TILE_SIZE_Y;
	}

	public static AsciiPanel getTela(){
		return tela;
    }
	
	public static Font getFontePadrao(){
		return fontePadrão;
	}
	
	public static Font getFonteItalico(){
		return fonteItálico;
	}
		
	public static boolean isTTFEnabled(){
		return tela.isTTFEnabled();
	}
	
	public static void setTTFEnabled(boolean enabled){
		tela.setTTFEnabled(enabled);
	}
	
	public static void setFontePadrao(Font fonte){
		fontePadrão = fonte;
	}
		
	public static void setFonteItalico(Font fonte){
		fonteItálico = fonte;
	}
		
	//===
}