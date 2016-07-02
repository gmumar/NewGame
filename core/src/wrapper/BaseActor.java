package wrapper;

import java.util.ArrayList;
import java.util.HashMap;

import Component.ComponentPhysicsProperties;
import JSONifier.JSONComponentName;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
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
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class BaseActor {

	private Sprite sprite;
	private Body body;
	private GamePhysicalState gameState;

	private HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

	// private String name, textureStr;
	private JSONComponentName name;
	private String textureStr;
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
	private float friction = 0.1f;
	private final float DEFAULT_SIZE = 1f;
	private float massScale = 1;
	private boolean setFixtureData = false;

	private ArrayList<Vector2> mounts = new ArrayList<Vector2>();
	private HashMap<Integer, Joint> joints = new HashMap<Integer, Joint>();

	private float spriteHalfHeight, spriteHalfWidth;

	public BaseActor(JSONComponentName name, String texture,
			GamePhysicalState gameState) {

		this.gameState = gameState;
		this.texture = gameState.getGameLoader().Assets.get(texture,
				Texture.class);
		this.name = name;
		this.world = gameState.getWorld();
		this.textureStr = texture;

		initSprite();
		initBody();
	}

	public BaseActor(JSONComponentName name, GamePhysicalState gameState) {
		this.gameState = gameState;
		this.onlyPhysicBody = true;
		this.name = name;
		this.world = gameState.getWorld();

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
		this.massScale = other.massScale;
		this.setFixtureData = other.setFixtureData;

		initSprite();
		initBody();
	}

	public BaseActor(JSONComponentName name,
			ComponentPhysicsProperties properties, GamePhysicalState gameState) {

		this.textureStr = (properties.getTexture() == null) ? this.textureStr
				: properties.getTexture();
		this.density = (properties.getDensity() == -1) ? this.density
				: properties.getDensity();
		this.restitution = (properties.getRestituition() == -1) ? this.restitution
				: properties.getRestituition();
		this.friction = (properties.getFriction() == -1) ? this.friction
				: properties.getFriction();
		this.setFixtureData = properties.isSetFixtureData();

		if (textureStr != null) {
			this.texture = gameState.getGameLoader().Assets.get(textureStr,
					Texture.class);
		} else {
			this.onlyPhysicBody = true;
		}

		this.gameState = gameState;
		this.name = name;
		this.world = gameState.getWorld();

		initSprite();
		initBody();
	}

	private Vector2 pos;

	public void draw(SpriteBatch batch) {
		if (!onlyPhysicBody) {
			pos = body.getPosition();
			sprite.setPosition(pos.x - spriteHalfWidth, pos.y
					- spriteHalfHeight);
			sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
			sprite.draw(batch);
		}
	}

	public void draw(SpriteBatch batch, String textureName) {
		if (!onlyPhysicBody) {
			Sprite s = sprites.get(textureName);
			if (s != null) {
				pos = body.getPosition();
				s.setPosition(pos.x - s.getWidth() / 2, pos.y - s.getHeight()
						/ 2);
				s.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				s.draw(batch);
			}
		}
	}

	public void setAlpha(float alpha) {
		if (!onlyPhysicBody) {
			sprite.setAlpha(alpha);
		}
	}

	public void step() {
		// if (!onlyPhysicBody) {
		// sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2,
		// body.getPosition().y - sprite.getHeight() / 2);
		// sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		// }
	}

	public Sprite getSprite() {
		if (!onlyPhysicBody) {

			return sprite;
		}
		return null;
	}

	private void initSprite() {
		if (!onlyPhysicBody) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			sprite = new Sprite(texture);
			sprite.setSize(Globals.PixelToMeters(texture.getWidth()),
					Globals.PixelToMeters(texture.getHeight()));
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
			spriteHalfHeight = sprite.getHeight() / 2;
			spriteHalfWidth = sprite.getWidth() / 2;
		}
	}

	public void addSprite(String name, String textureName) {
		if (!onlyPhysicBody) {
			Texture t = gameState.getGameLoader().Assets
					.getFilteredTexture(textureName);
			Sprite s = new Sprite(t);
			s.setSize(Globals.PixelToMeters(t.getWidth()),
					Globals.PixelToMeters(t.getHeight()));

			s.setOrigin(s.getWidth() / 2, s.getHeight() / 2);
			sprites.put(name, s);
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
		if (setFixtureData)
			fixture.setUserData(name);
		fixture.setSensor(false);

		body.setUserData(name);
	}

	public Fixture getFixture() {
		return fixture;
	}

	public void setFixtureData(Object userData) {
		fixture.setUserData(userData);
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

	public void setOrigin(Vector2 vec) {
		if (!onlyPhysicBody) {
			sprite.setOrigin(vec.x, vec.y);
			spriteHalfHeight = sprite.getHeight() / 2;
			spriteHalfWidth = sprite.getWidth() / 2;
		}
	}

	public float getOriginX() {
		if (!onlyPhysicBody) {
			return sprite.getOriginX();
		}
		return 0;
	}

	public float getOriginY() {
		if (!onlyPhysicBody) {
			return sprite.getOriginY();
		}
		return 0;
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
		while (rot > 2 * Math.PI) {
			rot -= (2 * Math.PI);
			setRotation(rot * MathUtils.radiansToDegrees);
		}

		while (rot < -2 * Math.PI) {
			rot += (2 * Math.PI);
			setRotation(rot * MathUtils.radiansToDegrees);
			// setRotation((float)(body.getAngle() + 2 *
			// Math.PI)*MathUtils.radiansToDegrees);
		}

		return rot;
	}

	public void setScale(float xy) {
		scaleX = scaleY = xy;
		if (!onlyPhysicBody) {
			sprite.setSize(sprite.getWidth() * scaleX, sprite.getHeight()
					* scaleY);
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
			spriteHalfHeight = sprite.getHeight() / 2;
			spriteHalfWidth = sprite.getWidth() / 2;
		}
		initBody();
	}

	public void setScale(String name, float xy) {
		scaleX = scaleY = xy;
		if (!onlyPhysicBody) {

			Sprite s = sprites.get(name);
			s.setSize(s.getWidth() * scaleX, s.getHeight() * scaleY);
			s.setOrigin(s.getWidth() / 2, s.getHeight() / 2);

		}
		initBody();
	}

	public void setScaleY(float y) {
		scaleY = y;
		if (!onlyPhysicBody) {
			sprite.setSize(sprite.getWidth() * scaleX, sprite.getHeight()
					* scaleY);
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
			spriteHalfHeight = sprite.getHeight() / 2;
			spriteHalfWidth = sprite.getWidth() / 2;
		}
		initBody();
	}

	public void setScaleY(String name, float y) {
		scaleY = y;
		if (!onlyPhysicBody) {
			Sprite s = sprites.get(name);
			s.setSize(s.getWidth() * scaleX, s.getHeight() * scaleY);
			s.setOrigin(s.getWidth() / 2, s.getHeight() / 2);
			// spriteHalfHeight = sprite.getHeight() / 2;
			// spriteHalfWidth = sprite.getWidth() / 2;
		}
		initBody();
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

	public float getMountDistance() {
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

	// public String getName() {
	// return name;
	// }

	public JSONComponentName getjName() {
		return name;
	}

	public String getTextureStr() {
		if (!onlyPhysicBody) {
			return textureStr;
		}
		return null;
	}

	public Texture getTexture() {
		if (!onlyPhysicBody) {
			return texture;
		}
		return null;
	}

	public void destroy() {
		// destroyTexture();

		if (body != null && world != null && world.getBodyCount() > 0) {
			if (world == null)
				return;
			world.destroyBody(body);
		}
	}

	public void destroyTexture() {
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

	public void setFilter(short iAm, short collideWith) {
		Filter filter = new Filter();
		filter.categoryBits = iAm;
		filter.maskBits = collideWith;
		fixture.setFilterData(filter);
	}

	public void setDensity(float density) {
		this.density = density;
		fixture.setDensity(density);
		// body.resetMassData();

	}

	public void setMassScale(float scale) {
		this.massScale = scale;
		MassData data = body.getMassData();
		data.mass *= scale;
		body.setMassData(data);
	}

	public float getDensity() {
		return fixture.getDensity();
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
