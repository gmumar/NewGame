package RESTWrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.google.gson.Gson;

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
	private final static String SERVICE_URL = "http://api.backendless.com/v1/services/climberService/1.0.0/";

	public static final int PAGE_SIZE = 5;

	private static HttpRequest getRequest(String path, String method) {
		HttpRequest request = new HttpRequest(method);
		request.setUrl(URL + "/" + path);
		request.setHeader(SECRET_KEY, SECRET_KEY_VALUE);
		request.setHeader(APP_ID, APP_ID_VALUE);
		request.setHeader(APP_TYPE, APP_TYPE_VALUE);
		if (method != HttpMethods.DELETE) {
			request.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		}
		request.setTimeOut(10000);

		return request;
	}

	class sendRequest implements AsyncTask<String> {
		/*
		 * HttpRequest request; HttpResponseListener listener; public
		 * sendRequest(HttpRequest request , HttpResponseListener listener) {
		 * this.request = request; this.listener = listener; }
		 */

		@Override
		public String call() throws Exception {
			// Gdx.net.sendHttpRequest(request, listener);
			return null;
		}

	}

	public static HttpRequest postData(String path,
			HashMap<String, String> parameters, HttpResponseListener listener) {
		HttpRequest request = getRequest(path, HttpMethods.POST);
		Gson json = new Gson();
		// json.toJson(parameters);
		request.setContent(json.toJson(parameters));
		// (Backendless_JsonifyParameters(parameters));

		Gdx.net.sendHttpRequest(request, listener);

		return request;
	}

	public static HttpRequest getData(String path,
			final HttpResponseListener listener) {
		final HttpRequest request = getRequest(path, HttpMethods.GET);
		// request.setContent("");

		Gdx.net.sendHttpRequest(request, listener);

		return request;

	}

	public static HttpRequest deleteEntry(String path, String objectId,
			final HttpResponseListener listener) {
		final HttpRequest request = getRequest(path + "/" + objectId,
				HttpMethods.DELETE);
		request.setContent("");

		Gdx.net.sendHttpRequest(request, listener);

		return request;

	}

	public static HttpRequest updateEntry(String path, String objectId,
			HashMap<String, String> parameters,
			final HttpResponseListener listener) {
		final HttpRequest request = getRequest(path + "/" + objectId,
				HttpMethods.PUT);
		Gson json = new Gson();
		request.setContent(json.toJson(parameters));
		Gdx.net.sendHttpRequest(request, listener);

		return request;

	}

	public static HttpRequest customFunctionCallGET(String function,
			String argumentName, String argument, HttpResponseListener listener) {

		HttpRequest request = new HttpRequest(HttpMethods.GET);

		try {
			request.setUrl(SERVICE_URL + function + "?" + argumentName + "="
					+ URLEncoder.encode("\"" + argument + "\"", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setHeader(SECRET_KEY, SECRET_KEY_VALUE);
		request.setHeader(APP_ID, APP_ID_VALUE);
		request.setHeader(APP_TYPE, APP_TYPE_VALUE);
		request.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		request.setHeader("Accept", CONTENT_TYPE_VALUE);
		request.setTimeOut(10000);

		// Gson json = new Gson();
		// request.setContent(json.toJson(parameters));

		Gdx.net.sendHttpRequest(request, listener);

		return request;
	}
	
	public static HttpRequest customFunctionCallPOST(String function,
			HashMap<String, String> parameters , HttpResponseListener listener) {

		HttpRequest request = new HttpRequest(HttpMethods.POST);
		
		Gson json = new Gson();
		request.setUrl(SERVICE_URL + function);
		
		request.setContent(json.toJson(parameters));

		request.setHeader(SECRET_KEY, SECRET_KEY_VALUE);
		request.setHeader(APP_ID, APP_ID_VALUE);
		request.setHeader(APP_TYPE, APP_TYPE_VALUE);
		request.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		request.setHeader("Accept", CONTENT_TYPE_VALUE);
		request.setTimeOut(10000);

		Gdx.net.sendHttpRequest(request, listener);

		return request;
	}

	/*
	 * private static String Backendless_JsonifyParameters(HashMap<String,
	 * String> parameters) {
	 * 
	 * Set<Entry<String, String>> data = parameters.entrySet();
	 * 
	 * Iterator<Entry<String, String>> iter = data.iterator();
	 * 
	 * boolean skip = true; String ret="", key="", value=""; byte[]
	 * compressedValue = null;
	 * 
	 * ret = "{"; while (iter.hasNext()) { Entry<String, String> item =
	 * iter.next();
	 * 
	 * 
	 * if (!skip) { ret += ","; skip = false; }
	 * 
	 * try {
	 * 
	 * compressedValue = Gzip.compress(item.getValue()); //compressedValue =
	 * DatatypeConverter.parseBase64Binary(URLEncoder.encode(item.getValue(),
	 * "UTF-8")); key = "\"" + URLEncoder.encode(item.getKey(), "UTF-8") + "\"";
	 * value = "\"" + new String(Base64Coder.encode(compressedValue)) +
	 * "\"";//in).encodeBase64 } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * 
	 * ret += key + ":" + value; } ret += "}";
	 * 
	 * 
	 * return ret;
	 * 
	 * }
	 */

}
