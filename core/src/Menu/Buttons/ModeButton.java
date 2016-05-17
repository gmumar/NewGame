package Menu.Buttons;

import wrapper.Globals;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gudesigns.climber.GameLoader;

public class ModeButton {

	public enum ModeButtonTypes {
		INFINITY, ADVENTURE
	};

	public static Button create(Skin skin, GameLoader gameLoader,
			ModeButtonTypes type, boolean isNew) {

		Button button = new Button(skin, "modeButton");//
		Image image = null;
		Label buttonName = null;

		if (type == ModeButtonTypes.INFINITY) {
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/images/infinity.png"));
			buttonName = new Label("Infinity Mode", skin);
		} else if (type == ModeButtonTypes.ADVENTURE) {
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/images/adventure.png"));
			buttonName = new Label("Adventrue Mode", skin);
		}

		Stack stack = new Stack();
		stack.add(image);

		TextureRegion tr = new TextureRegion(
				gameLoader.Assets.getFilteredTexture("menu/tags/new.png"));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);

		trd.setMinWidth(Globals.baseSize * 1.8f);
		trd.setMinHeight(Globals.baseSize * 1.8f);

		ImageButton newTag = new ImageButton(trd);
		newTag.align(Align.top | Align.right);

		if (isNew) {
			stack.add(newTag);
		}
		// infinityMode.add(infinityImage).pad(8).expand();
		button.add(stack).pad(8).expand();

		button.row();

		button.add(buttonName).pad(10);

		return button;

	}
}
