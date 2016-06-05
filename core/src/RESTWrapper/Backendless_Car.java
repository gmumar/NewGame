package RESTWrapper;


public class Backendless_Car extends Backendless_ParentContainer {

	//private int totalObjects;
	//private int offset;
	//private ArrayList<String> data = new ArrayList<String>();
	
	/*@Override
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
			String b = item.getString(RESTProperties.CAR_JSON, "UTF-8");
			//byte[] bytes = Base64Coder.decode(b);//.decodeBase64(b);
			String car = null;

			
			try {
				car = Gzip.decompress(b);
			} catch (IOException e) {
				e.printStackTrace();
			}

			data.add(car);
		}

	}*/

	/*public int getTotalObjects() {
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
	}

	@Override
	public Backendless_Car deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		JsonObject obj = json.getAsJsonObject();
		
		totalObjects = obj.get("totalObjects").getAsInt();
		offset = obj.get("offset").getAsInt();
		
		JsonArray localData = obj.get("data").getAsJsonArray();
		
		Iterator<JsonElement> iter = localData.iterator();
		while (iter.hasNext()) {
			JsonObject item = iter.next().getAsJsonObject();
			String car = item.get(RESTProperties.CAR_JSON).getAsString();//(RESTProperties.CAR_JSON, "UTF-8");
			
			data.add(car);
		}
		
		return this;
	}*/

}
