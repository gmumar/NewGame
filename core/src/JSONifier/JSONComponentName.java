package JSONifier;

import Component.ComponentNames;

public class JSONComponentName {

	private String BaseName;
	private String SubName;
	private String ComponentId = null;
	private String MountId;

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
		return "JSONComponentName [BaseName=" + BaseName + ", SubName="
				+ SubName + ", ComponentId=" + ComponentId + ", MountId="
				+ MountId + "]";
	}

}
