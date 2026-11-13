package util;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class Audio {
    private static Clip clipMusicaFundo;
	
	private static float volumeMusicaAtual = 1.0f;
	
	private static Thread threadFadeAtual = null;
	private static Clip clipFadeAtual = null;
	
	private static String caminhoSE = "data/audio/se/";
	private static String caminhoME = "data/audio/me/";
	private static String caminhoBGM = "data/audio/bgm/";
	
    private Audio(){
    }
	
    public static void tocarSom(String caminho, float volume){
        try {
			caminho = caminhoSE+caminho+".wav";
			
            File arquivoSom = new File(caminho);
            if (arquivoSom.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(arquivoSom);
                Clip clipSom = AudioSystem.getClip();
                clipSom.open(audioInput);
                
                setarVolume(clipSom, volume);
                
                clipSom.start(); 
            }else{
                System.out.println("Som não encontrado: "+caminho);
            }
        }catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            System.out.println("Erro ao tocar o som: "+e.getMessage());
        }
    }
	
    public static void tocarMusica(String caminho, boolean loop, float volume){
        pararMusica();
		
		if (!loop) caminho = caminhoME+caminho+".wav";
		if (loop) caminho = caminhoBGM+caminho+".wav";
		
        try {
            File arquivoMusica = new File(caminho);
            if (arquivoMusica.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(arquivoMusica);
                clipMusicaFundo = AudioSystem.getClip();
                clipMusicaFundo.open(audioInput);

                volumeMusicaAtual = volume;
                setarVolume(clipMusicaFundo, volume);

			    if (loop){
					clipMusicaFundo.loop(Clip.LOOP_CONTINUOUSLY);
			    }else{
					clipMusicaFundo.start(); 
			    }
            }else{
                System.out.println("Música não encontrada: "+caminho);
            }
        }catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
            System.out.println("Erro ao tocar a música: "+e.getMessage());
        }
    }
	
    public static void pararMusica(){
        if (threadFadeAtual != null && threadFadeAtual.isAlive()){
			threadFadeAtual.interrupt();
		}
		
		if (clipFadeAtual != null && clipFadeAtual.isOpen()){
			clipFadeAtual.stop();
			clipFadeAtual.close();
			clipFadeAtual = null;
		}
		
        if (clipMusicaFundo != null && clipMusicaFundo.isRunning()){
            clipMusicaFundo.stop();
            clipMusicaFundo.close();
            clipMusicaFundo = null;
        }
    }
	
	public static void pararMusicaFadeOut(int duracaoMs){
		if (clipMusicaFundo == null || !clipMusicaFundo.isRunning()){
			pararMusica();
			return;
		}

		if (threadFadeAtual != null && threadFadeAtual.isAlive()){
			threadFadeAtual.interrupt();
		}

		final Clip clipParaParar = clipMusicaFundo;
		final float volumeInicial = volumeMusicaAtual;
		
		clipMusicaFundo = null; 
		clipFadeAtual = clipParaParar;
		
		threadFadeAtual = new Thread(() -> {
			int passos = Math.max(1, duracaoMs / 50);
			
			try{
				for (int i = 1; i <= passos; i++) {
					if (!clipParaParar.isOpen()) break;
					
					// Calcula a redução progressiva do volume linear.
					float progresso = (float) i / passos;
					float volumeAtualLinear = volumeInicial * (1.0f - progresso);
					
					setarVolume(clipParaParar, volumeAtualLinear);
					
					Thread.sleep(50);
				}
			}catch (InterruptedException e){
				Thread.currentThread().interrupt();
			}finally{
				if (clipParaParar.isOpen()){
					clipParaParar.stop();
					clipParaParar.close();
				}
				
				if (clipFadeAtual == clipParaParar){
					clipFadeAtual = null;
				}
			}
		});
		
		threadFadeAtual.setDaemon(true); 
		threadFadeAtual.start();
	}
	
	// Método auxiliar que converte a escala linear (0.0f a 1.0f) para a escala logarítmica de Decibéis.
    private static void setarVolume(Clip clip, float volume){
        // Verifica se o controle de volume é suportado pelo sistema operacional.
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)){
            FloatControl controleVolume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            
            // Trava o limite do volume entre 0.0f (0%) e 1.0f (100%) para evitar que quebre o cálculo.
            volume = Math.max(0.0f, Math.min(volume, 1.0f));
            
            if (volume == 0.0f){
                // Se for 0, define como o volume mínimo suportado (mudo absoluto).
                controleVolume.setValue(controleVolume.getMinimum());
            }else{
                // Converte a porcentagem para Decibéis e aplica ao clip.
                float decibeis = 20f * (float) Math.log10(volume);
                controleVolume.setValue(decibeis);
            }
        }
    }
	
	//===
}