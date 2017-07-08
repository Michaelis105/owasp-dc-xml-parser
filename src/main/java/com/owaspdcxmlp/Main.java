package main.java.com.owaspdcxmlp;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Main {

    private static void usage() {
        System.out.println("Usage: <directory-containing-OWASPDC-XML-files> <absolute-path-to-output-data-report> <report type: json|xml|csv> [absolute-path-to-output-HTML-report] [absolute-path-to-jinjava-template]");
    }

    /**
     * Extracts files from directory dir.
     * @param dir Absolute path to a directory
     * @return List of files
     */
    private static File[] extractFilesFromDirectory(String dir) throws IOException {
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
            throw new IOException("Error attempting to retrieve files from directory");
        }
        if (xmlList.length == 0) {
            throw new IllegalArgumentException("Directory " + dir + "is empty!");
        }
        return xmlList;
    }

    /**
     * Main calls methods to setup the report generator and XML parser, extract the XML files from arg directory, parses
     * XMLs fire dependencies, and writes dependency entries to a file.
     * @param args Parameters specified in usage()
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 3 || args.length > 5) {
            usage();
            throw new IllegalArgumentException("See usage");
        }

        // Set up
        DataFormatReportGenerator.setupFileWriter(args[1]);
        XMLParser.setupParser();

        File[] xmlList = extractFilesFromDirectory(args[0]);
        Collection<Dependency> uniqueDeps = XMLParser.parseXMLS(xmlList);
        DataFormatReportGenerator.generateDataFormatReport(uniqueDeps, args[2]);
        if (args.length == 5) {
            HTMLReportGenerator.generateHTMLReport(args[1], args[3], args[4], "|");
        }

        // Tear Down
        DataFormatReportGenerator.closeFileWriter();
    }
}
