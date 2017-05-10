package com.kyleduo.rabbits;

/**
 * for Rabbits
 * Created by kyleduo on 2017/5/10.
 */

public interface MappingsLoaderCallback {
    void onMappingsLoaded(MappingsGroup mappings);

    void onMappingsLoadFail();

    void onMappingsPersisted(boolean success);
}
