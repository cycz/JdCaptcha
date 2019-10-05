package util;

import java.util.Map;

public class CookiesUtil {
    public static void update(Map<String, String> sourceMap, Map<String, String> updateMap) {
        if (sourceMap == null || updateMap == null || updateMap.size() == 0) {
            return;
        }
        for (String key : updateMap.keySet()) {
            sourceMap.put(key, updateMap.get(key));
        }
    }
}
