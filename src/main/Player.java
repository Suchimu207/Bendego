package main;

public final class Player {
	private static final int OURO_INICIAL = 300;
	private static final int TORNEIOS_VENCIDOS_INICIAL = 0;
	private static final int POSIÇÃO_INICIAL_X = 19;
	private static final int POSIÇÃO_INICIAL_Y = 9;
	
	private static Player jogador;
    private int ouro;
	private int torneiosVencidos;
	private int jogadorX, jogadorY;
	
	private Player(int ouro, int torneiosVencidos, int jogadorX, int jogadorY){
		this.ouro = ouro;
		this.torneiosVencidos = torneiosVencidos;
		this.jogadorX = jogadorX;
		this.jogadorY = jogadorY;
	}
	
	public static void setarJogador(){
		jogador = new Player(OURO_INICIAL, TORNEIOS_VENCIDOS_INICIAL,
		POSIÇÃO_INICIAL_X, POSIÇÃO_INICIAL_Y);
	}
	
	public static int getJogadorX(){
		if (jogador == null) return 0;
		return jogador.jogadorX;
	}
	
	public static int getJogadorY(){
		if (jogador == null) return 0;
		return jogador.jogadorY;
	}
		
	public static void setJogadorX(int jogadorX){
		if (jogador == null) return;
		if (jogadorX < 0) jogadorX = 0;
		jogador.jogadorX = jogadorX;
	}
	
	public static void setJogadorY(int jogadorY){
		if (jogador == null) return;
		if (jogadorY < 0) jogadorY = 0;
		jogador.jogadorY = jogadorY;
	}
	
	public static int getOuro(){
		if (jogador == null) return 0;
		return jogador.ouro;
	}
	
	public static void setOuro(int ouroDado){
		if (jogador == null) return;
		if (ouroDado < 0) ouroDado = 1;
		jogador.ouro = ouroDado;
	}
	
	public static void ganharOuro(int ouroDado){
		if (jogador == null) return;
		if (ouroDado <= 0) ouroDado = 1;
		jogador.ouro += ouroDado;
	}
	
	public static void perderOuro(int ouroPerdido){
		if (jogador == null) return;
		if (ouroPerdido <= 0) ouroPerdido = 0;
		jogador.ouro -= ouroPerdido;
		if (jogador.ouro < 0) jogador.ouro = 0;
	}
	
	public static int getTorneiosVencidos(){
		if (jogador == null) return -1;
		return jogador.torneiosVencidos;
	}
	
	public static void setTorneiosVencidos(int torneiosVencidos){
		if (jogador == null) return;
		jogador.torneiosVencidos = torneiosVencidos;
	}
	
	public static void registrarTorneioVencido(){
		jogador.torneiosVencidos += 1;
	}
		
	//===
}