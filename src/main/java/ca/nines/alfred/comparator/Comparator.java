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

package ca.nines.alfred.comparator;

import ca.nines.alfred.entity.TextCollection;
import ca.nines.alfred.main.Settings;
import org.atteo.classindex.ClassIndex;
import org.atteo.classindex.IndexSubclasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

/**
 * Parent class for all comparators.
 */
@IndexSubclasses
abstract public class Comparator {

    protected Settings settings;

    protected final Logger logger;

    static final Map<String, Class<? extends Comparator>> comparatorList = new TreeMap<>();

    /**
     * Collection of text documents.
     */
    protected final TextCollection collection;

    /**
     * Optional stop word file to filter words during matching.
     */
    protected final String stopWordsFile;

    public static final Map<String, Class<? extends Comparator>> getComparatorList() {
        if(comparatorList.isEmpty()) {
            for(Class<? extends Comparator> cls : ClassIndex.getSubclasses(Comparator.class)) {
                if(Modifier.isAbstract(cls.getModifiers())) {
                    continue;
                }
                ComparatorInfo props = cls.getAnnotation(ComparatorInfo.class);
                if(props == null) {
                    continue;
                }
                comparatorList.put(props.name(), cls);
            }
        }
        return comparatorList;
    }

    public static final Comparator getComparator(String name, TextCollection collection, String stopWordsFile) throws Exception {
        Map<String, Class<? extends Comparator>> list = getComparatorList();
        if( ! list.containsKey(name)) {
            throw new RuntimeException("Unknown comparator " + name);
        }
        return list.get(name).getConstructor(TextCollection.class, String.class).newInstance(collection, stopWordsFile);
    }

    public static final String describe(String name) {
        Map<String, Class<? extends Comparator>> list = getComparatorList();
        if( ! list.containsKey(name)) {
            throw new RuntimeException("Unknown comparator " + name);
        }
        Class<? extends Comparator> cls = list.get(name);
        if(cls == null) {
            throw new RuntimeException("Unknown comparator " + name);
        }
        ComparatorInfo props = cls.getAnnotation(ComparatorInfo.class);
        if(props == null) {
            throw new RuntimeException("Unknown comparator " + name);
        }
        return props.description();
    }

    /**
     * Set the collection and optional stopWordsFile
     *
     * @param collection collection of text documents
     * @param stopWordsFile name of a stop words file to use.
     */
    public Comparator(TextCollection collection, String stopWordsFile) {
        settings = Settings.getInstance();
        logger = LoggerFactory.getLogger(this.getClass());
        this.collection = collection;
        this.stopWordsFile = stopWordsFile;
    }

    /**
     * Get a short name for the comparison, eg. "lev", "cos", "vsm".
     * @return a short name
     */
    abstract public String getType();

    /**
     * Fetch two documents from the text collection and compare them, returning the result.
     *
     * @param aId First document ID
     * @param bId Second document ID
     * @return a percentage match of the documents
     */
    abstract public double compare(String aId, String bId);
}
