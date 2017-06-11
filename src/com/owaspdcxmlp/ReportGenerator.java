package com.owaspdcxmlp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class ReportGenerator {

    private static FileWriter fw;

    /**
     * Generate dependency check report in JSON format.
     * @param deps List of unique dependencies.
     */
    public static void writeJSON (Collection<Dependency> deps) {
        throw new UnsupportedOperationException("Unimplemented method!");
    }

    /**
     * Generate dependency check report in XML format.
     * @param deps List of unique dependencies.
     */
    public static void writeXML (Collection<Dependency> deps) {
        throw new UnsupportedOperationException("Unimplemented method!");
    }

    /**
     * Generate dependency check report in CSV format.
     * @param deps List of unique dependencies.
     */
    public static void writeCSV (Collection<Dependency> deps) {
        throw new UnsupportedOperationException("Unimplemented method!");
    }


    public static void generateReport(Collection<Dependency> deps, String type) {
        switch(type) {
            case "json":
                writeJSON(deps);
                break;
            case "xml":
                writeXML(deps);
                break;
            case "csv":
                writeCSV(deps);
                break;
            default:
                throw new IllegalArgumentException("Could not determine report type!");
        }
    }

    public static void setupFileWriter(String path) throws IOException {
        try {
            fw = new FileWriter(path ,false);
        } catch (IOException e) {
            throw new IOException("Error creating file writer for report at " + path + "!");
        }
    }
}
