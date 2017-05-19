package com.kyleduo.rabbits;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Mappings Loader
 *
 * Created by kyleduo on 2017/5/9.
 */

class MappingsLoader {
    private static final String TAG = "MappingsLoader";
    private static final String PERSIST_MAPPING_FILE_PREFIX = "rabbits_mappings_";
    private static final String PERSIST_MAPPING_FILE_SUFFIX = ".rmappings";
    private static final String PERSIST_MAPPING_TEMP_FILE = "rabbits_mappings_temp.json";
    private static final String MAPPING_ASSETS_NAME = "mappings.json";
    private static final String MAPPING_ASSETS_PREFIX = "mappings_";
    private static final String MAPPING_ASSETS_SUFFIX = ".json";

    /**
     * Flag for async loading. Just one loading operation is permitted in one time.
     */
    private boolean mIsLoading;
    /**
     * Flag for persisting.
     */
    private boolean mIsPersisting;

    /**
     * Load from source in current thread.
     *
     * @param context            context
     * @param source             source
     * @param forceUpdatePersist whether should update persist ignoring version code.
     * @return MappingsGroup
     */
    MappingsGroup load(Context context, MappingsSource source, boolean forceUpdatePersist) {
        final Context app = context.getApplicationContext();
        int sourceType = source.getType();
        MappingsGroup origin = source.getOriginMappings();
        MappingsGroup loaded = null;
        // Always persist to disk besides just load from it.
        boolean persist = true;

        switch (sourceType) {
            case MappingsSource.TYPE_DEFAULT:
                // load from assets directly
                if (!forceUpdatePersist && !shouldUseAssets(app)) {
                    File file = findPersistFile(app);
                    loaded = loadFromFile(file);
                    persist = false;
                }
                if (loaded == null) {
                    loaded = loadFromAssets(app);
                    persist = true;
                }
                break;
            case MappingsSource.TYPE_ASSETS:
                loaded = loadFromAssets(app);
                break;
            case MappingsSource.TYPE_FILE:
                File file = new File(source.getValue());
                loaded = loadFromFile(file);
                break;
            case MappingsSource.TYPE_JSON:
                loaded = MappingsLoader.parseJson(source.getValue());
                break;
            default:
                throw new IllegalArgumentException("Bad MappingsSource type");
        }

        MappingsGroup result;

        if (origin != null) {
            result = new MappingsGroup(origin);
            result.merge(loaded, source.shouldFullyUpdate());
        } else {
            result = new MappingsGroup(loaded);
        }

        if (persist && result.valid()) {
            persist(app, result, null);
        }

        return result;
    }

    /**
     * Load from source in child thread.
     *
     * @param context            context
     * @param source             source
     * @param forceUpdatePersist whether should update persist ignoring version code.
     * @param callback           callback
     */
    synchronized void loadAsync(final Context context, final MappingsSource source, final boolean forceUpdatePersist, final MappingsLoaderCallback callback) {
        final Context app = context.getApplicationContext();
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        new AsyncTask<Void, Void, MappingsGroup>() {
            @Override
            protected MappingsGroup doInBackground(Void... voids) {
                return load(app, source, forceUpdatePersist);
            }

            @Override
            protected void onPostExecute(MappingsGroup mappings) {
                mIsLoading = false;
                if (callback != null) {
                    if (mappings != null) {
                        callback.onMappingsLoaded(mappings);
                    } else {
                        callback.onMappingsLoadFail();
                    }
                }
            }

            @Override
            protected void onCancelled() {
                mIsLoading = false;
                if (callback != null) {
                    callback.onMappingsLoadFail();
                }
            }
        }.execute();
    }

    private MappingsGroup loadFromFile(File file) {
        MappingsGroup mappings = null;
        try {
            if (file != null && file.exists()) {
                InputStream is = new FileInputStream(file);
                mappings = loadFromStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mappings;
    }

    private MappingsGroup loadFromAssets(Context app) {
        MappingsGroup mappings = null;
        try {
            String[] files = app.getAssets().list("");
            ArrayList<String> wanted = new ArrayList<>();
            for (String name : files) {
                if (name.equals(MAPPING_ASSETS_NAME) || (name.startsWith(MAPPING_ASSETS_PREFIX) &&
                        name.endsWith(MAPPING_ASSETS_SUFFIX))) {
                    wanted.add(name);
                }
            }
            Collections.sort(wanted);
            for (String name : wanted) {
                InputStream is = app.getAssets().open(name);
                MappingsGroup part = loadFromStream(is);
                if (mappings == null) {
                    mappings = part;
                } else {
                    mappings.merge(part, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mappings;
    }

    /**
     * persist dir
     *
     * @param app application
     * @return persist dir
     */
    private static File getPersistDir(Context app) {
        return app.getFilesDir();
    }

    private File findPersistFile(Context app) {
        File dir = getPersistDir(app);
        String[] names = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(PERSIST_MAPPING_FILE_PREFIX) && name.endsWith(PERSIST_MAPPING_FILE_SUFFIX);
            }
        });
        if (names.length == 0) {
            return null;
        }
        if (names.length > 1) {
            for (int i = 1; i < names.length; i++) {
                File file = new File(dir, names[i]);
                boolean delete = file.delete();
                if (!delete) {
                    Log.e(TAG, "Failed to delete file: " + file);
                }
            }
        }
        return new File(dir, names[0]);
    }

    private File currentBuildPersistFile(Context app) {
        File dir = getPersistDir(app);
        int currentBuildNumber = 1;
        try {
            PackageInfo packageInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            currentBuildNumber = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return new File(dir, PERSIST_MAPPING_FILE_PREFIX + currentBuildNumber + PERSIST_MAPPING_FILE_SUFFIX);
    }

    /**
     * Whether mappings from assets is newer than persist. This is happened when persisted mappings
     * was deleted or App is just updated (judge through {@link android.content.pm.PackageInfo#versionCode}).
     *
     * @return true if Assets is newer
     */
    private boolean shouldUseAssets(Context app) {
        File persistFile = findPersistFile(app);
        if (persistFile == null || !persistFile.exists()) {
            return true;
        }
        String filename = persistFile.getName();
        String buildNumber = filename.substring(PERSIST_MAPPING_FILE_PREFIX.length(), filename.length() - PERSIST_MAPPING_FILE_SUFFIX.length());
        if (buildNumber.length() == 0) {
            buildNumber = "1";
        }

        int metaBuild = Integer.parseInt(buildNumber);

        int currentBuildNumber;
        try {
            PackageInfo packageInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            currentBuildNumber = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return true;
        }

        return currentBuildNumber > metaBuild;
    }

    /**
     * remove persist file
     */
    private void clearPersist(Context app) {
        File persistFile = findPersistFile(app);
        if (persistFile != null && persistFile.exists()) {
            boolean delete = persistFile.delete();
            if (!delete) {
                Log.e(TAG, "Failed to delete persist file.");
            }
        }
    }

    /**
     * Persist {@param mappings} to disk.
     *
     * @param context  application
     * @param mappings complete and valid mappings
     */
    private synchronized void persist(Context context, final MappingsGroup mappings, final MappingsLoaderCallback callback) {
        if (mIsPersisting) {
            return;
        }
        mIsPersisting = true;
        final Context app = context;
        new AsyncTask<Void, Void, Boolean>() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            protected Boolean doInBackground(Void... params) {
                String json = mappings.toJson();
                if (json == null) {
                    cancel(true);
                    return false;
                }

                File filesDir = app.getFilesDir();
                if (filesDir == null || !filesDir.exists()) {
                    cancel(true);
                    return false;
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
                    return false;
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
                    return false;
                }

                clearPersist(app);
                boolean ret = file.renameTo(currentBuildPersistFile(app));
                if (!ret) {
                    file.delete();
                    cancel(true);
                    throw new IllegalStateException("Can not rename mapping file.");
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                mIsPersisting = false;
                if (callback != null) {
                    callback.onMappingsPersisted(success);
                }
            }

            @Override
            protected void onCancelled() {
                mIsPersisting = false;
                if (callback != null) {
                    callback.onMappingsPersisted(false);
                }
            }
        }.execute();
    }

    /**
     * Load MappingsGroup from a single InputStream whether from an Assets file or a common File.
     *
     * @param is input stream
     * @return MappingsGroup
     */
    private static MappingsGroup loadFromStream(InputStream is) {
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
            return parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static MappingsGroup parseJson(String json) {
        return MappingsGroup.fromJson(json);
    }
}
