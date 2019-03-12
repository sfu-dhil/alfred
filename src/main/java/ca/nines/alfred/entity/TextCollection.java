package ca.nines.alfred.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A text collection is a map of IDs to text. It can be paragraph or document
 * ids.
 */
public class TextCollection {

    private final Logger logger;

    private Map<String, String> collection;

    public TextCollection() {
        logger = LoggerFactory.getLogger(this.getClass());
        collection = new HashMap<>();
    }

    public void put(String id, String content) {
        if(collection.containsKey(id)) {
            logger.error("Duplicate ID in text collection: {}", id);
        }
        collection.put(id, content);
    }

    public String get(String id) {
        return collection.get(id);
    }

    public String[] keys() {
        return collection.keySet().toArray(new String[size()]);
    }

    public int size() {
        return collection.size();
    }

}
