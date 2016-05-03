package Dialog;

import wrapper.Globals;
import Menu.PopQueObject;
import Sounds.SoundManager;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class SoundDialog {
	
	public static Table CreateDialog(GameLoader gameLoader, final PopQueObject popQueObject) {
		
		Skin skin = Skins.loadDefault(gameLoader,0);

		final Table base = new Table(skin);

		base.pad(10);

		base.setFillParent(true);
		
		TextButton musicMute = new TextButton("music", skin, "noButton");
		musicMute.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.toggleMusic(Globals.bgMusic);
				base.remove();
			}

		});

		TextButton sfxMute = new TextButton("sfx", skin, "noButton");
		sfxMute.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.toggleSFX();
				base.remove();
			}

		});

		Table textWrapper = new Table(skin);
		textWrapper.setBackground("dialogDim");
		
		textWrapper.add(musicMute).pad(5);
		textWrapper.add(sfxMute).pad(5);

		base.add(textWrapper).expandY().center();
		
		
		return base;
	}

}
