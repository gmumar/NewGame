package Dialog;

import wrapper.Globals;
import JSONifier.JSONTrack;
import Menu.Animations;
import Menu.PopQueObject;
import User.User;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class PauseDialog extends Table {

	// reference : https://github.com/EsotericSoftware/tablelayout

	Table base;
	Skin skin;

	public PauseDialog(GameLoader gameLoader, final PopQueObject popQueObject) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		buildTable(gameLoader, popQueObject);

		// return base;
	}

	private void buildTable(GameLoader gameLoader,
			final PopQueObject popQueObject) {
		// Skin skin = Skins.loadDefault(gameLoader, 0);

		base = new Table(skin);
		// base.debugAll();
		base.setBackground("dialogDim");
		base.setFillParent(true);
		Animations.fadeIn(base);

		TextButton restart = new TextButton("restart", skin, "noButton");
		restart.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().restart();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		TextButton exit = new TextButton("exit", skin, "noButton");
		exit.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().exit();
				// base.hide();
				super.clicked(event, x, y);
			}

		});

		Table textWrapper = new Table(skin);
		textWrapper.setTouchable(Touchable.enabled);

		Image resume = new Image(gameLoader.Assets.get("menu/icons/play.png", Texture.class));
		
		textWrapper.add(resume).width(50).height(50);
		
		textWrapper.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Action completeAction = new Action() {
					public boolean act(float delta) {
						base.remove();
						popQueObject.getGamePlayInstance().resume();
						return true;
					}
				};

				base.addAction(new SequenceAction(Actions.fadeOut(0.5f),
						completeAction));
				super.clicked(event, x, y);
			}
			
		});

		base.add(textWrapper).fill().expand().center();

		base.row();
		Table bottomBar = new Table(skin);
		bottomBar.setBackground("dialogDim");

		Table bottomWrapper = new Table(skin);

		bottomWrapper.add(exit).width(90).pad(2);
		bottomWrapper.add(restart).width(90);

		bottomBar.add(bottomWrapper).expand().left().fillY();
		// bottomBar.moveBy(0, -100);

		Animations.slideInFromBottom(bottomBar, 100);

		base.add(bottomBar).height(100).expandX().fillX();
	}

	public Table getBase() {
		return base;
	}

	public void update(GameLoader gameLoader, PopQueObject popQueObject) {
		JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
				.getCurrentTrack());
		buildTable(gameLoader, popQueObject);
		// popQueObject.getGamePlayInstance().calculateWinings() * ()
		User.getInstance()
				.addCoin(
						(popQueObject.getGamePlayInstance().calculatePosition() >= Globals.POSITION_LOST ? 0
								: popQueObject.getGamePlayInstance()
										.calculatePosition())
								* playedTrack.getDifficulty());

	}
}
