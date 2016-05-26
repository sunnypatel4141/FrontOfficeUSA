/*
 * Copyright (C) 2015 Sunny Patel
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
package frontoffice.usa.Reports;

import frontoffice.usa.Report;

import frontoffice.usa.EndOfDay;
import frontoffice.usa.FloatSettings;
import frontoffice.base.DBConnection;
import frontoffice.util.JRXMLTOJASPER;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Sunny Patel
 */

/**
 * This class can run zread and an xread.
 * an XRead is a read only option that allows us to see the totals so far.
 * the ZRead is a hard option it allows us to
 */
public class ZLog extends DBConnection {

    public static final int X_READ = 0;
    public static final int Z_READ = 1;

    /**
     * In this class we want to use the `zlog` table.
     * Everytime this class is called it will create
     * an entry in the zlog table
     */
    public ZLog(int readType) {
        if(readType == Z_READ) {
            String zreadMessage = "Are you sure you want to do a Z Read?";
            int decision = JOptionPane.showConfirmDialog(null, zreadMessage,
                    "Z Read", JOptionPane.YES_NO_OPTION);
            // Yes Do one please
            if (decision == 0 ) {
                // do a z read
                doAZREAD();
            }
        } else if (readType == X_READ) {
            generateReportInViewer();
        }
    }

    private void doAZREAD() {
        // generateReportInViewer
        //clearTheFloat();
        //clearEndOfDay();
        generateReportInViewer();
        markAsDone();
    }

    private void clearTheFloat() {
        int decision = JOptionPane.showConfirmDialog(null, "Do you want to clear the float now?",
                    "Z Read", JOptionPane.YES_NO_OPTION);
        // Yes the float
        if (decision == 0 ) {
            FloatSettings fs = new FloatSettings();
        }
    }

    private void clearEndOfDay() {
        int decision = JOptionPane.showConfirmDialog(null, "Do you want to do an End Of Day now?",
                    "Z Read", JOptionPane.YES_NO_OPTION);
        // Yes the float
        if (decision == 0 ) {
            EndOfDay eod = new EndOfDay();
        }
    }

    private void generateReportInViewer() {
        try {
            String lastZReadTime = "";
            String sql = "select * from zlog order by id desc limit 1";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                lastZReadTime = rs.getString("timestamp");
            }
            
            // TMP Reload this report if required
            //JRXMLTOJASPER jx = new JRXMLTOJASPER("XZRead_Totals");
            //JRXMLTOJASPER jxa = new JRXMLTOJASPER("XZRead");
            
            HashMap reportData = new HashMap();
            reportData.put("lastread", lastZReadTime);
            Report rp = new Report("Reports/XZRead.jrxml", reportData);
            rp.viewReport();
        } catch (SQLException ex) {
            Logger.getLogger(ZLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Once You are happy with this ZRead then it will commence
     */
    private void markAsDone() {
        try {
            String sql = "insert into zlog (`timestamp`, `userid`) "
                    + "values (current_timestamp(), ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Settings.get("userid").toString());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ZLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
