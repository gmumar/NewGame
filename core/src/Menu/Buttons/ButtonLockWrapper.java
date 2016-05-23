package Menu.Buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class ButtonLockWrapper {

	public Button button;
	public boolean locked;

	public ButtonLockWrapper(Button button, boolean locked) {
		super();
		this.button = button;
		this.locked = locked;
	}

}
