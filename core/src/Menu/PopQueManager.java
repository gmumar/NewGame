package Menu;

import java.util.ArrayList;

import Dialog.BuyDialog;
import Dialog.DialogBase;
import Dialog.Skins;
import Dialog.TextDialog;
import Menu.PopQueObject.PopQueObjectType;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PopQueManager {

	private ArrayList<PopQueObject> que = new ArrayList<PopQueObject>();
	private static float timePassed = 0;
	private Stage stage;

	private DialogBase dialog;
	private PopQueObject currentMsg;
	
	Skin skin = Skins.loadDefault();

	public PopQueManager(Stage stage) {
		this.stage = stage;
		skin = Skins.loadDefault();
	}

	public void update(float delta) {

		timePassed += delta;

		if (timePassed > 5) {
			timePassed = 0;
		}

		if (que.isEmpty()) {
			return;
		} else {
			currentMsg = pop();
			handlePop(currentMsg);
		}
	}

	public void push(PopQueObject obj) {
		que.add(obj);
	}

	private PopQueObject pop() {
		PopQueObject obj = que.get(0);
		que.remove(0);
		return obj;
	}

	private void handlePop(PopQueObject popQueObject) {

		if (popQueObject.getType() == PopQueObjectType.TEST) {
			createDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.LOADING) {
			createLoadingDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.DELETE) {
			dialog.hide();
		} else if (popQueObject.getType() == PopQueObjectType.BUY) {
			createBuyDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.WIN) {
			createWinDialog(popQueObject);
		} else if (popQueObject.getType() == PopQueObjectType.KILLED) {
			createLostDialog(popQueObject);
		}
	}

	private void createBuyDialog(PopQueObject popQueObject) {
		/*Skin skin = Skins.loadDefault();

		dialog = new Text2ButtonDialog("Buy", skin, "default");
		dialog.setTouchable(Touchable.enabled);
		dialog.show(stage);*/
		
		dialog = BuyDialog.CreateDialog(popQueObject);
		dialog.show(stage);
		
		
	}
	private void createLostDialog(PopQueObject popQueObject) {
		dialog = new TextDialog("Killed", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.show(stage);
	}
	
	private void createWinDialog(PopQueObject popQueObject) {
		dialog = new TextDialog("Win", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.show(stage);
	}

	private void createLoadingDialog(PopQueObject popQueObject) {
		dialog = new TextDialog("Loading", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.setZIndex(1);
		dialog.show(stage);
	}

	private void createDialog(PopQueObject popQueObject) {

		dialog = new TextDialog("blaa", skin, "default");
		dialog.setTouchable(Touchable.disabled);
		dialog.show(stage);
	}
}
