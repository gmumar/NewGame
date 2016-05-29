package Dialog;

import wrapper.GameState;
import wrapper.Globals;
import Assembly.Assembler;
import JSONifier.JSONTrack;
import Menu.Button;
import Menu.PopQueObject;
import Menu.Buttons.SimpleImageButton;
import Menu.Buttons.SimpleImageButton.SimpleImageButtonTypes;
import RESTWrapper.BackendFunctions;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import User.User;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gudesigns.climber.ADMINCarSelectorScreen;
import com.gudesigns.climber.CarBuilderScreen;
import com.gudesigns.climber.GameLoader;
import com.gudesigns.climber.GamePlayScreen;

public class CarDisplayDialog {

	public static DialogBase CreateDialog(final GameLoader gameLoader,
			final PopQueObject popQueObject) {

		Skin skin = Skins.loadDefault(gameLoader, 0);
		final User user = User.getInstance();
		final GameState gameState = new GameState(gameLoader, user);

		final DialogBase base = new DialogBase("", skin, "buyDialog");
		base.clear();
		base.setMovable(false);
		base.setModal(true);
		
		base.setTouchable(Touchable.enabled);
		
		base.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				Action completeAction = new Action() {
					public boolean act(float delta) {
						base.remove();
						return true;
					}
				};

				base.addAction(new SequenceAction(Actions.fadeOut(0.2f),
						completeAction));
				super.clicked(event, x, y);
			}
			
		});

		Table images = new Table();

		// Images
		// Vector2 originalOrigin = null;
		Table currentItem = new Table(skin);
		currentItem.setBackground("dialogDim");

		// ----- car display ----------
		final String itemJson = popQueObject.getCarJson();

		TextureRegion tr = Assembler.assembleObjectImage(gameLoader, itemJson,
				false);
		TextureRegionDrawable trd = new TextureRegionDrawable(tr);
		trd.setMinWidth(Globals.CAR_DISPLAY_BUTTON_WIDTH);
		trd.setMinHeight(Globals.CAR_DISPLAY_BUTTON_HEIGHT);

		ImageButton currentItemImage = new ImageButton(trd);
		// image.setZIndex(100);
		// b.setPosition(100, 100);
		// image.setSize(100, 100);

		currentItemImage.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				user.setCurrentCar(itemJson);
				gameLoader.setScreen(new GamePlayScreen(gameState));
				super.clicked(event, x, y);
			}

		});

		// image.row();

		currentItem.add(currentItemImage);

		currentItem.row();

		Table buttonsWrapper = new Table();

		final Table playButton = new Table(skin);
		playButton.setBackground("gameGreen");

		Label chooseCarText = new Label("Choose Car", skin);
		playButton.add(chooseCarText).pad(5);

		Image playImage = new Image(
				gameLoader.Assets.getFilteredTexture("menu/icons/play.png"));
		playButton.add(playImage).width(Globals.baseSize)
				.height(Globals.baseSize).pad(5);

		// TextButton play = new TextButton("play",skin, "noButton");
		playButton.addListener(new ActorGestureListener() {

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				playButton.setBackground("grey");
				gameState.getUser().setCurrentCar(itemJson);
				gameLoader.setScreen(new GamePlayScreen(new GameState(
						gameLoader, user)));
				super.touchDown(event, x, y, pointer, button);
			}

		});
		buttonsWrapper.add(playButton).colspan(20).expand().fill();

		ImageButton editImage = SimpleImageButton.create(
				SimpleImageButtonTypes.EDIT, gameLoader);

		final Table edit = new Table(skin);
		edit.setBackground("gameYellow");

		edit.add(editImage);

		edit.addListener(new ActorGestureListener() {

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				edit.setBackground("grey");
				gameState.getUser().setCurrentCar(itemJson);
				gameLoader.setScreen(new CarBuilderScreen(gameState));
				super.touchDown(event, x, y, pointer, button);
			}

		});

		buttonsWrapper.add(edit).colspan(1).expand().fill();
		
		if(popQueObject.isAdmin()){
			buttonsWrapper.row();
			Button delete = new Button("delete");
			buttonsWrapper.add(delete);
			
			delete.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {

					deleteCar(itemJson);
					
					super.clicked(event, x, y);
				}
				
			});
			
			
			Button community = new Button("community");
			buttonsWrapper.add(community);
			
			community.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					
					BackendFunctions.uploadCar(itemJson, RESTPaths.COMMUNITY_CARS);
					deleteCar(itemJson);
					
					super.clicked(event, x, y);
				}
				
			});
			
			Button myPick = new Button("my Pick");
			buttonsWrapper.add(myPick);
			
			myPick.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					BackendFunctions.uploadCar(itemJson, RESTPaths.CARS);
					deleteCar(itemJson);
					
					super.clicked(event, x, y);
				}
				
			});
			
			
		}


		currentItem.add(buttonsWrapper).expand().fill();

		currentItem.pad(5);
		// ----------------------------

		images.add(currentItem);

		base.add(images).center();
		base.row();

		return base;
	}
	
	private static void deleteCar(String itemJson){
		String objectId = JSONTrack.objectify(itemJson).getObjectId();
		REST.deleteEntry(RESTPaths.COMMUNITY_CARS_DUMP, objectId, new HttpResponseListener() {
			
			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				System.out.println(httpResponse.getResultAsString());
			}
			
			@Override
			public void failed(Throwable t) {
				System.out.println("Delete failed");
			}
			
			@Override
			public void cancelled() {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
