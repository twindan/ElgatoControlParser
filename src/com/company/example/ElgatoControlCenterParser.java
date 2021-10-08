package com.company.example;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class ElgatoControlCenterParser {

    private static final ElgatoKeyLight [] _noLights = new ElgatoKeyLight[0];

    public static ElgatoKeyLight[] getKeyLights()  {

        // on Windows, APPDATA will point to the app data directory. On other platforms, this is undefined
        var appData = System.getenv("APPDATA");
        if ( appData == null )
            return _noLights;

        // check to see if the ControlCenter settings exists
        var controlCenterSettingsFile = Paths.get(appData, "Elgato", "ControlCenter", "settings.xml").toFile();
        if ( !controlCenterSettingsFile.exists() )
            return _noLights;

        try {
            // open the settings XML file
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            var builder = factory.newDocumentBuilder();
            var settingsDocument = builder.parse(controlCenterSettingsFile);

            // setup xpath
            var xPathFactory = XPathFactory.newInstance();
            var xpath = xPathFactory.newXPath();

            // find all lights using an XPath query
            var accessoryQuery = xpath.compile("/AppSettings/Application/Accessories/Accessory");
            var results = (NodeList)accessoryQuery.evaluate(settingsDocument, XPathConstants.NODESET);

            // we might have invalid entries, so use a dynamic list to generate the return results
            var returnBuilder = new ArrayList<ElgatoKeyLight>();
            for (var accessoryIndex = 0; accessoryIndex < results.getLength(); ++accessoryIndex) {
                var node = (Node)results.item(accessoryIndex);

                String foundName = null;
                String foundServerAddress = null;

                // scan the child for the lights
                var children = node.getChildNodes();
                for ( var childIndex = 0; childIndex < children.getLength(); ++childIndex ) {
                    var testChild = (Node)children.item(childIndex);

                    // Prefer using <UserDefinedName>
                    if ( testChild.getNodeName() =="UserDefinedName" )
                        foundName = testChild.getTextContent();
                    // Fallback to using <Name> if <UserDefinedName> is not defined
                    else if ( testChild.getNodeName() == "Name" && foundName == null )
                        foundName = testChild.getTextContent();
                    // The server is stored in <IpAddress>
                    else if ( testChild.getNodeName() == "IpAddress" )
                        foundServerAddress = testChild.getTextContent();
                }

                // if we got both a name & a server, then add it
                if ( foundName != null && foundServerAddress != null )
                    returnBuilder.add( new ElgatoKeyLight(foundName, foundServerAddress));
            }

            // convert it to an array and return it
            var ret = new ElgatoKeyLight[returnBuilder.size()];
            returnBuilder.toArray(ret);
            return ret;
        } catch (IOException e) {
            // This happens if the XML file cannot be read
            return _noLights;
        } catch (ParserConfigurationException e) {
            // This happens if the XML file is corrupt
            return _noLights;
        } catch (SAXException e) {
            // This happens if the XML file is corrupt
            return _noLights;
        } catch (XPathExpressionException e) {
            // this is an internal programming error that is raised if the XPath expression is bad
            e.printStackTrace();
            return _noLights;
        }
    }
}