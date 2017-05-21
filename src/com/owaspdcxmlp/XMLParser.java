package com.owaspdcxmlp;

import java.io.File;
import java.util.HashMap;

public class XMLParser {

    private static void usage() {
        // TODO: Actually write out the usage.
        System.out.println();
    }

    private static void generateJSON(String[] args) {

        // TODO: There will be other arguments to check for.
        if (args.length == 0) {
            usage();
            throw new IllegalArgumentException("Need at least one parameter, see usage.");
        }

        // TODO: Iterate and process arguments.
        /*
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {

            }
        }*/

        File[] xmlList = extractFilesFromDirectory(args[0]);
        if (null == xmlList || xmlList.length == 0) {
            throw new IllegalArgumentException("Directory " + args[0] + "is empty!");
        }

        // String = Dependency Name
        // Only store/access unique dependency objects of all dependencies identified.
        HashMap<String, Dependency> dependencies = new HashMap<String, Dependency>();

        for (File xml : xmlList) {
            parseXML(xml);
            // TODO: Return list of dependencies, identify if unique or existing dependency according to dependencies hash map.
        }

        // TODO: Generate report if parameter exists.
        ReportGenerator.generateReport();
    }

    private static void parseXML(File xml) {
        throw new UnsupportedOperationException("Unimplemented method!");
    }

    /**
     * Extracts files from directory dir
     * @param dir Absolute path to a directory
     * @return List of files
     */
    private static File[] extractFilesFromDirectory(String dir) {
        if (StringUtil.isNull(dir)) {
            throw new IllegalArgumentException("Parameter 'dir' null or empty");
        }
        File srcDir = new File(dir);
        if (!srcDir.exists()) {
            throw new IllegalArgumentException("File " + dir + "does not exist!");
        } else if (!srcDir.isDirectory()) {
            throw new IllegalArgumentException("File " + dir + "is not a directory!");
        }

        File[] xmlList = srcDir.listFiles();
        if (null == xmlList) {
            throw new IllegalArgumentException("Directory " + dir + "is empty!");
        }
        return xmlList;
    }

    public static void main(String[] args) {
        try {
            generateJSON(args);
        } catch (Exception e) {
            // TODO: Handle exception.
        }
    }
}
