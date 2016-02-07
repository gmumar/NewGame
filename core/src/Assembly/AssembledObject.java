package Assembly;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.Globals;
import wrapper.TouchUnit;
import Component.Component;
import Component.ComponentNames;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class AssembledObject {

	private ArrayList<Component> partList;
	private ArrayList<BaseActor> driveList;
	private Body basePart;
	private Component leftMost, rightMost;
	// private int basePartIndex;

	private final float ANGULAR_DAMPING = 1f;
	// private final float ROTATION_FORCE = 4000;
	private final float MAX_VELOCITY = 45f;

	private Iterator<TouchUnit> touchIter;
	private int direction;
	private TouchUnit touch;
	private Iterator<BaseActor> driveListIter;
	private BaseActor comp;

	private Iterator<Component> iter;
	// private ArrayList<Component> delayedDraw = new ArrayList<Component>();
	private Component part;

	public Body getBasePart() {
		return basePart;
	}

	/*
	 * public void setBasePartbyIndex(int i) { this.basePart =
	 * partList.get(i).getJointBodies().get(1).getPhysicsBody(); basePartIndex =
	 * i;
	 * 
	 * }
	 */

	public void setLifeBasePart() {
		Iterator<Component> iter = partList.iterator();

		while (iter.hasNext()) {
			Component part = iter.next();
			if (part.getComponentName().contains(ComponentNames.LIFE)) {
				this.basePart = part.getJointBodies().get(1).getPhysicsBody();// .getObject().getPhysicsBody();
			}
		}

		// this.basePart.getObject().getPhysicsBody()
		// .setAngularDamping(ANGULAR_DAMPING);

		this.setLeftMost();
		this.setRightMost();
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

	public void setLeftMost() {
		Iterator<Component> iter = partList.iterator();
		float minY = Float.POSITIVE_INFINITY;

		while (iter.hasNext()) {
			Component component = iter.next();
			if (component.getObject().getPosition().x < minY) {
				leftMost = component;
				minY = component.getObject().getPosition().x;
			}
		}

		System.out.println("left Most: " + leftMost.getComponentName());
	}

	public void setRightMost() {
		Iterator<Component> iter = partList.iterator();
		float maxY = Float.NEGATIVE_INFINITY;

		while (iter.hasNext()) {
			Component component = iter.next();
			if (component.getObject().getPosition().x > maxY) {
				rightMost = component;
				maxY = component.getObject().getPosition().x;
			}
		}
		System.out.println("right Most: " + rightMost.getComponentName());
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
		String componentName = "";
		ArrayList<String> componentsSet = new ArrayList<String>();

		Iterator<Component> iter = partList.iterator();
		while (iter.hasNext()) {
			Component component = iter.next();
			componentName = Globals.getComponentName(
					component.getComponentName()).split(
					Assembler.NAME_SUBNAME_SPLIT)[0]
					+ Globals.getId(component.getComponentName());
			System.out.println(componentName);
			if (componentsSet.contains(componentName))
				continue;
			component.setPosition(x, y);
			componentsSet.add(componentName);
		}
	}

	public void draw(SpriteBatch batch) {
		iter = partList.iterator();

		while (iter.hasNext()) {
			part = iter.next();
			// if(part.getComponentName().contains(ComponentNames.SPRINGJOINT)){
			// delayedDraw.add(part);
			// }else{
			part.draw(batch);
			// }
		}

		/*
		 * iter = delayedDraw.iterator(); while (iter.hasNext()) {
		 * iter.next().draw(batch); }
		 * 
		 * delayedDraw.clear();
		 */
	}

	public void setScale(float scale) {

		iter = partList.iterator();

		while (iter.hasNext()) {
			part = iter.next();
			part.getObject().setScale(scale);
		}
	}

	public void drawImage(SpriteBatch batch) {
		ArrayList<Component> delayedDraw = new ArrayList<Component>();
		iter = partList.iterator();

		while (iter.hasNext()) {
			part = iter.next();
			if (part.getComponentName().contains(ComponentNames.SPRINGJOINT)) {
				delayedDraw.add(part);
			} else {
				part.draw(batch);
			}
		}

		iter = delayedDraw.iterator();
		while (iter.hasNext()) {
			iter.next().draw(batch);
		}

		delayedDraw.clear();

	}

	/*public void drawImage(SpriteBatch batch, FrameBuffer frameBufferObject) {

		Texture texture;
		BaseActor partObj;
		TextureRegion tr;

		ArrayList<BaseActor> secondaryParts = new ArrayList<BaseActor>();
		ArrayList<BaseActor> tempParts;

		float offsetX = frameBufferObject.getWidth() / 2;
		float offsetY = frameBufferObject.getHeight() / 2;

		float scale = 1;

		iter = partList.iterator();

		while (iter.hasNext()) {
			part = iter.next();
			// if(part.getComponentName().contains(ComponentNames.SPRINGJOINT))
			// continue;

			tempParts = part.getJointBodies();
			if (tempParts != null) {
				secondaryParts.addAll(tempParts);
			}

			partObj = part.getObject();
			texture = partObj.getTexture();
			if (texture == null)
				continue;

			tr = new TextureRegion(texture);

			partObj.getSprite().draw(batch);

			batch.draw(tr, (partObj.getPosition().x - texture.getWidth() / 250)
					+ offsetX,
					(partObj.getPosition().y - texture.getHeight() / 250)
							+ offsetY, 0, 0, texture.getWidth(),
					texture.getHeight(), 1 / scale, 1 / scale,
					MathUtils.radiansToDegrees * partObj.getRotation(), false);

		}

		Iterator<BaseActor> secondaryiter = secondaryParts.iterator();

		while (secondaryiter.hasNext()) {
			partObj = secondaryiter.next();
			texture = partObj.getTexture();
			if (texture == null)
				continue;

			tr = new TextureRegion(texture);
			batch.draw(
					tr,
					Globals.MetersToPixel(partObj.getPosition().x)
							- texture.getWidth() / 250 + offsetX,
					Globals.MetersToPixel(partObj.getPosition().y)
							- texture.getHeight() / 250 + offsetY, 0, 0,
					texture.getWidth(), texture.getHeight(), 1 / scale,
					1 / scale,
					MathUtils.radiansToDegrees * partObj.getRotation(), false);

		}

	}*/

	public void addToDriveList(Component c) {
		System.out.println("Adding to drive list");
		if (driveList == null) {
			driveList = new ArrayList<BaseActor>();
		}
		// c.getObject().getPhysicsBody().setAngularDamping(0.5f);

		System.out.println(c.getComponentName());
		if (c.getComponentName().contains(ComponentNames.TIRE)) {
			ArrayList<BaseActor> bodies = c.getJointBodies();
			driveList.add(bodies.get(0));
			bodies.get(0).getPhysicsBody().setAngularDamping(ANGULAR_DAMPING);
		}

	}

	public void handleInput(ArrayList<TouchUnit> touchesIn) {
		if (driveList == null || touchesIn == null)
			return;

		touchIter = touchesIn.iterator();
		direction = 0;

		// System.out.println(rightMost.getObject().getRotation());

		while (touchIter.hasNext()) {
			touch = touchIter.next();

			if (touch.isTouched()) {

				if (touch.screenX > Globals.GameWidth / 2) {
					direction = 1;
					/*
					 * float rotation = rightMost.getObject().getRotation();
					 * rightMost
					 * .getObject().getPhysicsBody().applyForceToCenter(
					 * (float)(ROTATION_FORCE*Math.cos(rotation)), (float)
					 * (ROTATION_FORCE*Math.sin(rotation)), true);
					 * rightMost.getObject
					 * ().getPhysicsBody().applyForceToCenter(1000,
					 * ROTATION_FORCE, true);
					 */
				} else {
					direction = -1;
					/*
					 * float rotation = leftMost.getObject().getRotation();
					 * leftMost.getObject().getPhysicsBody().applyForceToCenter(
					 * (float)(ROTATION_FORCE*Math.cos(rotation)), (float)
					 * (ROTATION_FORCE*Math.sin(rotation)), true);
					 * leftMost.getObject
					 * ().getPhysicsBody().applyForceToCenter(1000,
					 * ROTATION_FORCE, true);
					 */

				}

				driveListIter = driveList.iterator();
				while (driveListIter.hasNext()) {

					comp = driveListIter.next();
					comp.getPhysicsBody().applyAngularImpulse(-100 * direction,
							true);

					if (comp.getPhysicsBody().getAngularVelocity() > MAX_VELOCITY) {
						comp.getPhysicsBody().setAngularVelocity(MAX_VELOCITY);
					} else if (comp.getPhysicsBody().getAngularVelocity() < -MAX_VELOCITY) {
						comp.getPhysicsBody().setAngularVelocity(-MAX_VELOCITY);
					}
				}
			}
		}
	}

	public void dispose() {
		// comp.destroy();
	}
}
