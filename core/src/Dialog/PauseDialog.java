package Dialog;

import wrapper.Globals;
import Menu.Animations;
import Menu.PopQueManager;
import Menu.PopQueObject;
import Menu.PopQueObject.PopQueObjectType;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gudesigns.climber.GameLoader;

public class PauseDialog extends Table {

	// reference : https://github.com/EsotericSoftware/tablelayout

	Table base;
	Skin skin;

	public PauseDialog(GameLoader gameLoader,PopQueManager popQueManager, final PopQueObject popQueObject) {
		super();
		skin = Skins.loadDefault(gameLoader, 0);
		buildTable(gameLoader,popQueManager,  popQueObject);

		// return base;
	}

	private void buildTable(GameLoader gameLoader,final PopQueManager popQueManager,
			final PopQueObject popQueObject) {
		// Skin skin = Skins.loadDefault(gameLoader, 0);

		base = new Table(skin);
		// base.debugAll();
		base.setBackground("dialogDim");
		base.setFillParent(true);
		Animations.fadeIn(base);
		
		// Sound
		Button sound = SimpleImageButton.create(SimpleImageButtonTypes.SOUND,
				gameLoader);
		sound.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueManager.push(new PopQueObject(PopQueObjectType.SOUND));
				super.clicked(event, x, y);
			}

		});

		base.add(sound).right();
		base.row();

		Button restart = new Button(skin, "carBuilder_play");
		restart.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().restart();
				// base.hide();
				super.clicked(event, x, y);
			}

		});
		
		Label restartText = new Label("restart", skin);
		restart.add(restartText).pad(20);
		
		Image restartImage = new Image(gameLoader.Assets.getFilteredTexture("menu/icons/restart_black.png"));
		restart.add(restartImage).width(Globals.baseSize).height(Globals.baseSize*1.2f).pad(10);
		

		ImageButton home = SimpleImageButton.create(SimpleImageButtonTypes.HOME, gameLoader);
		home.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().exit();
				// base.hide();
				super.clicked(event, x, y);
			}

		});
		
		ImageButton car = SimpleImageButton.create(SimpleImageButtonTypes.CAR, gameLoader);
		car.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				popQueObject.getGamePlayInstance().carModeSelector();
				// base.hide();
				super.clicked(event, x, y);
			}

		});


		Table textWrapper = new Table(skin);
		textWrapper.setTouchable(Touchable.enabled);

		Image resume = new Image(gameLoader.Assets.getFilteredTexture("menu/icons/play.png"));
		
		textWrapper.add(resume).width(Globals.baseSize*4).height(Globals.baseSize*4);
		
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
		
		bottomBar.setBackground("blackOut");

		Table bottomWrapper = new Table(skin);

		bottomWrapper.add(home).width(90).fillY().expandY();
		bottomWrapper.add(car).width(90).fillY().expandY();

		bottomBar.add(bottomWrapper).expand().left().fillY();
		
		bottomBar.add(restart).right().fillY().expand();
		// bottomBar.moveBy(0, -100);

		//Animations.fadeInFromBottom(bottomBar, 50);

		base.add(bottomBar).height(80).expandX().fillX();
	}

	public Table getBase() {
		return base;
	}

	public void update(GameLoader gameLoader,PopQueManager popQueManager, PopQueObject popQueObject) {
		//JSONTrack playedTrack = JSONTrack.objectify(User.getInstance()
			//	.getCurrentTrack());
		buildTable(gameLoader,popQueManager,  popQueObject);
		// popQueObject.getGamePlayInstance().calculateWinings() * ()
		/*User.getInstance()
				.addCoin(
						(popQueObject.getGamePlayInstance().calculatePosition() >= Globals.POSITION_LOST ? 0
								: popQueObject.getGamePlayInstance()
										.calculatePosition())
								* playedTrack.getDifficulty());*/

	}
}
