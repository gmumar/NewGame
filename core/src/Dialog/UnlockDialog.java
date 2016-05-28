package Dialog;

import wrapper.Globals;
import Menu.PopQueObject;
import User.User;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class UnlockDialog {

	public static DialogBase CreateDialog(GameLoader gameLoader,
			final PopQueObject popQueObject) {

		Skin skin = Skins.loadDefault(gameLoader, 0);

		final DialogBase base = new DialogBase("", skin, "buyDialog");
		base.clear();
		base.setMovable(false);
		base.setModal(true);

		Table header = new Table();
		Table text = new Table();
		Table price = new Table();
		Table buttons = new Table();

		// Header
		Image lockImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/tags/lock.png"));

		header.add(lockImage).width(Globals.baseSize)
				.height(Globals.baseSize * 1.2f).pad(5);

		Label unlockText = new Label(popQueObject.getUnlockDialogHeader(), skin);
		header.add(unlockText);

		base.add(header).center();
		base.row();

		// text
		Label description = new Label(popQueObject.getUnlockDialogDes(), skin);
		// description.setWrap(true);
		// description.setWidth(base.getWidth());

		text.add(description);

		base.add(text).center().padLeft(15).padRight(15);
		base.row();

		// Price
		final Integer itemCost = popQueObject.getUnlockDialogPrice();
		Image coinImage = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/dull_coin.png"));

		price.add(coinImage).width(Globals.baseSize).height(Globals.baseSize)
				.pad(5);

		Label priceText = new Label(itemCost.toString(), skin);
		price.add(priceText);

		base.add(price).center();
		base.row();

		/*
		 * base.text("Buy level:" + popQueObject.getNextLevel() + " of " +
		 * popQueObject.getComponentName() + " for " + itemCost.toString());
		 */

		TextButton noButton = new TextButton("cancel", Skins.loadDefault(
				gameLoader, 1), "noButton");
		noButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				popQueObject.getTwoButtonFlowContext().failed();
				/*if (popQueObject.getType() == PopQueObjectType.UNLOCK_MODE) {
					popQueObject.getGameModeScreenInstance().failedBuy();
				} else if (popQueObject.getType() == PopQueObjectType.UNLOCK_TRACK) {
					popQueObject.getSelectorScreenInstance().failedBuy();
				} else if (popQueObject.getType() == PopQueObjectType.UNLOCK_CAR_MODE){
					//popQueObject.ge.failedBuy();
				}*/
				base.hide();
				super.clicked(event, x, y);
			}

		});
		buttons.add(noButton).height(Globals.baseSize * 2).fillX().expandX();

		TextButton yesButton = new TextButton("upgrade", Skins.loadDefault(
				gameLoader, 1), "yesButton");
		yesButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				User.getInstance().buyItem(popQueObject.getComponentName(),
						itemCost);
				popQueObject.getTwoButtonFlowContext().successful();
				/*if (popQueObject.getType() == PopQueObjectType.UNLOCK_MODE) {
					popQueObject.getGameModeScreenInstance().successfulBuy();
				} else if (popQueObject.getType() == PopQueObjectType.UNLOCK_TRACK) {
					popQueObject.getSelectorScreenInstance().successfulBuy();
				}*/
				base.hide();
				super.clicked(event, x, y);
			}

		});
		buttons.add(yesButton).height(Globals.baseSize * 2).fillX().expandX();

		base.add(buttons).expandX().fillX().pad(-2);

		return base;
	}

}
