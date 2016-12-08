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
	private static final String MAPPING_FILE = "mappings.json";
	private static final String MAPPING_KEY_VERSION = "version";
	private static final String MAPPING_KEY_MAPPINGS = "mappings";

	static final String MAPPING_QUERY_MODE = "rabbitsMode";

	static final String MODE_CLEAR_TOP = "clearTop";
	static final String MODE_NEW_TASK = "newTask";


	private static Map<String, String> sMAPPING = new HashMap<>();
	private static int sVERSION = 0;

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
		//noinspection TryWithIdenticalCatches
		try {
			InputStream is = context.getAssets().open(MAPPING_FILE);
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
			sVERSION = jo.optInt(MAPPING_KEY_VERSION);
			JSONObject mappings = jo.optJSONObject(MAPPING_KEY_MAPPINGS);
			Iterator<String> uris = mappings.keys();
			while (uris.hasNext()) {
				String uri = uris.next();
				String page = mappings.optString(uri);
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

	static int version() {
		return sVERSION;
	}
}
