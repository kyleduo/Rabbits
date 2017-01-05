package com.kyleduo.rabbits;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Router mapping.
 * <p>
 * Created by kyle on 2016/12/7.
 */

class Mappings {
	private static final String TAG = "Rabbits.Mappings";

	private static final String MAPPING_FILE = "mappings.json";
	private static final String MAPPING_KEY_VERSION = "version";
	private static final String MAPPING_KEY_FORCE_OVERRIDE = "force_override";
	private static final String MAPPING_KEY_MAPPINGS = "mappings";

	private static final String PERSIST_MAPPING_FILE = "rabbits_mappings.json";
	private static final String PERSIST_MAPPING_TEMP_FILE = "rabbits_mappings_temp.json";

	private static final String MAPPING_QUERY_MODE = "rabbitsMode";
	private static final String MODE_CLEAR_TOP = "clearTop";
	private static final String MODE_NEW_TASK = "newTask";

	private static final String MAPPING_QUERY_FREE = "rabbitsFree";

	private static boolean sPersisting;

	private static Map<String, String> sMAPPING = new LinkedHashMap<>();
	private static int sVERSION = 0;

	/**
	 * Load mappings to memory.
	 *
	 * @param context  context
	 * @param async    whether the operation run in work thread
	 * @param callback callback used for async task.
	 */
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

	/**
	 * Update mappings using a file.
	 *
	 * @param context context
	 * @param file    file
	 */
	static void update(Context context, File file) {
		final Context app = context.getApplicationContext();
		try {
			InputStream is = new FileInputStream(file);
			String json = doLoad(is);
			if (json != null) {
				Log.d(TAG, "Update success from file, start persisting.");
				persist(app, json);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update mappings using json string.
	 *
	 * @param context context
	 * @param json    json
	 */
	static void update(Context context, String json) {
		final Context app = context.getApplicationContext();
		String ret = parse(json);
		if (ret != null) {
			Log.d(TAG, "Update success, start persisting.");
			persist(app, ret);
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
			return parse(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String parse(String json) {
		try {
			JSONObject jo = new JSONObject(json);
			int version = jo.optInt(MAPPING_KEY_VERSION);
			int forceOverride = jo.optInt(MAPPING_KEY_FORCE_OVERRIDE);
			if (version <= sVERSION && forceOverride == 0) {
				Log.d("Rabbits.Mappings", "No need to update, already has the latest version: " + sVERSION);
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
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
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
					Set<Map.Entry<String, String>> entries = sMAPPING.entrySet();
					for (Map.Entry<String, String> entry : entries) {
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

	static Target match(Uri uri) {
		Uri.Builder builder = uri.buildUpon();
		if (uri.getScheme() == null) {
			builder.scheme(Rabbit.sAppScheme);
		}
		if (uri.getHost() == null) {
			builder.authority(Rabbit.sDefaultHost);
		}
		uri = builder.build();
		String pureUri;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			pureUri = builder.query(null).build().toString();
		} else {
			pureUri = builder.clearQuery().build().toString();
		}

		// Try to completely match.
		String page = sMAPPING.get(pureUri);
		Bundle bundle = null;
		if (page == null) {
			// Deep match.
			bundle = new Bundle();
			page = deepMatch(pureUri, bundle);
		}
		bundle = parseParams(uri, bundle);
		String queryFree = bundle.getString(MAPPING_QUERY_FREE);
		final boolean free = queryFree != null && queryFree.equals("1");
		if (page != null && !free) {
			// Match.
			Target target = new Target(uri);
			target.setPage(page);
			target.setExtras(bundle);
			target.setFlags(parseFlags(uri));
			return target;
		} else {
			Target target = new Target(uri);
			target.setExtras(bundle);
			return target;
		}
	}

	/**
	 * Deep checking whether a match uri exist with support for REST uri.
	 * <p>
	 * To support REST uri, witch may contains params in path, using "{key:type}"
	 * format representing a params.
	 * <p>
	 * Either KEY or TYPE part should contains none blank characters and TYPE part just support
	 * these ones (case matters):
	 * <pre>
	 * 		"i" : an int params.
	 * 		"l" : a long params.
	 * 		"d" : a double params.
	 * 		"B" : a boolean params.
	 * 		"s" : a String params.
	 * </pre>
	 *
	 * @param pureUri uri need to be matched, without queries.
	 * @param bundle  bundle object used to store params of REST uri.
	 * @return page name when found a match.
	 */
	private static String deepMatch(String pureUri, Bundle bundle) {
		Set<String> uris = sMAPPING.keySet();
		UriLoop:
		for (String uri : uris) {
			// Check match for each uri.
			String[] template = uri.split("(://|/)");
			String[] source = pureUri.split("(://|/)");
			if (template.length != source.length) {
				continue;
			}
			// Compare each part, parse params.
			for (int i = 0; i < source.length; i++) {
				if (template[i].equals(source[i])) {
					continue;
				}
				// Check whether a param field.
				if (template[i].matches("\\{\\S+(:\\S+)?\\}")) {
					String[] tt = template[i].substring(1, template[i].length() - 1).split(":");
					String key = tt[0];
					String value = decode(source[i]);
					String type = tt.length == 2 ? tt[1].toLowerCase() : "";
					switch (type) {
						case "i":
							try {
								bundle.putInt(key, Integer.parseInt(value));
							} catch (NumberFormatException e) {
								continue UriLoop;
							}
							break;
						case "l":
							try {
								bundle.putLong(key, Long.parseLong(value));
							} catch (NumberFormatException e) {
								continue UriLoop;
							}
							break;
						case "d":
							try {
								bundle.putDouble(key, Double.parseDouble(value));
							} catch (NumberFormatException e) {
								continue UriLoop;
							}
							break;
						case "b":
							try {
								bundle.putBoolean(key, Boolean.parseBoolean(value.toLowerCase()));
							} catch (NumberFormatException e) {
								continue UriLoop;
							}
							break;
						case "s":
							bundle.putString(key, value);
							break;
						default:
							bundle.putString(key, value);
							break;
					}
					continue;
				}
				continue UriLoop;
			}
			return sMAPPING.get(uri);
		}
		return null;
	}

	/**
	 * Fetch params for uri.
	 *
	 * @param uri    target uri.
	 * @param bundle bundle object where the params should be stored.
	 * @return A bundle object witch store the params.
	 */
	private static Bundle parseParams(Uri uri, Bundle bundle) {
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(Rabbit.KEY_ORIGIN_URI, uri.toString());
		Set<String> keys;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			keys = uri.getQueryParameterNames();
		} else {
			keys = getQueryParameterNames(uri);
		}
		if (keys == null || keys.size() == 0) {
			return bundle;
		}
		for (String key : keys) {
			String params = uri.getQueryParameter(key);
			bundle.putString(key, params);
		}
		return bundle;
	}

	/**
	 * Fetch keys from an Uri object. The code is from SDK 25.
	 *
	 * @param uri the Uri
	 * @return A set of keys.
	 */
	private static Set<String> getQueryParameterNames(Uri uri) {
		if (uri.isOpaque()) {
			throw new UnsupportedOperationException("This isn't a hierarchical URI.");
		}

		String query = uri.getEncodedQuery();
		if (query == null) {
			return Collections.emptySet();
		}

		Set<String> names = new LinkedHashSet<String>();
		int start = 0;
		do {
			int next = query.indexOf('&', start);
			int end = (next == -1) ? query.length() : next;

			int separator = query.indexOf('=', start);
			if (separator > end || separator == -1) {
				separator = end;
			}

			String name = query.substring(start, separator);
			names.add(decode(name));

			// Move start to end of name.
			start = end + 1;
		} while (start < query.length());

		return Collections.unmodifiableSet(names);
	}

	/**
	 * Fetch flags from uri.
	 *
	 * @param uri target uri.
	 * @return flags fetched from uri.
	 */
	private static int parseFlags(Uri uri) {
		String mode = uri.getQueryParameter(Mappings.MAPPING_QUERY_MODE);
		int flags = 0;
		if (TextUtils.isEmpty(mode)) {
			flags = 0;
		} else {
			if (mode.contains(Mappings.MODE_CLEAR_TOP)) {
				flags |= Intent.FLAG_ACTIVITY_CLEAR_TOP;
			}
			if (mode.contains(Mappings.MODE_NEW_TASK)) {
				flags |= Intent.FLAG_ACTIVITY_NEW_TASK;
			}
		}
		return flags;
	}

	static String dump() {
		StringBuilder sb = new StringBuilder();
		Set<Map.Entry<String, String>> entries = sMAPPING.entrySet();
		sb.append("{").append("\n");
		for (Map.Entry<String, String> e : entries) {
			sb.append(e.getKey()).append(" -> ").append(decode(e.getValue())).append("\n\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");
		return sb.toString();
	}

	private static String decode(String origin) {
		String out;
		try {
			out = URLDecoder.decode(origin, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			out = origin;
		}
		return out;
	}
}
