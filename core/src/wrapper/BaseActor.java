package wrapper;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class BaseActor {

	private Sprite sprite;
	private Body body;

	private String name, textureStr;
	private Texture texture;
	private boolean onlyPhysicBody = false;
	private boolean useDefaultShape = true;

	// Sprite Properties
	private float spawnX = 0, spawnY = 0;
	private float scaleX = 1f, scaleY = 1f;

	// Physics members
	private World world;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Fixture fixture;
	private Shape shapeBase;
	private short groupIndex;
	private float mountDistance;

	// Physics properties
	private float restitution = 0.2f;
	private float density = 25;
	private float friction = 1f;
	private final float DEFAULT_SIZE = 1f;

	private ArrayList<Vector2> mounts = new ArrayList<Vector2>();
	private HashMap<Integer, Joint> joints = new HashMap<Integer, Joint>();

	public BaseActor(String name, String texture, World world) {
		this.texture = new Texture(texture);
		this.name = name;
		this.world = world;
		this.textureStr = texture;

		initSprite();
		initBody();
	}

	public BaseActor(String name, World world) {
		this.onlyPhysicBody = true;
		this.name = name;
		this.world = world;

		initBody();
	}

	@SuppressWarnings("unchecked")
	public BaseActor(BaseActor other, World world) {
		// this.sprite = sprite;
		// this.body = body;
		this.name = other.name;
		this.textureStr = other.textureStr;
		this.texture = other.texture;
		this.useDefaultShape = other.useDefaultShape;
		this.spawnX = other.spawnX;
		this.spawnY = other.spawnY;
		this.scaleX = other.scaleX;
		this.scaleY = other.scaleY;
		this.world = world;
		this.bodyDef = other.bodyDef;
		this.fixtureDef = other.fixtureDef;
		// this.fixture = fixture;
		this.shapeBase = other.shapeBase;
		this.restitution = other.restitution;
		this.density = other.density;
		this.mounts = (ArrayList<Vector2>) other.mounts.clone();
		this.joints = (HashMap<Integer, Joint>) other.joints.clone();
		this.groupIndex = other.groupIndex;
		this.onlyPhysicBody = other.onlyPhysicBody;
		this.friction = other.friction;
		this.mountDistance = other.mountDistance;

		initSprite();
		initBody();
	}

	public void draw(SpriteBatch batch) {
		if (!onlyPhysicBody) {
			sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2,
					body.getPosition().y - sprite.getHeight() / 2);
			sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
			sprite.draw(batch);
		}
	}

	private void initSprite() {
		if (!onlyPhysicBody) {
			sprite = new Sprite(texture);
			sprite.setSize(Globals.PixelToMeters(texture.getWidth()),
					Globals.PixelToMeters(texture.getHeight()));
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		}
	}

	private void initBody() {
		if (body != null && world != null) {
			world.destroyBody(body);
		}

		if (world == null)
			return;

		bodyDef = new BodyDef();
		fixtureDef = new FixtureDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnX, spawnY);

		fixtureDef.shape = getShapeBase();
		fixtureDef.density = density;
		fixtureDef.friction = friction;
		fixtureDef.restitution = restitution;

		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		fixture.setSensor(false);

		body.setUserData(name);
	}

	public Shape getShapeBase() {
		if (useDefaultShape) {

			PolygonShape shape = new PolygonShape();
			if (!onlyPhysicBody) {
				shape.setAsBox(sprite.getWidth() / 2, sprite.getHeight() / 2);
			} else {
				shape.setAsBox(DEFAULT_SIZE * scaleX, DEFAULT_SIZE * scaleY);
			}
			return shape;
		} else {
			return shapeBase;
		}
	}

	public void setBodyType(BodyType type) {

		body.setType(type);
	}

	public void setShapeBase(Shape shape) {
		useDefaultShape = false;
		shapeBase = shape;

		initBody();
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public void setPosition(float x, float y) {
		spawnX = x;
		spawnY = y;

		body.setTransform(new Vector2(x, y), body.getAngle());
	}

	public void setRotation(float rotDegrees) {
		body.setTransform(getPosition(), rotDegrees
				* MathUtils.degreesToRadians);
	}

	public float getRotation() {
		float rot = body.getAngle();
		if (rot >= 2 * Math.PI) {
			System.out.println("resetting");
			setRotation((float)(body.getAngle() - 2 * Math.PI)*MathUtils.radiansToDegrees);
		}
		
		if (rot <= -2 * Math.PI) {
			System.out.println("resetting");
			setRotation((float)(body.getAngle() + 2 * Math.PI)*MathUtils.radiansToDegrees);
		}

		return body.getAngle();
	}

	public void setScale(float xy) {
		scaleX = scaleY = xy;
		if (!onlyPhysicBody) {
			sprite.setSize(sprite.getWidth() * scaleX, sprite.getHeight()
					* scaleY);
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		}
		initBody();
	}
	
	public void setScaleY(float y) {
		scaleY = y;
		if (!onlyPhysicBody) {
			sprite.setSize(sprite.getWidth() * scaleX, sprite.getHeight()
					* scaleY);
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		}
		initBody();
	}

	public void setRestitution(float r) {
		fixture.setRestitution(r);
	}

	public float getHeight() {
		if (!onlyPhysicBody) {
			return sprite.getHeight();
		}
		return DEFAULT_SIZE * scaleX;
	}

	public float getWidth() {
		if (!onlyPhysicBody) {
			return sprite.getWidth();
		}
		return DEFAULT_SIZE * scaleY;

	}

	public Body getPhysicsBody() {
		return body;
	}

	public Vector2 getCenter() {
		return body.getWorldCenter();
	}

	public void setMounts(ArrayList<Vector2> mounts, float f) {
		this.mounts = mounts;
		this.mountDistance = f;
	}
	
	public float getMountDistance(){
		return mountDistance;
	}

	public Vector2 getMount(int i) {
		return this.mounts.get(i);
	}

	public void setJoint(Integer mountIndex, Joint j) {
		joints.put(mountIndex, j);
	}

	public Joint getJoint(Integer i) {
		return joints.get(i);
	}

	public void disablePhysics() {
		if (world == null)
			return;
		world.destroyBody(body);
		body = null;
	}

	public void reenablePhysics() {
		if (world == null)
			return;
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		body.setUserData(name);
	}

	public void enablePhysics(World world2) {
		body = world2.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);

		body.setUserData(name);
	}

	public BodyDef getBodyDef() {
		return bodyDef;
	}

	public FixtureDef getFictureDef() {
		return fixtureDef;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public String getTextureStr() {
		if (!onlyPhysicBody) {
			return textureStr;
		}
		return null;
	}

	public void destroy() {
		destroyTexture();

		if (body != null && world != null && world.getBodyCount() > 0) {
			if (world == null)
				return;
			world.destroyBody(body);
		}
	}
	
	public void destroyTexture(){
		if (!onlyPhysicBody) {
			texture.dispose();
		}
	}

	public void setGroup(short i) {
		groupIndex = i;
		// fixtureDef.filter.groupIndex = i;
		Filter filter = new Filter();
		filter.groupIndex = i;
		fixture.setFilterData(filter);
	}

	public void setDensity(float density) {
		this.density = density;
		fixture.setDensity(density);
	}

	public void setSensor() {
		fixture.setSensor(true);
	}

	public void unSetSensor() {
		fixture.setSensor(false);
	}

	public ArrayList<Vector2> getMounts() {
		return mounts;
	}
}
