package Assembly;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.Globals;
import wrapper.TouchUnit;
import Component.Component;
import Component.ComponentBuilder.ComponentNames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class AssembledObject {

	ArrayList<Component> partList;
	ArrayList<BaseActor> driveList;
	Component basePart;
	int basePartIndex;

	final float ANGULAR_DAMPING = 1f;

	public Component getBasePart() {
		return basePart;
	}

	public void setBasePartbyIndex(int i) {
		this.basePart = partList.get(i);
		basePartIndex = i;

	}

	public void setLifeBasePart() {
		Iterator<Component> iter = partList.iterator();

		while (iter.hasNext()) {
			Component part = iter.next();
			if (part.getComponentName().contains(ComponentNames.life.name())) {
				this.basePart = part;
			}
		}

		this.basePart.getObject().getPhysicsBody()
				.setAngularDamping(ANGULAR_DAMPING);
	}

	public ArrayList<Component> getPartList() {
		return partList;
	}

	public void setPartList(ArrayList<Component> list) {
		partList = list;
		if (list == null)
			return;

		Iterator<Component> iter = partList.iterator();
		while (iter.hasNext()) {
			Component component = iter.next();
			if (component.isMotor()) {
				addToDriveList(component);
			}
		}
	}

	public Vector2 getCenter() {
		float sumX = 0, sumY = 0;
		float count = 0;

		Iterator<Component> iter = partList.iterator();

		while (iter.hasNext()) {
			Component component = iter.next();
			count++;
			sumX += component.getObject().getPosition().x;
			sumY += component.getObject().getPosition().y;
		}

		return new Vector2(sumX / count, sumY / count);
	}

	public void setPosition(float x, float y) {
		Iterator<Component> iter = partList.iterator();
		while (iter.hasNext()) {
			Component component = iter.next();
			Vector2 currentPos = component.getObject().getPosition();
			component.setPosition(currentPos.x + x, currentPos.y + y);
		}
	}

	public void draw(SpriteBatch batch) {
		Iterator<Component> iter = partList.iterator();
		while (iter.hasNext()) {
			iter.next().draw(batch);
		}
	}

	public void addToDriveList(Component c) {
		System.out.println("Adding to drive list");
		if (driveList == null) {
			driveList = new ArrayList<BaseActor>();
		}
		// c.getObject().getPhysicsBody().setAngularDamping(0.5f);

		if (c.getComponentName().compareTo(ComponentNames.axle.name()) == 0) {
			ArrayList<BaseActor> bodies = c.getJointBodies();
			driveList.add(bodies.get(0));
			bodies.get(0).getPhysicsBody().setAngularDamping(ANGULAR_DAMPING);
		}

	}

	public void handleInput(ArrayList<TouchUnit> touchesIn) {
		if (driveList == null || touchesIn == null)
			return;

		System.out.println(basePart.getObject().getPhysicsBody()
				.getAngularVelocity());

		Iterator<TouchUnit> touchIter = touchesIn.iterator();
		int direction = 0;

		while (touchIter.hasNext()) {
			TouchUnit touch = touchIter.next();

			if (touch.isTouched()) {

				if (touch.screenX > Globals.GameWidth / 2) {
					direction = 1;
				} else {
					direction = -1;

				}

				Iterator<BaseActor> iter = driveList.iterator();

				while (iter.hasNext()) {
					BaseActor comp = iter.next();
					comp.getPhysicsBody().applyAngularImpulse(-100 * direction,
							true);
					basePart.getObject().getPhysicsBody()
							.applyAngularImpulse(50f * direction, false);

					if (comp.getPhysicsBody().getAngularVelocity() > 50f) {
						comp.getPhysicsBody().setAngularVelocity(50f);
					} else if (comp.getPhysicsBody().getAngularVelocity() < -50f) {
						comp.getPhysicsBody().setAngularVelocity(-50f);
					}
				}
			}
		}
	}
}
