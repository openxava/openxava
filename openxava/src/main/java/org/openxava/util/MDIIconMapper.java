package org.openxava.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Maps legacy Material Design Icons (pre v7) names to current ones (v7+)
 * so existing controllers.xml from applications keep working without changes.
 *
 * Mapping is loaded from classpath resource "/xava-mdi-icon-mapping.properties".
 * Each line must follow the format: old-name=new-name
 *
 * Applications can override or extend this file by adding a resource with the
 * same path in their own classpath (it will shadow the core one).
 *
 * If no mapping is found for a given icon, the original name is returned.
 *
 * @author Javier Paniza / Cascade
 * @since 7.5
 */
public final class MdiIconMapper {

    private static volatile Map<String, String> MAP;

    private MdiIconMapper() {
    }

    /**
     * Returns the mapped icon name for the given legacy icon, or the same value if
     * there is no mapping. Accepts null and returns null.
     */
    public static String map(String icon) {
        if (Is.empty(icon)) return icon;
        ensureLoaded();
        String trimmed = icon.trim();
        String mapped = MAP.get(trimmed);
        return Is.empty(mapped) ? trimmed : mapped;
    }

    private static void ensureLoaded() {
        if (MAP != null) return;
        synchronized (MdiIconMapper.class) {
            if (MAP != null) return;
            MAP = loadMapping();
        }
    }

    private static Map<String, String> loadMapping() {
        // Load from classpath so applications can override by providing their own resource
        String resource = "/xava-mdi-icon-mapping.properties";
        try (InputStream in = MdiIconMapper.class.getResourceAsStream(resource)) {
            if (in == null) {
                return Collections.emptyMap();
            }
            Properties props = new Properties();
            props.load(in);
            Map<String, String> m = new HashMap<>();
            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                if (!Is.empty(key) && !Is.empty(value)) {
                    m.put(key.trim(), value.trim());
                }
            }
            return m;
        } catch (Exception ex) {
            // Fail safe: if mapping cannot be loaded, behave as identity mapping
            return Collections.emptyMap();
        }
    }
}
