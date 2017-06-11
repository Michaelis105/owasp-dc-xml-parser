package com.owaspdcxmlp;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import com.owaspdcxmlp.XMLParser;

public class Main {

    private static void usage() {
        System.out.println("Usage: <directory-containing-OWASPDC-XML-files> <absolute-path-to-json> <report type: JSON|XML|CSV>");
    }

    /**
     * Extracts files from directory dir
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
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            usage();
            throw new IllegalArgumentException("Need exactly three parameters, see usage.");
        }

        ReportGenerator.setupFileWriter(args[1]);
        XMLParser.setupParser();

        File[] xmlList = extractFilesFromDirectory(args[0]);
        Collection<Dependency> uniqueDeps = XMLParser.parseXMLS(xmlList);
        ReportGenerator.generateReport(uniqueDeps, args[2]);
    }
}
