package JSONifier;

import Component.ComponentNames;

import com.badlogic.gdx.utils.Json;

public class JSONComponentName {

	private String BaseName;
	private String SubName;
	private String ComponentId = null;
	private String MountId;
	private Integer Level;

	public Integer getLevel() {
		return Level;
	}

	public void setLevel(Integer level) {
		Level = level;
	}

	public String getBaseName() {
		return BaseName;
	}

	public void setBaseName(String baseName) {
		BaseName = baseName;
	}

	public String getSubName() {
		return SubName;
	}

	public void setSubName(String subName) {
		SubName = subName;
	}

	public String getComponentId() {
		return ComponentId;
	}

	public void setComponentId(String componentId) {
		ComponentId = componentId;
	}

	public String getMountId() {
		return MountId;
	}

	public void setMountId(String mountId) {
		MountId = mountId;
	}

	public String getBaseId() {
		return BaseName + "_" + ComponentId;
	}
	
	public String getMountedId() {
		if (BaseName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return BaseName + "_" + SubName + "_" + ComponentId + "_" + MountId;
		} else {
			return BaseName + "_" + ComponentId + "_" + MountId;
		}

	}

	public String getId() {
		if (BaseName.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return BaseName + "_" + SubName + "_" + ComponentId;
		} else {
			return BaseName + "_" + ComponentId;
		}

	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub

		JSONComponentName other = (JSONComponentName) obj;

		if ((other.getId().compareTo(this.getId()) == 0)) {
			return true;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		Json json = new Json();
		return json.toJson(this);
	}

}
