/*
 * Copyright (C) 2019 Michael Joyce
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

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

    /**
     * Build an empty collection.
     */
    public TextCollection() {
        logger = LoggerFactory.getLogger(this.getClass());
        collection = new HashMap<>();
    }

    /**
     * Add a document to the collection.
     *
     * @param id document identifier
     * @param content document content
     */
    public void put(String id, String content) {
        if(collection.containsKey(id)) {
            logger.error("Duplicate ID in text collection: {}", id);
        }
        collection.put(id, content);
    }

    /**
     * Get the content of a document from the collection.
     *
     * @param id document identifier
     * @return the text content or null if the document does not exist in the collection.
     */
    public String get(String id) {
        return collection.get(id);
    }

    /**
     * Get a list of IDs in the collection, suitable for iterating over. Multiple calls to
     * keys() may return the keys in different orders.
     *
     * @return array of document IDs
     */
    public String[] keys() {
        return collection.keySet().toArray(new String[size()]);
    }

    /**
     * Report the size fo the collection.
     *
     * @return the number of documents added to the collection
     */
    public int size() {
        return collection.size();
    }

}
