package Dialog;

import wrapper.Globals;
import JSONifier.JSONTrack;
import Menu.Animations;
import Menu.PopQueObject;
import Menu.TextBox;
import RESTWrapper.BackendFunctions;
import User.User;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class WinDialog extends Table {

	// reference : https://github.com/EsotericSoftware/tablelayout

	Table base;
	Skin skin;

	public WinDialog(GameLoader gameLoader, final PopQueObject popQueObject) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		buildTable(gameLoader, popQueObject);

		// return base;
	}

	private void buildTable(GameLoader gameLoader,
			final PopQueObject popQueObject) {
		int position = popQueObject.getGamePlayInstance().calculatePosition();

		// Skin skin = Skins.loadDefault(gameLoader, 0);
		JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
				.getCurrentTrack());

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

		Label text = new Label("Win!", skin);
		// text.setTextBoxString("Win!");

		textWrapper.add(text).expandY().left().padBottom(4);

		textWrapper.row();

		Label bestTime = new Label(Globals.makeTimeStr(playedTrack
				.getBestTime()), skin);
		textWrapper.add(bestTime).expandY().left().padBottom(4);

		textWrapper.row();

		Label stars = new Label(Integer.toString(position), skin);
		textWrapper.add(stars).expandY().left().padBottom(4);

		if (Globals.ADMIN_MODE) {
			textWrapper.row();

			final TextBox difficulty = new TextBox("difficulty");
			difficulty.setMaxLength(2);
			difficulty.setTextBoxString("Here");
			textWrapper.row();
			textWrapper.add(difficulty);

			TextButton upload = new TextButton("upload", skin, "yesButton");
			upload.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					BackendFunctions.uploadTrack(User.getInstance()
							.getCurrentTrack(), popQueObject
							.getGamePlayInstance().getMapTime(), Integer
							.parseInt(difficulty.getText()));
					super.clicked(event, x, y);
				}

			});
			textWrapper.row();
			textWrapper.add(upload);
		}

		base.add(textWrapper).expandY().center();

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
