package Dialog;

import wrapper.Globals;
import Menu.PopQueObject;
import Sounds.SoundManager;
import UserPackage.User;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class SoundDialog {

	public static Table CreateDialog(GameLoader gameLoader,
			final PopQueObject popQueObject) {

		Skin skin = Skins.loadDefault(gameLoader, 0);

		final int imagePadding = 8;
		final int textPadding = 8;

		final Table base = new Table(skin);
		User user = User.getInstance();

		base.setFillParent(true);
		base.setTouchable(Touchable.enabled);
		base.setBackground("dialogDim");

		base.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				base.remove();
			}

		});

		Table content = new Table(skin);
		content.setBackground("white");

		Table header = new Table();
		Table buttons = new Table();
		// Header
		Image soundImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/sound_black.png"));

		header.add(soundImage).width(Globals.baseSize).height(Globals.baseSize)
				.pad(imagePadding);

		Label soundText = new Label("Sound Control", skin, "dialogTitle");
		header.add(soundText).pad(textPadding);

		content.add(header).center();
		content.row();

		// Buttons

		Button musicMute = new Button(skin, "soundButton");
		musicMute.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.toggleMusic(Globals.bgMusic);
				base.remove();
			}

		});

		Button sfxMute = new Button(skin, "soundButton");
		sfxMute.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.toggleSFX();
				base.remove();
			}

		});

		Image muteImage1 = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/mute.png"));
		Image muteImage2 = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/mute.png"));

		Image soundImage1 = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/sound_black.png"));
		Image soundImage2 = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/sound_black.png"));

		Label musicText = new Label("Music", skin);
		musicMute.setChecked(user.getMusicPlayState());
		if (user.getMusicPlayState()) {
			musicMute.add(soundImage1).width(Globals.baseSize)
					.height(Globals.baseSize).pad(imagePadding);
		} else {
			musicMute.add(muteImage1).width(Globals.baseSize / 2)
					.height(Globals.baseSize).pad(imagePadding);
		}
		musicMute.add(musicText).pad(textPadding);
		;
		buttons.add(musicMute).pad(5);

		Label sfxText = new Label("Sound Effects", skin);
		sfxMute.setChecked(user.getSfxPlayState());
		if (user.getSfxPlayState()) {
			sfxMute.add(soundImage2).width(Globals.baseSize)
					.height(Globals.baseSize).pad(imagePadding);
		} else {
			sfxMute.add(muteImage2).width(Globals.baseSize / 2)
					.height(Globals.baseSize).pad(imagePadding);
		}
		sfxMute.add(sfxText).pad(textPadding);
		;
		buttons.add(sfxMute).pad(5);

		content.add(buttons).expandY().center();

		base.add(content).pad(10);

		return base;
	}

}
