package Dialog;

import wrapper.Globals;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Purchases.GamePurchaseObserver;
import Purchases.IAPManager;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class StoreBuyDialog {

	public static Table CreateDialog(final GameLoader gameLoader,
			final PopQueObject popQueObject) {

		Skin skin = Skins.loadDefault(gameLoader, 0);
		final int imagePadding = 8;
		final int textPadding = 8;

		final Table base = new Table(skin);
		// base.debugAll();
		base.setColor(1, 1, 1, 0);
		base.setFillParent(true);
		Animations.fadeIn(base);
		base.setTouchable(Touchable.enabled);
		base.addListener(new ClickListener() {

			boolean removable = true;

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (removable) {

				}

				super.clicked(event, x, y);
			}

			@Override
			public boolean isOver(Actor actor, float x, float y) {
				if (actor != null) {

					if (actor.getName() == null) {

						removable = true;
					} else {

						if (actor.getName().compareTo("centeredBox") == 0) {
							removable = false;
						} else {
							removable = true;
						}
					}
				}

				return super.isOver(actor, x, y);
			}

		});
		/*
		 * gameLoader.getPlatformResolver().requestInformation(
		 * IAPManager.PACK_ONE, new GamePurchaseObserver() {
		 * 
		 * @Override public void handleRecievedInformation(
		 * Purchases.GamePurchaseResult gamePurchaseResult) {
		 * 
		 * }
		 * 
		 * });
		 */

		Table contentHolder = new Table(skin);

		Table content = new Table(skin);
		content.setName("centeredBox");
		content.setBackground("white");
		content.pad(25);

		// Header
		Table header = new Table();
		Image cartImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/cart.png"));

		header.add(cartImage).width(Globals.baseSize * 2)
				.height(Globals.baseSize * 2).pad(imagePadding);

		Label buyMoreText = new Label("Get more Coins", skin, "dialogTitle");
		header.add(buyMoreText).pad(textPadding);

		content.add(header).center();
		content.row();

		if (gameLoader.IAPItemInformation.size() != 0) {
			if(Globals.ADMIN_MODE){
			createRow(gameLoader, skin, base, content, IAPManager.PACK_ONE,
					"Buy 10,000 coins for ", new ClickListener() {

						@Override
						public void clicked(InputEvent event, float x, float y) {
							gameLoader.getPlatformResolver().requestPurchase(
									IAPManager.IAP_TEST);
							removeTable(base);
							super.clicked(event, x, y);
						}

					});
			}
			
			// row 1
			createRow(gameLoader, skin, base, content, IAPManager.PACK_ONE,
					"Buy 10,000 coins for ", new ClickListener() {

						@Override
						public void clicked(InputEvent event, float x, float y) {
							gameLoader.getPlatformResolver().requestPurchase(
									IAPManager.PACK_ONE);
							removeTable(base);
							super.clicked(event, x, y);
						}

					});

			// row 2
			createRow(gameLoader, skin, base, content, IAPManager.PACK_TWO,
					"Buy 100,000 coins and No Ads for ", new ClickListener() {

						@Override
						public void clicked(InputEvent event, float x, float y) {
							gameLoader.getPlatformResolver().requestPurchase(
									IAPManager.PACK_TWO);
							removeTable(base);
							super.clicked(event, x, y);
						}

					});

			// row 3
			createRow(gameLoader, skin, base, content, IAPManager.PACK_THREE,
					"Buy 100,000,0 coins and No Ads for ", new ClickListener() {

						@Override
						public void clicked(InputEvent event, float x, float y) {
							gameLoader.getPlatformResolver().requestPurchase(
									IAPManager.PACK_THREE);
							removeTable(base);
							super.clicked(event, x, y);
						}

					});

			// row 4
			createRow(gameLoader, skin, base, content, IAPManager.PACK_FOUR,
					"Buy 100,000,000 coins and No Ads for ", new ClickListener() {

						@Override
						public void clicked(InputEvent event, float x, float y) {
							gameLoader.getPlatformResolver().requestPurchase(
									IAPManager.PACK_FOUR);
							removeTable(base);
							super.clicked(event, x, y);
						}

					});

		} else {
			Label text = new Label("Could not connect to the internets",
					Skins.loadDefault(gameLoader, 1));

			content.add(text).expandY().left().padBottom(4);
		}
		contentHolder.add(content);

		contentHolder.row();
		TextButton cancel = new TextButton("cancel", skin, "noButton");
		contentHolder.add(cancel).expand().fillX().height(Globals.baseSize * 2);

		cancel.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				removeTable(base);
				super.clicked(event, x, y);
			}

		});

		base.add(contentHolder).expandY().center();

		return base;
	}

	private static void createRow(final GameLoader gameLoader, Skin skin,
			final Table base, Table content, String itemName,
			String description, ClickListener buyListener) {
		final int imagePadding = 8;
		final int textPadding = 8;

		Table row1 = new Table(skin);
		Table row1Content = new Table();
		row1.setTouchable(Touchable.enabled);
		String row1Price = gameLoader.IAPItemInformation.get(itemName)
				.getPrice();
		Image coinImage1 = new Image(
				gameLoader.Assets
						.getFilteredTexture("menu/icons/dull_coin.png"));

		row1Content.add(coinImage1).width(Globals.baseSize * 2)
				.height(Globals.baseSize * 2).pad(imagePadding);

		Label packageDetails1 = new Label(description + row1Price, skin,
				"default");
		row1Content.add(packageDetails1).pad(textPadding);

		TextButton buy1 = new TextButton("BUY", skin, "yesButton");
		row1Content.add(buy1).width(Globals.baseSize * 2)
				.height(Globals.baseSize * 2).padLeft(50);

		row1.addListener(buyListener);
		row1.add(row1Content);
		Table line1 = new Table(skin);
		line1.setBackground("dialogDim");
		row1.row();
		row1.add(line1).expand().fillX().height(2);
		content.add(row1);
		content.row();
	}

	private static void removeTable(final Table localbase) {
		Action completeAction = new Action() {
			public boolean act(float delta) {
				localbase.remove();
				return true;
			}
		};

		localbase.addAction(new SequenceAction(Actions.fadeOut(0.5f),
				completeAction));
	}

	public static void launchDialogFlow(final GameLoader gameLoader,
			final PopQueManager popQueManager) {

		popQueManager.push(new PopQueObject(PopQueObjectType.LOADING));

		Globals.runOnUIThread(new Runnable() {

			@Override
			public void run() {
				gameLoader.getPlatformResolver().requestInformation(
						new GamePurchaseObserver() {
							@Override
							public void handleRecievedInformation(
									Purchases.GamePurchaseResult gamePurchaseResult) {
								popQueManager.push(new PopQueObject(
										PopQueObjectType.DELETE));

								popQueManager.push(new PopQueObject(
										PopQueObjectType.STORE_BUY));
							}
						});
			}
		});

	}

}
