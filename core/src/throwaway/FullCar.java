package throwaway;

import java.util.ArrayList;

import wrapper.BaseActor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class FullCar {

	public BaseActor carBody, frontTire, backTire;
	PrismaticJointDef frontSpring, backSpring;
	DistanceJointDef frontDamper, backDamper;

	public FullCar(World world) {
		carBody = new BaseActor("base", "temp_car.png", world);
		carBody.setMounts(createCarMounts(),0);

		frontTire = createTire("front_tire", world);
		backTire = createTire("back_tire", world);

		frontSpring = new PrismaticJointDef();
		frontSpring.initialize(carBody.getPhysicsBody(), frontTire.getJoint(0)
				.getBodyA(), carBody.getMount(1), new Vector2(0, 1));

		frontSpring.lowerTranslation = 0.1f;
		frontSpring.upperTranslation = 0.0f;
		frontSpring.enableLimit = true;
		frontSpring.localAnchorB.set(new Vector2(0, 0));
		world.createJoint(frontSpring);

		frontDamper = new DistanceJointDef();
		frontDamper.initialize(carBody.getPhysicsBody(),
				frontTire.getPhysicsBody(), carBody.getMount(1),
				frontTire.getCenter());
		frontDamper.length = 1.5f;
		frontDamper.dampingRatio = 5f;
		frontDamper.frequencyHz = 125;
		world.createJoint(frontDamper);

		
		// suspension
		// prismatic attaches to the axle and axle stay still
		// distance connects to tire and provides bounciness
		backSpring = new PrismaticJointDef();
		backSpring.initialize(carBody.getPhysicsBody(), backTire.getJoint(0)
				.getBodyA(), carBody.getMount(0), new Vector2(0, 1));

		backSpring.lowerTranslation = 0.1f;
		backSpring.upperTranslation = 0.0f;
		backSpring.enableLimit = true;
		backSpring.localAnchorB.set(new Vector2(0, 0));
		world.createJoint(backSpring);

		backDamper = new DistanceJointDef();
		backDamper.initialize(carBody.getPhysicsBody(),
				backTire.getPhysicsBody(), carBody.getMount(0),
				backTire.getCenter());
		backDamper.length = 1.5f;
		backDamper.dampingRatio = 5f;
		backDamper.frequencyHz = 125;
		world.createJoint(backDamper);
	}

	public void draw(SpriteBatch batch) {
		carBody.draw(batch);
		frontTire.draw(batch);
		backTire.draw(batch);
	}

	private ArrayList<Vector2> createCarMounts() {
		Vector2 center = carBody.getCenter();
		float y = -carBody.getHeight() * 2 / 4;

		ArrayList<Vector2> mounts = new ArrayList<Vector2>();
		mounts.add(new Vector2(center.x - carBody.getWidth() * 1 / 3, y));// back
		mounts.add(new Vector2(center.x + carBody.getWidth() * 1 / 3, y));// front

		// System.out.println(center.x - carBody.getWidth());
		return mounts;
	}

	BaseActor createTire(String name, World world) {
		PolygonShape axle = new PolygonShape();
		axle.setAsBox(0.1f, 0.1f);
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		bodyDef.type = BodyType.DynamicBody;

		fixtureDef.shape = axle;
		fixtureDef.density = 1.0f;

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		BaseActor tmp = new BaseActor(name, "temp_tire.png", world);
		CircleShape shape = new CircleShape();

		tmp.setRestitution(0.9f);
		tmp.setScale(0.75f);
		shape.setRadius(tmp.getWidth() / 2);
		tmp.setShapeBase(shape);

		RevoluteJointDef join = new RevoluteJointDef();
		join.initialize(body, tmp.getPhysicsBody(), body.getWorldCenter());
		
		join.maxMotorTorque = 2;
		//join.enableMotor = true;

		// join.localAnchorA.set( new Vector2(0,0));
		// join.localAnchorB.set( new Vector2(0,0));

		Joint j = world.createJoint(join);

		tmp.setJoint(0, j);

		return tmp;
	}

}
