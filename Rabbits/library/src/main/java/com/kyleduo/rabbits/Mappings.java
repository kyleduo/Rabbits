package com.kyleduo.rabbits;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Router mapping.
 * <p>
 * Created by kyle on 2016/12/7.
 */

class Mappings {
	private static Map<String, String> sMAPPING = new HashMap<>();

	static void setup(Context context, boolean async, final Runnable callback) {
		final Context app = context.getApplicationContext();
		if (async) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... voids) {
					load(app);
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					if (callback != null) {
						callback.run();
					}
				}
			}.execute();
		} else {
			load(app);
		}
	}

	private static void load(Context context) {
		try {
			InputStream is = context.getAssets().open("mappings.json");
			StringBuilder jsonBuilder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			while (line != null) {
				jsonBuilder.append(line);
				line = reader.readLine();
			}
			reader.close();
			String json = jsonBuilder.toString();
			JSONObject jo = new JSONObject(json);
			JSONObject carrots = jo.optJSONObject("mappings");
			Iterator<String> keysIterator = carrots.keys();
			while (keysIterator.hasNext()) {
				String uri = keysIterator.next();
				String page = carrots.optString(uri);
				sMAPPING.put(uri, page);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	static String match(String uri) {
		return sMAPPING.get(uri);
	}

}
