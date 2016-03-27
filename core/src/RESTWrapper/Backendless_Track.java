package RESTWrapper;


public class Backendless_Track extends Backendless_Parent {//implements Json.Serializable {
/*
	private int totalObjects;
	private int offset;
	private ArrayList<String> data = new ArrayList<String>();

	@Override
	public void write(Json json) {

	}

	@Override
	public void read(Json json, JsonValue jsonData) {

		totalObjects = jsonData.getInt("totalObjects");
		offset = jsonData.getInt("offset");

		JsonValue localData = jsonData.get("data");
		
		JsonIterator iter = localData.iterator();
		while (iter.hasNext()) {
			JsonValue item = iter.next();
			String b = item.getString(RESTProperties.TRACK_POINTS_JSON, "UTF-8");
			//byte[] bytes = Base64Coder.decode(b);//.decodeBase64(b);
			String track = null;

			//System.out.println(b);
			
			try {
				track = Gzip.decompress(b);
			} catch (IOException e) {
				e.printStackTrace();
			}

			data.add(track);
		}

	}

	public int getTotalObjects() {
		return totalObjects;
	}
	
	public int getOffset() {
		return offset;
	}

	public ArrayList<String> getData() {
		return data;
	}
	
	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public void setData(ArrayList<String> data) {
		this.data = data;
		
	}

	@Override
	public void setTotalObjects(int totalObjs) {
		this.totalObjects = totalObjs;
	}*/

}
