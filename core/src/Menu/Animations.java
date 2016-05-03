package Menu;

import wrapper.Globals;
import Dialog.DialogBase;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gudesigns.climber.SplashScreen.SplashActor;

public class Animations {

	public static final void fadeAndRemove(final Table table) {

		Action completeAction = new Action() {
			public boolean act(float delta) {
				table.remove();
				return true;
			}
		};

		table.addAction(new SequenceAction(Actions.fadeOut(0.5f),
				completeAction));
	}

	public static final void fadeAndHide(final DialogBase dialog) {

		Action completeAction = new Action() {
			public boolean act(float delta) {
				dialog.hide();
				return true;
			}
		};

		dialog.addAction(new SequenceAction(Actions.fadeOut(0.5f),
				completeAction));
	}

	public static final void fadeIn(final Table table) {
		table.setColor(1, 1, 1, 0);
		table.addAction(Actions.fadeIn(0.5f));
	}

	public static final void fadeInAndSlideUp(SplashActor splashActor) {
		splashActor.setColor(1, 1, 1, 0);
		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(Globals.ScreenWidth * 5 / 12,
				Globals.ScreenHeight * 2 / 5);
		moveAction.setDuration(0.8f);
		ParallelAction pa = new ParallelAction(moveAction, Actions.fadeIn(0.8f));

		splashActor.addAction(pa);

	}

	public static final void slideInFromBottom(Table table, int distance) {
		table.addAction(Actions.moveTo(0, -distance, 0.0f));
		table.addAction(new ParallelAction(Actions.fadeIn(0.5f), Actions
				.moveBy(0, distance, 0.2f)));
	}

}
