package com.smash.player.Helpers;

import android.os.*;
import org.json.*;
import android.net.*;

public class LyricGetAsync extends AsyncTask< String , Void , JSONObject> {
	String uri;
	JSONParser.OnJSONResultListener onListener;

	public LyricGetAsync(String name, String artist,JSONParser.OnJSONResultListener o){
		uri = Uri .parse("http://api.vagalume.com.br/search.php")
			.buildUpon()
			.appendQueryParameter("art", artist)
			.appendQueryParameter("mus", name)
			.appendQueryParameter("apikey","8b2b31c682a4e7b359b38ed76d7e75a6")
			.build().toString();
			this.onListener=o;
	}
	@Override
	protected JSONObject doInBackground(String... params)
	{
		JSONParser jParser = new JSONParser();
		return jParser.getJSONFromUrl(uri);
	}
	@Override
	protected void onPostExecute(JSONObject json)
	{
		super .onPostExecute(json);
		try
		{
			onListener.onGet(json.getJSONArray("mus"));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}

