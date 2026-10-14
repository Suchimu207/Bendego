package manager;

import static main.Main.getGameVersion;

import bestiary.Monsters;
import bestiary.Skills;

import main.Player;
import main.inventory.Inventory;

import save.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SaveManager {
	private static final Version SAVE_VERSION = getGameVersion();
	private static final Path CAMINHO_SAVE = Paths.get("data", "saves", "save1.json");
	
	private SaveManager(){
	}

	// ==================== SALVAR ====================

	public static boolean salvar(){
		try{
			JSONObject raiz = new JSONObject();
			raiz.put("version", SAVE_VERSION.toString());
			raiz.put("player", montarPlayerJson());
			raiz.put("inventario", montarInventarioJson());

			Path pastaSaves = CAMINHO_SAVE.getParent();
			if (pastaSaves != null && !Files.exists(pastaSaves)){
				Files.createDirectories(pastaSaves);
			}

			Files.writeString(CAMINHO_SAVE, raiz.toString(2));

			System.out.println(">>Jogo salvo com sucesso.");
			return true;
		}catch (IOException e){
			System.out.println(">>Erro ao salvar o jogo: "+e.getMessage());
			return false;
		}
	}

	private static JSONObject montarPlayerJson(){
		JSONObject jsonPlayer = new JSONObject();
		jsonPlayer.put("ouro", Player.getOuro());
		jsonPlayer.put("torneiosVencidos", Player.getTorneiosVencidos());
		jsonPlayer.put("jogadorX", Player.getJogadorX());
		jsonPlayer.put("jogadorY", Player.getJogadorY());
		return jsonPlayer;
	}

	private static JSONObject montarInventarioJson(){
		JSONObject jsonInventario = new JSONObject();

		Map<Integer, Monsters> todosMonstros = Inventory.getTodosMonstros();
		if (todosMonstros == null) return jsonInventario;
		
		JSONArray monstrosArray = new JSONArray();
		for (Monsters monstro : todosMonstros.values()){
			if (monstro == null) continue;
			monstrosArray.put(montarMonstroJson(monstro));
		}
		jsonInventario.put("monstros", monstrosArray);

		JSONArray equipeArray = new JSONArray();
		for (Monsters monstro : Inventory.getEquipeLista()){
			equipeArray.put(monstro != null ? monstro.getInstanceId() : JSONObject.NULL);
		}
		jsonInventario.put("equipe", equipeArray);

		return jsonInventario;
	}
	
	private static JSONObject montarMonstroJson(Monsters monstro){
		JSONObject jsonMonstro = new JSONObject();
		jsonMonstro.put("instanceId", monstro.getInstanceId());
		jsonMonstro.put("idEspecie", monstro.getIdMonstro());
		jsonMonstro.put("nivelAtual", monstro.getNivelAtual());
		jsonMonstro.put("expAtual", monstro.getExpAtual());
		jsonMonstro.put("favorito", monstro.isMonstroFavorito());

		JSONArray habilidadesAtivas = new JSONArray();
		for (int idHabilidade : monstro.getHabilidadesAtivasIds()){
			habilidadesAtivas.put(idHabilidade);
		}
		jsonMonstro.put("habilidadesAtivas", habilidadesAtivas);

		return jsonMonstro;
	}
	
	// ==================== CARREGAR ====================

	public static boolean existeSave(){
		return Files.exists(CAMINHO_SAVE);
	}

	public static boolean carregar(){
		if (!existeSave()){
			System.out.println(">>Nenhum save encontrado.");
			return false;
		}
		
		try{
			String conteudoJson = Files.readString(CAMINHO_SAVE);
			JSONObject raiz = new JSONObject(conteudoJson);

			carregarPlayer(raiz.optJSONObject("player"));
			carregarInventario(raiz.optJSONObject("inventario"));

			System.out.println(">>Jogo carregado com sucesso.");
			return true;
		}catch (IOException e){
			System.out.println(">>Erro ao carregar o jogo: "+e.getMessage());
			return false;
		}catch (JSONException e){
			System.out.println(">>Save corrompido ou em formato inválido: "+e.getMessage());
			return false;
		}
	}
	
	private static void carregarPlayer(JSONObject jsonPlayer){
		Player.setarJogador();
		if (jsonPlayer == null) return;

		Player.setOuro(jsonPlayer.optInt("ouro", Player.getOuro()));
		Player.setTorneiosVencidos(jsonPlayer.optInt("torneiosVencidos", 0));
		Player.setJogadorX(jsonPlayer.optInt("jogadorX", Player.getJogadorX()));
		Player.setJogadorY(jsonPlayer.optInt("jogadorY", Player.getJogadorY()));
	}
	
	private static void carregarInventario(JSONObject jsonInventario){
		Inventory.inicializarInventario(false);
		if (jsonInventario == null) return;

		Map<String, Monsters> monstrosPorInstanceId = new HashMap<>();

		JSONArray monstrosArray = jsonInventario.optJSONArray("monstros");
		if (monstrosArray != null){
			for (int i = 0; i < monstrosArray.length(); i++){
				Monsters monstro = carregarMonstro(monstrosArray.getJSONObject(i));
				if (monstro != null){
					monstrosPorInstanceId.put(monstro.getInstanceId(), monstro);
				}
			}
		}

		JSONArray equipeArray = jsonInventario.optJSONArray("equipe");
		if (equipeArray != null){
			for (int i = 0; i < equipeArray.length(); i++){
				if (equipeArray.isNull(i)) continue;

				String instanceId = equipeArray.getString(i);
				Monsters monstro = monstrosPorInstanceId.get(instanceId);
				if (monstro != null) Inventory.equiparProximoSlotLivre(monstro);
			}
		}
	}
	
	private static Monsters carregarMonstro(JSONObject jsonMonstro){
		int idEspecie = jsonMonstro.getInt("idEspecie");

		Monsters monstro = Inventory.adicionarMonstroSemEquipar(idEspecie);
		if (monstro == null){
			System.out.println(">>Monstro (ID:"+idEspecie+") não encontrado ao carregar o save; ignorado.");
			return null;
		}
		
		int nivelSalvo = jsonMonstro.optInt("nivelAtual", monstro.getNivelAtual());
		int deltaNivel = nivelSalvo - monstro.getNivelAtual();
		if (deltaNivel > 0) monstro.subirNivel(deltaNivel);

		monstro.setExpAtual(jsonMonstro.optInt("expAtual", 0));
		monstro.setMonstroFavorito(jsonMonstro.optBoolean("favorito", false));

		JSONArray habilidadesAtivas = jsonMonstro.optJSONArray("habilidadesAtivas");
		if (habilidadesAtivas != null){
			monstro.limparHabilidadesAtivas();
			for (int i = 0; i < habilidadesAtivas.length(); i++){
				int idHabilidade = habilidadesAtivas.getInt(i);
				Skills skill = monstro.getHabilidadeArvoreId(idHabilidade);
				if (skill != null) monstro.adicionarHabilidadeAtiva(skill);
			}
		}

		return monstro;
	}

	//===
}