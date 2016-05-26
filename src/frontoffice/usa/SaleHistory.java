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

import frontoffice.usa.Reports.CustomerReceipt;
import static frontoffice.base.XMLSettings.Settings;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Calendar;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 */
public class SaleHistory extends MainMenu implements ActionListener, ListSelectionListener {
    JDialog frameHistory = new JDialog(frame, "Sale History", true);
    
    Object[] cnHistory = {"Sale ID", "Total", "Change", "Paid", "Time" };
    Object[][] dataHistory = null;
    DefaultTableModel dtmHistory = new DefaultTableModel(dataHistory, cnHistory);
    JTable tableHistory = new JTable(dtmHistory);
    SpinnerDateModel sdmHistoryStart = new SpinnerDateModel();
    JSpinner jsnHistoryStart = new JSpinner(sdmHistoryStart);
    
    // For Actual Sale Information
    Object[] cnHistoryDtl = {"Product Code", "Name", "Qty", "Discount", "Tax Rate", "Unit Price", "Total"};
    Object[][] dataHistoryDtl = null;
    DefaultTableModel dtmHistoryDtl = new DefaultTableModel(dataHistoryDtl, cnHistoryDtl);
    JTable tableHistoryDtl = new JTable(dtmHistoryDtl);
    SpinnerDateModel sdmHistoryEnd = new SpinnerDateModel();
    JSpinner jsnHistoryEnd = new JSpinner(sdmHistoryEnd);
    
    // Buttons For this control
    JButton printRecieptHistory, closeHistory, searchHistory;
    
    public SaleHistory() {
        renderHistory();
    }
    
    private void renderHistory() {
        JPanel controlPnl = new JPanel(); // This is the top panel
        JPanel datePnl = new JPanel();  // This is the spinenr for the Dates
        JPanel btnsPnl = new JPanel();  // This is the Buttons Panel
        JPanel histPnl = new JPanel();  // The Transaction Log
        JPanel histDtlPnl = new JPanel();   // The transaction Item Log
        
        // Date Fields To and from
        JLabel sDateLbl = new JLabel("Start Date");
        JLabel eDateLbl = new JLabel("End Date");
        
        datePnl.add(sDateLbl);
        jsnHistoryStart.setPreferredSize(new Dimension(180, 40));
        jsnHistoryStart.setFont(large);
        datePnl.add(jsnHistoryStart);
        datePnl.add(eDateLbl);
        datePnl.add(jsnHistoryEnd);
        jsnHistoryEnd.setPreferredSize(new Dimension(180, 40));
        jsnHistoryEnd.setFont(large);
        datePnl.setSize(250, 250);
        
        // Buttons panel
        printRecieptHistory = new JButton("Print Reciept");
        printRecieptHistory.addActionListener(this);
        closeHistory = new JButton("Close");
        closeHistory.addActionListener(this);
        searchHistory = new JButton("Search");
        searchHistory.addActionListener(this);
        
        btnsPnl.add(printRecieptHistory);
        btnsPnl.add(closeHistory);
        btnsPnl.add(searchHistory);
        
        // The Tables
        JScrollPane jspHistory = new JScrollPane(tableHistory);
        // Table Listener for when a transaction is selected
        tableHistory.getSelectionModel().addListSelectionListener(this);
        tableHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jspHistory.setPreferredSize(new Dimension(700, 270));
        histPnl.add(jspHistory);
        
        JScrollPane jspHistDtl = new JScrollPane(tableHistoryDtl);
        jspHistDtl.setPreferredSize(new Dimension(700, 270));
        histDtlPnl.add(jspHistDtl);
        
        controlPnl.add(datePnl);
        controlPnl.add(btnsPnl);
        
        JPanel BoxLayoutPnl = new JPanel();
        
        BoxLayoutPnl.add(controlPnl);
        BoxLayoutPnl.add(histPnl);
        BoxLayoutPnl.add(histDtlPnl);
        
        BoxLayoutPnl.setLayout(new BoxLayout(BoxLayoutPnl, BoxLayout.PAGE_AXIS));
        
        frameHistory.add(BoxLayoutPnl);
        frameHistory.setSize(740, 740);
        frameHistory.setVisible(true);
    }
    
    private void getTransactions(Calendar SDateArg, Calendar EDateArg) {
        String sdate = new Date(SDateArg.getTimeInMillis()).toString();
        String edate = new Date(EDateArg.getTimeInMillis()).toString();
        // Get tranactions for the days specified
        try {
            // Is you want data for a single day then the query is different
            String sql;
            if (sdate.equals(edate)) {
                sql = "select `id`, `amounttopay`, `change`, `created`, `account`, " +
                        " (cash + card + voucher + online + account ) as ttl " +
                        "from sale where date(`created`) = ? order by `id`";
            } else {
                sql = "select `id`, `amounttopay`, `change`, `created`, `account`, " +
                        " (cash + card + voucher + online + account ) as ttl " +
                        "from sale where date(`created`) between ? and ? order by `id`";
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sdate.toString());
            if(!sdate.equals(edate)) {
                pstmt.setString(2, edate.toString());
            }
            rs = pstmt.executeQuery();
            while( rs.next() ) {
                float change = rs.getFloat("change") * -1;
                Object[] row = {rs.getString("id"), rs.getString("amounttopay"),
                    change, rs.getString("ttl"), rs.getString("created")};
                // Add the row to table
                dtmHistory.addRow(row);
            }
        } catch( Exception a ) {
            a.printStackTrace();
        }
    }
    
    private void getTransactionByID(String IDArg) {
        // Clear any unwanted rows
        int rowCount = tableHistoryDtl.getRowCount();
        if ( rowCount > 0 ) {
            while ( rowCount > 0 ) {
                dtmHistoryDtl.removeRow(0);
                rowCount--;
            }
        }
        // Get Transaction By ID Typically as selected from the days
        try {
            String sql = "select * from saleitem where saleid='" + IDArg +"'";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                // Build the row reference
                Object[] row = { rs.getString("prid"), rs.getString("name"), rs.getString("qty"),
                    rs.getString("discount"), rs.getString("taxrate"), rs.getString("unitprice"), rs.getString("total")};
                // Add the row to table
                dtmHistoryDtl.addRow(row);
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        
        if(trigger == printRecieptHistory) {
            // Send this to the report queue
            CustomerReceipt rp = new CustomerReceipt("Reports/CustomerReceipt.jrxml",
            getSelectedRowTranscationID());
        rp.printReport();
        } else if (trigger == closeHistory) {
            frameHistory.dispose();
        } else if (trigger == searchHistory) {
            Calendar sdate = Calendar.getInstance();
            sdate.setTime(sdmHistoryStart.getDate());
            Calendar edate = Calendar.getInstance();
            edate.setTime(sdmHistoryEnd.getDate());
            getTransactions(sdate, edate);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        
        getTransactionByID(getSelectedRowTranscationID());
    }
    
    /**
     * Common place to get transactionID
     */
    private String getSelectedRowTranscationID() {
        int selectedRow = tableHistory.getSelectedRow();
        String transactionID = dtmHistory.getValueAt(selectedRow, 0).toString();
        return transactionID;
    }
}
