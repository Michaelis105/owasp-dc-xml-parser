package main.java.com.owaspdcxmlp;

import com.hubspot.jinjava.Jinjava;
import com.google.

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class HTMLReportGenerator {

    private final static String NVD_CVE_URL = "https://nvd.nist.gov/vuln/detail";
    private final static String DEFAULT_REPORT_PREFIX_NAME = "report";
    private final static String DEFAULT_REPORT_EXT = ".html";
    private final static String FS = File.separator;

    /**
     * Generates OWASP-DC HTML report from CSV file.
     * @param pathToHTML absolute path to output directory where HTML file will be generated
     * @param pathToCSV absolute path to CSV file (preferably one created by OWASPDCXMLP)
     * @param pathToJJTemplate absolute path to jinjava template
     * @param delimiter delimiter used to separate values in CSV
     */
    public static void generateHTMLReport(String pathToHTML, String pathToCSV,
                                          String pathToJJTemplate, String delimiter) throws IOException {
        if (StringUtil.isNull(delimiter)) {
            throw new IllegalArgumentException("Delimiter was null or empty!");
        }

        // Use default path of running jar's directory if path unspecified.
        if (StringUtil.isNull(pathToHTML)) {
            try {
                pathToHTML = URLDecoder.decode(HTMLReportGenerator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+"-reports", "UTF-8"));
            } catch (URISyntaxException e) {
                throw new IOException("Error decoding default path to HTML");
            }
        }

        // Create HTML directory if it does not exist.
        File reportsDir = new File(pathToHTML);
        if (!reportsDir.exists()) {
            if(!reportsDir.mkdir()) {
                throw new IOException("Failed to make directory " + reportsDir.getAbsolutePath());
            }
        }

        // CSV Checks
        if (StringUtil.isNull(pathToCSV)) {
            throw new IllegalArgumentException("pathToCSV was null or empty!");
        }

        File csv = new File(pathToCSV);
        if (!csv.exists() || csv.length() <= 0) {
            throw new IllegalArgumentException(pathToCSV + "does not exist!");
        }

        exportResources(reportsDir.getCanonicalPath());

        String curDate = new SimpleDateFormat("MMddyy-HHmmss").format(new Date());
        File newReport = new File(pathToHTML+ FS + DEFAULT_REPORT_PREFIX_NAME + "-" + curDate + DEFAULT_REPORT_EXT);
        if (!newReport.exists()) newReport.createNewFile();
        try (FileWriter fw = new FileWriter(newReport, false);
            BufferedReader br = new BufferedReader(new FileReader(csv))) {

            // Initialize jinjava then immediately insert time and standard NVD CVE URL into context.
            Jinjava jj = new Jinjava();
            Map<String, Object> context = new HashMap<>();
            context.put("time", curDate);
            context.put("NVD_CVE_URL", NVD_CVE_URL);
            String template;
            if (StringUtil.isNull(pathToJJTemplate)) {
                template = Resources.toString(Resources.getResource("owasp-dc.html.j2"), Charsets.UTF_8);
            } else {
                template = new String(Files.readAllBytes(Paths.get(pathToJJTemplate), Charsets.UTF_8));
            }
            context = parseCSV(context, br, delimiter);
            fw.write(jj.render(template, context));
        }
    }

    /**
     * Parses OWASP-DC CSV for context dictionary.
     * @param context   Dictionary defining template variables
     * @param br        Reader for CSV file
     * @param delimiter Delimiter used to separate values in csv
     * @return context  Template variables including properties and vulnerabilities lists for rendering
     */
    private static Map<String, Object> parseCSV(Map<String, Object> context, BufferedReader br, String delimiter) {
        if (null == context || null == br || StringUtil.isNull(delimiter)) {
            throw new IllegalArgumentException("Either context, buffered reader, or delimiter was null!");
        }

        List<List<String>> vulList = new ArrayList<>();
        String brStr;
        try {
            // Parse for properties first
            if (null != (brStr = br.readLine())) {
                context.put("propList", parseValuesToList(brStr, "\\"+delimiter));
            } else {
                throw new RuntimeException("No properties found in first line of buffered reader!");
            }

            // Parse for vulnerability values.
            while (null != (brStr = br.readLine())) {
                vulList.add(parseValuesToList(brStr, "\\"+delimiter));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.put("vulList", vulList);
        return context;
    }

    /**
     * Parses each delimited value in a string into a list of strings
     * @param line String of values delimited by delimiter
     * @param delimiter Character separating values in line
     * @return Individual values in line as a list of strings
     */
    private static List<String> parseValuesToList(String line, String delimiter) {
        List<String> res = new ArrayList<>();
        Scanner scan = new Scanner(line).useDelimiter(delimiter);
        String scanStr;
        while(scan.hasNext()) {
            scanStr = scan.next();
            res.add(scanStr);
        }
        scan.close();
        return res;
    }

    /**
     * Exports resources (js, css files) to output resport directory.
     * Required to render report correctly.
     * @param pathToHTML absolute path to output directory where HTML file will be generated
     * @pre reportsDir Must exist and be a directory
     * @post resources in /src/main/resources are exported to reportsDir
     * @throws IOException
     */
    private static void exportResources(String pathToHTML) throws IOException {
        File reportsResourceDir = new File(pathToHTML+FS+"resources");
        File cssResourceDir = new File(reportsResourceDir,FS+"css");
        File jsResourceDir = new File(reportsResourceDir,FS+"js");
        File[] fileList = { reportsResourceDir, cssResourceDir, jsResourceDir };
        for (File f : fileList) {
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Error creating report's resource directories");
                }
            }
        }
        String cssPrefix = FS + "css" + FS;
        String jsPrefix = FS + "js" + FS;
        String[] cssRsrcList = { cssPrefix + "materialize.css" };
        String[] jsRsrcList = { jsPrefix + "materialize.js", jsPrefix + "jquery-2.1.1.min.js", jsPrefix + "init.js" };

        try {
            for (String cssRsrc : cssRsrcList) {
                exportFromJarToPath(cssRsrc, reportsResourceDir.getAbsolutePath());
            }
            for (String jsRsrc : jsRsrcList) {
                exportFromJarToPath(jsRsrc, reportsResourceDir.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new IOException("Error copying resources to reports resource directory!");
        }

    }

    /**
     * Exports file from inside JAR to file path on local system.
     * @param rsrcName name of resource
     * @param destFilePath absolute file path to export resource to
     * @pre Resource inside Jar must exist
     * @post Resource copied to destFilePath
     */
    private static void exportFromJarToPath(String rsrcName, String destFilePath) throws IOException {
        if (StringUtil.isNull(rsrcName)) {
            throw new IllegalArgumentException("Resource file path cannot be empty or null!");
        }
        if (StringUtil.isNull(destFilePath)) {
            throw new IllegalArgumentException("Dest file path cannot be empty or null!");
        }

        File newRsrc = new File(destFilePath+rsrcName);
        try {
            newRsrc.createNewFile();
        } catch (IOException e) {
            throw new IOException("Error creating resource file");
        }

        try (InputStream is = HTMLReportGenerator.class.getResourceAsStream(rsrcName);
            OutputStream os = new FileOutputStream(newRsrc)) {
            int readBytes;
            byte[] buffer = new byte[4096];
            while((readBytes = is.read(buffer)) > 0) {
                os.write(buffer, 0, readBytes);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("CCould not create or find file " + destFilePath);
        } catch (IOException e) {
            throw new IOException("Error with stream!");
        }
    }
}
