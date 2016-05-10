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

	public static final void click(final Table base) {
		base.addAction(new SequenceAction(Actions.alpha(0.8f, 0.2f), Actions
				.alpha(Globals.BUTTON_OPACITY, 0.4f)));
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

	public static final void fadeInAndSlideSide(Table base) {
		base.setColor(1, 1, 1, 0);
		base.addAction(Actions.moveBy(-10, 0));
		base.addAction(new ParallelAction(Actions.fadeIn(0.5f), Actions.moveBy(
				10, 0, 0.5f)));

	}

	public static void ExploadeIn(Table table) {
		table.setColor(1, 1, 1, 0);
		// table.setScale(2);
		table.sizeBy(100);
		table.addAction(new ParallelAction(Actions.fadeIn(0.5f), Actions
				.scaleBy(0.3f, 0.3f)));
	}

	public static void slideInFromTop(Table table, int distance) {
		table.addAction(Actions.moveTo(0, Globals.ScreenHeight));
		table.addAction(new SequenceAction(Actions.delay(0.5f),
				new ParallelAction(Actions.fadeIn(0.5f), Actions.moveBy(0,
						distance, 0.2f))));
	}

	public static final void fadeInFromBottom(Table table, int distance) {
		table.addAction(Actions.moveTo(0, -distance));
		table.addAction(new SequenceAction(Actions.delay(0.5f),
				new ParallelAction(Actions.fadeIn(0.5f), Actions.moveBy(0,
						distance, 0.2f))));
	}

}
