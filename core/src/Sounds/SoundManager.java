package Sounds;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
	
	public static final float FX_VOLUME = 0.2f;
	public static final float MUSIC_VOLUME = 0.3f;
	
	public static long playFXSound(Sound clip){
		return clip.play(FX_VOLUME);
	}
	
	public static long loopFXSound(Sound clip){
		return clip.loop(FX_VOLUME);
	}
	
	public static void loopMusic(Music clip){
		clip.setLooping(true);
		clip.setVolume(MUSIC_VOLUME);
		clip.play();
		//clip.resume();
	}
	
	public static void stop(Sound clip){
		//clip.pause();
		clip.stop();
	}

	
	public static void stopMusic(Music bgMusic){
		//clip.pause();
		bgMusic.stop();
	}
	
	public static void disposeSound(Music clip){
		clip.stop();
		clip.dispose();
	}
	
	public static void disposeSound(Sound clip){
		clip.stop();
		clip.dispose();
	}

}
