package wrapper;

import Component.ComponentNames;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {

	private boolean killed = false;

	@Override
	public void beginContact(Contact contact) {

		if (killed)
			return;

		JSONComponentName nameBodyA = ((JSONComponentName) contact
				.getFixtureA().getUserData());
		JSONComponentName nameBodyB = ((JSONComponentName) contact
				.getFixtureB().getUserData());

		if (nameBodyA == null) {
			return;
		}
		String nameA = nameBodyA.getBaseName();
		if (nameBodyB == null) {
			return;
		}
		String nameB = nameBodyB.getBaseName();

		if (nameA.compareTo(ComponentNames.LIFE) == 0
				&& nameB.compareTo(ComponentNames.GROUND) == 0
				|| nameA.compareTo(ComponentNames.GROUND) == 0
				&& nameB.compareTo(ComponentNames.LIFE) == 0) {
			killed = true;
		}

	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	public boolean isKilled() {
		return killed;
	}

}
