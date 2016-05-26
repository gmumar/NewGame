package Menu;

import wrapper.Globals;
import Dialog.DialogBase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gudesigns.climber.SplashScreen.SplashActor;

public class Animations {

	public final static float BIG_TEXT = 1.000001f;

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

		ParallelAction pa = new ParallelAction(Actions.moveBy(0, 10, 0.8f),
				Actions.fadeIn(0.8f));

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

	static float timePassed = 0;

	public static final Integer money(Label coinLabel, Integer previousMoney,
			Integer currentMoney) {

		timePassed += Gdx.graphics.getDeltaTime();

		if (previousMoney > currentMoney) {
			currentMoney++;

			Action completeAction = new ParallelAction(Actions.moveBy(0, -1,
					0.01f), Actions.fadeIn(0.1f));
			Action mainAction = new ParallelAction(Actions.moveBy(0, 1, 0.01f),
					Actions.fadeOut(0.1f));

			coinLabel.addAction(new SequenceAction(mainAction, completeAction));
		}

		if (previousMoney < currentMoney) {
			currentMoney--;

			if (timePassed > 0.5f) {
				Action completeAction = new ParallelAction(Actions.moveBy(0,
						-2, 0.1f), Actions.fadeIn(0.25f));
				Action mainAction = new ParallelAction(Actions.moveBy(0, 2,
						0.1f), Actions.fadeOut(0.25f));

				coinLabel.addAction(new SequenceAction(mainAction,
						completeAction));
				timePassed = 0;
			}
		}
		coinLabel.setText(currentMoney.toString());

		return currentMoney;
	}

}
