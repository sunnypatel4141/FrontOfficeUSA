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

package frontoffice;

import frontoffice.base.XMLSettings;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sunny Patel
 */
public class Update extends XMLSettings {
    private String currentVersion = "";
    private String url = "";
    private String updateVersion= "";
    private String fileLocation = "";
    private String fileName = "";
    private boolean approved = false; // Is the update allowed
    private Document doc;
    private JFrame checkFrame = new JFrame("Update");
    
    public Update(String curVer, String urlArg, boolean approvedArg) {
        currentVersion = curVer;
        url = urlArg;
        approved = approvedArg;
    }
    
    /**
     * Automatically check is an update is available and downloads
     * the next version from our current one. Ready for installing
     */
    public Update() {
        url = Settings.get("updateURL").toString();
    }
    
    /**
     * Grab the update.xml, process it, if an update is 
     * available then download it ready for install
     */
    public boolean checkForUpdate() {
        // Try and download the file
        if ( !downloadUpdateFile() ) {
            return false;
        }
        
        try {
            File upfile = new File("update.xml"); //open the file for reading
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(upfile); //read the file

            updateVersion = readUpdateXMLField("Version");
            fileLocation = readUpdateXMLField("Location");
            fileName = readUpdateXMLField("FileName");
            
            downloadVersionFile();
        } catch (SAXException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    /**
     * Download Version that is to be installed in place of this version
     */
    private void downloadVersionFile() {
        try {
            URL urldwn = new URL(fileLocation);
            URLConnection urlcon = urldwn.openConnection();
            BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream());
            FileOutputStream FOS = new FileOutputStream(fileName);
            
            int i = 0;
            byte[] bytein = new byte[1024];
            while(( i = bis.read(bytein)) >= 0) {
                    FOS.write(bytein, 0, i); //write the file
            }
            
            FOS.close();
            bis.close();//close everything
        } catch (MalformedURLException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Downloads the update.xml file
     */
    private boolean downloadUpdateFile() {
        try {
            URL urldwn = new URL(url); //open url
            URLConnection urlcon = urldwn.openConnection(); //open the url
            BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream()); //open file stream
            FileOutputStream FOS = new FileOutputStream("update.xml"); //make file output stream
            
            int i = 0;
            byte[] bytein = new byte[1024];
            while(( i = bis.read(bytein)) >= 0) {
                    FOS.write(bytein, 0, i); //write the file
            }
            
            FOS.close();
            bis.close();//close everything
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }
    
    // When Instruction is given to go with the update
    public void proceedWithUpdate() {
        if ( checkForUpdate() && approved ) {
            // update is available we can proceed
            final JOptionPane exportConfirm = new JOptionPane("DANGER! \n " +
                    "This will OVERWRITE Current Software Version", 
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION);
            final JDialog dialog = new JDialog(checkFrame, "Export Schema", true);
            dialog.setContentPane(exportConfirm);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            exportConfirm.addPropertyChangeListener(
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent e) {
                            String prop = e.getPropertyName();
                            if ( dialog.isVisible()
                                && (e.getSource() == exportConfirm)
                                && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
                                dialog.setVisible(false);
                            }
                        }
                    });
            dialog.pack();
            dialog.setVisible(true);
            int value = ((Integer)exportConfirm.getValue()).intValue();
            if ( value == JOptionPane.YES_OPTION ) {
                runTheUpdate();
            }
        } else {
            // No update Available 
        }
    }
    
    // When Everything is ok and update will take place
    void runTheUpdate() {
        
    }
    
    /**
     * Read the fields of the update.xml
     * @param String fieldNameArg
     * @return String fieldValue
     */
    private String readUpdateXMLField(String fieldArg) {
        NodeList general = doc.getElementsByTagName("update");
        Node gnlNode = general.item(0);
        Element ElementOne = (Element) gnlNode;

        NodeList Ver = ElementOne.getElementsByTagName(fieldArg);
        Element Vere = (Element) Ver.item(0);
        NodeList VerNL = Vere.getChildNodes();
        String fieldName = ((Node) VerNL.item(0)).getNodeValue().toString();
        
        return fieldName;
    }
    
}
