package Assembly;

import java.util.ArrayList;
import java.util.Iterator;

import wrapper.BaseActor;
import wrapper.Globals;
import wrapper.TouchUnit;
import Component.Component;
import Component.ComponentNames;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class AssembledObject {

	private ArrayList<Component> partList;
	private ArrayList<BaseActor> driveList;
	private Body cameraFocusPart, basePart;
	private Body driveBody ;
	//private Component leftMost, rightMost;
	// private int basePartIndex;

	private final float ANGULAR_DAMPING = 1f;
	// private final float ROTATION_FORCE = 4000;
	private float MAX_VELOCITY = 45f;

	private int direction;

	private Iterator<Component> iter;
	// private ArrayList<Component> delayedDraw = new ArrayList<Component>();
	private Component part;

	public Body getCameraFocusPart() {
		return cameraFocusPart;
	}
	
	private Body getBasePart() {
		return basePart;
	}

	public Vector2 getPosition(){
		return getBasePart().getPosition();
	}
	
	public Vector2 getSpeed(){
		return getCameraFocusPart().getLinearVelocity();
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
			if (part.getComponentName().compareTo(ComponentNames.LIFE)==0) {
				this.cameraFocusPart = part.getJointBodies().get(1).getPhysicsBody();// .getObject().getPhysicsBody();
				this.basePart = part.getJointBodies().get(0).getPhysicsBody();
			}
		}

		// this.basePart.getObject().getPhysicsBody()
		// .setAngularDamping(ANGULAR_DAMPING);

		//this.setLeftMost();
		//this.setRightMost();
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

	/*public void setLeftMost() {
		Iterator<Component> iter = partList.iterator();
		float minY = Float.POSITIVE_INFINITY;

		while (iter.hasNext()) {
			Component component = iter.next();
			if (component.getObject().getPosition().x < minY) {
				leftMost = component;
				minY = component.getObject().getPosition().x;
			}
		}

		//System.out.println("left Most: " + leftMost.getComponentName());
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
		//System.out.println("right Most: " + rightMost.getComponentName());
	}*/

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
		//String componentName = "";
		//JSONComponentName componentName = new JSONComponentName();
		ArrayList<String> componentsSet = new ArrayList<String>();

		Iterator<Component> iter = partList.iterator();
		while (iter.hasNext()) {
			Component component = iter.next();
			JSONComponentName componentName = component.getjComponentName();
			/*componentName = Globals.getComponentName(
					component.getComponentName()).split(
					Assembler.NAME_SUBNAME_SPLIT)[0]
					+ Globals.getId(component.getComponentName());*/
			//System.out.println(componentName);
			if (componentsSet.contains(componentName.getBaseId()))
				continue;
			component.setPosition(x, y);
			componentsSet.add(componentName.getBaseId());
		}
		
		
	}

	public void draw(SpriteBatch batch) {

		for (Component part : partList){
			part.draw(batch);
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

	public void addToDriveList(Component c) {
		//System.out.println("Adding to drive list");
		if (driveList == null) {
			driveList = new ArrayList<BaseActor>();
		}
		// c.getObject().getPhysicsBody().setAngularDamping(0.5f);

		//System.out.println(c.getComponentName());
		if (c.getComponentName().contains(ComponentNames.TIRE)) {
			ArrayList<BaseActor> bodies = c.getJointBodies();
			driveList.add(bodies.get(0));
			bodies.get(0).getPhysicsBody().setAngularDamping(ANGULAR_DAMPING);
		}

	}

	public void handleInput(ArrayList<TouchUnit> touchesIn) {
		if (driveList == null || touchesIn == null)
			return;

		//touchIter = touchesIn.iterator();
		direction = 0;
		
		float resultantForce = 0;

		// System.out.println(rightMost.getObject().getRotation());

		for (TouchUnit touch : touchesIn) {
			//touch = touchIter.next();

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

				//driveListIter = driveList.iterator();
				resultantForce += -100 * direction;
			}
			
		}

		for (BaseActor comp : driveList) {

			driveBody = comp.getPhysicsBody();

			driveBody.applyAngularImpulse(resultantForce,true);
			
			if (driveBody.getAngularVelocity() > MAX_VELOCITY) {
				driveBody.setAngularVelocity(MAX_VELOCITY);
			} else if (driveBody.getAngularVelocity() < -MAX_VELOCITY) {
				driveBody.setAngularVelocity(-MAX_VELOCITY);
			}
		}
	}

	public void dispose() {
		// comp.destroy();
	}
	
	public void setMaxVelocity(float maxVelocity){
		MAX_VELOCITY = maxVelocity;
	}
}
