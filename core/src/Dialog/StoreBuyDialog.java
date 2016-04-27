package Dialog;

import Menu.PopQueObject;
import Purchases.PlatformResolver;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
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

		final Table base = new Table(skin);
		// base.debugAll();
		base.setColor(1, 1, 1, 0);
		base.setFillParent(true);
		base.addAction(Actions.fadeIn(0.5f));
		base.setTouchable(Touchable.enabled);
		base.addListener(new ClickListener() {

			boolean removable = true;

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (removable) {

					Action completeAction = new Action() {
						public boolean act(float delta) {
							base.remove();
							return true;
						}
					};

					base.addAction(new SequenceAction(Actions.fadeOut(0.5f),
							completeAction));
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

		TextButton packOne = new TextButton("pack_one "
				/*+ gameLoader.getPlatformResolver()
						.requestInformation("pack_one").getLocalPricing()*/,
				skin, "noButton");
		packOne.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.getPlatformResolver().requestPurchase(PlatformResolver.PACK_ONE);
				super.clicked(event, x, y);
			}

		});

		Table centeredBox = new Table(skin);
		centeredBox.setName("centeredBox");
		centeredBox.setBackground("dialogDim");
		centeredBox.pad(15);

		Label text = new Label("How much many would you like to give me",
				Skins.loadDefault(gameLoader, 1));
		// text.setTextBoxString("Win!");

		centeredBox.add(text).expandY().left().padBottom(4);

		centeredBox.row();

		centeredBox.add(packOne);

		base.add(centeredBox).expandY().center();

		return base;
	}

}
