package Menu;

import java.util.concurrent.Semaphore;

import wrapper.Globals;
import Dialog.DialogBase;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
		table.addAction(Actions.moveBy(0, -distance));
		table.addAction(new SequenceAction(Actions.delay(0.5f),
				new ParallelAction(Actions.fadeIn(0.5f), Actions.moveBy(0,
						distance, 0.2f))));
	}

	// static float timePassed = 0;
	private volatile static boolean moneyAnimationComplete;
	private static Semaphore moneyAnimationLock;

	public static void InitMoneyAnimation() {

		moneyAnimationComplete = true;
		moneyAnimationLock = new Semaphore(1);
	}

	public static final Integer money(Label animationLabel, Label baseLabel,
			Integer previousMoney, Integer currentMoney) {

		// timePassed += Gdx.graphics.getDeltaTime();

		Vector2 orignalPos = new Vector2(baseLabel.getX(), baseLabel.getY());

		if (previousMoney > currentMoney) {
			
			if (previousMoney - currentMoney> 1000) {
				currentMoney += 1000;
			} else if (previousMoney- currentMoney > 500) {
				currentMoney += 500;
			} else if ( previousMoney - currentMoney> 100) {
				currentMoney += 100;
			} else if ( previousMoney - currentMoney> 10) {
				currentMoney += 10;
			} else if (previousMoney - currentMoney> 5) {
				currentMoney += 5;
			} else {
				currentMoney++;
			}

			Action completeAction = new ParallelAction(Actions.moveTo(
					orignalPos.x, orignalPos.y), Actions.fadeIn(0.0F));
			Action mainAction = new ParallelAction(Actions.moveBy(0, 1, 0.1f),
					Actions.fadeOut(0.3f));

			animationLabel.addAction(new SequenceAction(mainAction,
					completeAction));
		} else if (previousMoney < currentMoney) {
			if (moneyAnimationLock.tryAcquire()) {

				if (currentMoney - previousMoney > 1000) {
					currentMoney -= 1000;
				} else if (currentMoney - previousMoney > 500) {
					currentMoney -= 500;
				} else if (currentMoney - previousMoney > 100) {
					currentMoney -= 100;
				} else if (currentMoney - previousMoney > 10) {
					currentMoney -= 10;
				} else if (currentMoney - previousMoney > 5) {
					currentMoney -= 5;
				} else {
					currentMoney--;
				}
				moneyAnimationLock.release();
			}

			Action completeAction = new ParallelAction(Actions.moveTo(
					orignalPos.x, orignalPos.y), Actions.fadeIn(0.0F));
			Action mainAction = new ParallelAction(Actions.moveBy(0, -6, 0.1f),
					Actions.fadeOut(0.1f));

			if (moneyAnimationComplete) {
				animationLabel.addAction(new SequenceAction(new Action() {

					@Override
					public boolean act(float delta) {
						moneyAnimationLock.tryAcquire();
						moneyAnimationComplete = false;
						return true;
					}

				}, mainAction, completeAction, new Action() {

					@Override
					public boolean act(float delta) {
						moneyAnimationLock.release();
						moneyAnimationComplete = true;
						return true;
					}

				}));
			}
			// timePassed = 0;
			// }
		}
		baseLabel.setText(Globals.makeMoneyString(currentMoney));
		animationLabel.setText(Globals.makeMoneyString(currentMoney));

		return currentMoney;
	}

	// 0 = left, 1 = top, 2 = right , 3 = down
	public static void slideAndBlink(Image arrow, int direction) {

		int moveX = 0, moveY = 0;

		if (direction == 0) {
			moveX = 10;
		} else if (direction == 1) {
			moveY = -10;
		} else if (direction == 2) {
			moveX = -10;
		} else if (direction == 3) {
			moveY = 10;
		}

		Action s = new SequenceAction(Actions.moveBy(-moveX, -moveY, 0.5f),
				Actions.fadeOut(0.1f), Actions.moveBy(moveX, moveY, 0.1f),
				Actions.fadeIn(0.4f), Actions.delay(0.5f));

		arrow.addAction(Actions.repeat(100, s));

	}

	public static void fadeInAndOut(Label table) {
		table.addAction(Actions.repeat(1000,
				new SequenceAction(Actions.fadeOut(0.5f), Actions.delay(0.5f),
						Actions.fadeIn(0.5f), Actions.delay(0.5f)

				)));
	}

	public static void rotate(Image loading) {
		loading.addAction(Actions.repeat(1000,
				new SequenceAction(Actions.rotateBy(360, 2.5f)

				)));
	}

}
