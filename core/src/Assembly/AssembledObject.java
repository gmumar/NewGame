package Assembly;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.Globals;
import wrapper.TouchUnit;
import Component.Component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class AssembledObject {

	ArrayList<Component> partList;
	ArrayList<Component> driveList;
	Component basePart;
	int basePartIndex;

	public void addPart(Component part) {
		partList.add(part);
	}

	public Component getBasePart() {
		return basePart;
	}

	public void setBasePart(Component basePart) {
		this.basePart = basePart;
	}

	public void setBasePartbyIndex(int i) {
		this.basePart = partList.get(i);
		basePartIndex = i;
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

	public void setPosition(float x, float y) {
		Iterator<Component> iter = partList.iterator();
		while (iter.hasNext()) {
			Component component = iter.next();
			Vector2 currentPos = component.getObject().getPosition();
			component.getObject().setPosition(currentPos.x + x,
					currentPos.y + y);
		}
	}

	public void draw(SpriteBatch batch) {
		Iterator<Component> iter = partList.iterator();
		while (iter.hasNext()) {
			iter.next().getObject().draw(batch);
		}
	}

	public void addToDriveList(Component c) {
		if (driveList == null) {
			driveList = new ArrayList<Component>();
		}
		c.getObject().getPhysicsBody().setAngularDamping(0.5f);
		driveList.add(c);
	}

	public void handleInput(ArrayList<TouchUnit> touchesIn) {
		if (driveList == null || touchesIn == null)
			return;

		Iterator<TouchUnit> touchIter = touchesIn.iterator();
		int direction = 0;

		while (touchIter.hasNext()) {
			TouchUnit touch = touchIter.next();

			if (touch.screenX > Globals.GameWidth / 2) {
				direction = 1;
			} else {
				direction = -1;
			}

			if (touch.isTouched()) {
				Iterator<Component> iter = driveList.iterator();

				while (iter.hasNext()) {
					Component comp = iter.next();
					if (Math.abs(comp.getObject().getPhysicsBody()
							.getAngularVelocity()) < 50f) {
						comp.getObject().getPhysicsBody()
								.applyAngularImpulse(-300 * direction, true);
					}
				}
			}
		}

	}

}
