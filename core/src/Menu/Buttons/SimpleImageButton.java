package Menu.Buttons;

import wrapper.Globals;
import Dialog.Skins;
import Menu.Animations;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gudesigns.climber.GameLoader;

public class SimpleImageButton extends ImageButton {

	public SimpleImageButton(Drawable imageUp) {
		super(imageUp);
	}

	public enum SimpleImageButtonTypes {
		PAUSE, BACK, SOUND, UPLOAD, HOME, CAR, RESTART, EDIT, LEFT, RIGHT
	};

	public static ImageButton create(SimpleImageButtonTypes type,
			GameLoader gameLoader) {

		float scaleX = 1.3f, scaleY = 1.3f;
		float paddingOffset = 0;

		TextureRegionDrawable trd = null;

		if (type == SimpleImageButtonTypes.PAUSE) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/pause.png")));
			scaleY = 2.3f;
			scaleX = 2f;
			paddingOffset = -8;
		} else if (type == SimpleImageButtonTypes.BACK) {
			trd = new TextureRegionDrawable(
					new TextureRegion(
							gameLoader.Assets
									.getFilteredTexture("menu/icons/back.png")));

		} else if (type == SimpleImageButtonTypes.SOUND) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/sound_white.png")));

		} else if (type == SimpleImageButtonTypes.UPLOAD) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/upload.png")));

		} else if (type == SimpleImageButtonTypes.HOME) {
			trd = new TextureRegionDrawable(
					new TextureRegion(
							gameLoader.Assets
									.getFilteredTexture("menu/icons/home.png")));
			scaleY = 1.2f;
			scaleX = 1.4f;
			paddingOffset = -8;
		} else if (type == SimpleImageButtonTypes.CAR) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets.getFilteredTexture("menu/icons/car.png")));
			scaleY = 1f;
			scaleX = 1.5f;
			paddingOffset = -8;

		} else if (type == SimpleImageButtonTypes.RESTART) {
			trd = new TextureRegionDrawable(
					new TextureRegion(gameLoader.Assets
							.getFilteredTexture("menu/icons/restart_white.png")));
			scaleY = 1.3f;
			scaleX = 1f;
			paddingOffset = -8;
		} else if (type == SimpleImageButtonTypes.EDIT) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/wrench.png")));
			scaleY = 1f;
			scaleX = 1f;
			paddingOffset = -8;
		} else if (type == SimpleImageButtonTypes.LEFT) {
			trd = new TextureRegionDrawable(
					new TextureRegion(
							gameLoader.Assets
									.getFilteredTexture("menu/icons/left.png")));
			paddingOffset = -18;
			scaleY = 2f;
		} else if (type == SimpleImageButtonTypes.RIGHT) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/right.png")));
			scaleY = 2f;
			paddingOffset = -18;
		}

		trd.setMinHeight(Globals.baseSize * scaleY);
		trd.setMinWidth(Globals.baseSize * scaleX);

		final ImageButton button = new ImageButton(trd);

		// if(type == SimpleImageButtonTypes.EDIT){
		// button.setSkin(Skins.loadDefault(gameLoader, 1));//= new
		// ImageButton(Skins.loadDefault(gameLoader, 1),"blackButton");
		// button.setBackground("blackOut");
		// }

		button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Animations.click(button);
				super.clicked(event, x, y);
			}

		});

		button.pad(18.5f + paddingOffset);
		if (type != SimpleImageButtonTypes.CAR
				&& type != SimpleImageButtonTypes.HOME
				&& type != SimpleImageButtonTypes.RESTART
				&& type != SimpleImageButtonTypes.EDIT) {
			button.addAction(Actions.alpha(Globals.BUTTON_OPACITY));
		}

		return button;
	}

}
