package com.smash.player.Helpers;

import android.util.*;
import java.io.*;
import java.security.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.*;
import org.apache.http.conn.scheme.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.*;
import org.apache.http.params.*;
import org.json.*;

public class JSONParser 
{
	static InputStream is = null ;
	static JSONObject jObj = null ;
	static String json = "";
	HttpClient httpClient = createHttpClient();
	public JSONParser() {
	}
	public static HttpClient createHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore .getDefaultType());
			trustStore .load( null , null );
//            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
			HttpConnectionParams . setConnectionTimeout(params, 15000 );
			HttpConnectionParams . setSoTimeout(params, 5000 );
			SchemeRegistry registry = new SchemeRegistry();
			registry .register(new Scheme( "http" , PlainSocketFactory .getSocketFactory(), 80 ));
//            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
	public JSONObject getJSONFromUrl (String url ) {
		try {
//DefaultHttpClient httpClient = new DefaultHttpClient();
			Log.i("JsonParser",url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient. execute(httpGet);
			HttpEntity httpEntity = httpResponse .getEntity();
			is = httpEntity .getContent();
		} catch (UnsupportedEncodingException e) {
			e .printStackTrace();
		} catch (ClientProtocolException e) {
			e .printStackTrace();
		} catch (IOException e) {
			e .printStackTrace();
		}
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(
														   is, "iso-8859-1" ), 8 );
			StringBuilder sb = new StringBuilder();
			String line = null ;
			while ((line = reader .readLine()) != null ) {
				sb.append(line + "n" );
			}
			is .close();
			json = sb. toString();
		} catch (Exception e) {
			Log .e( "Buffer Error" , "Error converting result " + e. toString());
		}
		try {
			jObj = new JSONObject(json);
			Log.e("JsonParser",jObj.toString());
		} catch (JSONException e) {
			Log .e( "JSON Parser" , "Error parsing data " + e .toString());
		}
		return jObj;
	}
	public static interface OnJSONResultListener{
		public void onGet(JSONArray array);
	}
}
