package Menu.Buttons;

import wrapper.Globals;
import Dialog.Skins;

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

public class CarBuilderButton {

	public enum CarBuilderButtonType {
		BAR, SPRING, WHEEL, DELETE, ROTATE_LEFT, ROTATE_RIGHT, PLAY, LEVEL_UP, LEVEL_DOWN
	};

	public static final ButtonLockWrapper create(GameLoader gameLoader,
			CarBuilderButtonType type, boolean isNew, boolean isLocked) {

		Skin skin = Skins.loadDefault(gameLoader, 1);

		Button base = null;

		Image image = null;
		Label buttonName = null;

		float sizeXAdjust = 1, sizeYAdjust = 1;

		if (type == CarBuilderButtonType.BAR) {
			base = new Button(skin, "carBuilder_general");
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/builder_bar.png"));
			buttonName = new Label("Bar", skin);
		} else if (type == CarBuilderButtonType.SPRING) {
			base = new Button(skin, "carBuilder_general");
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/builder_spring.png"));
			buttonName = new Label("Spring", skin);
		} else if (type == CarBuilderButtonType.WHEEL) {
			base = new Button(skin, "carBuilder_general");
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/builder_wheel.png"));
			buttonName = new Label("Wheel", skin);
		} else if (type == CarBuilderButtonType.ROTATE_LEFT) {
			base = new Button(skin, "carBuilder_general");
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/builder_rotate_left.png"));
			sizeYAdjust = 0.5f;
			buttonName = new Label("Rotate Left", skin);
		} else if (type == CarBuilderButtonType.ROTATE_RIGHT) {
			base = new Button(skin, "carBuilder_general");
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/builder_rotate_right.png"));
			sizeYAdjust = 0.5f;
			buttonName = new Label("Rotate Right", skin);
		} else if (type == CarBuilderButtonType.DELETE) {
			base = new Button(skin, "carBuilder_delete");
			image = new Image(
					gameLoader.Assets
							.getFilteredTexture("menu/icons/builder_clear.png"));
			buttonName = new Label("Delete/Clear", skin);
		} else if (type == CarBuilderButtonType.PLAY) {
			base = new Button(skin, "carBuilder_play");
			image = new Image(
					gameLoader.Assets.getFilteredTexture("menu/icons/play.png"));
			buttonName = new Label("Play", skin);
		} else if (type == CarBuilderButtonType.LEVEL_DOWN) {
			base = new Button(skin, "carBuilder_general");
			image = new Image(
					gameLoader.Assets.getFilteredTexture("menu/icons/down.png"));
			sizeYAdjust = 0.5f;
			buttonName = new Label("-", skin);
		} else if (type == CarBuilderButtonType.LEVEL_UP) {
			base = new Button(skin, "carBuilder_general");
			image = new Image(
					gameLoader.Assets.getFilteredTexture("menu/icons/up.png"));
			sizeYAdjust = 0.5f;
			buttonName = new Label("Level Up", skin);
		}

		Stack stack = new Stack();
		stack.add(image);

		TextureRegion tr = new TextureRegion(
				gameLoader.Assets.getFilteredTexture("menu/tags/new.png"));
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);

		trd.setMinWidth(Globals.baseSize * 1.5f);
		trd.setMinHeight(Globals.baseSize * 1.5f);

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
		lockTextureRegionDrawable.setMinHeight(Globals.baseSize);

		ImageButton lock = new ImageButton(lockTextureRegionDrawable);
		lock.align(Align.center);

		if (isLocked)
			stack.add(lock);
		// infinityMode.add(infinityImage).pad(8).expand();
		base.add(stack).pad(8).expand()
				.width(Globals.baseSize * 1.5f * sizeXAdjust)
				.height(Globals.baseSize * 1.5f * sizeYAdjust);

		base.row();

		if (type != CarBuilderButtonType.LEVEL_DOWN
				// && type != CarBuilderButtonType.LEVEL_UP
				&& type != CarBuilderButtonType.ROTATE_LEFT
				&& type != CarBuilderButtonType.ROTATE_RIGHT) {
			base.add(buttonName).pad(10);
		}

		return new ButtonLockWrapper(base, isLocked);

	}
}
