package Sounds;

import UserPackage.User;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

	public static final float FX_VOLUME = 0.2f;
	public static final float MUSIC_VOLUME = 0.3f;

	public static long playFXSound(Sound clip) {
		if (User.getInstance().getSfxPlayState()) {
			clip.stop();
			return clip.play(FX_VOLUME);
		}
		return 0;
	}

	public static long loopFXSound(Sound clip) {
		return clip.loop(FX_VOLUME);
	}

	public static void toggleSFX() {
		User.getInstance().setSfxPlayState(
				!User.getInstance().getSfxPlayState());
	}

	public static void loopMusic(Music clip) {
		clip.setLooping(true);
		clip.setVolume(MUSIC_VOLUME);

		if (User.getInstance().getMusicPlayState()) {
			clip.play();
		}
		// clip.resume();
	}

	public static void stop(Sound clip) {
		// clip.pause();
		clip.stop();
	}

	public static void toggleMusic(Music bgMusic) {
		// clip.pause();

		if (bgMusic.isPlaying()) {
			User.getInstance().setMusicPlayState(false);
			bgMusic.pause();
		} else {
			User.getInstance().setMusicPlayState(true);
			bgMusic.play();
		}

	}

	public static void stopMusic(Music bgMusic) {
		// clip.pause();
		bgMusic.stop();
	}

	public static void disposeSound(Music clip) {
		clip.stop();
		clip.dispose();
	}

	public static void disposeSound(Sound clip) {
		clip.stop();
		clip.dispose();
	}

}
