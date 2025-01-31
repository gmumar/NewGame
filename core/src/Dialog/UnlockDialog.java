package Dialog;

import wrapper.Globals;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import UserPackage.User;

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

		header.add(lockImage).width(Globals.baseSize * 0.6f)
				.height(Globals.baseSize * 0.8f).pad(5);

		Label unlockText = new Label(popQueObject.getUnlockDialogHeader(),
				skin, "dialogTitle");
		header.add(unlockText);

		base.add(header).center().padTop(15).padLeft(15).padRight(15);
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

		TextButton noButton = new TextButton("\t\tcancel\t\t", Skins.loadDefault(
				gameLoader, 1), "noButton");
		noButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				/*
				 * if (popQueObject.getType() == PopQueObjectType.UNLOCK_MODE) {
				 * popQueObject.getGameModeScreenInstance().failedBuy(); } else
				 * if (popQueObject.getType() == PopQueObjectType.UNLOCK_TRACK)
				 * { popQueObject.getSelectorScreenInstance().failedBuy(); }
				 * else if (popQueObject.getType() ==
				 * PopQueObjectType.UNLOCK_CAR_MODE){
				 * //popQueObject.ge.failedBuy(); }
				 */
				base.hide();
				super.clicked(event, x, y);
			}

		});
		buttons.add(noButton).height(Globals.baseSize * 2).fillX().expandX();

		String yesButtonText = "\t\tupgrade\t\t";
		
		if(popQueObject.getType() == PopQueObjectType.UNLOCK_ARCTIC_WORLD ||
				popQueObject.getType() == PopQueObjectType.UNLOCK_CAR_MODE ||
				popQueObject.getType() == PopQueObjectType.UNLOCK_MODE ||
				popQueObject.getType() == PopQueObjectType.UNLOCK_TRACK ){
			yesButtonText = "\t\tunlock\t\t";
		}
		
		TextButton yesButton = new TextButton(yesButtonText, Skins.loadDefault(
				gameLoader, 1), "yesButton");
		yesButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				Integer moneyRequired = User.getInstance().buyItem(
						popQueObject.getItemToBeBoughtName(), itemCost);
						
				if (moneyRequired == -1) {
					popQueObject.getTwoButtonFlowContext()
							.successfulTwoButtonFlow(
									popQueObject.getItemToBeBoughtName());
				} else {
					popQueObject.getTwoButtonFlowContext().failedTwoButtonFlow(moneyRequired);
				}
				/*
				 * if (popQueObject.getType() == PopQueObjectType.UNLOCK_MODE) {
				 * popQueObject.getGameModeScreenInstance().successfulBuy(); }
				 * else if (popQueObject.getType() ==
				 * PopQueObjectType.UNLOCK_TRACK) {
				 * popQueObject.getSelectorScreenInstance().successfulBuy(); }
				 */
				base.hide();
				super.clicked(event, x, y);
			}

		});
		buttons.add(yesButton).height(Globals.baseSize * 2).fillX().expandX();

		base.add(buttons).expandX().fillX().pad(-2);

		return base;
	}

}
