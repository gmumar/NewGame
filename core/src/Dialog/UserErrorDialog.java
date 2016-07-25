package Dialog;

import wrapper.Globals;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import UserPackage.ItemsLookupPrefix;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class UserErrorDialog {

	public static DialogBase CreateDialog(GameLoader gameLoader,
			final PopQueObject popQueObject) {

		Skin skin = Skins.loadDefault(gameLoader, 0);

		final DialogBase base = new DialogBase("", skin, "buyDialog");
		base.clear();
		base.setMovable(false);
		base.setModal(true);

		Table header = new Table();
		Table text = new Table();
		Table buttons = new Table();

		// Header
		Image warningImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/warning.png"));

		header.add(warningImage).width(Globals.baseSize)
				.height(Globals.baseSize).pad(5);

		Label warningHeaderText = new Label(
				popQueObject.getErrorHeaderString(), skin, "dialogTitle");
		header.add(warningHeaderText);

		base.add(header).center().pad(10);
		base.row();

		// text
		Label description = new Label(popQueObject.getErrorString(), skin);
		// description.setWrap(true);
		// description.setWidth(base.getWidth());

		text.add(description);

		base.add(text).center().padLeft(15).padRight(15);
		base.row();

		if (popQueObject.getType() != PopQueObjectType.ERROR_USER_NAME_ENTRY) {
			String noButtonText;

			if (popQueObject.getType() == PopQueObjectType.ERROR_USER_BUILD) {
				noButtonText = "ok";
			} else {
				noButtonText = "cancel";
			}

			// Buttons
			TextButton noButton = new TextButton(noButtonText,
					Skins.loadDefault(gameLoader, 1), "noButton");
			noButton.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {

					/*
					 * if (popQueObject.getType() ==
					 * PopQueObjectType.UNLOCK_MODE) {
					 * popQueObject.getGameModeScreenInstance().failedBuy(); }
					 * else if (popQueObject.getType() ==
					 * PopQueObjectType.UNLOCK_TRACK) {
					 * popQueObject.getSelectorScreenInstance().failedBuy(); }
					 * else if (popQueObject.getType() ==
					 * PopQueObjectType.UNLOCK_CAR_MODE){
					 * //popQueObject.ge.failedBuy(); }
					 */
					base.hide();
					super.clicked(event, x, y);
				}

			});
			buttons.add(noButton).height(Globals.baseSize * 2).fillX()
					.expandX();
		}

		String yesButtonText = null;

		if (popQueObject.getType() == PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED) {
			yesButtonText = "Unlock Parts";
		} else if (popQueObject.getType() == PopQueObjectType.ERROR_NOT_ENOUGH_MONEY) {
			yesButtonText = "Buy Coins";
		} else if (popQueObject.getType() == PopQueObjectType.ERROR_USER_NAME_ENTRY) {
			yesButtonText = "Ok";
		}

		if (popQueObject.getType() == PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED
				|| popQueObject.getType() == PopQueObjectType.ERROR_NOT_ENOUGH_MONEY
				|| popQueObject.getType() == PopQueObjectType.ERROR_USER_NAME_ENTRY) {
			TextButton yesButton = new TextButton(yesButtonText,
					Skins.loadDefault(gameLoader, 1), "yesButton");
			yesButton.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (popQueObject.getType() == PopQueObjectType.ERROR_PARTS_NOT_UNLOCKED) {
						popQueObject
								.getTwoButtonFlowContext()
								.successfulTwoButtonFlow(
										ItemsLookupPrefix.ERROR_PARTS_NOT_UNLOCKED);
					} else if (popQueObject.getType() == PopQueObjectType.ERROR_NOT_ENOUGH_MONEY) {
						popQueObject
								.getTwoButtonFlowContext()
								.successfulTwoButtonFlow(
										ItemsLookupPrefix.ERROR_NOT_ENOUGH_MONEY);
					} 
					/*
					 * if (popQueObject.getType() ==
					 * PopQueObjectType.UNLOCK_MODE) {
					 * popQueObject.getGameModeScreenInstance().successfulBuy();
					 * } else if (popQueObject.getType() ==
					 * PopQueObjectType.UNLOCK_TRACK) {
					 * popQueObject.getSelectorScreenInstance().successfulBuy();
					 * }
					 */
					base.hide();
					super.clicked(event, x, y);
				}

			});
			buttons.add(yesButton).height(Globals.baseSize * 2).fillX()
					.expandX();
		}
		base.add(buttons).expandX().fillX().pad(-2);

		return base;
	}

}
