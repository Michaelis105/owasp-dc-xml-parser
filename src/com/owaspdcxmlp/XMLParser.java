package com.owaspdcxmlp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XMLParser {

    // Key: Dependency Name, Value: Dependency Object (Dependency.java)
    private static HashMap<String, Dependency> dependencies;
    private static DocumentBuilderFactory dbFactory;
    private static DocumentBuilder dBuilder;

    /**
     * Parses dependency data from OWASP-DC XML files.
     * Actual parsing is done in parseXML().
     * @param xmlList List of XML files
     * @return dependencies Collection of unique dependency entries
     */
    public static Collection<Dependency> parseXMLS(File[] xmlList) throws IOException {
        if (null == xmlList) {
            throw new IOException("xmlList is null!");
        }
        if (xmlList.length == 0) {
            throw new IllegalArgumentException("xmlList is empty!");
        }
        for (File xml : xmlList) {
            if (!xml.exists()) {
                throw new IllegalArgumentException("File " + xml + "does not exist!");
            }

            Document doc = null;
            try {
                doc = dBuilder.parse(xml);
                doc.getDocumentElement().normalize();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            NodeList depList = doc.getDocumentElement().getElementsByTagName("dependency");
            if (null == depList) continue; // No dependencies or not an OWASP-DC XML file.
            for (int i = 0; i < depList.getLength(); i++) {
                parseDependency(depList.item(i));
            }
        }
        return dependencies.values();
    }

    /**
     * Parses dependency data from OWASP-DC XML node.
     * @param dep OWASP-DC XML node
     */
    private static void parseDependency(Node dep) {
        if (null == dep) {
            throw new IllegalArgumentException("Dependency dep was null!");
        }
        Element depElem = (Element) dep;
        if (dep.getNodeType() == Node.ELEMENT_NODE) {
            String fileName = getStringElementByTag(depElem, "fileName");
            if (!StringUtil.isNull(fileName) && !dependencies.containsKey(fileName)) {

                Collection<String> vulListNames = new ArrayList<String>();
                NodeList vulList = depElem.getElementsByTagName("vulnerability");
                // if (null == vulList || vulList.getLength() == 0)
                Double curMaxVulCVSSScore = 0.0; // Assume minimum severity
                for (int i = 0; i < vulList.getLength(); i++) {
                    Vulnerability tempV = parseVulnerability(vulList.item(i));
                    if (null != tempV) {
                        vulListNames.add(tempV.getName());
                    }
                    if (tempV.getCvssScore() > curMaxVulCVSSScore) {
                        curMaxVulCVSSScore = tempV.getCvssScore();
                    }
                }

                Dependency d = new Dependency(fileName, curMaxVulCVSSScore, vulListNames);
                dependencies.put(d.name, d);
            }
        }
    }

    /**
     * Obtains single string value to key tag t under element e.
     * @param e XML element
     * @param t XML tag child of e
     * @return Value of tag of element node e
     */
    private static String getStringElementByTag(Element e, String t) {
        if (null == e || StringUtil.isNull(t)) return null;
        NodeList l = e.getElementsByTagName(t);
        if (null == l || l.getLength() == 0) return null;
        Node n = l.item(0);
        if (null == n) return null;
        return n.getTextContent();
    }

    /**
     * Parses vulnerability data from dependency data.
     * @param vul Vulnerability XML node
     * @return Vulnerability object
     */
    private static Vulnerability parseVulnerability(Node vul) {
        if (vul.getNodeType() == Node.ELEMENT_NODE) {
            Element vulElem = (Element) vul;
            String vulName = getStringElementByTag(vulElem, "name");
            if(!StringUtil.isNull(vulName)) return null;
            Double vulCVSSScore = Double.parseDouble(vulElem.getElementsByTagName("cvssScore").item(0).getTextContent());
            return new Vulnerability(vulName, vulCVSSScore);
        }
        return null;
    }

    /**
     * Initializes hash map for storing unique dependency entries.
     */
    public static void setupParser() throws Exception {
        // String = Dependency Name
        // Only store/access unique dependency objects of all dependencies identified.
        HashMap<String, Dependency> dependencies = new HashMap<String, Dependency>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Exception("Error initializing document builder!");
        }

    }

}
