package com.kyleduo.rabbits;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
	private static final String MAPPING_KEY_ALLOWED_HOSTS = "allowed_hosts";

	private static final String PERSIST_MAPPING_FILE = "rabbits_mappings.json";
	private static final String PERSIST_MAPPING_TEMP_FILE = "rabbits_mappings_temp.json";

	private static final String MAPPING_QUERY_MODE = "rabbitsMode";
	private static final String MODE_CLEAR_TOP = "clearTop";
	private static final String MODE_NEW_TASK = "newTask";

	private static final String PARAM_KEY_INT = "i";
	private static final String PARAM_KEY_LONG = "l";
	private static final String PARAM_KEY_FLOAT = "f";
	private static final String PARAM_KEY_DOUBLE = "d";
	private static final String PARAM_KEY_BOOLEAN = "b";
	private static final String PARAM_KEY_STRING = "s";

	private static final String MAPPING_QUERY_FREE = "rabbitsFree";

	private static boolean sPersisting;

	private static Map<String, String> sMAPPING = new LinkedHashMap<>();
	private static List<String> sALLOWED_HOSTS;
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
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... voids) {
					return load(app);
				}

				@Override
				protected void onPostExecute(Boolean json) {
					if (callback != null) {
						callback.run();
					}
					if (json != null) {
						persist(context);
					}
				}
			}.execute();
		} else {
			boolean success = load(app);
			if (success) {
				persist(context);
			}
		}
	}

	/**
	 * Set other hosts which can be replacement of the origin.
	 *
	 * @param hosts hosts
	 */
	static void setALLOWED_HOSTS(String... hosts) {
		if (hosts == null || hosts.length == 0) {
			sALLOWED_HOSTS = null;
			return;
		}
		sALLOWED_HOSTS = Arrays.asList(hosts);
	}

	/**
	 * Update mappings using a file.
	 *
	 * @param context  context
	 * @param file     file
	 * @param override override current if true
	 */
	static void update(Context context, File file, boolean override) {
		final Context app = context.getApplicationContext();
		try {
			InputStream is = new FileInputStream(file);
			String json = doLoad(is);
			if (json != null) {
				parse(json, override);
				Log.d(TAG, "Update success from file, start persisting.");
				persist(app);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update mappings using json string.
	 *
	 * @param context  context
	 * @param json     json
	 * @param override override current if true
	 */
	static void update(Context context, String json, boolean override) {
		final Context app = context.getApplicationContext();
		boolean ret = parse(json, override);
		if (ret) {
			Log.d(TAG, "Update success, start persisting.");
			persist(app);
		}
	}

	/**
	 * Load mapping from persist file or assert to memory.
	 *
	 * @param context Application context.
	 * @return Success or not.
	 */
	private static boolean load(Context context) {
		try {
			File filesDir = context.getFilesDir();
			File file = new File(filesDir, PERSIST_MAPPING_FILE);
			String json;
			if (file.exists()) {
				InputStream is = new FileInputStream(file);
				json = doLoad(is);
				if (json != null) {
					parse(json, false);
				}
			}
			InputStream is = context.getAssets().open(MAPPING_FILE);
			json = doLoad(is);
			if (json != null) {
				parse(json, false);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Load json string from {@param is}.
	 *
	 * @param is Input stream from assert or file.
	 * @return Json string.
	 */
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
			return jsonBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Parse json string to mapping.
	 *
	 * @param json          Origin json.
	 * @param clearPrevious whether override current mapping
	 * @return true for latest mapping has been loaded or there is already a latest version.
	 * false for load failure.
	 */
	private synchronized static boolean parse(String json, boolean clearPrevious) {
		try {
			JSONObject jo = new JSONObject(json);
			int version = jo.optInt(MAPPING_KEY_VERSION);
			boolean forceOverride = jo.optInt(MAPPING_KEY_FORCE_OVERRIDE) == 1;
			if (version < sVERSION || (version == sVERSION && !forceOverride)) {
				Log.d(TAG, "No need to update, already has the latest version: " + sVERSION);
				return true;
			}
			JSONArray allowed_hosts = jo.optJSONArray(MAPPING_KEY_ALLOWED_HOSTS);
			if (allowed_hosts != null && allowed_hosts.length() > 0) {
				sALLOWED_HOSTS = new ArrayList<>();
				for (int i = 0; i < allowed_hosts.length(); i++) {
					sALLOWED_HOSTS.add(allowed_hosts.optString(i));
				}
			}
			Map<String, String> temp = new LinkedHashMap<>();
			JSONObject mappings = jo.optJSONObject(MAPPING_KEY_MAPPINGS);
			Iterator<String> uris = mappings.keys();
			while (uris.hasNext()) {
				String uri = uris.next();
				String page = mappings.optString(uri);
				temp.put(uri, page);
			}
			if (clearPrevious || forceOverride) {
				sMAPPING.clear();
			}
			sMAPPING.putAll(temp);
			sVERSION = version;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Persist mapping to local file.
	 *
	 * @param context Application context.
	 */
	private synchronized static void persist(final Context context) {
		if (sPersisting) {
			return;
		}
		sPersisting = true;

		new AsyncTask<Void, Void, Void>() {
			@SuppressWarnings("ResultOfMethodCallIgnored")
			@Override
			protected Void doInBackground(Void... params) {
				String json = null;
				if (sMAPPING.size() > 0) {
					try {
						JSONObject wrapper = new JSONObject();
						if (sALLOWED_HOSTS != null && sALLOWED_HOSTS.size() == 0) {
							JSONArray allowed = new JSONArray();
							for (String h : sALLOWED_HOSTS) {
								allowed.put(h);
							}
							wrapper.put(MAPPING_KEY_ALLOWED_HOSTS, allowed);
						}
						JSONObject mappings = new JSONObject();
						Set<Map.Entry<String, String>> entries = sMAPPING.entrySet();
						for (Map.Entry<String, String> entry : entries) {
							mappings.put(entry.getKey(), entry.getValue());
						}
						wrapper.put(MAPPING_KEY_MAPPINGS, mappings);
						wrapper.put(MAPPING_KEY_VERSION, sVERSION);
						json = wrapper.toString();
					} catch (JSONException e) {
						e.printStackTrace();
						throw new IllegalStateException("mapping can not be parsed to json", e);
					}
				}
				if (json == null) {
					cancel(true);
					return null;
				}

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
					writer.write(json);
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
		}.execute();
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
		Uri pureUri;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			pureUri = builder.query(null).build();
		} else {
			pureUri = builder.clearQuery().build();
		}

		// Try to completely match.
		String page = sMAPPING.get(pureUri.toString());
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
	private static String deepMatch(Uri pureUri, Bundle bundle) {
		Set<String> uris = sMAPPING.keySet();
		String[] source = pureUri.toString().split("(://|/)");
		UriLoop:
		for (String uri : uris) {
			// Check match for each uri.
			String[] template = uri.split("(://|/)");
			if (!template[0].equals(source[0]) || template.length != source.length) {
				continue;
			}
			if (!template[1].equals(source[1]) && (sALLOWED_HOSTS == null || !sALLOWED_HOSTS.contains(source[1]))) {
				continue;
			}
			// Compare each part, parse params.
			for (int i = 2; i < source.length; i++) {
				String s = source[i];
				String t = template[i];
				if (t.equals(s)) {
					continue;
				}
				// Check whether a param field.
				if (t.matches("\\{\\S+(:\\S+)?\\}")) {
					try {
						formatParam(t, s, bundle);
					} catch (NumberFormatException e) {
						continue UriLoop;
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
	 * format param and put it in bundle.
	 *
	 * @param t      template segment
	 * @param param  param value
	 * @param bundle bundle
	 * @throws NumberFormatException
	 */
	private static void formatParam(String t, String param, Bundle bundle) throws NumberFormatException {
		String[] tt = t.substring(1, t.length() - 1).split(":");
		String key = tt[0];
		String value = decode(param);
		String type = tt.length == 2 ? tt[1].toLowerCase() : "";
		switch (type) {
			case PARAM_KEY_INT:
				bundle.putInt(key, Integer.parseInt(value));
				break;
			case PARAM_KEY_LONG:
				bundle.putLong(key, Long.parseLong(value));
				break;
			case PARAM_KEY_FLOAT:
				bundle.putFloat(key, Float.parseFloat(value));
				break;
			case PARAM_KEY_DOUBLE:
				bundle.putDouble(key, Double.parseDouble(value));
				break;
			case PARAM_KEY_BOOLEAN:
				bundle.putBoolean(key, Boolean.parseBoolean(value));
				break;
			case PARAM_KEY_STRING:
			default:
				bundle.putString(key, value);
				break;
		}
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

		Set<String> names = new LinkedHashSet<>();
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
		sb.append("mappings : ");
		sb.append("{").append("\n");
		for (Map.Entry<String, String> e : entries) {
			sb.append(e.getKey()).append(" -> ").append(decode(e.getValue())).append("\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");
		if (sALLOWED_HOSTS != null && sALLOWED_HOSTS.size() > 0) {
			sb.append("\n\n").append("allowed hosts : ");
			sb.append("{").append("\n");
			for (String h : sALLOWED_HOSTS) {
				sb.append("\t").append(h).append("\n\n");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("}");
		}
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
