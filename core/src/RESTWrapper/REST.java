package RESTWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponseListener;

public class REST {

	private final static String APP_ID = "application-id";
	private final static String APP_ID_VALUE = "85B126A8-223C-6F79-FF4B-4C6C4CAACB00";

	private final static String SECRET_KEY = "secret-key";
	private final static String SECRET_KEY_VALUE = "8A69FB86-67BA-731C-FF40-599A05AD0600";

	private final static String APP_TYPE = "application-type";
	private final static String APP_TYPE_VALUE = "REST";

	private final static String CONTENT_TYPE = "Content-Type";
	private final static String CONTENT_TYPE_VALUE = "application/json";

	private final static String URL = "https://api.backendless.com/v1/data";
	
	public enum REST_PATHS {
		cars
	}

	public static void postData(String path,
			HashMap<String, String> parameters, HttpResponseListener listener) {
		HttpRequest request = getRequest(path, HttpMethods.POST);
		request.setContent(JsonifyParameters(parameters));

		Gdx.net.sendHttpRequest(request, listener);
	}

	public static void getData(String path, HttpResponseListener listener) {
		HttpRequest request = getRequest(path, HttpMethods.GET);
		
		Gdx.net.sendHttpRequest(request, listener);
	}

	private static HttpRequest getRequest(String path, String method) {
		HttpRequest request = new HttpRequest(method);
		request.setUrl(URL + "/" + path);
		request.setHeader(SECRET_KEY, SECRET_KEY_VALUE);
		request.setHeader(APP_ID, APP_ID_VALUE);
		request.setHeader(APP_TYPE, APP_TYPE_VALUE);
		request.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		request.setTimeOut(10000);

		return request;
	}

	private static String JsonifyParameters(HashMap<String, String> parameters) {

		Set<Entry<String, String>> data = parameters.entrySet();

		Iterator<Entry<String, String>> iter = data.iterator();

		boolean skip = true;
		String ret, key, value;

		ret = "{";
		while (iter.hasNext()) {
			Entry<String, String> item = iter.next();
			if (!skip) {
				ret += ",";
				skip = false;
			}
			key = "\"" + item.getKey() + "\"";
			value = "\"" + item.getValue() + "\"";

			ret += key + ":" + value;
		}
		ret += "}";

		return ret;

	}

}
