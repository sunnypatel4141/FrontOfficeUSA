/*
 * Copyright (C) 2014 Sunny Patel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author Sunny Patel
 */

package frontoffice.base;

import java.io.File;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;

/**
 *
 * @author Family
 */
public class XMLSettings extends StaticObjects {
    
    
    public XMLSettings() {
        loadSettings();
    }
    
    public static HashMap Settings = new HashMap();

    static Object[] XMLList = {"companyName", 
                            "address", 
                            "town", 
                            "postCode", 
                            "tel", 
                            "vat", 
                            "e-mail", 
                            "web", 
                            "footNoteone", 
                            "footNote2", 
                            "footNote3", 
                            "url", 
                            "user", 
                            "password",
                            "backupurl", 
                            "backupuser", 
                            "backuppassword",
                            "socketserver", 
                            "socketport", 
                            "tillNumber", 
                            "printer", 
                            "custDispCOM", 
                            "COMPort", 
                            "custDispUSB", 
                            "cashDrawer", 
                            "weightScale", 
                            "printerCOM",
                            "cashDrawerType",
                            "lineDisp",
                            "lineDisp2",
                            "currentVersion",
                            "updateURL",
                            "TestMode",
                        };
    
    void loadSettings() {
        try {
            // Prepare the file for opening
            File file = new File("Settings.XML");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            // Fetch the parent Element
            doc.getDocumentElement().normalize();
            NodeList general = doc.getElementsByTagName("general");
            Node gnlNode = general.item(0);
            Element ElementOne = (Element) gnlNode;
            
            for ( int i = 0; i < XMLList.length; i++ ) {
                String tagName = XMLList[i].toString();
                NodeList elmNL = ElementOne.getElementsByTagName(tagName);
                Element nlElmConvert = (Element) elmNL.item(0);
                NodeList nlElmConvertNL = nlElmConvert.getChildNodes();
                String elmValue = ((Node) nlElmConvertNL.item(0)).getNodeValue().toString();
                Settings.put(tagName, elmValue);
            }
        } catch (Exception a) {
            a.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot load settings: \n" + a);
        }
    }

    void setVals(String settingArg, String nodeName) {
        try {
            String XMSet = "Settings.xml";
            DocumentBuilderFactory dof = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dof.newDocumentBuilder();
            Document doc = db.parse(XMSet);

            Node SettingsFile = doc.getFirstChild();
            Node general = doc.getElementsByTagName("general").item(0);
            
            NodeList VerOne = general.getChildNodes();
            // Lets iterate through the nodes
            for(int i = 0; i < VerOne.getLength(); i++) {
                Node node = VerOne.item(i);
                if ( nodeName.equals(node.getNodeName()) ) {
                    node.setTextContent("" + settingArg);
                }
            }

            TransformerFactory TF = TransformerFactory.newInstance();
            Transformer T = TF.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(XMSet));
            T.transform(source, result);

        } catch(Exception a) {
            JOptionPane.showMessageDialog(null, "An Error occured" + a, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}