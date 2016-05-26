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

package frontoffice.usa;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jpos.POSPrinter;
import jpos.CashDrawer;
import jpos.JposException;
/**
 *
 * @author Sunny Patel
 */
class CashDrawerGeneric {
    
    private int DRAWER_METHOD = 0; // This is the default menthod as controlled by the cashdrawer.bat file
    public int DRAWER_METHOD_BATCH = 0;
    public int DRAWER_METHOD_STAR_TSP = 1;
    private String DeviceName = "Star TSP100 Cutter (TSP143)";
    //System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");
    
    // Cash Drawer Objects
    CashDrawer drw;
    POSPrinter posPrinter;
    
    /**
     * Set the cashdrawer params 
     */
    public CashDrawerGeneric(Object[] arg) {
        DRAWER_METHOD = Integer.parseInt(arg[0].toString());
        if ( arg.length >= 1 ) {
            DeviceName = arg[1].toString(); // the device name for the printer
        }
    }
    
    /**tsp100lm:5&amp;10344491&amp;0&amp;1
     * This will decide how to open the drawer 
     * from the params specified when constructing this class
     */
    public void openDrawer() {
        if ( DRAWER_METHOD == DRAWER_METHOD_BATCH ) {
            try {
                // Just run the cashdrawer.bat
                Process process = Runtime.getRuntime().exec("cashdrawer.bat");
            } catch (IOException ex) {
                Logger.getLogger(CashDrawerGeneric.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Cannot Open Drawer " + ex.getMessage(),
                        "Cannot Open Drawer", JOptionPane.ERROR_MESSAGE);
            }
        } else if ( DRAWER_METHOD == DRAWER_METHOD_STAR_TSP ) {
            openDrawerThroughStarTSP();
        }
    }
    
    private void openDrawerThroughStarTSP() {
        try {
            drw = new CashDrawer();
            drw.open(DeviceName);
            drw.claim(1);
            drw.setDeviceEnabled(true);
            drw.openDrawer();
        } catch( Exception a ) {
            JOptionPane.showMessageDialog(null, "Cannot Open Drawer " + a.getMessage(), 
                    "Drawer Error", JOptionPane.ERROR_MESSAGE );
            Logger.getLogger(CashDrawerGeneric.class.getName()).log(Level.SEVERE, null, a);
        }
        // This seperate incase of errors above thus allows it to be ensured
        try {
            drw.close();
        } catch (JposException ex) {
            Logger.getLogger(CashDrawerGeneric.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}