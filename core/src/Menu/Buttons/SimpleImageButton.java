package Menu.Buttons;

import wrapper.Globals;
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
		// TODO Auto-generated constructor stub
	}

	public enum SimpleImageButtonTypes {
		PAUSE, BACK, SOUND, UPLOAD
	};

	public static ImageButton create(SimpleImageButtonTypes type,
			GameLoader gameLoader) {
		
		float scaleX = 1, scaleY = 1;

		TextureRegionDrawable trd = null;

		if (type == SimpleImageButtonTypes.PAUSE) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/pause.png")));
			scaleY = 2.3f;
			scaleX = 2f;
		} else if (type == SimpleImageButtonTypes.BACK) {
			trd = new TextureRegionDrawable(
					new TextureRegion(gameLoader.Assets.getFilteredTexture(
							"menu/icons/back.png")));
			
		} else if (type == SimpleImageButtonTypes.SOUND) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/sound_white.png")));
			
		} else if (type == SimpleImageButtonTypes.UPLOAD) {
			trd = new TextureRegionDrawable(new TextureRegion(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/upload.png")));
			
		}

		trd.setMinHeight(Globals.baseSize * scaleY);
		trd.setMinWidth(Globals.baseSize * scaleX);

		final ImageButton button = new ImageButton(trd);
		button.pad(8.5f);
		button.addAction(Actions.alpha(Globals.BUTTON_OPACITY));

		button.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Animations.click(button);
				super.clicked(event, x, y);
			}

		});
		
		button.pad(8.5f);
		button.addAction(Actions.alpha(Globals.BUTTON_OPACITY));
		
		return button;
	}

}
