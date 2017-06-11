package com.owaspdcxmlp;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class XMLParser {

    HashMap<String, Dependency> dependencies;

    /**
     *
     * @param xmlList
     * @return
     */
    public static Collection<Dependency> parseXMLS(File[] xmlList) {
        // String = Dependency Name
        // Only store/access unique dependency objects of all dependencies identified.
        HashMap<String, Dependency> dependencies = new HashMap<String, Dependency>();

        for (File xml : xmlList) {
            Collection<Dependency> deps = parseXML(xml);
            if (null == deps) continue;
            for (Object o : deps.toArray()) {
                Dependency d = (Dependency) o;
                if (null == d) continue;
                dependencies.put(d.name, d);
            }

            // TODO: Algorithm improvement,
            // TODO: Return list of dependencies, identify if unique or existing dependency according to dependencies hash map.
        }
        return null;

    }

    private static Collection<Dependency> parseXML(File xml) {
        throw new UnsupportedOperationException("Unimplemented method!");
    }

    /**
     *
     */
    public static void setupParser() {
        // String = Dependency Name
        // Only store/access unique dependency objects of all dependencies identified.
        HashMap<String, Dependency> dependencies = new HashMap<String, Dependency>();
    }

}
