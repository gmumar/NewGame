package UserPackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import wrapper.GamePreferences;
import wrapper.Globals;
import Component.ComponentNames;
import DataMutators.TrippleDes;
import JSONifier.JSONCar;
import JSONifier.JSONChallenge;
import JSONifier.JSONComponent;
import JSONifier.JSONTrack;
import JSONifier.JSONTrack.TrackType;
import Multiplayer.Challenge;
import Multiplayer.RecorderUnit;
import RESTWrapper.Backendless_JSONParser;
import RESTWrapper.Backendless_ParentContainer;
import RESTWrapper.REST;
import RESTWrapper.RESTPaths;
import RESTWrapper.RESTProperties;
import RESTWrapper.ServerDataUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.Gson;
import com.gudesigns.climber.ChallengeLobbyScreen;

public class User {

	public static final Integer MAX_BAR3_LEVEL = 15;
	public static final Integer MAX_TIRE_LEVEL = 8;
	public static final Integer MAX_SPRING_LEVEL = 15;

	public static final String TRACK_PREFIX = "track_";
	public static final String WORLD_PREFIX = "lastPlayWorld";
	public static final String FILE_NAME_PREFIX = "file_";
	public static final String FILE_NAME_PREFIX_MD5 = "file_md5_";

	public enum STARS implements Serializable {
		NONE, ONE, TWO, THREE

	}

	public enum GameMode {
		NORMAL, SET_CHALLENGE, PLAY_CHALLENGE

	}

	public enum CarSetErrors {
		NONE, PARTS_NOT_UNLOCKED, CAR_NOT_SUTIBLE_FOR_CHALLENGE
	}

	private UserState userState = new UserState();

	class LookupItemsType {
		HashMap<String, Boolean> items = new HashMap<String, Boolean>();
	};

	public LookupItemsType lockedItems = null;
	public LookupItemsType nonNewItems = null;

	private int opponentBarLevel = 0, opponentTireLevel = 0,
			opponentSpringLevel = 0;

	private String currentCar = null;
	private String currentTrack = null;
	private Challenge currentChallenge = null;
	private JSONChallenge currentJSONChallenge = null;
	private ArrayList<RecorderUnit> currentChallengeSortedRecording = null;
	private String currentChallengeCar = null;

	private static User instance;
	private TrippleDes encryptor = new TrippleDes();

	private volatile GameMode currentGameMode = GameMode.NORMAL;

	private String userName = null;

	private Preferences prefs = null;

	private User() {
		if (prefs == null) {
			prefs = Gdx.app.getPreferences(GamePreferences.CAR_PREF_STR);
		}

		currentCar = (Globals.defualt_car);
		currentTrack = (Globals.default_track);

		restoreUserState();
	}

	private void saveUserState() {

		Gson json = new Gson();
		String encrypted = encryptor.encrypt(json.toJson(userState));
		String encryptedLocked = encryptor.encrypt(json.toJson(lockedItems));
		String encryptednonNew = encryptor.encrypt(json.toJson(nonNewItems));

		prefs.putString(GamePreferences.USR_STR, encrypted);
		prefs.putString(GamePreferences.USR_LOCKED, encryptedLocked);
		prefs.putString(GamePreferences.USR_NON_NEW, encryptednonNew);
		prefs.flush();

	}

	private void saveOnlyUserState() {

		Gson json = new Gson();
		String encrypted = encryptor.encrypt(json.toJson(userState));

		prefs.putString(GamePreferences.USR_STR, encrypted);
		prefs.flush();

	}

	private void restoreUserState() {
		Gson json = new Gson();

		String inputString = prefs.getString(GamePreferences.USR_STR, null);// "cveSuvxEjX1GNBw7FDuABebTQF0Dz9fsJxrLH/Mwy7GrJkIonjtO0icayx/zMMuxSm9j+vdJQwq6typf+G0FHw=="

		if (inputString == null) {
			userState = new UserState();
		} else {

			userState = json.fromJson(encryptor.decrypt(inputString),
					UserState.class);
		}

		String lockedItemsString = prefs.getString(GamePreferences.USR_LOCKED,
				null);

		if (lockedItemsString != null) {
			lockedItems = json.fromJson(encryptor.decrypt(lockedItemsString),
					LookupItemsType.class);
		}

		String nonNewItemsString = prefs.getString(GamePreferences.USR_NON_NEW,
				null);

		if (nonNewItemsString != null) {
			nonNewItems = json.fromJson(encryptor.decrypt(nonNewItemsString),
					LookupItemsType.class);
		}

		// Initially unlocked
		if (lockedItems == null) {
			lockedItems = new LookupItemsType();
		}

		lockedItems.items.put(ItemsLookupPrefix.getForrestPrefix("1"), false);
		lockedItems.items.put(ItemsLookupPrefix.getForrestPrefix("2"), false);
		lockedItems.items.put(ItemsLookupPrefix.getForrestPrefix("3"), false);

		lockedItems.items.put(ItemsLookupPrefix.getArticPrefix("1"), false);

		saveUserState();
	}

	public String getLocalUserName() {
		// return userName if registered else return null
		String userName = prefs.getString(ItemsLookupPrefix.USER_NAME, null);

		if (userName == null) {
			return this.userName;
		}

		return userName;

	}

	public String getLocalUserObjectId() {
		// return userName if registered else return null

		String userObjectId = null;

		String userName = prefs.getString(ItemsLookupPrefix.USER_NAME, null);
		if (userName != null) {
			userObjectId = prefs.getString(
					ItemsLookupPrefix.getUserObjectIDPrefix(userName), null);
		}

		return userObjectId;

	}

	public String userRegisterLocally(String userName, String objectId) {
		System.out.println("User: registed user " + userName + " " + objectId);
		this.userName = userName;

		prefs.putString(ItemsLookupPrefix.USER_NAME, userName);
		prefs.putString(ItemsLookupPrefix.getUserObjectIDPrefix(userName),
				objectId);
		prefs.flush();

		return null;
	}

	public GameMode getCurrentGameMode() {
		return currentGameMode;
	}

	public void setCurrentGameMode(GameMode currentGameMode) {
		System.out.println("User: " + currentGameMode.toString());
		this.currentGameMode = currentGameMode;
	}

	public void saveRecording(String recording) {

		prefs.putString(GamePreferences.RECORDING, recording);
		prefs.flush();

		System.out.println("Saved: " + recording);

	}

	public String getRecording() {
		String inputString = prefs.getString(GamePreferences.RECORDING, null);

		return inputString;
	}

	public String getCurrentCar() {
		String inputString = prefs.getString(GamePreferences.CAR_MAP_STR,
				Globals.defualt_car);
		currentCar = inputString;

		return currentCar;
	}

	// return true if all parts are available to user
	public CarSetErrors setCurrentCar(String currentCar, boolean test) {

		{
			JSONCar car = JSONCar.objectify(currentCar);
			ArrayList<JSONComponent> parts = car.getComponentList();

			Integer barLevel = 0, springLevel = 0, tireLevel = 0;

			for (JSONComponent part : parts) {
				if (part.getBaseName().compareTo(ComponentNames.BAR3) == 0) {
					if (part.getjComponentName().getLevel() > barLevel) {
						barLevel = part.getjComponentName().getLevel();
					}
				} else if (part.getBaseName().compareTo(
						ComponentNames.SPRINGJOINT) == 0) {
					if (part.getjComponentName().getLevel() > springLevel) {
						springLevel = part.getjComponentName().getLevel();
					}
				} else if (part.getBaseName().compareTo(ComponentNames.TIRE) == 0
						|| part.getBaseName().compareTo(ComponentNames.AXLE) == 0) {
					if (part.getjComponentName().getLevel() > tireLevel) {
						tireLevel = part.getjComponentName().getLevel();
					}
				}
			}

			if (barLevel > userState.smallBarLevel) {
				return CarSetErrors.PARTS_NOT_UNLOCKED;
			}

			if (springLevel > userState.springLevel) {
				return CarSetErrors.PARTS_NOT_UNLOCKED;
			}

			if (tireLevel > userState.tireLevel) {
				return CarSetErrors.PARTS_NOT_UNLOCKED;
			}
		}

		if (getCurrentGameMode() == GameMode.PLAY_CHALLENGE) {
			JSONCar opponentCar = JSONCar.objectify(getCurrentChallengeCar());

			for (JSONComponent opponentPart : opponentCar.getComponentList()) {
				if (opponentPart.getBaseName().compareTo(ComponentNames.BAR3) == 0) {
					if (opponentPart.getjComponentName().getLevel() > opponentBarLevel) {
						opponentBarLevel = opponentPart.getjComponentName()
								.getLevel();
					}
				} else if (opponentPart.getBaseName().compareTo(
						ComponentNames.SPRINGJOINT) == 0) {
					if (opponentPart.getjComponentName().getLevel() > opponentSpringLevel) {
						opponentSpringLevel = opponentPart.getjComponentName()
								.getLevel();
					}
				} else if (opponentPart.getBaseName().compareTo(
						ComponentNames.TIRE) == 0
						|| opponentPart.getBaseName().compareTo(
								ComponentNames.AXLE) == 0) {
					if (opponentPart.getjComponentName().getLevel() > opponentTireLevel) {
						opponentTireLevel = opponentPart.getjComponentName()
								.getLevel();
					}
				}
			}

			JSONCar myCar = JSONCar.objectify(currentCar);

			int currentBarLevel = 0, currentTireLevel = 0, currentSpringLevel = 0;

			for (JSONComponent myCarPart : myCar.getComponentList()) {
				if (myCarPart.getBaseName().compareTo(ComponentNames.BAR3) == 0) {
					if (myCarPart.getjComponentName().getLevel() > currentBarLevel) {
						currentBarLevel = myCarPart.getjComponentName()
								.getLevel();
					}
				} else if (myCarPart.getBaseName().compareTo(
						ComponentNames.SPRINGJOINT) == 0) {
					if (myCarPart.getjComponentName().getLevel() > currentSpringLevel) {
						currentSpringLevel = myCarPart.getjComponentName()
								.getLevel();
					}
				} else if (myCarPart.getBaseName().compareTo(
						ComponentNames.TIRE) == 0
						|| myCarPart.getBaseName().compareTo(
								ComponentNames.AXLE) == 0) {
					if (myCarPart.getjComponentName().getLevel() > currentTireLevel) {
						currentTireLevel = myCarPart.getjComponentName()
								.getLevel();
					}
				}
			}

			if (currentBarLevel > opponentBarLevel) {
				return CarSetErrors.CAR_NOT_SUTIBLE_FOR_CHALLENGE;
			}

			if (currentSpringLevel > opponentSpringLevel) {
				return CarSetErrors.CAR_NOT_SUTIBLE_FOR_CHALLENGE;
			}

			if (currentTireLevel > opponentTireLevel) {
				return CarSetErrors.CAR_NOT_SUTIBLE_FOR_CHALLENGE;
			}

		}

		if (!test) {
			prefs.putString(GamePreferences.CAR_MAP_STR, currentCar);
			prefs.flush();

			this.currentCar = currentCar;
		}

		return CarSetErrors.NONE;
	}

	public  int getOpponentBarLevel() {
		if (getCurrentGameMode() == GameMode.PLAY_CHALLENGE) {
			return opponentBarLevel;
		} else {
			return 0;
		}
	}

	public int getOpponentTireLevel() {
		if (getCurrentGameMode() == GameMode.PLAY_CHALLENGE) {
			return opponentTireLevel;
		} else {
			return 0;
		}
	}

	public int getOpponentSpringLevel() {
		if (getCurrentGameMode() == GameMode.PLAY_CHALLENGE) {
			return opponentSpringLevel;
		} else {
			return 0;
		}
	}

	public String getCurrentTrack() {
		String inputString = prefs.getString(GamePreferences.TRACK_MAP_STR,
				Globals.default_track);

		currentTrack = inputString;

		return currentTrack;
	}

	public void setCurrentTrack(String currentTrack, TrackMode mode,
			boolean lockBypass) {

		// prefs.clear();

		JSONTrack track = JSONTrack.objectify(currentTrack);
		TrackType type = track.getType();

		if (!lockBypass) {
			if (type == TrackType.FORREST) {
				if (isLocked(ItemsLookupPrefix.getForrestPrefix(Integer
						.toString(track.getItemIndex())))) {

					return;
				}
			} else if (type == TrackType.ARTIC) {
				if (isLocked(ItemsLookupPrefix.getArticPrefix(Integer
						.toString(track.getItemIndex())))) {
					return;
				}
			} else {
				return;
			}
		}

		prefs.putString(GamePreferences.TRACK_MAP_STR, currentTrack);
		prefs.putString(GamePreferences.TRACK_MODE_STR, mode.toString());
		prefs.flush();

		this.currentTrack = currentTrack;
	}

	public Integer getLevel(String partName) {

		if (partName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return getSpringLevel();
		} else if (partName.compareTo(ComponentNames.TIRE) == 0
				|| partName.compareTo(ComponentNames.AXLE) == 0) {
			return getTireLevel();
		} else if (partName.compareTo(ComponentNames.BAR3) == 0) {
			return getSmallBarLevel();
		}
		return 1;
	}

	public Integer buyItem(String itemName, Integer cash) {

		Integer moneyRequired = decrementMoney(cash);

		if (moneyRequired == -1) {
			if (itemName.compareTo(ComponentNames.BAR3) == 0) {
				incrementSmallBarLevel();
			} else if (itemName.compareTo(ComponentNames.TIRE) == 0
					|| itemName.compareTo(ComponentNames.AXLE) == 0) {
				incrementTireLevel();
			} else if (itemName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
				incrementSpringLevel();
			} else if (itemName
					.contains(ItemsLookupPrefix.getForrestPrefix(""))
					|| itemName.contains(ItemsLookupPrefix.getArticPrefix(""))) {
				Unlock(itemName);
			} else if (itemName
					.compareTo(ItemsLookupPrefix.COMMUNITY_CARS_MODE) == 0) {
				Unlock(ItemsLookupPrefix.COMMUNITY_CARS_MODE);
			} else if (itemName.contains(ItemsLookupPrefix
					.getInfiniteTrackPrefix(""))) {
				Unlock(itemName);
			} else if (itemName
					.compareTo(ItemsLookupPrefix.INFINITY_TRACK_MODE) == 0) {
				Unlock(ItemsLookupPrefix.INFINITY_TRACK_MODE);
			} else if (itemName.compareTo(ItemsLookupPrefix.ARCTIC_WORLD) == 0) {
				Unlock(ItemsLookupPrefix.ARCTIC_WORLD);
			} else if (itemName.compareTo(ItemsLookupPrefix.NO_ADS) == 0) {
				userState.ads_bought = true;
				Globals.setAds(false);
			} else {
				System.out.println("ERROR: User: Trying to buy unknown Item");
			}

			saveUserState();

			return -1;
		} else {
			return moneyRequired;
		}

	}

	public Challenge getCurrentChallenge() {
		return currentChallenge;
	}

	public JSONChallenge getCurrentJSONChallenge() {
		return currentJSONChallenge;
	}

	boolean resultsRemaining = true;
	int currentOffset = 0;

	public String getCurrentChallengeCar() {
		String inputString = prefs.getString(GamePreferences.CH_CAR_MAP_STR,
				Globals.defualt_car);
		currentChallengeCar = inputString;

		return currentChallengeCar;
	}

	public ArrayList<RecorderUnit> getCurrentChallengeRecording() {
		return this.currentChallengeSortedRecording;
	}

	public void setCurrentChallenge(final JSONChallenge JSONchallenge,
			final ChallengeLobbyScreen context) {

		this.currentChallenge = null;
		this.currentJSONChallenge = null;

		if (JSONchallenge == null)
			return;

		final Challenge challenge = Challenge.objectify(JSONchallenge
				.getChallenge());

		this.currentChallenge = challenge;
		this.currentJSONChallenge = JSONchallenge;

		this.currentChallengeSortedRecording = challenge.getRecording();

		Collections.sort(this.currentChallengeSortedRecording,
				new Comparator<RecorderUnit>() {

					@Override
					public int compare(RecorderUnit o1, RecorderUnit o2) {

						Long table1 = o1.getTime();
						Long table2 = o2.getTime();

						return table1.compareTo(table2);
					}

				});

		prefs.putString(GamePreferences.CH_CAR_MAP_STR, challenge.getCarJson()
				.jsonify());
		prefs.flush();

		this.currentChallengeCar = challenge.getCarJson().jsonify();

		final AsyncExecutor ae = new AsyncExecutor(2);

		final Semaphore stallSemaphore = new Semaphore(1);

		ae.submit(new AsyncTask<String>() {

			@Override
			public String call() {

				String database = "";
				if (challenge.getTrackMode() == TrackMode.INFINTE) {
					database = RESTPaths.INFINITE_MAPS;
				} else {
					if (challenge.getTrackType() == TrackType.ARTIC) {
						database = RESTPaths.ARCTIC_MAPS;
					} else if (challenge.getTrackType() == TrackType.FORREST) {
						database = RESTPaths.FORREST_MAPS;
					}
				}

				REST.getData(
						database
								+ RESTProperties.URL_ARG_SPLITTER
								+ RESTProperties.WhereObjectIdIs(challenge
										.getTrackObjectId()),
						new HttpResponseListener() {

							@Override
							public void handleHttpResponse(
									HttpResponse httpResponse) {

								Backendless_ParentContainer obj = null;

								obj = Backendless_JSONParser
										.processDownloadedTrack(httpResponse
												.getResultAsString());

								for (ServerDataUnit fromServer : obj.getData()) {

									final JSONTrack trackJson = JSONTrack
											.objectify(fromServer.getData());
									trackJson.setObjectId(fromServer
											.getObjectId());
									trackJson.setBestTime(fromServer
											.getTrackBestTime());
									trackJson.setDifficulty(fromServer
											.getTrackDifficulty());
									trackJson.setItemIndex(fromServer
											.getItemIndex());
									trackJson.setCreationTime(fromServer
											.getCreationTime());

									context.challengeMapLoaded(trackJson,
											challenge.getTrackMode());
									return;

								}

								if (obj.getTotalObjects() - obj.getOffset() > 0) {
									resultsRemaining = true;
								} else {
									resultsRemaining = false;
								}
								stallSemaphore.release();
								// stall = false;

							}

							@Override
							public void failed(Throwable t) {
								t.printStackTrace();
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								// return;
							}

							@Override
							public void cancelled() {
								// stallSemaphore.release();
								// stall = false;
								resultsRemaining = false;
								// return;
							}

						});

				// while (stall);
				return "";
			}
		});

		Globals.globalRunner.submit(new AsyncTask<String>() {

			@Override
			public String call() throws Exception {
				ae.dispose();
				return null;
			}

		});

	}

	public static User getInstance() {
		if (instance == null) {
			instance = new User();
		}
		return instance;
	}

	public static Integer getMaxLevel(String componentName) {

		if (componentName.compareTo(ComponentNames.BAR3) == 0) {
			return MAX_BAR3_LEVEL;
		} else if (componentName.compareTo(ComponentNames.TIRE) == 0
				|| componentName.compareTo(ComponentNames.AXLE) == 0) {
			return MAX_TIRE_LEVEL;
		} else if (componentName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return MAX_SPRING_LEVEL;
		}

		return -1;
	}

	public Integer addCoin(Integer value) {
		userState.money += value;

		if (userState.money <= 0) {
			userState.money = 0;
		}
		saveOnlyUserState();
		return userState.money;
	}

	public boolean isAdsBought() {
		return userState.ads_bought;
	}

	public Integer getSmallBarLevel() {
		if (userState.smallBarLevel <= 1) {
			return 1;
		}
		return new Integer(userState.smallBarLevel);
	}

	private void incrementSmallBarLevel() {
		userState.smallBarLevel++;
		if (userState.smallBarLevel >= MAX_BAR3_LEVEL) {
			userState.smallBarLevel = MAX_BAR3_LEVEL;
		}
		saveUserState();
	}

	public Integer getTireLevel() {
		if (userState.tireLevel <= 1) {
			return 1;
		}
		return new Integer(userState.tireLevel);
	}

	private void incrementTireLevel() {
		userState.tireLevel++;
		if (userState.tireLevel >= MAX_TIRE_LEVEL) {
			userState.tireLevel = MAX_TIRE_LEVEL;
		}
		saveUserState();
	}

	public Integer getSpringLevel() {
		if (userState.springLevel <= 1) {
			return 1;
		}
		return new Integer(userState.springLevel);
	}

	private void incrementSpringLevel() {
		userState.springLevel++;
		if (userState.springLevel >= MAX_SPRING_LEVEL) {
			userState.springLevel = MAX_SPRING_LEVEL;
		}
		saveUserState();
	}

	public Integer getMoney() {
		return userState.money;
	}

	private Integer decrementMoney(Integer value) {
		// Always returns positive difference when not enough money unless
		// purchase successful
		if (this.userState.money - value < 0) {
			return Math.abs(this.userState.money - value);
		}

		this.userState.money -= value;
		return -1;
	}

	public void setMusicPlayState(boolean playing) {
		userState.playingMusic = playing;
		saveUserState();
	}

	public boolean getMusicPlayState() {
		return userState.playingMusic;
	}

	public void setSfxPlayState(boolean playing) {
		userState.playingSfx = playing;
		saveUserState();
	}

	public boolean getSfxPlayState() {
		return userState.playingSfx;
	}

	public void saveFileMD5(String fileName, String md5) {
		prefs.putString(FILE_NAME_PREFIX_MD5 + fileName, md5);

		prefs.flush();
	}

	public String getFileMD5(String fileName) {

		String fileState = prefs.getString(FILE_NAME_PREFIX_MD5 + fileName, "");

		return fileState;
	}

	public void saveFileTimeStamp(String fileName, String timeStamp) {
		prefs.putString(FILE_NAME_PREFIX + fileName, timeStamp);

		prefs.flush();
	}

	public Long getFileTimeStamp(String fileName) {

		String fileState = prefs.getString(FILE_NAME_PREFIX + fileName, "0");

		return Long.parseLong(fileState);
	}

	public void setLastPlayedWorld(TrackType type) {

		String world = null;

		if (type == TrackType.ARTIC) {
			world = TrackType.ARTIC.toString();
		} else if (type == TrackType.FORREST) {
			world = TrackType.FORREST.toString();
		} else {
			System.out.println("Error: trying to set incorrrect last world");
		}

		prefs.putString(WORLD_PREFIX, world);
		prefs.flush();

	}

	public TrackType getLastPlayedWorld() {

		String world = prefs.getString(WORLD_PREFIX,
				TrackType.FORREST.toString());

		if (world.compareTo(TrackType.ARTIC.toString()) == 0) {
			return TrackType.ARTIC;
		} else if (world.compareTo(TrackType.FORREST.toString()) == 0) {
			return TrackType.FORREST;
		} else {
			System.out.println("Error: trying to get incorrrect last world");
		}

		return null;

	}

	public void setStars(String trackId, STARS stars) {

		STARS currentStars = getStars(trackId);

		if (currentStars.ordinal() >= stars.ordinal()) {
			return;
		}

		prefs.putString(TRACK_PREFIX + trackId, stars.toString());
		prefs.flush();

	}

	public STARS getStars(String trackId) {

		String starString = prefs.getString(TRACK_PREFIX + trackId,
				STARS.NONE.toString());

		if (starString.compareTo(STARS.NONE.toString()) == 0) {
			return STARS.NONE;
		} else if (starString.compareTo(STARS.ONE.toString()) == 0) {
			return STARS.ONE;
		} else if (starString.compareTo(STARS.TWO.toString()) == 0) {
			return STARS.TWO;
		} else if (starString.compareTo(STARS.THREE.toString()) == 0) {
			return STARS.THREE;
		}

		return STARS.NONE;

	}

	public void Lock(String id) {
		setLock(id, true);
	}

	public void Unlock(String id) {
		setLock(id, false);
	}

	private void setLock(String id, Boolean lock) {
		if (lockedItems == null) {
			lockedItems = new LookupItemsType();
		}
		lockedItems.items.put(id, lock);
		saveUserState();
	}

	public Boolean isLocked(String id) {
		if (lockedItems == null) {
			return true;
		}

		return lockedItems.items.get(id) == null ? true : lockedItems.items
				.get(id);
	}

	public void setNonNew(String id, Boolean isNew) {
		if (nonNewItems == null) {
			nonNewItems = new LookupItemsType();
		}
		nonNewItems.items.put(id, isNew);
		saveUserState();
	}

	public Boolean isNew(String id) {

		if (nonNewItems == null) {
			return true;
		}

		return nonNewItems.items.get(id) == null ? true : nonNewItems.items
				.get(id);
	}

	public TrackMode getCurrentTrackMode() {
		TrackMode ret = TrackMode.ADVENTURE;

		String inputString = prefs.getString(GamePreferences.TRACK_MODE_STR,
				TrackMode.ADVENTURE.toString());
		String currentTrackMode = inputString;

		if (currentTrackMode.compareTo(TrackMode.ADVENTURE.toString()) == 0) {
			ret = TrackMode.ADVENTURE;
		} else if (currentTrackMode.compareTo(TrackMode.INFINTE.toString()) == 0) {
			ret = TrackMode.INFINTE;
		}

		return ret;
	}

}
