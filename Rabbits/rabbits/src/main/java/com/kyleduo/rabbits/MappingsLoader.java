package com.kyleduo.rabbits;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * for Module
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


    interface MappingsLoaderCallback {
        void onMappingsLoaded(MappingsGroup mappings);
    }

    /**
     * load from source in current thread.
     *
     * @param app    application
     * @param source source
     * @return MappingsGroup
     */
    MappingsGroup load(Context app, MappingsSource source) {
        try {
            if (source.getType() == MappingsSource.TYPE_DEFAULT) {
                MappingsGroup mappings = null;

                // load from assets directly
                if (shouldUseAssets(app)) {
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
                    // TODO: 2017/5/9 persist
                } else {
                    File file = findPersistFile(app);
                    if (file != null && file.exists()) {
                        InputStream is = new FileInputStream(file);
                        mappings = loadFromStream(is);
                    }
                }
                return mappings;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void loadAsync(Context app, MappingsSource source, MappingsLoaderCallback callback) {

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
        String versionData = filename.substring(PERSIST_MAPPING_FILE_PREFIX.length(), filename.length() - PERSIST_MAPPING_FILE_SUFFIX.length());
        String decoded = new String(Base64.decode(versionData.getBytes(), Base64.DEFAULT));
        // TODO: 2017/5/9 remove log
        Log.d(TAG, decoded);
        String[] metas = decoded.split("_");
        if (metas.length != 2) {
            return true;
        }

        int metaBuild = Integer.parseInt(metas[0]);

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
     * @param app      application
     * @param mappings complete and valid mappings
     */
    private synchronized void persist(Context app, MappingsGroup mappings) {

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
            return MappingsGroup.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
