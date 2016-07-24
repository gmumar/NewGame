package Dialog;

import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.TextBox;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.MainMenuScreen;

public class SignInDialog extends Table {

	// reference : https://github.com/EsotericSoftware/tablelayout

	Table base;
	Skin skin;

	public SignInDialog(final GameLoader gameLoader, PopQueManager popQueManager,
			final PopQueObject popQueObject) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		buildTable(gameLoader, popQueManager, popQueObject);

		// return base;
	}

	private void buildTable(final GameLoader gameLoader,
			final PopQueManager popQueManager, final PopQueObject popQueObject) {
		// Skin skin = Skins.loadDefault(gameLoader, 0);

		base = new Table(skin);
		// base.debugAll();
		base.setBackground("dialogDim");
		base.setFillParent(true);
		Animations.fadeIn(base);

		base.setTouchable(Touchable.enabled);

		Table backBar = new Table();

		Button back = SimpleImageButton.create(SimpleImageButtonTypes.BACK,
				gameLoader);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameLoader.setScreen(new MainMenuScreen(gameLoader));
			}
		});
		backBar.add(back).left().expandX();
		base.add(backBar).top().left().fillX().expandX();
		base.row();

		Table content = new Table(skin);

		Table userNameInput = new Table();

		Label userNameText = new Label("UserName: ", skin, "dialogTitle");
		userNameInput.add(userNameText);

		final TextBox userName = new TextBox("user", skin, "userInput");
		userNameInput.add(userName);

		content.add(userNameInput);

		content.row();

		Label info = new Label(
				"You must create a user name to play head to head", skin,
				"default");
		content.add(info);

		Button signIn = new Button(skin, "carBuilder_play");
		signIn.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getTwoButtonFlowContext().successfulTwoButtonFlow(userName.getText());
				super.clicked(event, x, y);
			}

		});

		Label signInText = new Label("Sign In!", skin);
		signIn.add(signInText).pad(20);

		Table contentWrapper = new Table();
		// contentWrapper.setFillParent(true);

		Table contentPadding = new Table(skin);
		contentPadding.setBackground("white");
		contentPadding.add(content).pad(20);

		contentPadding.row();
		contentPadding.add(signIn).expandX().fillX();

		contentWrapper.add(contentPadding);

		base.add(contentWrapper).expandY();

	}

	public Table getBase() {
		return base;
	}

	public void update(GameLoader gameLoader, PopQueManager popQueManager,
			PopQueObject popQueObject) {
		// JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
		// .getCurrentTrack());
		buildTable(gameLoader, popQueManager, popQueObject);
		// popQueObject.getGamePlayInstance().calculateWinings() * ()
		/*
		 * User.getInstance() .addCoin(
		 * (popQueObject.getGamePlayInstance().calculatePosition() >=
		 * Globals.POSITION_LOST ? 0 : popQueObject.getGamePlayInstance()
		 * .calculatePosition()) playedTrack.getDifficulty());
		 */

	}
}
