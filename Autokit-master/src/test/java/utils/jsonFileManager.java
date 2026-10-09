package utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class jsonFileManager {
    private static final Logger log = LogManager.getLogger(jsonFileManager.class);
    private static final Type TYPE = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();

    private final LinkedHashMap<String, Object> data;

    /**
     * Loads the JSON file at the given path into memory.
     *
     * @param jsonPath the path to the JSON file
     */
    public jsonFileManager(String jsonPath) {
        if (jsonPath == null || jsonPath.isEmpty()) {
            log.error("❌ JSON path is null or empty");
            throw new IllegalArgumentException("JSON path must be provided");
        }
        log.info("📄 Loading JSON from [{}]", jsonPath);
        try (FileReader reader = new FileReader(jsonPath)) {
            LinkedHashMap<String, Object> loaded = new Gson().fromJson(reader, TYPE);
            data = loaded == null ? new LinkedHashMap<>() : loaded;
        } catch (IOException e) {
            log.error("❌ Could not read JSON file [{}]", jsonPath, e);
            throw new UncheckedIOException("Could not read JSON file: " + jsonPath, e);
        } catch (JsonParseException e) {
            log.error("❌ Invalid JSON in [{}]", jsonPath, e);
            throw e;
        }
        log.debug("✅ Loaded {} top-level keys from [{}]", data.size(), jsonPath);
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key the key to search for
     * @return the corresponding value, or null if the key is not found
     */
    public Object getValueByKey(String key) {
        if (!data.containsKey(key)) {
            log.warn("⚠️ Key '{}' not found in JSON", key);
            return null;
        }
        Object value = data.get(key);
        log.debug("🔑 Value of key '{}': {}", key, value);
        return value;
    }

    /**
     * Retrieves the value under the key as a map of key-value pairs.
     *
     * @param key the key to search for
     * @return a LinkedHashMap of the value, or null if the key is missing, empty, or not a map
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Object> getKeyAndValueByKey(String key) {
        if (key == null || key.isEmpty()) {
            log.warn("⚠️ Key is null or empty");
            return null;
        }
        if (!data.containsKey(key)) {
            log.warn("⚠️ Key '{}' not found in JSON", key);
            return null;
        }
        Object value = data.get(key);
        if (!(value instanceof Map)) {
            log.warn("⚠️ Value of key '{}' is not a map: {}", key, value);
            return null;
        }
        Map<String, Object> rawMap = (Map<String, Object>) value;
        if (rawMap.isEmpty()) {
            log.warn("⚠️ Map under key '{}' is empty", key);
            return null;
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<>(rawMap);
        log.debug("🗺️ Map under key '{}' has {} entries", key, result.size());
        return result;
    }

    /**
     * Retrieves the value under the key as a list. Elements are converted to strings.
     *
     * @param key the key to search for
     * @return a list of values, or null if the key is missing, empty, or not a list
     */
    public List<String> getValueListByKey(String key) {
        if (key == null || key.isEmpty()) {
            log.warn("⚠️ Key is null or empty");
            return null;
        }
        if (!data.containsKey(key)) {
            log.warn("⚠️ Key '{}' not found in JSON", key);
            return null;
        }
        Object value = data.get(key);
        if (!(value instanceof List)) {
            log.warn("⚠️ Value of key '{}' is not a list: {}", key, value);
            return null;
        }
        List<String> result = new ArrayList<>();
        for (Object item : (List<?>) value) {
            result.add(String.valueOf(item));
        }
        log.debug("📋 List under key '{}' has {} items", key, result.size());
        return result;
    }

    /**
     * Retrieves all keys that contain the given prefix, ignoring case and whitespace.
     *
     * @param keyPrefix the text to match
     * @return the matching keys, or null if the prefix is empty or nothing matches
     */
    public List<String> getKeys(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isEmpty()) {
            log.warn("⚠️ Key prefix is null or empty");
            return null;
        }
        String needle = normalize(keyPrefix);
        List<String> keys = new ArrayList<>();
        for (String key : data.keySet()) {
            if (normalize(key).contains(needle)) {
                keys.add(key);
            }
        }
        if (keys.isEmpty()) {
            log.warn("⚠️ No keys match prefix '{}'", keyPrefix);
            return null;
        }
        log.debug("🔍 Keys matching '{}': {}", keyPrefix, keys);
        return keys;
    }

    /**
     * Retrieves all keys from the JSON data.
     *
     * @return a list of all keys, or null if the JSON has no keys
     */
    public List<String> getKeys() {
        if (data.isEmpty()) {
            log.warn("⚠️ JSON contains no keys");
            return null;
        }
        List<String> keys = new ArrayList<>(data.keySet());
        log.debug("🗝️ Retrieved {} keys", keys.size());
        return keys;
    }

    /**
     * Retrieves all values whose text contains the given prefix, ignoring case and whitespace.
     *
     * @param valuePrefix the text to match
     * @return the matching values, or null if the prefix is empty or nothing matches
     */
    public List<Object> getValues(String valuePrefix) {
        if (valuePrefix == null || valuePrefix.isEmpty()) {
            log.warn("⚠️ Value prefix is null or empty");
            return null;
        }
        String needle = normalize(valuePrefix);
        List<Object> values = new ArrayList<>();
        for (Object value : data.values()) {
            if (value != null && normalize(value.toString()).contains(needle)) {
                values.add(value);
            }
        }
        if (values.isEmpty()) {
            log.warn("⚠️ No values match '{}'", valuePrefix);
            return null;
        }
        log.debug("🔍 Values matching '{}': {}", valuePrefix, values);
        return values;
    }

    /**
     * Retrieves all values from the JSON data.
     *
     * @return a list of all values, or null if the JSON has no entries
     */
    public List<Object> getValues() {
        if (data.isEmpty()) {
            log.warn("⚠️ JSON contains no values");
            return null;
        }
        List<Object> values = new ArrayList<>(data.values());
        log.debug("📦 Retrieved {} values", values.size());
        return values;
    }

    private String normalize(String text) {
        return text.toLowerCase().replaceAll("\\s+", "");
    }
}