package com.kyleduo.rabbits;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Router mapping.
 * <p>
 * Created by kyle on 2016/12/7.
 */

class Mappings {
	private static final String MAPPING_FILE = "mappings.json";
	private static final String MAPPING_KEY_VERSION = "version";
	private static final String MAPPING_KEY_MAPPINGS = "mappings";

	private static final String PERSIST_MAPPING_FILE = "rabbits_mappings.json";
	private static final String PERSIST_MAPPING_TEMP_FILE = "rabbits_mappings_temp.json";

	static final String MAPPING_QUERY_MODE = "rabbitsMode";

	static final String MODE_CLEAR_TOP = "clearTop";
	static final String MODE_NEW_TASK = "newTask";

	private static boolean sPersisting;

	private static Map<String, String> sMAPPING = new HashMap<>();
	private static int sVERSION = 0;

	static void setup(final Context context, boolean async, final Runnable callback) {
		final Context app = context.getApplicationContext();
		if (async) {
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... voids) {
					return load(app);
				}

				@Override
				protected void onPostExecute(String json) {
					if (json != null) {
						persist(context, json);
					}
					if (callback != null) {
						callback.run();
					}
				}
			}.execute();
		} else {
			String json = load(app);
			if (json != null) {
				persist(context, json);
			}
		}
	}

	private static String load(Context context) {
		try {
			File filesDir = context.getFilesDir();
			File file = new File(filesDir, PERSIST_MAPPING_FILE);
			String json = null;
			if (file.exists()) {
				InputStream is = new FileInputStream(file);
				json = doLoad(is);
			}
			InputStream is = context.getAssets().open(MAPPING_FILE);
			String json2 = doLoad(is);
			if (json2 != null) {
				return json2;
			} else if (json != null) {
				return json;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String doLoad(InputStream is) {
		//noinspection TryWithIdenticalCatches
		try {
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
			int version = jo.optInt(MAPPING_KEY_VERSION);
			if (version <= sVERSION) {
				return null;
			}
			sVERSION = version;
			JSONObject mappings = jo.optJSONObject(MAPPING_KEY_MAPPINGS);
			Iterator<String> uris = mappings.keys();
			while (uris.hasNext()) {
				String uri = uris.next();
				String page = mappings.optString(uri);
				sMAPPING.put(uri, page);
			}

			return json;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private synchronized static void persist(final Context context, String json) {
		if (sPersisting) {
			return;
		}
		sPersisting = true;

		if (json == null) {
			if (sMAPPING.size() > 0) {
				try {
					JSONObject mappings = new JSONObject();
					Set<Map.Entry<String, String>> entrys = sMAPPING.entrySet();
					for (Map.Entry<String, String> entry : entrys) {
						mappings.put(entry.getKey(), entry.getValue());
					}
					JSONObject wrapper = new JSONObject();
					wrapper.put(MAPPING_KEY_MAPPINGS, mappings);
					wrapper.put(MAPPING_KEY_VERSION, sVERSION);
					json = wrapper.toString();
				} catch (JSONException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("mapping can not parse to json", e);
				}
			}
		}

		new AsyncTask<String, Void, Void>() {

			@SuppressWarnings("ResultOfMethodCallIgnored")
			@Override
			protected Void doInBackground(String... strings) {
				File filesDir = context.getFilesDir();
				if (filesDir == null || !filesDir.exists()) {
					cancel(true);
					return null;
				}
				File file = new File(filesDir, PERSIST_MAPPING_TEMP_FILE);
				if (file.exists()) {
					file.delete();
				}
				try {
					boolean ret = file.createNewFile();
					if (!ret) {
						throw new IllegalStateException("Can not create mapping file.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					cancel(true);
					return null;
				}

				//noinspection TryWithIdenticalCatches
				try {
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
					writer.write(strings[0]);
					writer.flush();
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
					cancel(true);
					return null;
				}

				File originFile = new File(filesDir, PERSIST_MAPPING_FILE);
				if (originFile.exists()) {
					originFile.delete();
				}
				boolean ret = file.renameTo(originFile);
				if (!ret) {
					file.delete();
					cancel(true);
					throw new IllegalStateException("Can not rename mapping file.");
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				sPersisting = false;
			}

			@Override
			protected void onCancelled() {
				sPersisting = false;
			}
		}.execute(json);


	}

	static String match(String uri) {
		return sMAPPING.get(uri);
	}

	static int version() {
		return sVERSION;
	}
}
