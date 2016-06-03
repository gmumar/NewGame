package Dialog;

import wrapper.Globals;
import Menu.PopQueObject;
import User.Costs;
import User.User;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class BuyDialog {

	public static DialogBase CreateDialog(GameLoader gameLoader,
			final PopQueObject popQueObject) {

		Skin skin = Skins.loadDefault(gameLoader, 0);

		final DialogBase base = new DialogBase("", skin, "buyDialog");
		base.clear();
		base.setMovable(false);
		base.setModal(true);

		Table header = new Table();
		Table images = new Table();
		Table price = new Table();
		Table buttons = new Table();

		// Header
		Image upgradeImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/upgrade.png"));

		header.add(upgradeImage).width(Globals.baseSize)
				.height(Globals.baseSize).pad(5);

		Label upgradeText = new Label("Upgrade", skin, "dialogTitle");
		header.add(upgradeText);

		base.add(header).center();
		base.row();

		// Images
		// Vector2 originalOrigin = null;
		Table currentItem = new Table(skin);
		currentItem.setBackground("dialogDim");
		Image currentItemImage = new Image(
				gameLoader.Assets.getFilteredTexture("bar/level1.png"));
		// originalOrigin = new
		// Vector2(currentItemImage.getOriginX(),currentItemImage.getOriginY());
		// currentItemImage.setOrigin(currentItemImage.getWidth()/2,
		// currentItemImage.getHeight()/2);
		// currentItemImage.setRotation(20);
		// currentItemImage.setOrigin(originalOrigin.x, originalOrigin.y);
		Label currentItemText = new Label("Level "
				+ Integer.toString(popQueObject.getNextLevel() - 1), skin);
		currentItem.add(currentItemImage).width(Globals.baseSize * 3)
				.height(Globals.baseSize).pad(10);
		currentItem.row();
		currentItem.add(currentItemText);

		Table nextItem = new Table(skin);
		nextItem.setBackground("dialogDim");
		Image nextItemImage = new Image(
				gameLoader.Assets.getFilteredTexture("bar/level2.png"));
		// originalOrigin = new
		// Vector2(nextItemImage.getOriginX(),nextItemImage.getOriginY());
		// nextItemImage.setOrigin(nextItemImage.getWidth()/2,
		// nextItemImage.getHeight()/2);
		// nextItemImage.setRotation(80);
		// nextItemImage.setOrigin(originalOrigin.x, originalOrigin.y);
		Label nextItemText = new Label("Level "
				+ Integer.toString(popQueObject.getNextLevel()), skin);
		nextItem.add(nextItemImage).width(Globals.baseSize * 3)
				.height(Globals.baseSize).pad(10);
		nextItem.row();
		nextItem.add(nextItemText);

		images.add(currentItem);
		Image arrow = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/dull_forward.png"));
		arrow.setOrigin(arrow.getWidth() / 2, arrow.getHeight() / 2);
		// arrow.setRotation(180);
		images.add(arrow).width(Globals.baseSize).height(Globals.baseSize)
				.pad(10);
		images.add(nextItem);
		base.add(images).center().padRight(15).padLeft(15);
		base.row();

		// Price
		final Integer itemCost = Costs.lookup(
				popQueObject.getItemToBeBoughtName(),
				popQueObject.getNextLevel());
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
				//popQueObject.getCallingInstance().failedBuy();
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

				Integer moneyRequired = User.getInstance().buyItem(
						popQueObject.getItemToBeBoughtName(), itemCost);

				if (moneyRequired == -1) {
					popQueObject.getCallingInstance().successfulBuy();
				} else {
					popQueObject.getCallingInstance().failedBuy(moneyRequired);
				}
				base.hide();

				super.clicked(event, x, y);
			}

		});
		buttons.add(yesButton).height(Globals.baseSize * 2).fillX().expandX();

		base.add(buttons).expandX().fillX().pad(-2);

		return base;
	}

}
