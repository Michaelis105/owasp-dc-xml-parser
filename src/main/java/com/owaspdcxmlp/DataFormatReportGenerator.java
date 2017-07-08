package main.java.com.owaspdcxmlp;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class DataFormatReportGenerator {

    private static FileWriter fw;
    private static String delimiter = "|";

    /**
     * Generate data report in JSON format.
     * @param deps List of unique dependencies
     */
    private static void createJSON (Collection<Dependency> deps) throws IOException {
        Gson gson = new Gson();
        if (null != deps) {
            for (Dependency d : deps) {
                try {
                    fw.write(gson.toJson(d));
                } catch (IOException e) {
                    throw new IOException("Error writing dependency to JSON for dependency " + d.getName() + "!");
                }
            }
        }
    }

    /**
     * Generate data report in XML format.
     * @param deps List of unique dependencies
     */
    private static void createXML (Collection<Dependency> deps) {
        throw new UnsupportedOperationException("Unimplemented method!");
    }

    /**
     * Generate data report in CSV format.
     * @param deps List of unique dependencies
     */
    private static void createCSV (Collection<Dependency> deps) throws IOException {
        if (null != deps) {
            writeCSV(createCSVHeader());
            fw.write("\n");
            for (Dependency d : deps) {
                writeCSV(d.getName());
                writeCSV(String.valueOf(d.getSeverity()));
                // TODO: Check for other fields that could/should be committed to the report.
                for (String vulName : d.getVulnerabilities()) {
                    fw.write(vulName + delimiter);
                }
                fw.write("\n");
            }
        }
    }

    /**
     * Creates CSV header based on specific OWASP fields.
     * @return CSV header.
     */
    private static String createCSVHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dependency Name");
        sb.append(delimiter);
        sb.append("Vulnerability Severity");
        sb.append(delimiter);
        // TODO: Check for other fields that could/should be committed to the report.
        sb.append("CVE Name");
        return sb.toString();
    }

    /**
     * Writes dependency info to CSV.
     * @param s String to write to CSV.
     * @throws IOException
     */
    private static void writeCSV(String s) throws IOException {
        try {
            if (!StringUtil.isNull(s)) {
                fw.write(s);
            }
            fw.write(delimiter);
        } catch (IOException e) {
            throw new IOException("Issue writing string or delimiter to file writer!");
        }
    }

    /**
     * Generates report in data format. Usually the intermediate form for generating human-readable report.
     * @param deps collection of dependencies
     * @param type data format type
     */
    public static void generateDataFormatReport(Collection<Dependency> deps, String type) throws IOException {
        switch(type) {
            case "json":
                createJSON(deps);
                break;
            case "xml":
                createXML(deps);
                break;
            case "csv":
                createCSV(deps);
                break;
            default:
                throw new IllegalArgumentException("Could not determine report type!");
        }
    }

    /**
     * Initializes single file writer.
     * @param path absolute path to file
     * @throws IOException
     */
    public static void setupFileWriter(String path) throws IOException {
        if (StringUtil.isNull(path)) {
            throw new IllegalArgumentException("Parameter 'path' null or empty");
        }
        try {
            fw = new FileWriter(path ,false);
        } catch (IOException e) {
            throw new IOException("Error creating file writer for report at " + path + "!");
        }
    }

    /**
     * Closes file writer
     * @throws IOException
     */
    public static void closeFileWriter() throws IOException {
        try {
            fw.close();
        } catch (IOException e) {
            throw new IOException("Error closing file writer!");
        }
    }
}
