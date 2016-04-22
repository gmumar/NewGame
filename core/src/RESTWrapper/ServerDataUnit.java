package RESTWrapper;

public class ServerDataUnit {

	String data;
	String objectId;

	public ServerDataUnit(String actualDataStr, String objectId2) {
		data = actualDataStr;
		objectId = objectId2;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	
	
}
