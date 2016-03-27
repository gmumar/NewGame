package JSONifier;

import Component.ComponentNames;

import com.google.gson.Gson;

public class JSONComponentName {

	private String B;//BaseName;
	private String S;//SubName;
	private String C;//ComponentId = null;
	private String M;//MountId;
	private Integer L;//Level;

	public Integer getLevel() {
		return L;
	}

	public void setLevel(Integer level) {
		L = level;
	}

	public String getBaseName() {
		return B;
	}

	public void setBaseName(String baseName) {
		B = baseName;
	}

	public String getSubName() {
		return S;
	}

	public void setSubName(String subName) {
		S = subName;
	}

	public String getComponentId() {
		return C;
	}

	public void setComponentId(String componentId) {
		C = componentId;
	}

	public String getMountId() {
		return M;
	}

	public void setMountId(String mountId) {
		M = mountId;
	}

	public String getBaseId() {
		return B + "_" + C;
	}
	
	public String getMountedId() {
		if (B.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return B + "_" + S + "_" + C + "_" + M;
		} else {
			return B + "_" + C + "_" + M;
		}

	}

	public String getId() {
		if (B.compareTo(ComponentNames.SPRINGJOINT) == 0) {
			return B + "_" + S + "_" + C;
		} else {
			return B + "_" + C;
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
		Gson json = new Gson();
		return json.toJson(this);
	}

}
