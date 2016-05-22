package Menu.Buttons;

import wrapper.Globals;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gudesigns.climber.GameLoader;

public class ModeButton {

	public enum ModeButtonTypes {
		INFINITY, ADVENTURE, CAR_BUILDER, CAR_MY_PICKS, CAR_COMMUNITY_CARS
	};

	public static Button create(Skin skin, GameLoader gameLoader,
			ModeButtonTypes type, boolean isNew, boolean isLocked) {

		Button button = new Button(skin, "modeButton");//
		Image image = null;
		Label buttonName = null;

		Table stackInlay = new Table();

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
		} else if (type == ModeButtonTypes.CAR_BUILDER) {
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/images/car_builder.png"));
			buttonName = new Label("Car Builder", skin);
		} else if (type == ModeButtonTypes.CAR_COMMUNITY_CARS) {
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/images/car_community.png"));
			buttonName = new Label("Community Cars", skin);
		} else if (type == ModeButtonTypes.CAR_MY_PICKS) {
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/images/car_my_picks.png"));
			buttonName = new Label("Author's Pick", skin);
		}

		Stack stack = new Stack();
		// stack.add(image);
		stackInlay.add(image).pad(8);

		stackInlay.row();
		stackInlay.add(buttonName).pad(10);
		stack.add(stackInlay);

		if (isLocked) {

			Pixmap overlay = new Pixmap((100), (100), Format.RGBA8888);
			overlay.setColor(Globals.LOCKED_COLOR);
			overlay.fill();

			stack.add(new Image(new Texture(overlay)));
			// stackInlay.add(new Image(new Texture(overlay)));
		}

		TextureRegion tr = new TextureRegion(
				gameLoader.Assets.getFilteredTexture("menu/tags/new.png"));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);

		trd.setMinWidth(Globals.baseSize * 1.4f);
		trd.setMinHeight(Globals.baseSize * 1.4f);

		ImageButton newTag = new ImageButton(trd);
		newTag.align(Align.top | Align.right);

		if (isNew) {
			stack.add(newTag);
		}

		TextureRegion lockTextureRegion = new TextureRegion(
				gameLoader.Assets.getFilteredTexture("menu/tags/lock.png"));
		TextureRegionDrawable lockTextureRegionDrawable = new TextureRegionDrawable(
				lockTextureRegion);

		lockTextureRegionDrawable.setMinWidth(Globals.baseSize);
		lockTextureRegionDrawable.setMinHeight(Globals.baseSize * 1.2f);

		ImageButton lock = new ImageButton(lockTextureRegionDrawable);
		lock.align(Align.center | Align.top);
		lock.pad(15);

		if (isLocked) {
			stack.add(lock);
		}

		// infinityMode.add(infinityImage).pad(8).expand();
		button.add(stack).expand();// .pad(8);

		return button;

	}
}
